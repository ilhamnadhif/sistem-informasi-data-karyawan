package com.karyawan.view.transaksi;

import com.karyawan.dao.AbsensiDAO;
import com.karyawan.dao.KaryawanDAO;
import com.karyawan.dao.PenggajianDAO;
import com.karyawan.model.Karyawan;
import com.karyawan.model.Penggajian;
import com.karyawan.util.Konstanta;
import com.karyawan.util.PaginationHelper;
import com.karyawan.util.RupiahFormat;
import com.karyawan.util.TabelAksi;
import com.karyawan.util.TanggalFormat;
import com.karyawan.util.TanggalPicker;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Form transaksi Penggajian karyawan. Pola referensi: toolbar + tabel berkolom
 * "Aksi" (Edit/Hapus) + pagination; tambah & edit lewat popup dialog.
 */
public class FrmPenggajian extends javax.swing.JPanel {

    private static final int KOL_AKSI = 10;

    private final PenggajianDAO dao = new PenggajianDAO();
    private final PaginationHelper page = new PaginationHelper();
    private DefaultTableModel model;
    private List<Penggajian> dataHal = new ArrayList<>(); // data baris halaman aktif

    private JTextField txtCari;
    private JComboBox<Integer> cmbPageSize;
    private JTable tblData;
    private JButton btnFirst;
    private JButton btnPrev;
    private JButton btnNext;
    private JButton btnLast;
    private JLabel lblHalaman;

    public FrmPenggajian() {
        buildUI();
        setupTabel();
        loadData();
    }

    /** Susun toolbar + tabel + pagination. */
    private void buildUI() {
        setLayout(new BorderLayout(0, 8));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        toolbar.add(new JLabel("Cari:"));
        txtCari = new JTextField(18);
        toolbar.add(txtCari);
        toolbar.add(new JLabel("Tampil:"));
        cmbPageSize = new JComboBox<>(new Integer[]{10, 25, 50});
        cmbPageSize.addActionListener(e -> {
            Integer s = (Integer) cmbPageSize.getSelectedItem();
            if (s != null) {
                page.setPageSize(s);
                loadData();
            }
        });
        toolbar.add(cmbPageSize);
        JButton btnTambah = new JButton("+ Tambah");
        btnTambah.addActionListener(e -> bukaDialog(null));
        toolbar.add(btnTambah);
        add(toolbar, BorderLayout.NORTH);

        tblData = new JTable();
        add(new JScrollPane(tblData), BorderLayout.CENTER);

        JPanel pnlPage = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        btnFirst = navBtn("«", e -> { page.first(); loadData(); });
        btnPrev = navBtn("‹", e -> { page.prev(); loadData(); });
        lblHalaman = new JLabel("Halaman 1 dari 1");
        btnNext = navBtn("›", e -> { page.next(); loadData(); });
        btnLast = navBtn("»", e -> { page.last(); loadData(); });
        pnlPage.add(btnFirst);
        pnlPage.add(btnPrev);
        pnlPage.add(lblHalaman);
        pnlPage.add(btnNext);
        pnlPage.add(btnLast);
        add(pnlPage, BorderLayout.SOUTH);
    }

    private JButton navBtn(String teks, java.awt.event.ActionListener act) {
        JButton b = new JButton(teks);
        b.addActionListener(act);
        return b;
    }

