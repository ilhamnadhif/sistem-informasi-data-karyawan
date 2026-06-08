package com.karyawan.app;

import com.karyawan.dao.UserDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/** Halaman login; sukses -> buka {@link MainFrame}, gagal -> pesan error. */
public class FrmLogin extends JFrame {

    private final JTextField txtUser = new JTextField(18);
    private final JPasswordField txtPass = new JPasswordField(18);
    private final UserDAO userDAO = new UserDAO();

    public FrmLogin() {
        setTitle("Masuk - Aplikasi Data Karyawan");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(buatKonten());
        setSize(440, 380);
        setMinimumSize(new Dimension(440, 380));
        setLocationRelativeTo(null);
    }

    private JPanel buatKonten() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Tema.KONTEN_BG);

        // header aksen
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(Tema.ACCENT);
        header.setBorder(BorderFactory.createEmptyBorder(22, 20, 22, 20));
        JLabel judul = new JLabel("Aplikasi Data Karyawan");
        judul.setForeground(Color.WHITE);
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 18f));
        header.add(judul);
        root.add(header, BorderLayout.NORTH);

        // form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Tema.KONTEN_BG);
        form.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 0, 6, 0);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0;
        g.gridy = 0;
        g.weightx = 1;

        JLabel info = new JLabel("Silakan masuk untuk melanjutkan");
        info.setForeground(new Color(0x5A6270));
        form.add(info, g);

        g.gridy++;
        form.add(label("Nama Pengguna"), g);
        g.gridy++;
        form.add(txtUser, g);

        g.gridy++;
        form.add(label("Kata Sandi"), g);
        g.gridy++;
        form.add(txtPass, g);

        g.gridy++;
        g.insets = new Insets(18, 0, 6, 0);
        form.add(tombolLogin(), g);

        root.add(form, BorderLayout.CENTER);

        // Enter di field mana pun -> login
        txtUser.addActionListener(e -> login());
        txtPass.addActionListener(e -> login());
        return root;
    }

    private JLabel label(String teks) {
        JLabel l = new JLabel(teks);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 12f));
        l.setForeground(new Color(0x394150));
        return l;
    }

    private JButton tombolLogin() {
        JButton b = new JButton("Masuk");
        b.setBackground(Tema.ACCENT);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 13f));
        b.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> login());
        getRootPane().setDefaultButton(b);
        return b;
    }

    private void login() {
        String u = txtUser.getText().trim();
        String p = new String(txtPass.getPassword());
        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Nama pengguna dan kata sandi wajib diisi.",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (userDAO.cekLogin(u, p)) {
                dispose();
                SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this,
                    "Nama pengguna atau kata sandi salah.",
                    "Gagal Masuk", JOptionPane.ERROR_MESSAGE);
                txtPass.setText("");
                txtPass.requestFocusInWindow();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Gagal terhubung ke database:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
