package com.karyawan.view.laporan;

import com.karyawan.dao.KaryawanDAO;
import com.karyawan.model.Karyawan;
import com.karyawan.report.JasperHelper;
import com.karyawan.report.PreviewHelper;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

/** Form laporan absensi dengan filter rentang tanggal & karyawan. */
public class FrmLaporanAbsensi extends javax.swing.JPanel {

    private static final String RESOURCE = "laporan_absensi";

    public FrmLaporanAbsensi() {
        initComponents();
        setupCombo();
    }

    /** Isi combo karyawan ("Semua" + daftar karyawan). */
    private void setupCombo() {
        cmbKaryawan.removeAllItems();
        cmbKaryawan.addItem("Semua"); // item pertama = tanpa filter
        try {
            List<Karyawan> list = new KaryawanDAO().findAll();
            for (Karyawan k : list) {
                cmbKaryawan.addItem(k.getIdKaryawan() + " - " + k.getNik() + " - " + k.getNama());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Gagal memuat data karyawan: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Bangun map parameter ({tgl_mulai, tgl_selesai, id_karyawan}).
     * Return null bila input tanggal tidak valid (pesan sudah ditampilkan).
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
        // ambil id dari "id - nik - nama"; "Semua" -> null
        Object kar = cmbKaryawan.getSelectedItem();
        if (kar != null && !"Semua".equals(kar)) {
            String teks = kar.toString();
            int idx = teks.indexOf(" - ");
            params.put("id_karyawan", Integer.valueOf(teks.substring(0, idx).trim()));
        } else {
            params.put("id_karyawan", null);
        }
        return params;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        pnlFilter = new javax.swing.JPanel();
        lblTglMulai = new javax.swing.JLabel();
        txtTglMulai = new com.karyawan.util.TanggalPicker();
        lblTglSelesai = new javax.swing.JLabel();
        txtTglSelesai = new com.karyawan.util.TanggalPicker();
        lblKaryawan = new javax.swing.JLabel();
        cmbKaryawan = new javax.swing.JComboBox<>();
        pnlButton = new javax.swing.JPanel();
        btnTampilkan = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        scrollPreview = new javax.swing.JScrollPane();
        tblPreview = new javax.swing.JTable();

        pnlFilter.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter"));

        lblTglMulai.setText("Tanggal Mulai");

        lblTglSelesai.setText("Tanggal Selesai");

        lblKaryawan.setText("Karyawan");

        javax.swing.GroupLayout pnlFilterLayout = new javax.swing.GroupLayout(pnlFilter);
        pnlFilter.setLayout(pnlFilterLayout);
        pnlFilterLayout.setHorizontalGroup(
            pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTglMulai)
                    .addComponent(lblTglSelesai)
                    .addComponent(lblKaryawan))
                .addGap(18, 18, 18)
                .addGroup(pnlFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmbKaryawan, 0, 280, Short.MAX_VALUE)
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
                    .addComponent(lblKaryawan)
                    .addComponent(cmbKaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            "SELECT a.tanggal, k.nik, k.nama, a.jam_masuk, a.jam_keluar, a.status, a.keterangan "
          + "FROM absensi a JOIN karyawan k ON a.id_karyawan = k.id_karyawan "
          + "WHERE a.tanggal BETWEEN ? AND ?");
        List<Object> params = new ArrayList<Object>();
        params.add(p.get("tgl_mulai"));
        params.add(p.get("tgl_selesai"));
        if (p.get("id_karyawan") != null) {
            sql.append(" AND a.id_karyawan = ?");
            params.add(p.get("id_karyawan"));
        }
        sql.append(" ORDER BY a.tanggal");
        String[] headers = {"Tanggal", "NIK", "Nama", "Jam Masuk", "Jam Keluar", "Status", "Keterangan"};
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
    private javax.swing.JComboBox<String> cmbKaryawan;
    private javax.swing.JLabel lblKaryawan;
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
