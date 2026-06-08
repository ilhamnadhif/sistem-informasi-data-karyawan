package com.karyawan.view.laporan;

import com.karyawan.dao.JabatanDAO;
import com.karyawan.model.Jabatan;
import com.karyawan.report.JasperHelper;
import com.karyawan.report.PreviewHelper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

/** Form laporan data karyawan dengan filter jabatan & status. */
public class FrmLaporanKaryawan extends javax.swing.JPanel {

    private static final String RESOURCE = "laporan_karyawan";

    public FrmLaporanKaryawan() {
        initComponents();
        setupCombo();
    }

    /** Isi combo jabatan ("Semua" + daftar jabatan) & status. */
    private void setupCombo() {
        cmbJabatan.removeAllItems();
        cmbJabatan.addItem("Semua"); // item pertama = tanpa filter
        try {
            List<Jabatan> list = new JabatanDAO().findAll();
            for (Jabatan j : list) {
                cmbJabatan.addItem(j.getIdJabatan() + " - " + j.getNamaJabatan());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Gagal memuat data jabatan: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Bangun map parameter laporan ({id_jabatan, status}). */
    private Map<String, Object> buildParams() {
        Map<String, Object> params = new HashMap<String, Object>();
        // ambil id dari "id - nama"; "Semua" -> null
        Object jab = cmbJabatan.getSelectedItem();
        if (jab != null && !"Semua".equals(jab)) {
            String teks = jab.toString();
            int idx = teks.indexOf(" - ");
            params.put("id_jabatan", Integer.valueOf(teks.substring(0, idx).trim()));
        } else {
            params.put("id_jabatan", null);
        }
        Object st = cmbStatus.getSelectedItem();
        params.put("status", (st == null || "Semua".equals(st)) ? null : st.toString());
        return params;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFilter = new javax.swing.JPanel();
        lblJabatan = new javax.swing.JLabel();
        cmbJabatan = new javax.swing.JComboBox<>();
        lblStatus = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        pnlButton = new javax.swing.JPanel();
        btnTampilkan = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        scrollPreview = new javax.swing.JScrollPane();
        tblPreview = new javax.swing.JTable();

        pnlFilter.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter"));

        lblJabatan.setText("Jabatan");

        lblStatus.setText("Status");

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Semua", "Aktif", "Non-Aktif" }));

        javax.swing.GroupLayout pnlFilterLayout = new javax.swing.GroupLayout(pnlFilter);
        pnlFilter.setLayout(pnlFilterLayout);
        pnlFilterLayout.setHorizontalGroup(
            pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblJabatan)
                    .addComponent(lblStatus))
                .addGap(18, 18, 18)
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmbStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbJabatan, 0, 260, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlFilterLayout.setVerticalGroup(
            pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblJabatan)
                    .addComponent(cmbJabatan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlButton.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnTampilkan.setText("Tampilkan");
        btnTampilkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTampilkanActionPerformed(evt);
            }
        });
        pnlButton.add(btnTampilkan);

        btnExport.setText("Export PDF");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });
        pnlButton.add(btnExport);

        scrollPreview.setViewportView(tblPreview);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(scrollPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPreview, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnTampilkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTampilkanActionPerformed
        Map<String, Object> p = buildParams();
        StringBuilder sql = new StringBuilder(
            "SELECT k.nik, k.nama, k.jenis_kelamin, j.nama_jabatan, k.tanggal_masuk, k.status, k.no_telp "
          + "FROM karyawan k LEFT JOIN jabatan j ON k.id_jabatan = j.id_jabatan WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        if (p.get("id_jabatan") != null) {
            sql.append(" AND k.id_jabatan = ?");
            params.add(p.get("id_jabatan"));
        }
        if (p.get("status") != null) {
            sql.append(" AND k.status = ?");
            params.add(p.get("status"));
        }
        sql.append(" ORDER BY k.nama");
        String[] headers = {"NIK", "Nama", "JK", "Jabatan", "Tgl Masuk", "Status", "No. Telp"};
        try {
            tblPreview.setModel(PreviewHelper.jalankan(headers, sql.toString(), params));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Gagal memuat preview: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnTampilkanActionPerformed

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        JasperHelper.exportPdf(RESOURCE, buildParams(), this);
    }//GEN-LAST:event_btnExportActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnTampilkan;
    private javax.swing.JComboBox<String> cmbJabatan;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JLabel lblJabatan;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel pnlButton;
    private javax.swing.JPanel pnlFilter;
    private javax.swing.JScrollPane scrollPreview;
    private javax.swing.JTable tblPreview;
    // End of variables declaration//GEN-END:variables
}
