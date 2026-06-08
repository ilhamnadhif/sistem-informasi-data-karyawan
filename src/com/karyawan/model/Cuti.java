package com.karyawan.model;

import java.sql.Date;

/** Entity cuti. */
public class Cuti {

    private int idCuti;
    private int idKaryawan;
    private String nama; // hasil join
    private Date tanggalPengajuan;
    private Date tanggalMulai;
    private Date tanggalSelesai;
    private String jenisCuti;
    private int lamaCuti;
    private String alasan;
    private String status; // Pending/Disetujui/Ditolak

    public Cuti() {
    }

    public int getIdCuti() {
        return idCuti;
    }

    public void setIdCuti(int idCuti) {
        this.idCuti = idCuti;
    }

    public int getIdKaryawan() {
        return idKaryawan;
    }

    public void setIdKaryawan(int idKaryawan) {
        this.idKaryawan = idKaryawan;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public Date getTanggalPengajuan() {
        return tanggalPengajuan;
    }

    public void setTanggalPengajuan(Date tanggalPengajuan) {
        this.tanggalPengajuan = tanggalPengajuan;
    }

    public Date getTanggalMulai() {
        return tanggalMulai;
    }

    public void setTanggalMulai(Date tanggalMulai) {
        this.tanggalMulai = tanggalMulai;
    }

    public Date getTanggalSelesai() {
        return tanggalSelesai;
    }

    public void setTanggalSelesai(Date tanggalSelesai) {
        this.tanggalSelesai = tanggalSelesai;
    }

    public String getJenisCuti() {
        return jenisCuti;
    }

    public void setJenisCuti(String jenisCuti) {
        this.jenisCuti = jenisCuti;
    }

    public int getLamaCuti() {
        return lamaCuti;
    }

    public void setLamaCuti(int lamaCuti) {
        this.lamaCuti = lamaCuti;
    }

    public String getAlasan() {
        return alasan;
    }

    public void setAlasan(String alasan) {
        this.alasan = alasan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
