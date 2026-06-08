package com.karyawan.model;

/** Entity jabatan. */
public class Jabatan {

    private int idJabatan;
    private String kodeJabatan;
    private String namaJabatan;
    private double gajiPokok;
    private String keterangan;

    public Jabatan() {
    }

    public int getIdJabatan() {
        return idJabatan;
    }

    public void setIdJabatan(int idJabatan) {
        this.idJabatan = idJabatan;
    }

    public String getKodeJabatan() {
        return kodeJabatan;
    }

    public void setKodeJabatan(String kodeJabatan) {
        this.kodeJabatan = kodeJabatan;
    }

    public String getNamaJabatan() {
        return namaJabatan;
    }

    public void setNamaJabatan(String namaJabatan) {
        this.namaJabatan = namaJabatan;
    }

    public double getGajiPokok() {
        return gajiPokok;
    }

    public void setGajiPokok(double gajiPokok) {
        this.gajiPokok = gajiPokok;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    /** Dipakai sebagai item JComboBox: tampil nama, simpan id. */
    @Override
    public String toString() {
        return namaJabatan;
    }
}