    /** Siapkan model tabel + kolom Aksi + listener pencarian. */
    private void setupTabel() {
        model = new DefaultTableModel(
            new Object[]{"ID", "IDK", "Periode", "NIK", "Nama", "Gaji Pokok",
                "Tunjangan", "Potongan", "Total Gaji", "Tgl Bayar", "Aksi"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == KOL_AKSI; // hanya kolom Aksi yang editable (agar tombol bisa diklik)
            }
        };
        tblData.setModel(model);
        // sembunyikan kolom ID (0) & ID Karyawan (1) -> dipakai internal
        sembunyikanKolom(0);
        sembunyikanKolom(1);
        TabelAksi.pasang(tblData, KOL_AKSI, this::editBaris, this::hapusBaris);

        txtCari.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { page.reset(); loadData(); }
            public void removeUpdate(DocumentEvent e) { page.reset(); loadData(); }
            public void changedUpdate(DocumentEvent e) { page.reset(); loadData(); }
        });
        cmbPageSize.setSelectedItem(page.getPageSize());
    }

    /** Sembunyikan kolom tabel pada indeks tertentu. */
    private void sembunyikanKolom(int idx) {
        tblData.getColumnModel().getColumn(idx).setMinWidth(0);
        tblData.getColumnModel().getColumn(idx).setMaxWidth(0);
        tblData.getColumnModel().getColumn(idx).setWidth(0);
    }

    /** Ambil 1 halaman data dari DB sesuai keyword & isi tabel. */
    private void loadData() {
        try {
            String keyword = txtCari.getText().trim();
            page.setTotalRows(dao.count(keyword));
            dataHal = dao.findPaged(page.getCurrentPage(), page.getPageSize(), keyword);
            model.setRowCount(0);
            for (Penggajian p : dataHal) {
                model.addRow(new Object[]{
                    p.getIdPenggajian(),
                    p.getIdKaryawan(),
                    p.getPeriodeBulan() + "/" + p.getPeriodeTahun(),
                    p.getNik(),
                    p.getNama(),
                    RupiahFormat.format(p.getGajiPokok()),
                    RupiahFormat.format(p.getTotalTunjangan()),
                    RupiahFormat.format(p.getPotongan()),
                    RupiahFormat.format(p.getTotalGaji()),
                    TanggalFormat.format(p.getTanggalBayar()),
                    "" // sel Aksi digambar oleh TabelAksi
                });
            }
            lblHalaman.setText(page.label());
            btnFirst.setEnabled(page.hasPrev());
            btnPrev.setEnabled(page.hasPrev());
            btnNext.setEnabled(page.hasNext());
            btnLast.setEnabled(page.hasNext());
        } catch (Exception ex) {
            pesanError("Gagal memuat data: " + ex.getMessage());
        }
    }

    private void editBaris(int modelRow) {
        if (modelRow >= 0 && modelRow < dataHal.size()) {
            bukaDialog(dataHal.get(modelRow));
        }
    }

    private void hapusBaris(int modelRow) {
        if (modelRow < 0 || modelRow >= dataHal.size()) {
            return;
        }
        Penggajian p = dataHal.get(modelRow);
        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Yakin hapus data penggajian ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.delete(p.getIdPenggajian());
            pesanInfo("Data berhasil dihapus.");
            loadData();
        } catch (Exception ex) {
            pesanError("Gagal menghapus: " + ex.getMessage());
        }
    }

    /** Popup tambah (existing=null) atau edit (existing!=null). */
    private void bukaDialog(Penggajian existing) {
        boolean editMode = existing != null;

        JComboBox<Karyawan> cmbKaryawan = new JComboBox<>();
        try {
            for (Karyawan k : new KaryawanDAO().findAll()) {
                cmbKaryawan.addItem(k);
            }
        } catch (Exception ex) {
            pesanError("Gagal memuat daftar karyawan: " + ex.getMessage());
        }
        JComboBox<Integer> cmbBulan = new JComboBox<>();
        for (int b = 1; b <= 12; b++) {
            cmbBulan.addItem(b);
        }
        JTextField txtTahun = new JTextField(20);
        txtTahun.setText(String.valueOf(Year.now().getValue()));
        JTextField txtGajiPokok = new JTextField(20);
        txtGajiPokok.setEditable(false);
        JTextField txtTunjangan = new JTextField(20);
        txtTunjangan.setEditable(false);
        JTextField txtPotongan = new JTextField(20);
        JTextField txtTotal = new JTextField(20);
        txtTotal.setEditable(false);
        TanggalPicker txtTglBayar = new TanggalPicker();
        JTextField txtKeterangan = new JTextField(20);

        // total = gajiPokok + tunjangan - potongan (dibaca dari field saat dipanggil)
        Runnable hitungTotal = () -> {
            double gajiPokok = RupiahFormat.parse(txtGajiPokok.getText());
            double tunjangan = RupiahFormat.parse(txtTunjangan.getText());
            double potongan = RupiahFormat.parse(txtPotongan.getText());
            txtTotal.setText(RupiahFormat.format(gajiPokok + tunjangan - potongan));
        };

        JButton btnHitung = new JButton("Hitung Otomatis");
        btnHitung.addActionListener(e -> {
            Object selKaryawan = cmbKaryawan.getSelectedItem();
            Object selBulan = cmbBulan.getSelectedItem();
            if (selKaryawan == null || selBulan == null) {
                pesanWarning("Pilih karyawan dan bulan terlebih dahulu.");
                return;
            }
            int tahun;
            try {
                tahun = Integer.parseInt(txtTahun.getText().trim());
            } catch (NumberFormatException ex) {
                pesanWarning("Tahun harus berupa angka.");
                return;
            }
            try {
                int id = ((Karyawan) selKaryawan).getIdKaryawan();
                int bulan = (Integer) selBulan;
                double gajiPokok = dao.hitungGajiPokok(id);
                double tunjangan = dao.hitungTotalTunjangan(id);
                int jumlahAlpha = new AbsensiDAO().countAlpha(id, bulan, tahun);
                double potongan = jumlahAlpha * Konstanta.RATE_POTONGAN_ALPHA;
                txtGajiPokok.setText(RupiahFormat.format(gajiPokok));
                txtTunjangan.setText(RupiahFormat.format(tunjangan));
                txtPotongan.setText(String.valueOf((long) potongan));
                hitungTotal.run();
            } catch (Exception ex) {
                pesanError("Gagal menghitung gaji: " + ex.getMessage());
            }
        });

        // recompute total whenever potongan changes
        txtPotongan.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { hitungTotal.run(); }
            public void removeUpdate(DocumentEvent e) { hitungTotal.run(); }
            public void changedUpdate(DocumentEvent e) { hitungTotal.run(); }
        });

        if (editMode) {
            pilihKaryawan(cmbKaryawan, existing.getIdKaryawan());
            cmbBulan.setSelectedItem(existing.getPeriodeBulan());
            txtTahun.setText(String.valueOf(existing.getPeriodeTahun()));
            txtGajiPokok.setText(RupiahFormat.format(existing.getGajiPokok()));
            txtTunjangan.setText(RupiahFormat.format(existing.getTotalTunjangan()));
            txtPotongan.setText(String.valueOf((long) existing.getPotongan()));
            txtTotal.setText(RupiahFormat.format(existing.getTotalGaji()));
            txtTglBayar.setDate(existing.getTanggalBayar());
            Object ket = existing.getKeterangan();
            txtKeterangan.setText(ket == null ? "" : ket.toString());
        }

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        baris(form, g, 0, "Karyawan", cmbKaryawan);
        baris(form, g, 1, "Bulan", cmbBulan);
        baris(form, g, 2, "Tahun", txtTahun);
        baris(form, g, 3, "Gaji Pokok", txtGajiPokok);
        baris(form, g, 4, "Total Tunjangan", txtTunjangan);
        baris(form, g, 5, "Potongan", txtPotongan);
        baris(form, g, 6, "Total Gaji", txtTotal);
        baris(form, g, 7, "Tanggal Bayar", txtTglBayar);
        baris(form, g, 8, "Keterangan", txtKeterangan);
        baris(form, g, 9, "", btnHitung);

        String judul = editMode ? "Edit Penggajian" : "Tambah Penggajian";
        while (true) {
            int ok = JOptionPane.showConfirmDialog(this, form, judul,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (ok != JOptionPane.OK_OPTION) {
                return;
            }
            // validasi
            if (cmbKaryawan.getSelectedItem() == null) {
                pesanWarning("Karyawan wajib dipilih.");
                continue;
            }
            int tahun;
            try {
                tahun = Integer.parseInt(txtTahun.getText().trim());
            } catch (NumberFormatException ex) {
                pesanWarning("Tahun harus berupa angka.");
                continue;
            }
            try {
                Karyawan k = (Karyawan) cmbKaryawan.getSelectedItem();
                int bulan = (Integer) cmbBulan.getSelectedItem();
                int excludeId = editMode ? existing.getIdPenggajian() : 0;
                // cegah dobel penggajian (karyawan, bulan, tahun)
                if (dao.isExists(k.getIdKaryawan(), bulan, tahun, excludeId)) {
                    pesanWarning("Karyawan ini sudah memiliki penggajian pada periode tersebut.");
                    continue;
                }
                Penggajian p = new Penggajian();
                p.setIdPenggajian(excludeId);
                p.setIdKaryawan(k.getIdKaryawan());
                p.setPeriodeBulan(bulan);
                p.setPeriodeTahun(tahun);
                p.setGajiPokok(RupiahFormat.parse(txtGajiPokok.getText()));
                p.setTotalTunjangan(RupiahFormat.parse(txtTunjangan.getText()));
                p.setPotongan(RupiahFormat.parse(txtPotongan.getText()));
                p.setTotalGaji(RupiahFormat.parse(txtTotal.getText()));
                p.setTanggalBayar(txtTglBayar.getDate());
                p.setKeterangan(txtKeterangan.getText().trim());
                if (editMode) {
                    dao.update(p);
                    pesanInfo("Data berhasil diubah.");
                } else {
                    dao.insert(p);
                    pesanInfo("Data berhasil ditambahkan.");
                }
                loadData();
                return;
            } catch (Exception ex) {
                pesanError("Gagal menyimpan: " + ex.getMessage());
                return;
            }
        }
    }

    /** Pilih item combo yang idKaryawan-nya cocok. */
    private void pilihKaryawan(JComboBox<Karyawan> cmbKaryawan, int idKaryawan) {
        for (int i = 0; i < cmbKaryawan.getItemCount(); i++) {
            Karyawan k = cmbKaryawan.getItemAt(i);
            if (k != null && k.getIdKaryawan() == idKaryawan) {
                cmbKaryawan.setSelectedIndex(i);
                return;
            }
        }
    }

    /** Tambah 1 baris label + field ke panel GridBag. */
    private void baris(JPanel panel, GridBagConstraints g, int y, String label, Component field) {
        g.gridx = 0;
        g.gridy = y;
        g.weightx = 0;
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(label), g);
        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, g);
    }

    private void pesanInfo(String m) {
        JOptionPane.showMessageDialog(this, m, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    private void pesanWarning(String m) {
        JOptionPane.showMessageDialog(this, m, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    private void pesanError(String m) {
        JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
