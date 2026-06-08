package com.karyawan.dao;

import com.karyawan.model.Penggajian;
import com.karyawan.util.KoneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Akses data tabel penggajian + perhitungan komponen gaji. */
public class PenggajianDAO {

    private static final String BASE =
        "SELECT p.*, k.nik, k.nama FROM penggajian p "
        + "JOIN karyawan k ON p.id_karyawan = k.id_karyawan ";

    public int count(String keyword) throws SQLException {
        String sql = "SELECT COUNT(*) FROM penggajian p "
            + "JOIN karyawan k ON p.id_karyawan = k.id_karyawan "
            + "WHERE k.nik LIKE ? OR k.nama LIKE ?";
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

    public List<Penggajian> findPaged(int page, int size, String keyword) throws SQLException {
        List<Penggajian> list = new ArrayList<>();
        int offset = (page - 1) * size;
        String sql = BASE
            + "WHERE k.nik LIKE ? OR k.nama LIKE ? "
            + "ORDER BY p.created_at DESC, p.id_penggajian DESC LIMIT ? OFFSET ?";
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

    public void insert(Penggajian p) throws SQLException {
        String sql = "INSERT INTO penggajian (id_karyawan, periode_bulan, periode_tahun, gaji_pokok, "
            + "total_tunjangan, potongan, total_gaji, tanggal_bayar, keterangan) "
            + "VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, p);
            ps.executeUpdate();
        }
    }

    public void update(Penggajian p) throws SQLException {
        String sql = "UPDATE penggajian SET id_karyawan=?, periode_bulan=?, periode_tahun=?, gaji_pokok=?, "
            + "total_tunjangan=?, potongan=?, total_gaji=?, tanggal_bayar=?, keterangan=? "
            + "WHERE id_penggajian=?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, p);
            ps.setInt(10, p.getIdPenggajian());
            ps.executeUpdate();
        }
    }

    public void delete(int idPenggajian) throws SQLException {
        String sql = "DELETE FROM penggajian WHERE id_penggajian=?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPenggajian);
            ps.executeUpdate();
        }
    }

    /** Cek dobel penggajian untuk (karyawan, bulan, tahun). */
    public boolean isExists(int idKaryawan, int bulan, int tahun, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM penggajian "
            + "WHERE id_karyawan=? AND periode_bulan=? AND periode_tahun=? AND id_penggajian<>?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idKaryawan);
            ps.setInt(2, bulan);
            ps.setInt(3, tahun);
            ps.setInt(4, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /** Gaji pokok dari jabatan karyawan. */
    public double hitungGajiPokok(int idKaryawan) throws SQLException {
        String sql = "SELECT j.gaji_pokok FROM karyawan k "
            + "JOIN jabatan j ON k.id_jabatan = j.id_jabatan WHERE k.id_karyawan=?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idKaryawan);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }

    /** Total tunjangan yang melekat pada jabatan karyawan (via jabatan_tunjangan). */
    public double hitungTotalTunjangan(int idKaryawan) throws SQLException {
        String sql = "SELECT COALESCE(SUM(t.jumlah),0) FROM karyawan k "
            + "JOIN jabatan_tunjangan jt ON k.id_jabatan = jt.id_jabatan "
            + "JOIN tunjangan t ON jt.id_tunjangan = t.id_tunjangan "
            + "WHERE k.id_karyawan=?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idKaryawan);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }

    private void bind(PreparedStatement ps, Penggajian p) throws SQLException {
        ps.setInt(1, p.getIdKaryawan());
        ps.setInt(2, p.getPeriodeBulan());
        ps.setInt(3, p.getPeriodeTahun());
        ps.setDouble(4, p.getGajiPokok());
        ps.setDouble(5, p.getTotalTunjangan());
        ps.setDouble(6, p.getPotongan());
        ps.setDouble(7, p.getTotalGaji());
        ps.setDate(8, p.getTanggalBayar());
        ps.setString(9, p.getKeterangan());
    }

    private Penggajian map(ResultSet rs) throws SQLException {
        Penggajian p = new Penggajian();
        p.setIdPenggajian(rs.getInt("id_penggajian"));
        p.setIdKaryawan(rs.getInt("id_karyawan"));
        p.setNik(rs.getString("nik"));
        p.setNama(rs.getString("nama"));
        p.setPeriodeBulan(rs.getInt("periode_bulan"));
        p.setPeriodeTahun(rs.getInt("periode_tahun"));
        p.setGajiPokok(rs.getDouble("gaji_pokok"));
        p.setTotalTunjangan(rs.getDouble("total_tunjangan"));
        p.setPotongan(rs.getDouble("potongan"));
        p.setTotalGaji(rs.getDouble("total_gaji"));
        p.setTanggalBayar(rs.getDate("tanggal_bayar"));
        p.setKeterangan(rs.getString("keterangan"));
        return p;
    }
}
