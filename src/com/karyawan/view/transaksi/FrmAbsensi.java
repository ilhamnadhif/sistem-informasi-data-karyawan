package com.karyawan.view.transaksi;

import com.karyawan.dao.AbsensiDAO;
import com.karyawan.dao.KaryawanDAO;
import com.karyawan.model.Absensi;
import com.karyawan.model.Karyawan;
import com.karyawan.util.PaginationHelper;
import com.karyawan.util.TabelAksi;
import com.karyawan.util.JamPicker;
import com.karyawan.util.TanggalFormat;
import com.karyawan.util.TanggalPicker;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Form transaksi Absensi karyawan. Pola referensi: toolbar + tabel berkolom
 * "Aksi" (Edit/Hapus) + pagination; tambah & edit lewat popup dialog.
 */
public class FrmAbsensi extends javax.swing.JPanel {

    private static final int KOL_AKSI = 9;

    private final AbsensiDAO dao = new AbsensiDAO();
    private final PaginationHelper page = new PaginationHelper();
    private DefaultTableModel model;
    private List<Absensi> dataHal = new ArrayList<>(); // data baris halaman aktif

    public FrmAbsensi() {
        initComponents();
        setupTabel();
        loadData();
    }

    /** Siapkan model tabel + kolom Aksi + listener pencarian. */
    private void setupTabel() {
        model = new DefaultTableModel(
            new Object[]{"ID", "IDK", "Tanggal", "NIK", "Nama",
                "Jam Masuk", "Jam Keluar", "Status", "Keterangan", "Aksi"}, 0) {
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
        cmbPageSize.setSelectedItem(String.valueOf(page.getPageSize()));
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
            for (Absensi a : dataHal) {
                model.addRow(new Object[]{
                    a.getIdAbsensi(),
                    a.getIdKaryawan(),
                    TanggalFormat.format(a.getTanggal()),
                    a.getNik(),
                    a.getNama(),
                    formatTime(a.getJamMasuk()),
                    formatTime(a.getJamKeluar()),
                    a.getStatus(),
                    a.getKeterangan(),
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
        Absensi a = dataHal.get(modelRow);
        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Yakin hapus data absensi ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.delete(a.getIdAbsensi());
            pesanInfo("Data berhasil dihapus.");
            loadData();
        } catch (Exception ex) {
            pesanError("Gagal menghapus: " + ex.getMessage());
        }
    }

    /** Popup tambah (existing=null) atau edit (existing!=null). */
    private void bukaDialog(Absensi existing) {
        boolean editMode = existing != null;

        JComboBox<Karyawan> cmbKaryawan = new JComboBox<>();
        try {
            List<Karyawan> list = new KaryawanDAO().findAll();
            for (Karyawan k : list) {
                cmbKaryawan.addItem(k);
            }
        } catch (Exception ex) {
            pesanError("Gagal memuat daftar karyawan: " + ex.getMessage());
            return;
        }
        TanggalPicker txtTanggal = new TanggalPicker();
        JamPicker txtJamMasuk = new JamPicker();        // default: jam sekarang
        JamPicker txtJamKeluar = new JamPicker(false);  // default: kosong
        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Hadir", "Izin", "Sakit", "Alpha"});
        JTextField txtKeterangan = new JTextField(20);

        if (editMode) {
            pilihKaryawan(cmbKaryawan, existing.getIdKaryawan());
            txtTanggal.setDate(existing.getTanggal());
            txtJamMasuk.setTime(existing.getJamMasuk());
            txtJamKeluar.setTime(existing.getJamKeluar());
            cmbStatus.setSelectedItem(existing.getStatus());
            txtKeterangan.setText(existing.getKeterangan() == null ? "" : existing.getKeterangan());
        }

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        baris(form, g, 0, "Karyawan", cmbKaryawan);
        baris(form, g, 1, "Tanggal", txtTanggal);
        baris(form, g, 2, "Jam Masuk", txtJamMasuk);
        baris(form, g, 3, "Jam Keluar", txtJamKeluar);
        baris(form, g, 4, "Status", cmbStatus);
        baris(form, g, 5, "Keterangan", txtKeterangan);

        String judul = editMode ? "Edit Absensi" : "Tambah Absensi";
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
            if (txtTanggal.isEmpty()) {
                pesanWarning("Tanggal wajib diisi.");
                continue;
            }
            try {
                Absensi a = new Absensi();
                a.setIdAbsensi(editMode ? existing.getIdAbsensi() : 0);
                Karyawan k = (Karyawan) cmbKaryawan.getSelectedItem();
                a.setIdKaryawan(k.getIdKaryawan());
                a.setTanggal(txtTanggal.getDate());
                a.setJamMasuk(txtJamMasuk.getTime());
                a.setJamKeluar(txtJamKeluar.getTime());
                a.setStatus(String.valueOf(cmbStatus.getSelectedItem()));
                a.setKeterangan(txtKeterangan.getText().trim());
                try {
                    if (editMode) {
                        dao.update(a);
                        pesanInfo("Data berhasil diubah.");
                    } else {
                        dao.insert(a);
                        pesanInfo("Data berhasil ditambahkan.");
                    }
                } catch (java.sql.SQLIntegrityConstraintViolationException se) {
                    // pelanggaran unique key uq_absensi (1 karyawan 1 tanggal)
                    pesanWarning("Karyawan ini sudah memiliki absensi pada tanggal tersebut.");
                    continue;
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

    /** Konversi java.sql.Time -> "HH:mm"; null -> "". */
    private String formatTime(Time t) {
        if (t == null) {
            return "";
        }
        String s = t.toString(); // format "HH:mm:ss"
        return s.length() >= 5 ? s.substring(0, 5) : s;
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
