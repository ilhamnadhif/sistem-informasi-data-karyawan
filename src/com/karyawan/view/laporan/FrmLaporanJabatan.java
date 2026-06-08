package com.karyawan.view.laporan;

import com.karyawan.report.JasperHelper;
import com.karyawan.report.PreviewHelper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

/** Form laporan jabatan (tanpa filter), hanya Tampilkan & Export PDF. */
public class FrmLaporanJabatan extends javax.swing.JPanel {

    private static final String RESOURCE = "laporan_jabatan";

    public FrmLaporanJabatan() {
        initComponents();
    }

    /** Laporan jabatan tidak punya parameter. */
    private Map<String, Object> buildParams() {
        return new HashMap<String, Object>();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        pnlButton = new javax.swing.JPanel();
        btnTampilkan = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        lblInfo = new javax.swing.JLabel();
        scrollPreview = new javax.swing.JScrollPane();
        tblPreview = new javax.swing.JTable();

        lblInfo.setText("Laporan rekap jabatan beserta jumlah karyawan.");

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
            .addComponent(pnlButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(scrollPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInfo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPreview, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>

    private void btnTampilkanActionPerformed(java.awt.event.ActionEvent evt) {
        String sql =
            "SELECT j.kode_jabatan, j.nama_jabatan, j.gaji_pokok, "
          + "COUNT(k.id_karyawan) AS jumlah_karyawan, j.keterangan "
          + "FROM jabatan j LEFT JOIN karyawan k ON j.id_jabatan = k.id_jabatan "
          + "GROUP BY j.id_jabatan, j.kode_jabatan, j.nama_jabatan, j.gaji_pokok, j.keterangan "
          + "ORDER BY j.nama_jabatan";
        String[] headers = {"Kode", "Nama Jabatan", "Gaji Pokok", "Jml Karyawan", "Keterangan"};
        try {
            tblPreview.setModel(PreviewHelper.jalankan(headers, sql, new ArrayList<Object>()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Gagal memuat preview: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {
        JasperHelper.exportPdf(RESOURCE, buildParams(), this);
    }

    // Variables declaration - do not modify
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnTampilkan;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JPanel pnlButton;
    private javax.swing.JScrollPane scrollPreview;
    private javax.swing.JTable tblPreview;
    // End of variables declaration
}
