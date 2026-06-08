package com.karyawan.dao;

import com.karyawan.model.Jabatan;
import com.karyawan.util.KoneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Akses data tabel jabatan + mapping jabatan_tunjangan. */
public class JabatanDAO {

    /** Hitung total baris sesuai keyword (untuk pagination). */
    public int count(String keyword) throws SQLException {
        String sql = "SELECT COUNT(*) FROM jabatan "
            + "WHERE kode_jabatan LIKE ? OR nama_jabatan LIKE ?";
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

    /** Ambil satu halaman data (server-side pagination). */
    public List<Jabatan> findPaged(int page, int size, String keyword) throws SQLException {
        List<Jabatan> list = new ArrayList<>();
        int offset = (page - 1) * size;
        String sql = "SELECT * FROM jabatan "
            + "WHERE kode_jabatan LIKE ? OR nama_jabatan LIKE ? "
            + "ORDER BY created_at DESC, id_jabatan DESC LIMIT ? OFFSET ?";
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

    /** Semua jabatan (untuk combo box). */
    public List<Jabatan> findAll() throws SQLException {
        List<Jabatan> list = new ArrayList<>();
        String sql = "SELECT * FROM jabatan ORDER BY nama_jabatan";
        try (Connection c = KoneksiDB.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    /** Simpan jabatan baru; id hasil generate di-set balik ke objek {@code j}. */
    public void insert(Jabatan j) throws SQLException {
        String sql = "INSERT INTO jabatan (kode_jabatan, nama_jabatan, gaji_pokok, keterangan) "
            + "VALUES (?,?,?,?)";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, j.getKodeJabatan());
            ps.setString(2, j.getNamaJabatan());
            ps.setDouble(3, j.getGajiPokok());
            ps.setString(4, j.getKeterangan());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    j.setIdJabatan(rs.getInt(1));
                }
            }
        }
    }

    public void update(Jabatan j) throws SQLException {
        String sql = "UPDATE jabatan SET kode_jabatan=?, nama_jabatan=?, gaji_pokok=?, keterangan=? "
            + "WHERE id_jabatan=?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, j.getKodeJabatan());
            ps.setString(2, j.getNamaJabatan());
            ps.setDouble(3, j.getGajiPokok());
            ps.setString(4, j.getKeterangan());
            ps.setInt(5, j.getIdJabatan());
            ps.executeUpdate();
        }
    }

    public void delete(int idJabatan) throws SQLException {
        String sql = "DELETE FROM jabatan WHERE id_jabatan=?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idJabatan);
            ps.executeUpdate();
        }
    }

    /** Cek kode unik; excludeId untuk mode edit (abaikan baris sendiri). */
    public boolean isKodeExists(String kode, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM jabatan WHERE kode_jabatan=? AND id_jabatan<>?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kode);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // ===================== Mapping jabatan_tunjangan =====================

    /** Id tunjangan yang melekat pada sebuah jabatan. */
    public List<Integer> findTunjanganIds(int idJabatan) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id_tunjangan FROM jabatan_tunjangan WHERE id_jabatan=?";
        try (Connection c = KoneksiDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idJabatan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
        }
        return ids;
    }

    /** Simpan ulang mapping tunjangan untuk sebuah jabatan (replace all). */
    public void saveTunjanganMapping(int idJabatan, List<Integer> idTunjanganList) throws SQLException {
        Connection c = KoneksiDB.getConnection();
        boolean autoCommit = c.getAutoCommit();
        try {
            c.setAutoCommit(false);
            try (PreparedStatement del = c.prepareStatement(
                    "DELETE FROM jabatan_tunjangan WHERE id_jabatan=?")) {
                del.setInt(1, idJabatan);
                del.executeUpdate();
            }
            try (PreparedStatement ins = c.prepareStatement(
                    "INSERT INTO jabatan_tunjangan (id_jabatan, id_tunjangan) VALUES (?,?)")) {
                for (Integer idt : idTunjanganList) {
                    ins.setInt(1, idJabatan);
                    ins.setInt(2, idt);
                    ins.addBatch();
                }
                ins.executeBatch();
            }
            c.commit();
        } catch (SQLException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(autoCommit);
        }
    }

    private Jabatan map(ResultSet rs) throws SQLException {
        Jabatan j = new Jabatan();
        j.setIdJabatan(rs.getInt("id_jabatan"));
        j.setKodeJabatan(rs.getString("kode_jabatan"));
        j.setNamaJabatan(rs.getString("nama_jabatan"));
        j.setGajiPokok(rs.getDouble("gaji_pokok"));
        j.setKeterangan(rs.getString("keterangan"));
        return j;
    }
}
