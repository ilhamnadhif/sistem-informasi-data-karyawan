package com.karyawan.model;

import java.sql.Date;

/** Entity penggajian. */
public class Penggajian {

    private int idPenggajian;
    private int idKaryawan;
    private String nik;  // hasil join
    private String nama; // hasil join
    private int periodeBulan;
    private int periodeTahun;
    private double gajiPokok;
    private double totalTunjangan;
    private double potongan;
    private double totalGaji;
    private Date tanggalBayar;
    private String keterangan;

    public Penggajian() {
    }

    public int getIdPenggajian() {
        return idPenggajian;
    }

    public void setIdPenggajian(int idPenggajian) {
        this.idPenggajian = idPenggajian;
    }

    public int getIdKaryawan() {
        return idKaryawan;
    }

    public void setIdKaryawan(int idKaryawan) {
        this.idKaryawan = idKaryawan;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getPeriodeBulan() {
        return periodeBulan;
    }

    public void setPeriodeBulan(int periodeBulan) {
        this.periodeBulan = periodeBulan;
    }

    public int getPeriodeTahun() {
        return periodeTahun;
    }

    public void setPeriodeTahun(int periodeTahun) {
        this.periodeTahun = periodeTahun;
    }

    public double getGajiPokok() {
        return gajiPokok;
    }

    public void setGajiPokok(double gajiPokok) {
        this.gajiPokok = gajiPokok;
    }

    public double getTotalTunjangan() {
        return totalTunjangan;
    }

    public void setTotalTunjangan(double totalTunjangan) {
        this.totalTunjangan = totalTunjangan;
    }

    public double getPotongan() {
        return potongan;
    }

    public void setPotongan(double potongan) {
        this.potongan = potongan;
    }

    public double getTotalGaji() {
        return totalGaji;
    }

    public void setTotalGaji(double totalGaji) {
        this.totalGaji = totalGaji;
    }

    public Date getTanggalBayar() {
        return tanggalBayar;
    }

    public void setTanggalBayar(Date tanggalBayar) {
        this.tanggalBayar = tanggalBayar;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
