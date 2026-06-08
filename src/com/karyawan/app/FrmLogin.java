package com.karyawan.app;

import com.karyawan.dao.UserDAO;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** Halaman login (GUI Builder); sukses -> buka {@link MainFrame}. */
public class FrmLogin extends javax.swing.JFrame {

    private final UserDAO userDAO = new UserDAO();

    public FrmLogin() {
        initComponents();
        getRootPane().setDefaultButton(btnMasuk);
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

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblJudul = new javax.swing.JLabel();
        lblInfo = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        lblPass = new javax.swing.JLabel();
        txtPass = new javax.swing.JPasswordField();
        btnMasuk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Masuk - Aplikasi Data Karyawan");

        lblJudul.setBackground(new java.awt.Color(47, 111, 237));
        lblJudul.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblJudul.setForeground(new java.awt.Color(255, 255, 255));
        lblJudul.setText("Aplikasi Data Karyawan");
        lblJudul.setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 20, 22, 20));
        lblJudul.setOpaque(true);

        lblInfo.setForeground(new java.awt.Color(90, 98, 112));
        lblInfo.setText("Silakan masuk untuk melanjutkan");

        lblUser.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblUser.setForeground(new java.awt.Color(57, 65, 80));
        lblUser.setText("Nama Pengguna");

        txtUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUserActionPerformed(evt);
            }
        });

        lblPass.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblPass.setForeground(new java.awt.Color(57, 65, 80));
        lblPass.setText("Kata Sandi");

        txtPass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPassActionPerformed(evt);
            }
        });

        btnMasuk.setBackground(new java.awt.Color(47, 111, 237));
        btnMasuk.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        btnMasuk.setForeground(new java.awt.Color(255, 255, 255));
        btnMasuk.setText("Masuk");
        btnMasuk.setFocusPainted(false);
        btnMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMasukActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblJudul, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblInfo)
                    .addComponent(lblUser)
                    .addComponent(txtUser, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .addComponent(lblPass)
                    .addComponent(txtPass, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .addComponent(btnMasuk))
                .addGap(36, 36, 36))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblJudul)
                .addGap(24, 24, 24)
                .addComponent(lblInfo)
                .addGap(14, 14, 14)
                .addComponent(lblUser)
                .addGap(3, 3, 3)
                .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(lblPass)
                .addGap(3, 3, 3)
                .addComponent(txtPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(btnMasuk)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMasukActionPerformed
        login();
    }//GEN-LAST:event_btnMasukActionPerformed

    private void txtUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUserActionPerformed
        login();
    }//GEN-LAST:event_txtUserActionPerformed

    private void txtPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPassActionPerformed
        login();
    }//GEN-LAST:event_txtPassActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMasuk;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblJudul;
    private javax.swing.JLabel lblPass;
    private javax.swing.JLabel lblUser;
    private javax.swing.JTextField txtUser;
    private javax.swing.JPasswordField txtPass;
    // End of variables declaration//GEN-END:variables
}
