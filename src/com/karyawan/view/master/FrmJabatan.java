package com.karyawan.view.master;

import com.karyawan.dao.JabatanDAO;
import com.karyawan.dao.TunjanganDAO;
import com.karyawan.model.Jabatan;
import com.karyawan.model.Tunjangan;
import com.karyawan.util.PaginationHelper;
import com.karyawan.util.RupiahFormat;
import com.karyawan.util.TabelAksi;
import com.karyawan.util.Validasi;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
 * Form master Data Jabatan. Pola referensi: toolbar + tabel berkolom "Aksi"
 * (Edit/Hapus) + pagination; tambah & edit lewat popup dialog.
 */
public class FrmJabatan extends javax.swing.JPanel {

    private static final int KOL_AKSI = 5;

    private final JabatanDAO dao = new JabatanDAO();
    private final PaginationHelper page = new PaginationHelper();
    private DefaultTableModel model;
    private List<Jabatan> dataHal = new ArrayList<>(); // data baris halaman aktif

    private JTextField txtCari;
    private JComboBox<Integer> cmbPageSize;
    private JTable tblData;
    private JButton btnFirst;
    private JButton btnPrev;
    private JButton btnNext;
    private JButton btnLast;
    private JLabel lblHalaman;

    public FrmJabatan() {
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
            new Object[]{"ID", "Kode", "Nama Jabatan", "Gaji Pokok", "Keterangan", "Aksi"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == KOL_AKSI; // hanya kolom Aksi yang editable (agar tombol bisa diklik)
            }
        };
        tblData.setModel(model);
        // sembunyikan kolom ID
        tblData.getColumnModel().getColumn(0).setMinWidth(0);
        tblData.getColumnModel().getColumn(0).setMaxWidth(0);
        tblData.getColumnModel().getColumn(0).setWidth(0);
        TabelAksi.pasang(tblData, KOL_AKSI, this::editBaris, this::hapusBaris);

