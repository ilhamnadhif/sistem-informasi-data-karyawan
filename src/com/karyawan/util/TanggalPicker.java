package com.karyawan.util;

import com.karyawan.app.Tema;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Calendar;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Input tanggal berbentuk kalender, tanpa library tambahan. Terdiri dari
 * field read-only (tampilan dd-MM-yyyy) + tombol yang membuka popup grid
 * tanggal. Nilai disimpan dan dikembalikan sebagai {@link java.sql.Date}.
 */
public class TanggalPicker extends JPanel {

    private static final String[] NAMA_BULAN = {
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    };
    private static final String[] NAMA_HARI = {"Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab"};

    private final JTextField txtTampil = new JTextField(12);
    private final JButton btnKalender = new JButton("📅"); // 📅
    private java.sql.Date nilai; // null = kosong

    public TanggalPicker() {
        super(new BorderLayout(4, 0));
        setOpaque(false);
        txtTampil.setEditable(false);
        txtTampil.setBackground(Color.WHITE);
        btnKalender.setMargin(new Insets(2, 6, 2, 6));
        btnKalender.setFocusPainted(false);
        btnKalender.setToolTipText("Pilih tanggal");
        btnKalender.addActionListener(e -> tampilkanKalender());
        add(txtTampil, BorderLayout.CENTER);
        add(btnKalender, BorderLayout.EAST);
    }

    /** Tanggal terpilih, atau {@code null} bila kosong. */
    public java.sql.Date getDate() {
        return nilai;
    }

    /** Isi nilai (untuk prefill mode edit); {@code null} mengosongkan. */
    public void setDate(java.util.Date d) {
        nilai = (d == null) ? null : new java.sql.Date(d.getTime());
        txtTampil.setText(TanggalFormat.format(nilai));
    }

    public boolean isEmpty() {
        return nilai == null;
    }

    private void tampilkanKalender() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        final JDialog dlg = new JDialog(owner); // modeless: tidak diblok dialog form
        dlg.setUndecorated(true);
        KalenderPanel kp = new KalenderPanel(dlg);
        dlg.setContentPane(kp);
        dlg.pack();

        Point loc = btnKalender.getLocationOnScreen();
        dlg.setLocation(loc.x, loc.y + btnKalender.getHeight());

        // tutup saat kehilangan fokus (klik di luar popup)
        dlg.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                dlg.dispose();
            }
        });
        dlg.setVisible(true);
    }

    private boolean sameDay(java.sql.Date d, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c.get(Calendar.YEAR) == year
            && c.get(Calendar.MONTH) == month
            && c.get(Calendar.DAY_OF_MONTH) == day;
    }

    /** Panel kalender (header bulan + grid tanggal + footer). */
    private class KalenderPanel extends JPanel {

        private final JDialog dialog;
        private final Calendar view = Calendar.getInstance();
        private final JLabel lblBulan = new JLabel("", SwingConstants.CENTER);
        private final JPanel grid = new JPanel(new GridLayout(0, 7, 2, 2));

        KalenderPanel(JDialog dialog) {
            super(new BorderLayout(4, 4));
            this.dialog = dialog;
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.ACCENT),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)));

            if (nilai != null) {
                view.setTime(nilai);
            }

            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            JButton prev = nav("‹");
            JButton next = nav("›");
            prev.addActionListener(e -> {
                view.add(Calendar.MONTH, -1);
                render();
            });
            next.addActionListener(e -> {
                view.add(Calendar.MONTH, 1);
                render();
            });
            lblBulan.setFont(lblBulan.getFont().deriveFont(Font.BOLD, 13f));
            header.add(prev, BorderLayout.WEST);
            header.add(lblBulan, BorderLayout.CENTER);
            header.add(next, BorderLayout.EAST);
            add(header, BorderLayout.NORTH);

            grid.setBackground(Color.WHITE);
            add(grid, BorderLayout.CENTER);

            JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
            footer.setOpaque(false);
            JButton btnHariIni = new JButton("Hari Ini");
            JButton btnHapus = new JButton("Hapus");
            btnHariIni.setFocusPainted(false);
            btnHapus.setFocusPainted(false);
            btnHariIni.addActionListener(e -> pilih(new java.sql.Date(System.currentTimeMillis())));
            btnHapus.addActionListener(e -> pilih(null));
            footer.add(btnHariIni);
            footer.add(btnHapus);
            add(footer, BorderLayout.SOUTH);

            // ESC menutup popup
            getInputMap(WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "tutup");
            getActionMap().put("tutup", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            });

            render();
        }

        private JButton nav(String teks) {
            JButton b = new JButton(teks);
            b.setMargin(new Insets(2, 8, 2, 8));
            b.setFocusPainted(false);
            return b;
        }

        private void render() {
            lblBulan.setText(NAMA_BULAN[view.get(Calendar.MONTH)] + " " + view.get(Calendar.YEAR));
            grid.removeAll();

            for (String h : NAMA_HARI) {
                JLabel l = new JLabel(h, SwingConstants.CENTER);
                l.setForeground(new Color(0x8C929C));
                l.setFont(l.getFont().deriveFont(Font.BOLD, 11f));
                grid.add(l);
            }

            Calendar c = (Calendar) view.clone();
            c.set(Calendar.DAY_OF_MONTH, 1);
            int offset = c.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY; // 0..6
            int maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);

            for (int i = 0; i < offset; i++) {
                grid.add(new JLabel());
            }
            int year = view.get(Calendar.YEAR);
            int month = view.get(Calendar.MONTH);
            for (int d = 1; d <= maxDay; d++) {
                final int day = d;
                JButton b = new JButton(String.valueOf(d));
                b.setMargin(new Insets(3, 5, 3, 5));
                b.setFocusPainted(false);
                if (nilai != null && sameDay(nilai, year, month, day)) {
                    b.setBackground(Tema.ACCENT);
                    b.setForeground(Color.WHITE);
                    b.setOpaque(true);
                    b.setBorderPainted(false);
                }
                b.addActionListener(e -> {
                    Calendar pick = (Calendar) view.clone();
                    pick.set(Calendar.DAY_OF_MONTH, day);
                    pick.set(Calendar.HOUR_OF_DAY, 0);
                    pick.set(Calendar.MINUTE, 0);
                    pick.set(Calendar.SECOND, 0);
                    pick.set(Calendar.MILLISECOND, 0);
                    pilih(new java.sql.Date(pick.getTimeInMillis()));
                });
                grid.add(b);
            }

            grid.revalidate();
            grid.repaint();
            if (dialog.isShowing()) {
                dialog.pack();
            }
        }

        private void pilih(java.sql.Date d) {
            setDate(d);
            dialog.dispose();
        }
    }
}
