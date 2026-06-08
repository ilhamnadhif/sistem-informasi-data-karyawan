package com.karyawan.view.master;

import com.karyawan.dao.JabatanDAO;
import com.karyawan.dao.KaryawanDAO;
import com.karyawan.model.Jabatan;
import com.karyawan.model.Karyawan;
import com.karyawan.util.PaginationHelper;
import com.karyawan.util.TabelAksi;
import com.karyawan.util.TanggalFormat;
import com.karyawan.util.TanggalPicker;
import com.karyawan.util.Validasi;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Form master Data Karyawan. Pola referensi: toolbar + tabel berkolom "Aksi"
 * (Edit/Hapus) + pagination; tambah & edit lewat popup dialog.
 */
public class FrmKaryawan extends javax.swing.JPanel {

    private static final int KOL_AKSI = 9;

    private final KaryawanDAO dao = new KaryawanDAO();
    private final PaginationHelper page = new PaginationHelper();
    private DefaultTableModel model;
    private List<Karyawan> dataHal = new ArrayList<>(); // data baris halaman aktif

    public FrmKaryawan() {
        initComponents();
        setupTabel();
        loadData();
    }

    /** Siapkan model tabel + kolom Aksi + listener pencarian. */
    private void setupTabel() {
        model = new DefaultTableModel(
            new Object[]{"ID", "IDJAB", "NIK", "Nama", "JK", "Jabatan",
                "Tgl Masuk", "Status", "No. Telp", "Aksi"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == KOL_AKSI; // hanya kolom Aksi yang editable (agar tombol bisa diklik)
            }
        };
        tblData.setModel(model);
        // sembunyikan kolom ID (id_karyawan) yang dipakai internal
        sembunyikanKolom(0);
        // sembunyikan kolom IDJAB (id_jabatan) yang dipakai untuk memilih combo
        sembunyikanKolom(1);
        TabelAksi.pasang(tblData, KOL_AKSI, this::editBaris, this::hapusBaris);

        txtCari.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { page.reset(); loadData(); }
            public void removeUpdate(DocumentEvent e) { page.reset(); loadData(); }
            public void changedUpdate(DocumentEvent e) { page.reset(); loadData(); }
        });
        cmbPageSize.setSelectedItem(String.valueOf(page.getPageSize()));
    }

    /** Sembunyikan kolom tabel berdasarkan index. */
    private void sembunyikanKolom(int index) {
        tblData.getColumnModel().getColumn(index).setMinWidth(0);
        tblData.getColumnModel().getColumn(index).setMaxWidth(0);
        tblData.getColumnModel().getColumn(index).setWidth(0);
    }

    /** Ambil 1 halaman data dari DB sesuai keyword & isi tabel. */
    private void loadData() {
        try {
            String keyword = txtCari.getText().trim();
            page.setTotalRows(dao.count(keyword));
            dataHal = dao.findPaged(page.getCurrentPage(), page.getPageSize(), keyword);
            model.setRowCount(0);
            for (Karyawan k : dataHal) {
                model.addRow(new Object[]{
                    k.getIdKaryawan(),
                    k.getIdJabatan(),
                    k.getNik(),
                    k.getNama(),
                    k.getJenisKelamin(),
                    k.getNamaJabatan(),
                    TanggalFormat.format(k.getTanggalMasuk()),
                    k.getStatus(),
                    k.getNoTelp(),
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
        Karyawan k = dataHal.get(modelRow);
        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Yakin hapus karyawan \"" + k.getNama() + "\"?",
            "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.delete(k.getIdKaryawan());
            pesanInfo("Data berhasil dihapus.");
            loadData();
        } catch (Exception ex) {
            pesanError("Gagal menghapus: " + ex.getMessage());
        }
    }

    /** Popup tambah (existing=null) atau edit (existing!=null). */
    private void bukaDialog(Karyawan existing) {
        boolean editMode = existing != null;

        // ambil record lengkap dari DB bila ada field yang belum ter-prefill
        if (editMode) {
            existing = ambilLengkap(existing);
        }

        JTextField txtNik = new JTextField(20);
        JTextField txtNama = new JTextField(20);
        JComboBox<String> cmbJk = new JComboBox<>(new String[]{"L", "P"});
        JTextField txtTempat = new JTextField(20);
        TanggalPicker txtTglLahir = new TanggalPicker();
        JTextArea txtAlamat = new JTextArea(3, 20);
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        JTextField txtTelp = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        JComboBox<Jabatan> cmbJabatan = new JComboBox<>();
        TanggalPicker txtTglMasuk = new TanggalPicker();
        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Aktif", "Non-Aktif"});

        // isi combo jabatan dari database
        try {
            for (Jabatan j : new JabatanDAO().findAll()) {
                cmbJabatan.addItem(j);
            }
            cmbJabatan.setSelectedIndex(-1);
        } catch (Exception ex) {
            pesanError("Gagal memuat data jabatan: " + ex.getMessage());
        }

        if (editMode) {
            txtNik.setText(existing.getNik());
            txtNama.setText(existing.getNama());
            cmbJk.setSelectedItem(existing.getJenisKelamin());
            txtTempat.setText(existing.getTempatLahir() == null ? "" : existing.getTempatLahir());
            txtTglLahir.setDate(existing.getTanggalLahir());
            txtAlamat.setText(existing.getAlamat() == null ? "" : existing.getAlamat());
            txtTelp.setText(existing.getNoTelp() == null ? "" : existing.getNoTelp());
            txtEmail.setText(existing.getEmail() == null ? "" : existing.getEmail());
            txtTglMasuk.setDate(existing.getTanggalMasuk());
            cmbStatus.setSelectedItem(existing.getStatus());
            pilihJabatan(cmbJabatan, existing.getIdJabatan());
        }

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        baris(form, g, 0, "NIK", txtNik);
        baris(form, g, 1, "Nama", txtNama);
        baris(form, g, 2, "Jenis Kelamin", cmbJk);
        baris(form, g, 3, "Tempat Lahir", txtTempat);
        baris(form, g, 4, "Tanggal Lahir", txtTglLahir);
        baris(form, g, 5, "Alamat", new JScrollPane(txtAlamat));
        baris(form, g, 6, "No. Telp", txtTelp);
        baris(form, g, 7, "Email", txtEmail);
        baris(form, g, 8, "Jabatan", cmbJabatan);
        baris(form, g, 9, "Tanggal Masuk", txtTglMasuk);
        baris(form, g, 10, "Status", cmbStatus);

        String judul = editMode ? "Edit Karyawan" : "Tambah Karyawan";
        while (true) {
            int ok = JOptionPane.showConfirmDialog(this, form, judul,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (ok != JOptionPane.OK_OPTION) {
                return;
            }
            // validasi
            if (Validasi.isKosong(txtNik)) { pesanWarning("NIK wajib diisi."); continue; }
            if (Validasi.isKosong(txtNama)) { pesanWarning("Nama wajib diisi."); continue; }
            if (cmbJabatan.getSelectedItem() == null) { pesanWarning("Jabatan wajib dipilih."); continue; }
            String email = txtEmail.getText().trim();
            if (!email.isEmpty() && !Validasi.isEmailValid(email)) {
                pesanWarning("Format email tidak valid."); continue;
            }
            try {
                Karyawan k = new Karyawan();
                k.setIdKaryawan(editMode ? existing.getIdKaryawan() : 0);
                k.setNik(txtNik.getText().trim());
                k.setNama(txtNama.getText().trim());
                k.setJenisKelamin((String) cmbJk.getSelectedItem());
                k.setTempatLahir(txtTempat.getText().trim());
                k.setTanggalLahir(txtTglLahir.getDate());
                k.setAlamat(txtAlamat.getText().trim());
                k.setNoTelp(txtTelp.getText().trim());
                k.setEmail(txtEmail.getText().trim());
                Jabatan j = (Jabatan) cmbJabatan.getSelectedItem();
                k.setIdJabatan(j == null ? 0 : j.getIdJabatan());
                k.setTanggalMasuk(txtTglMasuk.getDate());
                k.setStatus((String) cmbStatus.getSelectedItem());
                // NIK harus unik (abaikan baris sendiri saat edit)
                if (dao.isNikExists(k.getNik(), k.getIdKaryawan())) {
                    pesanWarning("NIK sudah dipakai. Gunakan NIK lain.");
                    continue;
                }
                if (editMode) {
                    dao.update(k);
                    pesanInfo("Data berhasil diubah.");
                } else {
                    dao.insert(k);
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

    /** Ambil data lengkap dari DB berdasarkan id (fallback bila field belum lengkap). */
    private Karyawan ambilLengkap(Karyawan parsial) {
        try {
            for (Karyawan k : dao.findAll()) {
                if (k.getIdKaryawan() == parsial.getIdKaryawan()) {
                    return k;
                }
            }
        } catch (Exception ex) {
            pesanError("Gagal memuat detail: " + ex.getMessage());
        }
        return parsial;
    }

    /** Loop model combo untuk memilih Jabatan dengan id sesuai. */
    private void pilihJabatan(JComboBox<Jabatan> cmbJabatan, int idJabatan) {
        for (int i = 0; i < cmbJabatan.getItemCount(); i++) {
            Jabatan j = cmbJabatan.getItemAt(i);
            if (j != null && j.getIdJabatan() == idJabatan) {
                cmbJabatan.setSelectedIndex(i);
                return;
            }
        }
        cmbJabatan.setSelectedIndex(-1);
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlToolbar = new javax.swing.JPanel();
        lblCari = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
        lblTampil = new javax.swing.JLabel();
        cmbPageSize = new javax.swing.JComboBox<>();
        btnTambah = new javax.swing.JButton();
        scrollData = new javax.swing.JScrollPane();
        tblData = new javax.swing.JTable();
        pnlPage = new javax.swing.JPanel();
        btnFirst = new javax.swing.JButton();
        btnPrev = new javax.swing.JButton();
        lblHalaman = new javax.swing.JLabel();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();

        pnlToolbar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));

        lblCari.setText("Cari:");
        pnlToolbar.add(lblCari);

        txtCari.setColumns(18);
        pnlToolbar.add(txtCari);

        lblTampil.setText("Tampil:");
        pnlToolbar.add(lblTampil);

        cmbPageSize.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "10", "25", "50" }));
        cmbPageSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPageSizeActionPerformed(evt);
            }
        });
        pnlToolbar.add(cmbPageSize);

        btnTambah.setText("+ Tambah");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });
        pnlToolbar.add(btnTambah);

        tblData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        scrollData.setViewportView(tblData);

        pnlPage.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 6, 4));

        btnFirst.setText("«");
        btnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstActionPerformed(evt);
            }
        });
        pnlPage.add(btnFirst);

        btnPrev.setText("‹");
        btnPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevActionPerformed(evt);
            }
        });
        pnlPage.add(btnPrev);

        lblHalaman.setText("Halaman 1 dari 1");
        pnlPage.add(lblHalaman);

        btnNext.setText("›");
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });
        pnlPage.add(btnNext);

        btnLast.setText("»");
        btnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastActionPerformed(evt);
            }
        });
        pnlPage.add(btnLast);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlToolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(scrollData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlPage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollData, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmbPageSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPageSizeActionPerformed
        Object s = cmbPageSize.getSelectedItem();
        if (s != null) {
            page.setPageSize(Integer.parseInt(s.toString()));
            loadData();
        }
    }//GEN-LAST:event_cmbPageSizeActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        bukaDialog(null);
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
        page.first();
        loadData();
    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevActionPerformed
        page.prev();
        loadData();
    }//GEN-LAST:event_btnPrevActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        page.next();
        loadData();
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        page.last();
        loadData();
    }//GEN-LAST:event_btnLastActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JButton btnTambah;
    private javax.swing.JComboBox<String> cmbPageSize;
    private javax.swing.JLabel lblCari;
    private javax.swing.JLabel lblHalaman;
    private javax.swing.JLabel lblTampil;
    private javax.swing.JPanel pnlPage;
    private javax.swing.JPanel pnlToolbar;
    private javax.swing.JScrollPane scrollData;
    private javax.swing.JTable tblData;
    private javax.swing.JTextField txtCari;
    // End of variables declaration//GEN-END:variables
}