        txtCari.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { page.reset(); loadData(); }
            public void removeUpdate(DocumentEvent e) { page.reset(); loadData(); }
            public void changedUpdate(DocumentEvent e) { page.reset(); loadData(); }
        });
        cmbPageSize.setSelectedItem(page.getPageSize());
    }

    /** Ambil 1 halaman data dari DB sesuai keyword & isi tabel. */
    private void loadData() {
        try {
            String keyword = txtCari.getText().trim();
            page.setTotalRows(dao.count(keyword));
            dataHal = dao.findPaged(page.getCurrentPage(), page.getPageSize(), keyword);
            model.setRowCount(0);
            for (Jabatan j : dataHal) {
                model.addRow(new Object[]{
                    j.getIdJabatan(),
                    j.getKodeJabatan(),
                    j.getNamaJabatan(),
                    RupiahFormat.format(j.getGajiPokok()),
                    j.getKeterangan(),
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
        Jabatan j = dataHal.get(modelRow);
        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Yakin hapus jabatan \"" + j.getNamaJabatan() + "\"?",
            "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.delete(j.getIdJabatan());
            pesanInfo("Data berhasil dihapus.");
            loadData();
        } catch (Exception ex) {
            pesanError("Gagal menghapus: " + ex.getMessage());
        }
    }

    /** Popup tambah (existing=null) atau edit (existing!=null). */
    private void bukaDialog(Jabatan existing) {
        boolean editMode = existing != null;

        JTextField txtKode = new JTextField(20);
        JTextField txtNama = new JTextField(20);
        JTextField txtGaji = new JTextField(20);
        JTextArea txtKeterangan = new JTextArea(3, 20);
        txtKeterangan.setLineWrap(true);
        txtKeterangan.setWrapStyleWord(true);

        if (editMode) {
            txtKode.setText(existing.getKodeJabatan());
            txtNama.setText(existing.getNamaJabatan());
            txtGaji.setText(String.valueOf((long) existing.getGajiPokok()));
            txtKeterangan.setText(existing.getKeterangan() == null ? "" : existing.getKeterangan());
        }

        // daftar checkbox tunjangan, langsung inline di form (tanpa popup terpisah)
        List<JCheckBox> checks = new ArrayList<>();
        Component fieldTunjangan = buildPanelTunjangan(existing, checks);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        baris(form, g, 0, "Kode Jabatan", txtKode);
        baris(form, g, 1, "Nama Jabatan", txtNama);
        baris(form, g, 2, "Gaji Pokok", txtGaji);
        baris(form, g, 3, "Keterangan", new JScrollPane(txtKeterangan));
        baris(form, g, 4, "Tunjangan", fieldTunjangan);

        String judul = editMode ? "Edit Jabatan" : "Tambah Jabatan";
        while (true) {
            int ok = JOptionPane.showConfirmDialog(this, form, judul,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (ok != JOptionPane.OK_OPTION) {
                return;
            }
            // validasi
            if (Validasi.isKosong(txtKode)) { pesanWarning("Kode Jabatan wajib diisi."); continue; }
            if (Validasi.isKosong(txtNama)) { pesanWarning("Nama Jabatan wajib diisi."); continue; }
            if (!Validasi.isNumber(txtGaji.getText())) { pesanWarning("Gaji Pokok harus angka >= 0."); continue; }
            try {
                Jabatan j = new Jabatan();
                j.setIdJabatan(editMode ? existing.getIdJabatan() : 0);
                j.setKodeJabatan(txtKode.getText().trim());
                j.setNamaJabatan(txtNama.getText().trim());
                j.setGajiPokok(Double.parseDouble(txtGaji.getText().trim()));
                j.setKeterangan(txtKeterangan.getText().trim());
                if (dao.isKodeExists(j.getKodeJabatan(), j.getIdJabatan())) {
                    pesanWarning("Kode Jabatan sudah dipakai. Gunakan kode lain.");
                    continue;
                }
                if (editMode) {
                    dao.update(j);
                } else {
                    dao.insert(j); // id hasil generate di-set ke j
                }
                // simpan mapping tunjangan (insert sudah mengisi id baru)
                List<Integer> dipilih = new ArrayList<>();
                for (JCheckBox cb : checks) {
                    if (cb.isSelected()) {
                        dipilih.add((Integer) cb.getClientProperty("idTunjangan"));
                    }
                }
                dao.saveTunjanganMapping(j.getIdJabatan(), dipilih);
                pesanInfo(editMode ? "Data berhasil diubah." : "Data berhasil ditambahkan.");
                loadData();
                return;
            } catch (Exception ex) {
                pesanError("Gagal menyimpan: " + ex.getMessage());
                return;
            }
        }
    }

    /** Daftar checkbox tunjangan (dalam scroll) untuk dipasang inline di form. */
    private Component buildPanelTunjangan(Jabatan existing, List<JCheckBox> checks) {
        JPanel panel = new JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        try {
            List<Tunjangan> semua = new TunjanganDAO().findAll();
            if (semua.isEmpty()) {
                panel.add(new JLabel("Belum ada data tunjangan."));
            } else {
                List<Integer> terpasang = existing == null
                    ? new ArrayList<>()
                    : dao.findTunjanganIds(existing.getIdJabatan());
                for (Tunjangan t : semua) {
                    JCheckBox cb = new JCheckBox(
                        t.getNamaTunjangan() + " (" + RupiahFormat.format(t.getJumlah()) + ")");
                    cb.setSelected(terpasang.contains(t.getIdTunjangan()));
                    cb.putClientProperty("idTunjangan", t.getIdTunjangan());
                    checks.add(cb);
                    panel.add(cb);
                }
            }
        } catch (Exception ex) {
            panel.add(new JLabel("Gagal memuat tunjangan: " + ex.getMessage()));
        }
        JScrollPane sp = new JScrollPane(panel);
        sp.setPreferredSize(new java.awt.Dimension(260, 110));
        return sp;
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
