package com.karyawan.view.laporan;

import com.karyawan.report.JasperHelper;
import com.karyawan.report.PreviewHelper;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

/** Form laporan cuti dengan filter rentang tanggal & status. */
public class FrmLaporanCuti extends javax.swing.JPanel {

    private static final String RESOURCE = "laporan_cuti";

    public FrmLaporanCuti() {
        initComponents();
        setupCombo();
    }

    /** Status sudah diisi via model di .form; method tetap disediakan untuk konsistensi. */
    private void setupCombo() {
        // combo status diisi statis di initComponents (Semua/Pending/Disetujui/Ditolak)
    }

    /**
     * Bangun map parameter ({tgl_mulai, tgl_selesai, status}).
     * Return null bila input tanggal tidak valid.
     */
    private Map<String, Object> buildParams() {
        Date mulai = txtTglMulai.getDate();
        Date selesai = txtTglSelesai.getDate();
        if (mulai == null || selesai == null) {
            JOptionPane.showMessageDialog(this,
                "Tanggal Mulai dan Tanggal Selesai wajib diisi.",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tgl_mulai", mulai);
        params.put("tgl_selesai", selesai);
        Object st = cmbStatus.getSelectedItem();
        params.put("status", (st == null || "Semua".equals(st)) ? null : st.toString());
        return params;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        pnlFilter = new javax.swing.JPanel();
        lblTglMulai = new javax.swing.JLabel();
        txtTglMulai = new com.karyawan.util.TanggalPicker();
        lblTglSelesai = new javax.swing.JLabel();
        txtTglSelesai = new com.karyawan.util.TanggalPicker();
        lblStatus = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        pnlButton = new javax.swing.JPanel();
        btnTampilkan = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        scrollPreview = new javax.swing.JScrollPane();
        tblPreview = new javax.swing.JTable();

        pnlFilter.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter"));

        lblTglMulai.setText("Tanggal Mulai");

        lblTglSelesai.setText("Tanggal Selesai");

        lblStatus.setText("Status");

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Semua", "Pending", "Disetujui", "Ditolak" }));

        javax.swing.GroupLayout pnlFilterLayout = new javax.swing.GroupLayout(pnlFilter);
        pnlFilter.setLayout(pnlFilterLayout);
        pnlFilterLayout.setHorizontalGroup(
            pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTglMulai)
                    .addComponent(lblTglSelesai)
                    .addComponent(lblStatus))
                .addGap(18, 18, 18)
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmbStatus, 0, 200, Short.MAX_VALUE)
                    .addComponent(txtTglMulai)
                    .addComponent(txtTglSelesai))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlFilterLayout.setVerticalGroup(
            pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTglMulai)
                    .addComponent(txtTglMulai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTglSelesai)
                    .addComponent(txtTglSelesai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    }// </editor-fold>

    private void btnTampilkanActionPerformed(java.awt.event.ActionEvent evt) {
        Map<String, Object> p = buildParams();
        if (p == null) {
            return;
        }
        StringBuilder sql = new StringBuilder(
            "SELECT k.nama, c.jenis_cuti, c.tanggal_mulai, c.tanggal_selesai, c.lama_cuti, c.status, c.alasan "
          + "FROM cuti c JOIN karyawan k ON c.id_karyawan = k.id_karyawan "
          + "WHERE c.tanggal_mulai BETWEEN ? AND ?");
        List<Object> params = new ArrayList<Object>();
        params.add(p.get("tgl_mulai"));
        params.add(p.get("tgl_selesai"));
        if (p.get("status") != null) {
            sql.append(" AND c.status = ?");
            params.add(p.get("status"));
        }
        sql.append(" ORDER BY c.tanggal_mulai");
        String[] headers = {"Nama", "Jenis Cuti", "Tgl Mulai", "Tgl Selesai", "Lama (hari)", "Status", "Alasan"};
        try {
            tblPreview.setModel(PreviewHelper.jalankan(headers, sql.toString(), params));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Gagal memuat preview: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {
        Map<String, Object> params = buildParams();
        if (params == null) {
            return;
        }
        JasperHelper.exportPdf(RESOURCE, params, this);
    }

    // Variables declaration - do not modify
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnTampilkan;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTglMulai;
    private javax.swing.JLabel lblTglSelesai;
    private javax.swing.JPanel pnlButton;
    private javax.swing.JPanel pnlFilter;
    private javax.swing.JScrollPane scrollPreview;
    private javax.swing.JTable tblPreview;
    private com.karyawan.util.TanggalPicker txtTglMulai;
    private com.karyawan.util.TanggalPicker txtTglSelesai;
    // End of variables declaration
}
