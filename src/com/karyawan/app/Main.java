package com.karyawan.app;

import javax.swing.SwingUtilities;

/** Entry point aplikasi. */
public class Main {

    public static void main(String[] args) {
        Tema.install();
        SwingUtilities.invokeLater(() -> new FrmLogin().setVisible(true));
    }
}
