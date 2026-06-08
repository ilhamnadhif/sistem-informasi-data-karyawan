package com.karyawan.util;

import java.util.regex.Pattern;
import javax.swing.JTextField;

/** Helper validasi input umum; mengembalikan boolean sederhana. */
public class Validasi {

    private static final Pattern EMAIL =
        Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private Validasi() {
    }

    public static boolean isKosong(JTextField field) {
        return field == null || field.getText().trim().isEmpty();
    }

    public static boolean isKosong(String teks) {
        return teks == null || teks.trim().isEmpty();
    }

    public static boolean isEmailValid(String email) {
        return email != null && EMAIL.matcher(email.trim()).matches();
    }

    /** True bila teks berupa angka (boleh desimal, non-negatif). */
    public static boolean isNumber(String teks) {
        if (isKosong(teks)) {
            return false;
        }
        try {
            double v = Double.parseDouble(teks.trim());
            return v >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
