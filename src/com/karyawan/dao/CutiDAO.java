package com.karyawan.dao;

import com.karyawan.model.Cuti;
import com.karyawan.util.KoneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Akses data tabel cuti (join karyawan untuk tampilan). */
public class CutiDAO {

    private static final String BASE =
        "SELECT c.*, k.nama FROM cuti c "
        + "JOIN karyawan k ON c.id_karyawan = k.id_karyawan ";

    public int count(String keyword) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cuti c "
            + "JOIN karyawan k ON c.id_karyawan = k.id_karyawan "
            + "WHERE k.nama LIKE ? OR c.jenis_cuti LIKE ? OR c.status LIKE ?";
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

    public List<Cuti> findPaged(int page, int size, String keyword) throws SQLException {
        List<Cuti> list = new ArrayList<>();
        int offset = (page - 1) * size;
        String sql = BASE
            + "WHERE k.nama LIKE ? OR c.jenis_cuti LIKE ? OR c.status LIKE ? "
            + "ORDER BY c.created_at DESC, c.id_cuti DESC LIMIT ? OFFSET ?";
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

    public void insert(Cuti c) throws SQLException {
        String sql = "INSERT INTO cuti (id_karyawan, tanggal_pengajuan, tanggal_mulai, tanggal_selesai, "
            + "jenis_cuti, lama_cuti, alasan, status) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection con = KoneksiDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, c);
            ps.executeUpdate();
        }
    }

    public void update(Cuti c) throws SQLException {
        String sql = "UPDATE cuti SET id_karyawan=?, tanggal_pengajuan=?, tanggal_mulai=?, "
            + "tanggal_selesai=?, jenis_cuti=?, lama_cuti=?, alasan=?, status=? WHERE id_cuti=?";
        try (Connection con = KoneksiDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, c);
            ps.setInt(9, c.getIdCuti());
            ps.executeUpdate();
        }
    }

    public void delete(int idCuti) throws SQLException {
        String sql = "DELETE FROM cuti WHERE id_cuti=?";
        try (Connection con = KoneksiDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCuti);
            ps.executeUpdate();
        }
    }

    /** Ubah status cuti (Setujui/Tolak). */
    public void updateStatus(int idCuti, String status) throws SQLException {
        String sql = "UPDATE cuti SET status=? WHERE id_cuti=?";
        try (Connection con = KoneksiDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, idCuti);
            ps.executeUpdate();
        }
    }

    private void bind(PreparedStatement ps, Cuti c) throws SQLException {
        ps.setInt(1, c.getIdKaryawan());
        ps.setDate(2, c.getTanggalPengajuan());
        ps.setDate(3, c.getTanggalMulai());
        ps.setDate(4, c.getTanggalSelesai());
        ps.setString(5, c.getJenisCuti());
        ps.setInt(6, c.getLamaCuti());
        ps.setString(7, c.getAlasan());
        ps.setString(8, c.getStatus());
    }

    private Cuti map(ResultSet rs) throws SQLException {
        Cuti c = new Cuti();
        c.setIdCuti(rs.getInt("id_cuti"));
        c.setIdKaryawan(rs.getInt("id_karyawan"));
        c.setNama(rs.getString("nama"));
        c.setTanggalPengajuan(rs.getDate("tanggal_pengajuan"));
        c.setTanggalMulai(rs.getDate("tanggal_mulai"));
        c.setTanggalSelesai(rs.getDate("tanggal_selesai"));
        c.setJenisCuti(rs.getString("jenis_cuti"));
        c.setLamaCuti(rs.getInt("lama_cuti"));
        c.setAlasan(rs.getString("alasan"));
        c.setStatus(rs.getString("status"));
        return c;
    }
}
