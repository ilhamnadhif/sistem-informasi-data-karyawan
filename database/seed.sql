-- ============================================================
-- Data dummy untuk testing. Jalankan setelah schema.sql:
--   mysql -u root db_karyawan < seed.sql
-- ============================================================
USE db_karyawan;

-- Bersihkan dulu (urutan child -> parent) agar bisa di-seed ulang
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE penggajian;
TRUNCATE TABLE cuti;
TRUNCATE TABLE absensi;
TRUNCATE TABLE jabatan_tunjangan;
TRUNCATE TABLE karyawan;
TRUNCATE TABLE tunjangan;
TRUNCATE TABLE jabatan;
SET FOREIGN_KEY_CHECKS = 1;

-- ============ JABATAN ============
INSERT INTO jabatan (id_jabatan, kode_jabatan, nama_jabatan, gaji_pokok, keterangan) VALUES
  (1, 'MGR', 'Manager',   8000000, 'Pimpinan divisi'),
  (2, 'STF', 'Staff',     4000000, 'Pelaksana operasional'),
  (3, 'SPV', 'Supervisor',6000000, 'Penyelia tim');

-- ============ TUNJANGAN ============
INSERT INTO tunjangan (id_tunjangan, kode_tunjangan, nama_tunjangan, jumlah, keterangan) VALUES
  (1, 'TJB', 'Tunjangan Jabatan',  2000000, 'Tunjangan untuk pemegang jabatan'),
  (2, 'TTR', 'Tunjangan Transport',  750000, 'Bantuan transportasi'),
  (3, 'TMK', 'Tunjangan Makan',      500000, 'Uang makan bulanan'),
  (4, 'TKS', 'Tunjangan Kesehatan', 1000000, 'BPJS / asuransi');

-- ============ JABATAN_TUNJANGAN (mapping) ============
-- Manager: jabatan, transport, makan, kesehatan
INSERT INTO jabatan_tunjangan (id_jabatan, id_tunjangan) VALUES
  (1,1),(1,2),(1,3),(1,4),
-- Supervisor: jabatan, transport, makan
  (3,1),(3,2),(3,3),
-- Staff: transport, makan
  (2,2),(2,3);

-- ============ KARYAWAN ============
INSERT INTO karyawan (id_karyawan, nik, nama, jenis_kelamin, tempat_lahir, tanggal_lahir, alamat, no_telp, email, id_jabatan, tanggal_masuk, status) VALUES
  (1, 'K001', 'Andi Saputra',    'L', 'Jakarta',   '1988-03-12', 'Jl. Melati No. 1',  '081200000001', 'andi@mail.com',   1, '2015-01-05', 'Aktif'),
  (2, 'K002', 'Budi Santoso',    'L', 'Bandung',   '1990-07-22', 'Jl. Mawar No. 2',   '081200000002', 'budi@mail.com',   3, '2016-02-10', 'Aktif'),
  (3, 'K003', 'Citra Dewi',      'P', 'Surabaya',  '1992-11-30', 'Jl. Anggrek No. 3', '081200000003', 'citra@mail.com',  2, '2017-03-15', 'Aktif'),
  (4, 'K004', 'Dewi Lestari',    'P', 'Semarang',  '1993-05-18', 'Jl. Kenanga No. 4', '081200000004', 'dewi@mail.com',   2, '2018-04-20', 'Aktif'),
  (5, 'K005', 'Eko Prasetyo',    'L', 'Yogyakarta','1991-09-09', 'Jl. Dahlia No. 5',  '081200000005', 'eko@mail.com',    2, '2018-06-01', 'Aktif'),
  (6, 'K006', 'Fitri Handayani', 'P', 'Medan',     '1994-12-25', 'Jl. Flamboyan No.6','081200000006', 'fitri@mail.com',  3, '2019-08-12', 'Aktif'),
  (7, 'K007', 'Gunawan Wibowo',  'L', 'Malang',    '1989-02-14', 'Jl. Cempaka No. 7', '081200000007', 'gunawan@mail.com',2, '2019-09-23', 'Aktif'),
  (8, 'K008', 'Hesti Pratiwi',   'P', 'Solo',      '1995-04-03', 'Jl. Teratai No. 8', '081200000008', 'hesti@mail.com',  2, '2020-01-15', 'Aktif'),
  (9, 'K009', 'Irfan Maulana',   'L', 'Bekasi',    '1996-06-21', 'Jl. Seroja No. 9',  '081200000009', 'irfan@mail.com',  2, '2021-03-08', 'Non-Aktif'),
  (10,'K010', 'Joko Susilo',     'L', 'Depok',     '1987-10-17', 'Jl. Kamboja No.10', '081200000010', 'joko@mail.com',   3, '2014-11-02', 'Aktif');

-- ============ ABSENSI (periode Juni 2026, termasuk Alpha untuk uji potongan) ============
INSERT INTO absensi (id_karyawan, tanggal, jam_masuk, jam_keluar, status, keterangan) VALUES
  (1, '2026-06-01', '08:00:00', '17:00:00', 'Hadir', ''),
  (1, '2026-06-02', '08:05:00', '17:00:00', 'Hadir', ''),
  (1, '2026-06-03', NULL, NULL, 'Alpha', 'Tanpa keterangan'),
  (2, '2026-06-01', '08:00:00', '17:00:00', 'Hadir', ''),
  (2, '2026-06-02', NULL, NULL, 'Izin',  'Acara keluarga'),
  (3, '2026-06-01', '08:10:00', '17:00:00', 'Hadir', ''),
  (3, '2026-06-02', NULL, NULL, 'Sakit', 'Demam'),
  (3, '2026-06-03', NULL, NULL, 'Alpha', 'Tanpa kabar'),
  (4, '2026-06-01', '08:00:00', '16:30:00', 'Hadir', ''),
  (5, '2026-06-01', '07:55:00', '17:05:00', 'Hadir', ''),
  (5, '2026-06-02', NULL, NULL, 'Alpha', ''),
  (5, '2026-06-03', NULL, NULL, 'Alpha', '');

-- ============ CUTI (status berbeda) ============
INSERT INTO cuti (id_karyawan, tanggal_pengajuan, tanggal_mulai, tanggal_selesai, jenis_cuti, lama_cuti, alasan, status) VALUES
  (1, '2026-05-20', '2026-06-10', '2026-06-12', 'Tahunan',    3, 'Liburan keluarga',  'Disetujui'),
  (3, '2026-05-25', '2026-06-15', '2026-06-15', 'Penting',    1, 'Urusan pribadi',    'Pending'),
  (4, '2026-05-28', '2026-06-20', '2026-06-25', 'Melahirkan', 6, 'Cuti melahirkan',   'Disetujui'),
  (6, '2026-06-01', '2026-06-05', '2026-06-06', 'Sakit',      2, 'Rawat inap',        'Ditolak');

-- ============ PENGGAJIAN (periode Juni 2026) ============
-- Andi (Manager): pokok 8jt, tunjangan 4.25jt, 1 Alpha => potongan 50rb
INSERT INTO penggajian (id_karyawan, periode_bulan, periode_tahun, gaji_pokok, total_tunjangan, potongan, total_gaji, tanggal_bayar, keterangan) VALUES
  (1, 6, 2026, 8000000, 4250000,  50000, 12200000, '2026-06-28', 'Gaji Juni 2026'),
  (2, 6, 2026, 6000000, 3250000,      0,  9250000, '2026-06-28', 'Gaji Juni 2026');
