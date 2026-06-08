package com.karyawan.util;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/** Format & parse tanggal dengan pola dd-MM-yyyy. */
public class TanggalFormat {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd-MM-yyyy");

    static {
        SDF.setLenient(false);
    }

    private TanggalFormat() {
    }

    /** java.util.Date / java.sql.Date -> "dd-MM-yyyy"; null -> "". */
    public static String format(java.util.Date tanggal) {
        return tanggal == null ? "" : SDF.format(tanggal);
    }

    /** "dd-MM-yyyy" -> java.sql.Date. String kosong -> null. */
    public static Date parse(String teks) throws ParseException {
        if (teks == null || teks.trim().isEmpty()) {
            return null;
        }
        java.util.Date d = SDF.parse(teks.trim());
        return new Date(d.getTime());
    }

    /** true bila teks valid sebagai tanggal dd-MM-yyyy (atau kosong). */
    public static boolean isValid(String teks) {
        if (teks == null || teks.trim().isEmpty()) {
            return true;
        }
        try {
            SDF.parse(teks.trim());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
