package com.karyawan.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Format mata uang Rupiah, mis. 1500000 -> "Rp1.500.000". */
public class RupiahFormat {

    private static final DecimalFormat DF;

    static {
        DecimalFormatSymbols sym = new DecimalFormatSymbols(Locale.of("id", "ID"));
        sym.setGroupingSeparator('.');
        DF = new DecimalFormat("#,##0", sym);
    }

    private RupiahFormat() {
    }

    public static String format(double nilai) {
        return "Rp" + DF.format(nilai);
    }

    /** Membaca string Rupiah/angka kembali menjadi double. */
    public static double parse(String teks) {
        if (teks == null) {
            return 0;
        }
        String bersih = teks.replaceAll("[^0-9-]", "");
        if (bersih.isEmpty() || bersih.equals("-")) {
            return 0;
        }
        return Double.parseDouble(bersih);
    }
}
