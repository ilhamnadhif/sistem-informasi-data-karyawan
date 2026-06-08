package com.karyawan.view.transaksi;

import com.karyawan.dao.CutiDAO;
import com.karyawan.dao.KaryawanDAO;
import com.karyawan.model.Cuti;
import com.karyawan.model.Karyawan;
import com.karyawan.util.PaginationHelper;
import com.karyawan.util.TabelAksi;
import com.karyawan.util.TanggalFormat;
import com.karyawan.util.TanggalPicker;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Form transaksi Cuti karyawan. Pola referensi: toolbar + tabel berkolom "Aksi"
 * (Edit/Hapus) + pagination; tambah & edit lewat popup dialog.
 */
public class FrmCuti extends javax.swing.JPanel {

    private static final int KOL_AKSI = 8;

    private final CutiDAO dao = new CutiDAO();
    private final PaginationHelper page = new PaginationHelper();
    private DefaultTableModel model;
    private List<Cuti> dataHal = new ArrayList<>(); // data baris halaman aktif

    private JTextField txtCari;
    private JComboBox<Integer> cmbPageSize;
    private JTable tblData;
    private JButton btnFirst;
    private JButton btnPrev;
    private JButton btnNext;
    private JButton btnLast;
    private JLabel lblHalaman;

    public FrmCuti() {
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
            new Object[]{"ID", "IDK", "Nama", "Jenis", "Tgl Mulai",
                "Tgl Selesai", "Lama (hari)", "Status", "Aksi"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == KOL_AKSI; // hanya kolom Aksi yang editable (agar tombol bisa diklik)
            }
        };
        tblData.setModel(model);
        // sembunyikan kolom ID (kolom 0) dan ID Karyawan (kolom 1) -> dipakai internal
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
            for (Cuti c : dataHal) {
                model.addRow(new Object[]{
                    c.getIdCuti(),
                    c.getIdKaryawan(),
                    c.getNama(),
                    c.getJenisCuti(),
                    TanggalFormat.format(c.getTanggalMulai()),
                    TanggalFormat.format(c.getTanggalSelesai()),
                    c.getLamaCuti(),
                    c.getStatus(),
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
        Cuti c = dataHal.get(modelRow);
        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Yakin hapus data cuti ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.delete(c.getIdCuti());
            pesanInfo("Data berhasil dihapus.");
            loadData();
        } catch (Exception ex) {
            pesanError("Gagal menghapus: " + ex.getMessage());
        }
    }

    /** Popup tambah (existing=null) atau edit (existing!=null). */
    private void bukaDialog(Cuti existing) {
        boolean editMode = existing != null;

        JComboBox<Karyawan> cmbKaryawan = new JComboBox<>();
        try {
            for (Karyawan k : new KaryawanDAO().findAll()) {
                cmbKaryawan.addItem(k);
            }
        } catch (Exception ex) {
            pesanError("Gagal memuat daftar karyawan: " + ex.getMessage());
            return;
        }
        TanggalPicker txtPengajuan = new TanggalPicker();
        TanggalPicker txtMulai = new TanggalPicker();
        TanggalPicker txtSelesai = new TanggalPicker();
        JComboBox<String> cmbJenis = new JComboBox<>(
            new String[]{"Tahunan", "Sakit", "Melahirkan", "Penting", "Lainnya"});
        JTextArea txtAlasan = new JTextArea(3, 20);
        txtAlasan.setLineWrap(true);
        txtAlasan.setWrapStyleWord(true);
        JComboBox<String> cmbStatus = new JComboBox<>(
            new String[]{"Pending", "Disetujui", "Ditolak"});

        if (editMode) {
            pilihKaryawan(cmbKaryawan, existing.getIdKaryawan());
            txtPengajuan.setDate(existing.getTanggalPengajuan());
            txtMulai.setDate(existing.getTanggalMulai());
            txtSelesai.setDate(existing.getTanggalSelesai());
            cmbJenis.setSelectedItem(existing.getJenisCuti());
            txtAlasan.setText(existing.getAlasan() == null ? "" : existing.getAlasan());
            cmbStatus.setSelectedItem(existing.getStatus());
        }

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        baris(form, g, 0, "Karyawan", cmbKaryawan);
        baris(form, g, 1, "Tanggal Pengajuan", txtPengajuan);
        baris(form, g, 2, "Tanggal Mulai", txtMulai);
        baris(form, g, 3, "Tanggal Selesai", txtSelesai);
        baris(form, g, 4, "Jenis Cuti", cmbJenis);
        baris(form, g, 5, "Alasan", new JScrollPane(txtAlasan));
        baris(form, g, 6, "Status", cmbStatus);

        String judul = editMode ? "Edit Cuti" : "Tambah Cuti";
        while (true) {
            int ok = JOptionPane.showConfirmDialog(this, form, judul,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (ok != JOptionPane.OK_OPTION) {
                return;
            }
            // validasi
            if (cmbKaryawan.getSelectedItem() == null) {
                pesanWarning("Karyawan wajib dipilih."); continue;
            }
            if (txtPengajuan.isEmpty()) {
                pesanWarning("Tanggal pengajuan wajib diisi."); continue;
            }
            if (txtMulai.isEmpty()) {
                pesanWarning("Tanggal mulai wajib diisi."); continue;
            }
            if (txtSelesai.isEmpty()) {
                pesanWarning("Tanggal selesai wajib diisi."); continue;
            }
            try {
                Date mulai = txtMulai.getDate();
                Date selesai = txtSelesai.getDate();
                // validasi: tanggal selesai tidak boleh sebelum tanggal mulai
                if (selesai.before(mulai)) {
                    pesanWarning("Tanggal selesai harus sama atau setelah tanggal mulai.");
                    continue;
                }
                Cuti c = new Cuti();
                c.setIdCuti(editMode ? existing.getIdCuti() : 0);
                Karyawan k = (Karyawan) cmbKaryawan.getSelectedItem();
                c.setIdKaryawan(k.getIdKaryawan());
                c.setTanggalPengajuan(txtPengajuan.getDate());
                c.setTanggalMulai(mulai);
                c.setTanggalSelesai(selesai);
                c.setJenisCuti(String.valueOf(cmbJenis.getSelectedItem()));
                // rumus lama cuti = selisih hari (selesai - mulai) + 1
                int lama = (int) ((selesai.getTime() - mulai.getTime()) / 86400000L) + 1;
                c.setLamaCuti(lama);
                c.setAlasan(txtAlasan.getText().trim());
                c.setStatus(String.valueOf(cmbStatus.getSelectedItem()));
                if (editMode) {
                    dao.update(c);
                    pesanInfo("Data berhasil diubah.");
                } else {
                    dao.insert(c);
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
