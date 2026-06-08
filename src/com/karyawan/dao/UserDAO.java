package com.karyawan.dao;

import com.karyawan.util.KoneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Akses data tabel user untuk autentikasi login. */
public class UserDAO {

    /** True bila pasangan username/password cocok dengan satu baris user. */
    public boolean cekLogin(String username, String password) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user WHERE username=? AND password=?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
