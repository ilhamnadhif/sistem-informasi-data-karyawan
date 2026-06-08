package com.karyawan.dao;

import com.karyawan.model.Absensi;
import com.karyawan.util.KoneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Akses data tabel absensi (join karyawan untuk tampilan). */
public class AbsensiDAO {

    private static final String BASE =
        "SELECT a.*, k.nik, k.nama FROM absensi a "
        + "JOIN karyawan k ON a.id_karyawan = k.id_karyawan ";

    public int count(String keyword) throws SQLException {
        String sql = "SELECT COUNT(*) FROM absensi a "
            + "JOIN karyawan k ON a.id_karyawan = k.id_karyawan "
            + "WHERE k.nik LIKE ? OR k.nama LIKE ? OR a.status LIKE ?";
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

    public List<Absensi> findPaged(int page, int size, String keyword) throws SQLException {
        List<Absensi> list = new ArrayList<>();
        int offset = (page - 1) * size;
        String sql = BASE
            + "WHERE k.nik LIKE ? OR k.nama LIKE ? OR a.status LIKE ? "
            + "ORDER BY a.created_at DESC, a.id_absensi DESC LIMIT ? OFFSET ?";
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

    public void insert(Absensi a) throws SQLException {
        String sql = "INSERT INTO absensi (id_karyawan, tanggal, jam_masuk, jam_keluar, status, keterangan) "
            + "VALUES (?,?,?,?,?,?)";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, a);
            ps.executeUpdate();
        }
    }

    public void update(Absensi a) throws SQLException {
        String sql = "UPDATE absensi SET id_karyawan=?, tanggal=?, jam_masuk=?, jam_keluar=?, "
            + "status=?, keterangan=? WHERE id_absensi=?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, a);
            ps.setInt(7, a.getIdAbsensi());
            ps.executeUpdate();
        }
    }

    public void delete(int idAbsensi) throws SQLException {
        String sql = "DELETE FROM absensi WHERE id_absensi=?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idAbsensi);
            ps.executeUpdate();
        }
    }

    /** Jumlah hari Alpha seorang karyawan pada periode bulan/tahun (untuk potongan gaji). */
    public int countAlpha(int idKaryawan, int bulan, int tahun) throws SQLException {
        String sql = "SELECT COUNT(*) FROM absensi WHERE id_karyawan=? AND status='Alpha' "
            + "AND MONTH(tanggal)=? AND YEAR(tanggal)=?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idKaryawan);
            ps.setInt(2, bulan);
            ps.setInt(3, tahun);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private void bind(PreparedStatement ps, Absensi a) throws SQLException {
        ps.setInt(1, a.getIdKaryawan());
        ps.setDate(2, a.getTanggal());
        ps.setTime(3, a.getJamMasuk());
        ps.setTime(4, a.getJamKeluar());
        ps.setString(5, a.getStatus());
        ps.setString(6, a.getKeterangan());
    }

    private Absensi map(ResultSet rs) throws SQLException {
        Absensi a = new Absensi();
        a.setIdAbsensi(rs.getInt("id_absensi"));
        a.setIdKaryawan(rs.getInt("id_karyawan"));
        a.setNik(rs.getString("nik"));
        a.setNama(rs.getString("nama"));
        a.setTanggal(rs.getDate("tanggal"));
        a.setJamMasuk(rs.getTime("jam_masuk"));
        a.setJamKeluar(rs.getTime("jam_keluar"));
        a.setStatus(rs.getString("status"));
        a.setKeterangan(rs.getString("keterangan"));
        return a;
    }
}
