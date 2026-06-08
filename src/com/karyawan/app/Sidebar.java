package com.karyawan.app;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Sidebar navigasi aplikasi sebagai SATU komponen mandiri.
 * Item dibuat dari JLabel opaque (bukan JButton) supaya warna gelap tetap
 * tampil konsisten di semua Look &amp; Feel termasuk Nimbus. Klik item memanggil
 * callback {@code onSelect} dengan "key" panel tujuan; key {@link #KEY_KELUAR}
 * dipakai untuk keluar aplikasi.
 */
public class Sidebar extends JPanel {

    /** Key khusus: keluar sesi (kembali ke halaman login). */
    public static final String KEY_KELUAR = "__keluar__";

    private final Consumer<String> onSelect;
    private JLabel itemAktif; // item yang sedang dipilih (disorot aksen)

    /** @param onSelect dipanggil dengan key panel saat item diklik. */
    public Sidebar(Consumer<String> onSelect) {
        this.onSelect = onSelect;
        setBackground(Tema.SIDEBAR_BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(220, 0));

        judul("Data Karyawan");

        kategori("MASTER");
        item("Data Jabatan", "jabatan");
        item("Data Tunjangan", "tunjangan");
        item("Data Karyawan", "karyawan");

        kategori("TRANSAKSI");
        item("Absensi", "absensi");
        item("Cuti", "cuti");
        item("Penggajian", "penggajian");

        kategori("LAPORAN");
        item("Laporan Karyawan", "lap_karyawan");
        item("Laporan Absensi", "lap_absensi");
        item("Laporan Cuti", "lap_cuti");
        item("Laporan Jabatan", "lap_jabatan");

        add(Box.createVerticalGlue());
        item("Keluar", KEY_KELUAR);
    }

    /** Judul/branding di atas sidebar. */
    private void judul(String teks) {
        JLabel l = new JLabel(teks);
        l.setForeground(Color.WHITE);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 16f));
        l.setBorder(BorderFactory.createEmptyBorder(18, 16, 12, 12));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        add(l);
    }

    /** Label kategori (pemisah kelompok menu). */
    private void kategori(String teks) {
        JLabel l = new JLabel(teks);
        l.setForeground(Tema.SIDEBAR_KATEGORI);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 11f));
        l.setBorder(BorderFactory.createEmptyBorder(14, 16, 6, 12));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        add(l);
    }

    /** Item menu lebar penuh; klik -> sorot aktif + callback berisi key. */
    private void item(String teks, String key) {
        final boolean aksi = KEY_KELUAR.equals(key);
        final JLabel l = new JLabel(teks);
        l.setOpaque(true);
        l.setBackground(Tema.SIDEBAR_BG);
        l.setForeground(Tema.SIDEBAR_FG);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        l.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 12));
        l.setFont(l.getFont().deriveFont(13f));
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (l != itemAktif) {
                    l.setBackground(Tema.SIDEBAR_HOVER);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (l != itemAktif) {
                    l.setBackground(Tema.SIDEBAR_BG);
                }
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!aksi) {
                    setAktif(l);
                }
                onSelect.accept(key);
            }
        });
        add(l);
    }

    /** Pindahkan sorotan aksen ke item yang baru dipilih. */
    private void setAktif(JLabel l) {
        if (itemAktif != null) {
            itemAktif.setBackground(Tema.SIDEBAR_BG);
            itemAktif.setForeground(Tema.SIDEBAR_FG);
        }
        itemAktif = l;
        l.setBackground(Tema.SIDEBAR_AKTIF);
        l.setForeground(Color.WHITE);
    }
}
