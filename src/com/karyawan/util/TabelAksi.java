package com.karyawan.util;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.function.IntConsumer;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * Pemasang kolom "Aksi" berisi dua tombol (Edit &amp; Hapus) di tiap baris tabel.
 * Dipakai ulang oleh semua form CRUD agar logika renderer/editor sel tombol
 * cukup ditulis sekali di sini.
 *
 * <p>Catatan: kolom Aksi harus dibuat <b>editable</b> di model tabel agar klik
 * tunggal langsung mengaktifkan editor (tombol bisa ditekan).
 */
public final class TabelAksi {

    private TabelAksi() {
    }

    /**
     * Pasang renderer + editor tombol pada kolom tertentu.
     *
     * @param tabel   tabel target
     * @param kolom   indeks kolom Aksi
     * @param onEdit  dipanggil dengan indeks baris (model) saat tombol Edit ditekan
     * @param onHapus dipanggil dengan indeks baris (model) saat tombol Hapus ditekan
     */
    public static void pasang(JTable tabel, int kolom, IntConsumer onEdit, IntConsumer onHapus) {
        tabel.setRowHeight(Math.max(tabel.getRowHeight(), 32));
        tabel.getColumnModel().getColumn(kolom).setCellRenderer(new Renderer());
        tabel.getColumnModel().getColumn(kolom).setCellEditor(new Editor(tabel, onEdit, onHapus));
        tabel.getColumnModel().getColumn(kolom).setMinWidth(140);
        tabel.getColumnModel().getColumn(kolom).setPreferredWidth(150);
        tabel.getColumnModel().getColumn(kolom).setMaxWidth(170);
    }

    /** Buat tombol kecil seragam (tampilan default, tanpa warna kustom). */
    private static JButton tombol(String teks) {
        JButton b = new JButton(teks);
        b.setMargin(new java.awt.Insets(2, 8, 2, 8));
        b.setFocusPainted(false);
        b.setFont(b.getFont().deriveFont(11f));
        return b;
    }

    private static JPanel panel(JButton edit, JButton hapus) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 1));
        p.add(edit);
        p.add(hapus);
        return p;
    }

    /** Renderer: hanya menggambar dua tombol (tidak interaktif). */
    private static class Renderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                boolean fokus, int row, int col) {
            JPanel p = panel(tombol("Edit"), tombol("Hapus"));
            p.setBackground(sel ? t.getSelectionBackground() : t.getBackground());
            return p;
        }
    }

    /** Editor: tombol asli yang menangkap klik lalu memanggil callback. */
    private static class Editor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel;

        Editor(JTable tabel, IntConsumer onEdit, IntConsumer onHapus) {
            JButton edit = tombol("Edit");
            JButton hapus = tombol("Hapus");
            panel = panel(edit, hapus);
            edit.addActionListener(e -> {
                int row = tabel.getEditingRow();
                fireEditingStopped();
                if (row >= 0) {
                    onEdit.accept(tabel.convertRowIndexToModel(row));
                }
            });
            hapus.addActionListener(e -> {
                int row = tabel.getEditingRow();
                fireEditingStopped();
                if (row >= 0) {
                    onHapus.accept(tabel.convertRowIndexToModel(row));
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int row, int col) {
            panel.setBackground(t.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}
