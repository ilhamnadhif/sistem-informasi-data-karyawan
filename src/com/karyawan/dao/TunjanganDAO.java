package com.karyawan.dao;

import com.karyawan.model.Tunjangan;
import com.karyawan.util.KoneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Akses data tabel tunjangan. */
public class TunjanganDAO {

    public int count(String keyword) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tunjangan "
            + "WHERE kode_tunjangan LIKE ? OR nama_tunjangan LIKE ?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public List<Tunjangan> findPaged(int page, int size, String keyword) throws SQLException {
        List<Tunjangan> list = new ArrayList<>();
        int offset = (page - 1) * size;
        String sql = "SELECT * FROM tunjangan "
            + "WHERE kode_tunjangan LIKE ? OR nama_tunjangan LIKE ? "
            + "ORDER BY created_at DESC, id_tunjangan DESC LIMIT ? OFFSET ?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setInt(3, size);
            ps.setInt(4, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    /** Semua tunjangan (untuk panel Atur Tunjangan). */
    public List<Tunjangan> findAll() throws SQLException {
        List<Tunjangan> list = new ArrayList<>();
        String sql = "SELECT * FROM tunjangan ORDER BY nama_tunjangan";
        try (Connection c = KoneksiDB.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public void insert(Tunjangan t) throws SQLException {
        String sql = "INSERT INTO tunjangan (kode_tunjangan, nama_tunjangan, jumlah, keterangan) "
            + "VALUES (?,?,?,?)";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, t.getKodeTunjangan());
            ps.setString(2, t.getNamaTunjangan());
            ps.setDouble(3, t.getJumlah());
            ps.setString(4, t.getKeterangan());
            ps.executeUpdate();
        }
    }

    public void update(Tunjangan t) throws SQLException {
        String sql = "UPDATE tunjangan SET kode_tunjangan=?, nama_tunjangan=?, jumlah=?, keterangan=? "
            + "WHERE id_tunjangan=?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, t.getKodeTunjangan());
            ps.setString(2, t.getNamaTunjangan());
            ps.setDouble(3, t.getJumlah());
            ps.setString(4, t.getKeterangan());
            ps.setInt(5, t.getIdTunjangan());
            ps.executeUpdate();
        }
    }

    public void delete(int idTunjangan) throws SQLException {
        String sql = "DELETE FROM tunjangan WHERE id_tunjangan=?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idTunjangan);
            ps.executeUpdate();
        }
    }

    public boolean isKodeExists(String kode, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tunjangan WHERE kode_tunjangan=? AND id_tunjangan<>?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kode);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private Tunjangan map(ResultSet rs) throws SQLException {
        Tunjangan t = new Tunjangan();
        t.setIdTunjangan(rs.getInt("id_tunjangan"));
        t.setKodeTunjangan(rs.getString("kode_tunjangan"));
        t.setNamaTunjangan(rs.getString("nama_tunjangan"));
        t.setJumlah(rs.getDouble("jumlah"));
        t.setKeterangan(rs.getString("keterangan"));
        return t;
    }
}
