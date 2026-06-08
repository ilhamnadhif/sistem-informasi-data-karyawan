package com.karyawan.report;

import com.karyawan.util.KoneksiDB;
import com.karyawan.util.RupiahFormat;
import com.karyawan.util.TanggalFormat;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * Menjalankan query laporan & membungkus hasilnya jadi {@link DefaultTableModel}
 * untuk preview di JTable sebelum export PDF. Pemformatan otomatis per tipe
 * kolom (tanggal dd-MM-yyyy, jam HH:mm, BigDecimal sebagai Rupiah).
 */
public class PreviewHelper {

    private static final SimpleDateFormat JAM = new SimpleDateFormat("HH:mm");

    private PreviewHelper() {
    }

    /**
     * @param headers nama kolom tampilan (jumlahnya = jumlah kolom SELECT)
     * @param sql     query dengan placeholder {@code ?}
     * @param params  nilai untuk tiap {@code ?} (urut)
     */
    public static DefaultTableModel jalankan(String[] headers, String sql, List<Object> params)
            throws SQLException {
        DefaultTableModel model = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                int cols = headers.length;
                while (rs.next()) {
                    Object[] row = new Object[cols];
                    for (int i = 0; i < cols; i++) {
                        row[i] = format(rs.getObject(i + 1));
                    }
                    model.addRow(row);
                }
            }
        }
        return model;
    }

    private static Object format(Object v) {
        if (v == null) {
            return "";
        }
        if (v instanceof java.sql.Time) {
            return JAM.format((java.sql.Time) v);
        }
        if (v instanceof java.util.Date) {
            return TanggalFormat.format((java.util.Date) v);
        }
        if (v instanceof BigDecimal) {
            return RupiahFormat.format(((BigDecimal) v).doubleValue());
        }
        return v;
    }
}
