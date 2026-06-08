package com.karyawan.model;

/** Entity tunjangan. */
public class Tunjangan {

    private int idTunjangan;
    private String kodeTunjangan;
    private String namaTunjangan;
    private double jumlah;
    private String keterangan;

    public Tunjangan() {
    }

    public int getIdTunjangan() {
        return idTunjangan;
    }

    public void setIdTunjangan(int idTunjangan) {
        this.idTunjangan = idTunjangan;
    }

    public String getKodeTunjangan() {
        return kodeTunjangan;
    }

    public void setKodeTunjangan(String kodeTunjangan) {
        this.kodeTunjangan = kodeTunjangan;
    }

    public String getNamaTunjangan() {
        return namaTunjangan;
    }

    public void setNamaTunjangan(String namaTunjangan) {
        this.namaTunjangan = namaTunjangan;
    }

    public double getJumlah() {
        return jumlah;
    }

    public void setJumlah(double jumlah) {
        this.jumlah = jumlah;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    @Override
    public String toString() {
        return namaTunjangan;
    }
}
