package com.karyawan.util;

import com.karyawan.app.Tema;
import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

/**
 * Input jam (HH:mm) berbentuk spinner, tanpa library tambahan. Tombol panah
 * naik/turun menambah/mengurangi jam atau menit pada bagian yang dipilih; boleh
 * dikosongkan. Nilai dikembalikan sebagai {@link java.sql.Time} ({@code null}
 * bila kosong).
 */
public class JamPicker extends javax.swing.JPanel {

    private final JSpinner spinner;
    private final JFormattedTextField ftf;
    private final SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");

    /** Default: terisi waktu sekarang. */
    public JamPicker() {
        this(true);
    }

    /**
     * @param defaultSekarang true = isi waktu sekarang; false = mulai kosong.
     */
    public JamPicker(boolean defaultSekarang) {
        super(new BorderLayout());
        setOpaque(false);
        fmt.setLenient(false);
        spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
        spinner.setEditor(editor);

        ftf = editor.getTextField();
        ftf.setEditable(true);
        ftf.setBackground(Color.WHITE);
        // PERSIST: teks kosong tidak otomatis dikembalikan ke nilai lama
        ftf.setFocusLostBehavior(JFormattedTextField.PERSIST);
        spinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Tema.ACCENT),
            BorderFactory.createEmptyBorder(1, 4, 1, 4)));

        if (defaultSekarang) {
            setTime(new Time(System.currentTimeMillis()));
        } else {
            kosongkan();
        }
        add(spinner, BorderLayout.CENTER);
    }

    /** Jam terpilih sebagai {@link java.sql.Time}; {@code null} bila kosong/invalid. */
    public Time getTime() {
        String txt = ftf.getText().trim();
        if (txt.isEmpty()) {
            return null;
        }
        try {
            Date d = fmt.parse(txt);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            return Time.valueOf(String.format("%02d:%02d:00",
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)));
        } catch (ParseException e) {
            return null;
        }
    }

    /** Isi nilai (prefill mode edit); {@code null} -> kosong. */
    public void setTime(Time t) {
        if (t == null) {
            kosongkan();
            return;
        }
        Calendar src = Calendar.getInstance();
        src.setTime(t);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, src.get(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE, src.get(Calendar.MINUTE));
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        spinner.setValue(c.getTime());
        ftf.setText(fmt.format(c.getTime()));
    }

    /** Kosongkan tampilan jam. */
    public void kosongkan() {
        ftf.setText("");
    }
}
