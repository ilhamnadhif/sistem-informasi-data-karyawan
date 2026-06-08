package com.karyawan.app;

import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * Tema visual aplikasi (palet warna + Look &amp; Feel) terpusat.
 * Dipasang sekali via {@link #install()} sebelum komponen UI dibuat, sehingga
 * gaya berlaku seragam di semua panel tanpa mengubah tiap form.
 */
public final class Tema {

    /** Warna aksen utama (header & seleksi). */
    public static final Color ACCENT = new Color(0x2F6FED);
    /** Latar gelap sidebar. */
    public static final Color SIDEBAR_BG = new Color(0x21252B);
    public static final Color SIDEBAR_HOVER = new Color(0x2D323B);
    public static final Color SIDEBAR_AKTIF = new Color(0x2F6FED);
    public static final Color SIDEBAR_FG = new Color(0xDCDFE4);
    public static final Color SIDEBAR_KATEGORI = new Color(0x8C929C);
    /** Latar area konten. */
    public static final Color KONTEN_BG = new Color(0xF4F5F7);
    public static final Color HEADER_FG = Color.WHITE;

    private static final Font FONT_DASAR = new Font("SansSerif", Font.PLAIN, 13);

    private Tema() {
    }

    /** Pasang Nimbus + sesuaikan beberapa warna/font. Aman bila Nimbus absen. */
    public static void install() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // biarkan Look &amp; Feel default bila Nimbus gagal dipasang
        }

        // font global
        UIManager.getLookAndFeelDefaults().put("defaultFont", new FontUIResource(FONT_DASAR));

        // penyesuaian palet Nimbus (diabaikan jika L&F lain)
        UIManager.put("control", KONTEN_BG);
        UIManager.put("background", KONTEN_BG);
        UIManager.put("nimbusBase", new Color(0x3B5B92));
        UIManager.put("nimbusBlueGrey", new Color(0xD7DAE0));
        UIManager.put("nimbusFocus", ACCENT);
        UIManager.put("nimbusSelectionBackground", ACCENT);
        UIManager.put("nimbusSelection", ACCENT);
        UIManager.put("text", new Color(0x21252B));
        UIManager.put("Table.alternateRowColor", new Color(0xEDEFF3));
    }
}
