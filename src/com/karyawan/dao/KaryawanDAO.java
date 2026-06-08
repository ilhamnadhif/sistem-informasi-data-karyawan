package com.karyawan.dao;

import com.karyawan.model.Karyawan;
import com.karyawan.util.KoneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Akses data tabel karyawan (join jabatan untuk tampilan). */
public class KaryawanDAO {

    private static final String BASE =
        "SELECT k.*, j.nama_jabatan FROM karyawan k "
        + "LEFT JOIN jabatan j ON k.id_jabatan = j.id_jabatan ";

    public int count(String keyword) throws SQLException {
        String sql = "SELECT COUNT(*) FROM karyawan k "
            + "LEFT JOIN jabatan j ON k.id_jabatan = j.id_jabatan "
            + "WHERE k.nik LIKE ? OR k.nama LIKE ? OR j.nama_jabatan LIKE ?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public List<Karyawan> findPaged(int page, int size, String keyword) throws SQLException {
        List<Karyawan> list = new ArrayList<>();
        int offset = (page - 1) * size;
        String sql = BASE
            + "WHERE k.nik LIKE ? OR k.nama LIKE ? OR j.nama_jabatan LIKE ? "
            + "ORDER BY k.created_at DESC, k.id_karyawan DESC LIMIT ? OFFSET ?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setInt(4, size);
            ps.setInt(5, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    /** Semua karyawan (untuk combo di transaksi). */
    public List<Karyawan> findAll() throws SQLException {
        List<Karyawan> list = new ArrayList<>();
        String sql = BASE + "ORDER BY k.nama";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public void insert(Karyawan k) throws SQLException {
        String sql = "INSERT INTO karyawan "
            + "(nik, nama, jenis_kelamin, tempat_lahir, tanggal_lahir, alamat, no_telp, email, "
            + "id_jabatan, tanggal_masuk, status) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, k);
            ps.executeUpdate();
        }
    }

    public void update(Karyawan k) throws SQLException {
        String sql = "UPDATE karyawan SET nik=?, nama=?, jenis_kelamin=?, tempat_lahir=?, "
            + "tanggal_lahir=?, alamat=?, no_telp=?, email=?, id_jabatan=?, tanggal_masuk=?, status=? "
            + "WHERE id_karyawan=?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, k);
            ps.setInt(12, k.getIdKaryawan());
            ps.executeUpdate();
        }
    }

    public void delete(int idKaryawan) throws SQLException {
        String sql = "DELETE FROM karyawan WHERE id_karyawan=?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idKaryawan);
            ps.executeUpdate();
        }
    }

    public boolean isNikExists(String nik, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM karyawan WHERE nik=? AND id_karyawan<>?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nik);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private void bind(PreparedStatement ps, Karyawan k) throws SQLException {
        ps.setString(1, k.getNik());
        ps.setString(2, k.getNama());
        ps.setString(3, k.getJenisKelamin());
        ps.setString(4, k.getTempatLahir());
        ps.setDate(5, k.getTanggalLahir());
        ps.setString(6, k.getAlamat());
        ps.setString(7, k.getNoTelp());
        ps.setString(8, k.getEmail());
        if (k.getIdJabatan() > 0) {
            ps.setInt(9, k.getIdJabatan());
        } else {
            ps.setNull(9, java.sql.Types.INTEGER);
        }
        ps.setDate(10, k.getTanggalMasuk());
        ps.setString(11, k.getStatus());
    }

    private Karyawan map(ResultSet rs) throws SQLException {
        Karyawan k = new Karyawan();
        k.setIdKaryawan(rs.getInt("id_karyawan"));
        k.setNik(rs.getString("nik"));
        k.setNama(rs.getString("nama"));
        k.setJenisKelamin(rs.getString("jenis_kelamin"));
        k.setTempatLahir(rs.getString("tempat_lahir"));
        k.setTanggalLahir(rs.getDate("tanggal_lahir"));
        k.setAlamat(rs.getString("alamat"));
        k.setNoTelp(rs.getString("no_telp"));
        k.setEmail(rs.getString("email"));
        k.setIdJabatan(rs.getInt("id_jabatan"));
        k.setNamaJabatan(rs.getString("nama_jabatan"));
        k.setTanggalMasuk(rs.getDate("tanggal_masuk"));
        k.setStatus(rs.getString("status"));
        return k;
    }
}
