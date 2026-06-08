package com.karyawan.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton koneksi MySQL. Kredensial di-hardcode (root / root) sesuai
 * kebutuhan project kuliah; ubah USER/PASS bila berbeda.
 */
public class KoneksiDB {

    private static final String URL =
        "jdbc:mysql://localhost:3306/db_karyawan?useSSL=false&serverTimezone=Asia/Jakarta";
    private static final String USER = "root";
    private static final String PASS = "root";

    private static Connection conn;

    private KoneksiDB() {
    }

    /** Mengembalikan koneksi tunggal; dibuka kembali bila tertutup. */
    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            try {
                // Pastikan driver Connector/J ter-load (opsional di JDBC 4+)
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL tidak ditemukan. "
                    + "Pastikan mysql-connector-j-8.3.0.jar terdaftar di Libraries.", e);
            }
            conn = DriverManager.getConnection(URL, USER, PASS);
        }
        return conn;
    }
}
