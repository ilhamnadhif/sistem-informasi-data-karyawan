-- ============================================================
-- Skema database aplikasi Data Karyawan
-- Jalankan di MySQL 8.x:  mysql -u root < schema.sql
-- ============================================================
CREATE DATABASE IF NOT EXISTS db_karyawan
  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE db_karyawan;

-- ============ LOGIN ============
CREATE TABLE IF NOT EXISTS user (
  id_user    INT AUTO_INCREMENT PRIMARY KEY,
  username   VARCHAR(50)  NOT NULL UNIQUE,
  password   VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- akun default: admin / admin (idempotent bila skema dijalankan ulang)
INSERT INTO user (username, password) VALUES ('admin', 'admin')
  ON DUPLICATE KEY UPDATE username = username;

-- ============ MASTER ============
CREATE TABLE IF NOT EXISTS jabatan (
  id_jabatan    INT AUTO_INCREMENT PRIMARY KEY,
  kode_jabatan  VARCHAR(10)  NOT NULL UNIQUE,
  nama_jabatan  VARCHAR(100) NOT NULL,
  gaji_pokok    DECIMAL(15,2) NOT NULL DEFAULT 0,
  keterangan    TEXT,
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tunjangan (
  id_tunjangan   INT AUTO_INCREMENT PRIMARY KEY,
  kode_tunjangan VARCHAR(10)  NOT NULL UNIQUE,
  nama_tunjangan VARCHAR(100) NOT NULL,
  jumlah         DECIMAL(15,2) NOT NULL DEFAULT 0,
  keterangan     TEXT,
  created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Junction: tunjangan apa saja yang melekat pada sebuah jabatan
CREATE TABLE IF NOT EXISTS jabatan_tunjangan (
  id_jabatan   INT NOT NULL,
  id_tunjangan INT NOT NULL,
  PRIMARY KEY (id_jabatan, id_tunjangan),
  FOREIGN KEY (id_jabatan)   REFERENCES jabatan(id_jabatan)   ON DELETE CASCADE,
  FOREIGN KEY (id_tunjangan) REFERENCES tunjangan(id_tunjangan) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS karyawan (
  id_karyawan   INT AUTO_INCREMENT PRIMARY KEY,
  nik           VARCHAR(20)  NOT NULL UNIQUE,
  nama          VARCHAR(100) NOT NULL,
  jenis_kelamin ENUM('L','P') NOT NULL,
  tempat_lahir  VARCHAR(50),
  tanggal_lahir DATE,
  alamat        TEXT,
  no_telp       VARCHAR(20),
  email         VARCHAR(100),
  id_jabatan    INT,
  tanggal_masuk DATE,
  status        ENUM('Aktif','Non-Aktif') DEFAULT 'Aktif',
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_jabatan) REFERENCES jabatan(id_jabatan) ON DELETE SET NULL
);

-- ============ TRANSAKSI ============
CREATE TABLE IF NOT EXISTS absensi (
  id_absensi  INT AUTO_INCREMENT PRIMARY KEY,
  id_karyawan INT NOT NULL,
  tanggal     DATE NOT NULL,
  jam_masuk   TIME,
  jam_keluar  TIME,
  status      ENUM('Hadir','Izin','Sakit','Alpha') NOT NULL DEFAULT 'Hadir',
  keterangan  VARCHAR(255),
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_karyawan) REFERENCES karyawan(id_karyawan) ON DELETE CASCADE,
  UNIQUE KEY uq_absensi (id_karyawan, tanggal)  -- 1 karyawan 1 absen per hari
);

CREATE TABLE IF NOT EXISTS cuti (
  id_cuti           INT AUTO_INCREMENT PRIMARY KEY,
  id_karyawan       INT NOT NULL,
  tanggal_pengajuan DATE NOT NULL,
  tanggal_mulai     DATE NOT NULL,
  tanggal_selesai   DATE NOT NULL,
  jenis_cuti        ENUM('Tahunan','Sakit','Melahirkan','Penting','Lainnya') NOT NULL,
  lama_cuti         INT NOT NULL,           -- jumlah hari (dihitung otomatis)
  alasan            TEXT,
  status            ENUM('Pending','Disetujui','Ditolak') DEFAULT 'Pending',
  created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_karyawan) REFERENCES karyawan(id_karyawan) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS penggajian (
  id_penggajian   INT AUTO_INCREMENT PRIMARY KEY,
  id_karyawan     INT NOT NULL,
  periode_bulan   INT NOT NULL,    -- 1..12
  periode_tahun   INT NOT NULL,
  gaji_pokok      DECIMAL(15,2) NOT NULL DEFAULT 0,
  total_tunjangan DECIMAL(15,2) NOT NULL DEFAULT 0,
  potongan        DECIMAL(15,2) NOT NULL DEFAULT 0,
  total_gaji      DECIMAL(15,2) NOT NULL DEFAULT 0,
  tanggal_bayar   DATE,
  keterangan      VARCHAR(255),
  created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_karyawan) REFERENCES karyawan(id_karyawan) ON DELETE CASCADE,
  UNIQUE KEY uq_gaji (id_karyawan, periode_bulan, periode_tahun)  -- cegah dobel gaji
);
