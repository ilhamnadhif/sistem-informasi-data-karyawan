package com.karyawan.app;

import com.karyawan.view.laporan.FrmLaporanAbsensi;
import com.karyawan.view.laporan.FrmLaporanCuti;
import com.karyawan.view.laporan.FrmLaporanJabatan;
import com.karyawan.view.laporan.FrmLaporanKaryawan;
import com.karyawan.view.master.FrmJabatan;
import com.karyawan.view.master.FrmKaryawan;
import com.karyawan.view.master.FrmTunjangan;
import com.karyawan.view.transaksi.FrmAbsensi;
import com.karyawan.view.transaksi.FrmCuti;
import com.karyawan.view.transaksi.FrmPenggajian;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Jendela utama: Sidebar (kiri) + header judul + area konten ber-CardLayout.
 * Semua panel form didaftarkan sekali di awal; klik sidebar hanya menukar
 * kartu yang tampil (tanpa membuat ulang panel).
 */
public class MainFrame extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final JLabel lblJudul = new JLabel();
    /** key panel -> judul yang ditampilkan di header. */
    private final Map<String, String> judul = new LinkedHashMap<>();

    public MainFrame() {
        setTitle("Aplikasi Data Karyawan");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // sidebar (satu komponen); callback -> navigasi(key)
        add(new Sidebar(this::navigasi), BorderLayout.WEST);
        add(areaKonten(), BorderLayout.CENTER);

        // daftarkan semua panel + judul header (key harus sama dengan di Sidebar)
        daftar("jabatan", "Data Jabatan", new FrmJabatan());
        daftar("tunjangan", "Data Tunjangan", new FrmTunjangan());
        daftar("karyawan", "Data Karyawan", new FrmKaryawan());
        daftar("absensi", "Absensi Karyawan", new FrmAbsensi());
        daftar("cuti", "Cuti Karyawan", new FrmCuti());
        daftar("penggajian", "Penggajian Karyawan", new FrmPenggajian());
        daftar("lap_karyawan", "Laporan Data Karyawan", new FrmLaporanKaryawan());
        daftar("lap_absensi", "Laporan Absensi", new FrmLaporanAbsensi());
        daftar("lap_cuti", "Laporan Cuti", new FrmLaporanCuti());
        daftar("lap_jabatan", "Laporan Jabatan", new FrmLaporanJabatan());

        navigasi("jabatan"); // panel awal saat aplikasi dibuka

        setSize(1150, 700);
        setMinimumSize(new Dimension(920, 560));
        setLocationRelativeTo(null);
    }

    /** Susun header (judul) + area kartu dengan padding. */
    private JPanel areaKonten() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Tema.ACCENT);
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        lblJudul.setForeground(Tema.HEADER_FG);
        lblJudul.setFont(lblJudul.getFont().deriveFont(Font.BOLD, 17f));
        header.add(lblJudul, BorderLayout.WEST);

        content.setBackground(Tema.KONTEN_BG);
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(Tema.KONTEN_BG);
        wrap.add(header, BorderLayout.NORTH);
        wrap.add(content, BorderLayout.CENTER);
        return wrap;
    }

    /** Tambahkan panel form ke CardLayout dan simpan judul header-nya. */
    private void daftar(String key, String teksJudul, JComponent panel) {
        judul.put(key, teksJudul);
        content.add(panel, key);
    }

    /** Tukar panel yang tampil + perbarui judul; KEY_KELUAR -> kembali ke login. */
    private void navigasi(String key) {
        if (Sidebar.KEY_KELUAR.equals(key)) {
            keluar();
            return;
        }
        lblJudul.setText(judul.getOrDefault(key, ""));
        cards.show(content, key);
    }

    /** Tutup jendela utama lalu kembali ke halaman login. */
    private void keluar() {
        int pilih = JOptionPane.showConfirmDialog(this,
            "Yakin ingin keluar?", "Konfirmasi Keluar",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (pilih != JOptionPane.YES_OPTION) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            new FrmLogin().setVisible(true);
            dispose();
        });
    }
}
