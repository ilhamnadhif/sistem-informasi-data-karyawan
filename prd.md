# PRD — Aplikasi Data Karyawan Berbasis Java (NetBeans Desktop)

> Dokumen ini adalah spesifikasi untuk dikerjakan oleh **Claude Code**. Baca seluruhnya sebelum mulai. Bagian bertanda **[WAJIB]** adalah hard constraint dari pemilik project dan tidak boleh dilanggar.

---

## 0. Cara Kerja untuk Claude Code (Working Agreement)

1. **Cek project existing dulu.** Project sudah ada di NetBeans sebagai **Ant Java Application** (`build.xml` + `nbproject/`). Jangan hapus konfigurasi yang sudah ada tanpa konfirmasi; tambahkan JAR via Project Properties → Libraries, bukan ubah build manual.
2. **Kerjakan per milestone** sesuai urutan di Bagian 11. Selesaikan & pastikan compile di tiap milestone sebelum lanjut.
3. **Bikin 1 form referensi dulu** (lihat 6.1) sebelum generate semua form, supaya pola `.form` + `.java` tervalidasi.
4. **Jangan over-engineer.** Ini project kuliah; kode harus rapi, mudah dibaca, dan mudah dijelaskan saat sidang.
5. **Konfirmasi sebelum aksi destruktif** (drop tabel, hapus file existing, ganti struktur folder besar-besaran).
6. Tulis komentar berbahasa Indonesia singkat di bagian logika yang penting (validasi, rumus gaji, pagination).

---

## 1. Ringkasan & Tujuan

Aplikasi desktop untuk **manajemen data karyawan** sebuah perusahaan, mencakup pengelolaan data master, pencatatan transaksi (absensi, cuti, penggajian), dan pencetakan laporan. Aplikasi berbasis **Java Swing** dengan tampilan dibangun via **NetBeans GUI Builder (.form)**, data tersimpan di **MySQL**, dan laporan dirender pakai **JasperReports**.

**Tujuan:**
- CRUD lengkap untuk semua data master.
- Pencatatan transaksi yang saling terhubung (absensi → potongan gaji, jabatan+tunjangan → komponen gaji).
- Laporan profesional yang bisa ditampilkan & diekspor ke PDF.

---

## 2. Ruang Lingkup

**In scope:**
- **Master:** Data Karyawan, Data Jabatan, Data Tunjangan.
- **Transaksi:** Absensi Karyawan, Cuti Karyawan, Penggajian Karyawan.
- **Laporan:** Laporan Data Karyawan, Laporan Absensi, Laporan Cuti, Laporan Jabatan.
- Pagination di semua tabel modul master & transaksi.
- Laporan via JasperReports + export PDF.

**Out of scope (opsional, lihat Bagian 13):** Login/multi-user role, audit log, backup otomatis, export Excel.

---

## 3. Tech Stack & Dependencies

| Komponen | Pilihan | Catatan |
|---|---|---|
| Bahasa | **JDK 21 (LTS)** | Target stabil + modern; cocok NetBeans GUI Builder, Swing, & Jasper 6.x. Hindari maksa ke 7.x/Jakarta yang berisiko buat deadline |
| UI | Java Swing + **NetBeans GUI Builder (.form)** | [WAJIB] lihat 6.1 |
| Database | **MySQL 8.x** (DB: `db_karyawan`) | |
| Driver | **MySQL Connector/J 8.3.0** (`mysql-connector-j-8.3.0.jar`) | Artifact lama `mysql-connector-java` sudah deprecated |
| Laporan | **JasperReports 6.21.5** | [WAJIB] hanya untuk tabel laporan. **BUKAN 7.x** (lihat alasan di bawah) |
| Build | **Ant** (NetBeans Ant Java Application) | JAR ditaruh di `lib/` & didaftarkan via Project Properties → Libraries |
| Desain laporan | **Jaspersoft Studio 6.21.x** | Versi Studio harus cocok dengan versi library (6.21.x) agar format `.jrxml` kompatibel |

> **Kenapa JasperReports 6.21.5, bukan 7.x?** JasperReports 7.x **menghapus dukungan build Ant** dan dipecah jadi banyak JAR modular (setup manual jauh lebih ribet), butuh **Spring 6 / Java 17+ / basis Jakarta**, dan **format `.jrxml`/`.jasper`-nya tidak kompatibel** dengan versi 6 ke bawah (mayoritas tutorial Indonesia pakai 6.x). Untuk project Ant + kuliah, 6.21.5 jauh lebih praktis & aman.

**Cara menyiapkan dependency (Ant — manual JAR):**
1. Download **JasperReports 6.21.5 project distribution** dari SourceForge (`https://sourceforge.net/projects/jasperreports/files/`) — paket ini berisi folder `dist/` (core JAR) dan `lib/` (semua JAR dependensi). Ekstrak.
2. Download **MySQL Connector/J 8.3.0** (`mysql-connector-j-8.3.0.jar`) dari `https://dev.mysql.com/downloads/connector/j/`.
3. Salin ke folder `lib/` project:
   - `dist/jasperreports-6.21.5.jar`
   - **semua** isi `lib/*.jar` dari distribusi Jasper (jangan dipilih-pilih — cara teraman menghindari `ClassNotFoundException`)
   - `mysql-connector-j-8.3.0.jar`
4. Di NetBeans: **klik kanan project → Properties → Libraries → Add JAR/Folder** → pilih semua JAR di `lib/`.

> **JAR inti yang minimal wajib ada** (jika ingin ramping, tapi disarankan ambil semua): `jasperreports-6.21.5.jar`, `commons-beanutils`, `commons-collections4`, `commons-digester`, `commons-logging`, `groovy-all` (compiler ekspresi), serta `itext`/`openpdf` (untuk export PDF). Jika muncul `ClassNotFoundException` saat runtime, tambahkan JAR terkait dari folder `lib/`.

> **Catatan format laporan:** desain `.jrxml` pakai **Jaspersoft Studio 6.21.x**, lalu compile ke `.jasper`. Claude Code boleh generate `.jrxml` (format v6) sebagai draft awal, lalu di-refine/compile di Studio. Jangan campur dengan Studio 7 (format beda).

---

## 4. Struktur Proyek & Arsitektur

Pakai arsitektur berlapis: **Model → DAO → View**, dengan util terpisah.

```
src/com/karyawan/                          # (Ant) source langsung di bawah src/, bukan src/main/java
├── app/
│   ├── Main.java              # entry point
│   └── MainFrame.java/.form   # JFrame utama (MDI: JDesktopPane + menu bar)
├── model/                     # POJO entity
│   ├── Karyawan.java
│   ├── Jabatan.java
│   ├── Tunjangan.java
│   ├── Absensi.java
│   ├── Cuti.java
│   └── Penggajian.java
├── dao/                       # akses DB (JDBC + PreparedStatement)
│   ├── KaryawanDAO.java
│   ├── JabatanDAO.java
│   ├── TunjanganDAO.java
│   ├── AbsensiDAO.java
│   ├── CutiDAO.java
│   └── PenggajianDAO.java
├── view/
│   ├── master/                # FrmKaryawan, FrmJabatan, FrmTunjangan (.java + .form)
│   ├── transaksi/             # FrmAbsensi, FrmCuti, FrmPenggajian (.java + .form)
│   └── laporan/               # FrmLaporanKaryawan, ...Absensi, ...Cuti, ...Jabatan (.java + .form)
├── util/
│   ├── KoneksiDB.java         # singleton koneksi
│   ├── PaginationHelper.java  # helper pagination reusable
│   ├── RupiahFormat.java      # format mata uang
│   └── Validasi.java          # validasi input umum
└── report/                    # integrasi Jasper + template laporan
    ├── JasperHelper.java      # fill report + tampilkan viewer + export PDF
    ├── laporan_karyawan.jrxml  (+ .jasper hasil compile)
    ├── laporan_absensi.jrxml   (+ .jasper)
    ├── laporan_cuti.jrxml      (+ .jasper)
    └── laporan_jabatan.jrxml   (+ .jasper)

lib/                           # SEMUA JAR di sini (Jasper 6.21.5 dist+lib + mysql-connector-j-8.3.0)
database/
├── schema.sql                 # DDL semua tabel
└── seed.sql                   # data dummy untuk testing
```
> **Catatan Ant:** source langsung di bawah `src/` (bukan `src/main/java`); package tetap `com.karyawan.*`. Template `.jrxml`/`.jasper` ditaruh **di dalam package** (`src/com/karyawan/report/`) supaya ikut ter-copy ke `build/classes` dan bisa di-load sebagai classpath resource: `getClass().getResourceAsStream("/com/karyawan/report/laporan_x.jasper")`. Alternatif lebih simpel buat pemula: folder `reports/` di root project, di-load via path file relatif.

---

## 5. Desain Database

**ERD ringkas:**
- `jabatan` 1—N `karyawan`
- `jabatan` N—N `tunjangan` (via `jabatan_tunjangan`)
- `karyawan` 1—N `absensi`, 1—N `cuti`, 1—N `penggajian`

### `database/schema.sql`
```sql
CREATE DATABASE IF NOT EXISTS db_karyawan
  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE db_karyawan;

-- ============ MASTER ============
CREATE TABLE jabatan (
  id_jabatan    INT AUTO_INCREMENT PRIMARY KEY,
  kode_jabatan  VARCHAR(10)  NOT NULL UNIQUE,
  nama_jabatan  VARCHAR(100) NOT NULL,
  gaji_pokok    DECIMAL(15,2) NOT NULL DEFAULT 0,
  keterangan    TEXT,
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tunjangan (
  id_tunjangan   INT AUTO_INCREMENT PRIMARY KEY,
  kode_tunjangan VARCHAR(10)  NOT NULL UNIQUE,
  nama_tunjangan VARCHAR(100) NOT NULL,
  jumlah         DECIMAL(15,2) NOT NULL DEFAULT 0,
  keterangan     TEXT,
  created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Junction: tunjangan apa saja yang melekat pada sebuah jabatan
CREATE TABLE jabatan_tunjangan (
  id_jabatan   INT NOT NULL,
  id_tunjangan INT NOT NULL,
  PRIMARY KEY (id_jabatan, id_tunjangan),
  FOREIGN KEY (id_jabatan)   REFERENCES jabatan(id_jabatan)   ON DELETE CASCADE,
  FOREIGN KEY (id_tunjangan) REFERENCES tunjangan(id_tunjangan) ON DELETE CASCADE
);

CREATE TABLE karyawan (
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
CREATE TABLE absensi (
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

CREATE TABLE cuti (
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

CREATE TABLE penggajian (
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
```

### `database/seed.sql`
Sediakan minimal: 3 jabatan, 4 tunjangan, mapping `jabatan_tunjangan`, 8–10 karyawan, beberapa baris absensi (termasuk minimal 1–2 Alpha untuk uji potongan), dan beberapa pengajuan cuti dengan status berbeda. Tujuannya agar semua laporan & rumus penggajian bisa langsung diuji.

---

## 6. ATURAN WAJIB (Hard Constraints)

### 6.1 Tampilan = NetBeans GUI Builder (.form) **[WAJIB]**
- **Tampilan TIDAK boleh di-hardcode.** Semua layout komponen Swing harus dihasilkan lewat mekanisme NetBeans Form, yaitu setiap form punya **2 file**: `NamaForm.java` + `NamaForm.form`.
- Layout komponen **hanya** berada di dalam blok guarded `initComponents()`:
  ```java
  // <editor-fold defaultstate="collapsed" desc="Generated Code">
  private void initComponents() { ... }
  // </editor-fold>
  ```
  dan blok deklarasi variabel:
  ```java
  // Variables declaration - do not modify
  private javax.swing.JTable tblData;
  // End of variables declaration
  ```
- **Dilarang menulis kode layout** (`new GroupLayout(...)`, `setBounds`, `add(...)`, dll) di luar blok guarded. Logika kustom (event handler isi, query, validasi) ditulis di method terpisah dan dipanggil dari event yang dibuat lewat form.
- Pakai **GroupLayout** (default NetBeans). Penataan harus konsisten dengan isi file `.form`.

**Alur kerja yang WAJIB diikuti (penting):**
1. **M0 — buat 1 form referensi** (`FrmJabatan`) lengkap pasangan `.form` + `.java`.
2. **Owner membuka form ini di NetBeans GUI Builder** untuk memastikan terbaca & ter-render. NetBeans akan me-regenerate `initComponents()` dari `.form` bila perlu.
3. Setelah pola tervalidasi, **replikasi pola yang sama** ke semua form lain.
4. Jika suatu `.form` gagal dibaca NetBeans, `.java` tetap harus bisa compile & jalan; owner bisa menyusun ulang form tsb secara visual.

> Catatan untuk Claude Code: file `.form` adalah XML standar NetBeans (root `<Form>` versi `1.x`, berisi `<NonVisualComponents>`, `<Container>`/`<SubComponents>`, `<Layout>` GroupLayout, `<Properties>`, `<Events>`). Pastikan nama variabel, tipe komponen, dan event di `.form` **identik** dengan yang ada di `initComponents()` pada `.java`.

### 6.2 Pagination di semua tabel **[WAJIB]**
- **Setiap `JTable`** di modul **master & transaksi** wajib pakai pagination (server-side, via `LIMIT ? OFFSET ?`). **Bukan** load semua row sekaligus.
- Tiap form bertabel menyediakan: tombol **First («), Prev (‹), Next (›), Last (»)**, label **"Halaman X dari Y"**, dan dropdown **page size** (default 10; opsi 10/25/50).
- Tersedia field **pencarian (search)**; saat search berubah, balik ke **halaman 1** dan jumlah halaman dihitung ulang.
- Implementasi terpusat di `util/PaginationHelper.java` (lihat 10.2). DAO menyediakan `findPaged(page, size, keyword)` dan `count(keyword)`.

### 6.3 JasperReports **[WAJIB]**
- **Tabel pada LAPORAN wajib pakai JasperReports.** Modul laporan **tidak** menampilkan `JTable`; "tabel" laporan adalah detail band Jasper.
- **Jasper HANYA untuk tabel laporan.** Modul master & transaksi tetap pakai `JTable` + pagination (jangan pakai Jasper di sana).
- Tiap laporan: template `.jrxml` (boleh di-generate Claude Code), di-compile/`fill` saat runtime, ditampilkan via `JasperViewer` (atau `JRViewer` yang ditempel di panel form laporan), dan bisa **export PDF**.

### 6.4 Konvensi Kode
- DAO **wajib** `PreparedStatement` (cegah SQL injection), `try-with-resources` untuk `Connection`/`Statement`/`ResultSet`.
- Tampilkan error ke user via `JOptionPane`; jangan biarkan stack trace bocor ke UI tanpa pesan ramah.
- Format uang pakai `RupiahFormat` (`Rp1.500.000`), tanggal `dd-MM-yyyy`.
- Penamaan: class `PascalCase`, method/variabel `camelCase`, tabel/kolom DB `snake_case`.

---

## 7. Modul Master

Pola umum tiap form master: panel input (kiri/atas) + `JTable` berpaginasi (kanan/bawah) + tombol **Tambah, Simpan, Ubah, Hapus, Bersihkan**. Klik baris tabel → isi field untuk edit. Validasi sebelum simpan.

### 7.1 Data Jabatan — `FrmJabatan`
- **Field:** Kode Jabatan, Nama Jabatan, Gaji Pokok, Keterangan.
- **Validasi:** kode & nama wajib; kode unik; gaji_pokok numerik ≥ 0.
- **Tabel (pagination):** Kode | Nama Jabatan | Gaji Pokok (Rupiah) | Keterangan.
- **Fitur khusus:** tombol/panel **"Atur Tunjangan"** untuk mengelola mapping `jabatan_tunjangan` (pilih tunjangan mana yang melekat ke jabatan ini, mis. lewat daftar checkbox/dual-list). Ini sumber data komponen tunjangan saat penggajian.

### 7.2 Data Tunjangan — `FrmTunjangan`
- **Field:** Kode Tunjangan, Nama Tunjangan, Jumlah, Keterangan.
- **Validasi:** kode & nama wajib; kode unik; jumlah numerik ≥ 0.
- **Tabel (pagination):** Kode | Nama Tunjangan | Jumlah (Rupiah) | Keterangan.

### 7.3 Data Karyawan — `FrmKaryawan`
- **Field:** NIK, Nama, Jenis Kelamin (combo L/P), Tempat Lahir, Tanggal Lahir (date picker / `JFormattedTextField`), Alamat, No. Telp, Email, **Jabatan (combo dari tabel jabatan)**, Tanggal Masuk, Status (combo Aktif/Non-Aktif).
- **Validasi:** NIK & Nama wajib; NIK unik; email format valid (jika diisi); jabatan wajib dipilih.
- **Tabel (pagination):** NIK | Nama | JK | Jabatan | Tgl Masuk | Status | No. Telp.
- **Search:** by NIK / nama / nama jabatan.
- **Catatan:** combo Jabatan menampilkan `nama_jabatan` tapi menyimpan `id_jabatan`.

---

## 8. Modul Transaksi

### 8.1 Absensi Karyawan — `FrmAbsensi`
- **Field:** Karyawan (combo, tampil NIK - Nama), Tanggal, Jam Masuk, Jam Keluar, Status (Hadir/Izin/Sakit/Alpha), Keterangan.
- **Validasi:** karyawan & tanggal wajib; cegah duplikat (1 karyawan 1 absen/tanggal — manfaatkan unique key, tangkap error & kasih pesan ramah).
- **Tabel (pagination):** Tanggal | NIK | Nama | Jam Masuk | Jam Keluar | Status | Keterangan.
- **Search/filter:** by tanggal & by karyawan.

### 8.2 Cuti Karyawan — `FrmCuti`
- **Field:** Karyawan (combo), Tanggal Pengajuan, Tanggal Mulai, Tanggal Selesai, Jenis Cuti (combo), Alasan, Status (Pending/Disetujui/Ditolak).
- **Logika:** `lama_cuti` dihitung otomatis = selisih hari (tanggal_selesai − tanggal_mulai + 1). Validasi tanggal_selesai ≥ tanggal_mulai.
- **Tabel (pagination):** Nama | Jenis | Tgl Mulai | Tgl Selesai | Lama (hari) | Status.
- **Fitur:** ubah status (Setujui / Tolak).

### 8.3 Penggajian Karyawan — `FrmPenggajian`
- **Field:** Karyawan (combo), Periode (Bulan + Tahun), Gaji Pokok (auto), Total Tunjangan (auto), Potongan (auto, bisa override), Total Gaji (auto), Tanggal Bayar, Keterangan.
- **Logika perhitungan otomatis saat karyawan + periode dipilih:**
  - `gaji_pokok` = `jabatan.gaji_pokok` milik karyawan tsb.
  - `total_tunjangan` = `SUM(tunjangan.jumlah)` untuk semua tunjangan yang melekat pada jabatan karyawan (via `jabatan_tunjangan`).
  - `potongan` = `jumlah_Alpha_pada_periode × RATE_POTONGAN_ALPHA` (konstanta, default `50000`), **bisa di-override manual**.
  - `total_gaji` = `gaji_pokok + total_tunjangan − potongan`.
  - Field gaji_pokok/total_tunjangan/total_gaji bersifat read-only (terisi otomatis).
- **Validasi:** cegah dobel penggajian untuk (karyawan, bulan, tahun) — manfaatkan unique key.
- **Tabel (pagination):** Periode | NIK | Nama | Gaji Pokok | Tunjangan | Potongan | Total Gaji | Tgl Bayar.
- **Search/filter:** by periode & karyawan.
- **Opsional:** tombol "Cetak Slip" (boleh ditandai opsional, pakai Jasper jika dikerjakan).

---

## 9. Modul Laporan (JasperReports) **[WAJIB pakai Jasper]**

Pola tiap form laporan: panel **filter** + tombol **"Tampilkan"** dan **"Export PDF"**. Saat ditampilkan → `JasperFillManager.fillReport(...)` dengan parameter & koneksi DB → render ke `JRViewer`/`JasperViewer`.

**Helper render** (`com/karyawan/report/JasperHelper.java`): load `.jasper` (atau compile `.jrxml` → `.jasper` saat runtime via `JasperCompileManager` bila perlu), fill dengan `Map<String,Object> params` + `Connection` dari `KoneksiDB`, lalu tampilkan via `JasperViewer.viewReport(print, false)` (atau tempel `JRViewer` di panel form). Export PDF pakai `JasperExportManager.exportReportToPdfFile(...)`.
- **Load template (Ant/classpath):** `JasperReport jr = (JasperReport) JRLoader.loadObject(getClass().getResourceAsStream("/com/karyawan/report/laporan_x.jasper"));` lalu `JasperPrint print = JasperFillManager.fillReport(jr, params, conn);`
- **Versi & format:** semua `.jrxml` pakai format **JasperReports 6.21.x** (didesain di Jaspersoft Studio 6.21.x). Pre-compile ke `.jasper` lebih disarankan agar runtime lebih ringan (tak perlu JAR compiler). Claude Code boleh men-generate draft `.jrxml` v6, owner refine + compile di Studio.

### 9.1 Laporan Data Karyawan — `laporan_karyawan.jrxml`
- **Filter:** Jabatan (combo, opsi "Semua"), Status (opsi "Semua").
- **Parameter Jasper:** `$P{id_jabatan}` (nullable), `$P{status}` (nullable).
- **Kolom:** No | NIK | Nama | JK | Jabatan | Tgl Masuk | Status | No. Telp.
- **Query:** `karyawan` JOIN `jabatan`, filter opsional berdasarkan parameter.

### 9.2 Laporan Absensi — `laporan_absensi.jrxml`
- **Filter:** Tanggal Mulai, Tanggal Selesai, Karyawan (opsi "Semua").
- **Parameter:** `$P{tgl_mulai}`, `$P{tgl_selesai}`, `$P{id_karyawan}` (nullable).
- **Kolom:** Tanggal | NIK | Nama | Jam Masuk | Jam Keluar | Status | Keterangan.
- **Summary:** rekap jumlah per status (Hadir/Izin/Sakit/Alpha) di group/summary band.

### 9.3 Laporan Cuti — `laporan_cuti.jrxml`
- **Filter:** Tanggal Mulai, Tanggal Selesai, Status (opsi "Semua").
- **Parameter:** `$P{tgl_mulai}`, `$P{tgl_selesai}`, `$P{status}` (nullable).
- **Kolom:** Nama | Jenis Cuti | Tgl Mulai | Tgl Selesai | Lama (hari) | Status | Alasan.

### 9.4 Laporan Jabatan — `laporan_jabatan.jrxml`
- **Filter:** tidak ada (atau opsional).
- **Kolom:** Kode | Nama Jabatan | Gaji Pokok | Jumlah Karyawan | Keterangan.
- **Query:** `jabatan` LEFT JOIN `karyawan` dengan `COUNT(karyawan)` di-group per jabatan.

---

## 10. Komponen Reusable

### 10.1 `util/KoneksiDB.java`
- Singleton koneksi MySQL (`jdbc:mysql://localhost:3306/db_karyawan?useSSL=false&serverTimezone=Asia/Jakarta`).
- Kredensial bisa di-hardcode dulu (root / kosong) atau dibaca dari `config.properties`. Sediakan method `getConnection()`.

### 10.2 `util/PaginationHelper.java`
Helper agar logika pagination tidak ditulis ulang di tiap form. Contoh kontrak:
```java
public class PaginationHelper {
    private int currentPage = 1;
    private int pageSize = 10;
    private int totalRows = 0;

    public int getOffset() { return (currentPage - 1) * pageSize; }
    public int getTotalPages() {
        return (int) Math.ceil((double) totalRows / pageSize);
    }
    public void setTotalRows(int t) { this.totalRows = t; }
    public boolean hasNext() { return currentPage < getTotalPages(); }
    public boolean hasPrev() { return currentPage > 1; }
    public void next()  { if (hasNext()) currentPage++; }
    public void prev()  { if (hasPrev()) currentPage--; }
    public void first() { currentPage = 1; }
    public void last()  { currentPage = Math.max(1, getTotalPages()); }
    public void reset() { currentPage = 1; }
    // getters/setters page & size...
    public String label() { // "Halaman X dari Y"
        return "Halaman " + currentPage + " dari " + Math.max(1, getTotalPages());
    }
}
```
Tiap form: panggil `dao.count(keyword)` → `helper.setTotalRows(...)`, lalu `dao.findPaged(helper.getCurrentPage(), helper.getPageSize(), keyword)` untuk isi tabel; update label & enable/disable tombol berdasarkan `hasNext/hasPrev`.

### 10.3 `util/RupiahFormat.java`
- `format(double)` → `Rp1.500.000` (pakai `NumberFormat`/`DecimalFormat` locale `id-ID`).
- `parse(String)` → `double` untuk membaca input bila perlu.

### 10.4 `util/Validasi.java`
- Helper: `isKosong(JTextField)`, `isEmailValid(String)`, `isNumber(String)`, dll. Kembalikan pesan error yang ramah.

---

## 11. Build Order / Milestones

> Pastikan **compile & jalan** di akhir tiap milestone sebelum lanjut.

**M0 — Fondasi**
- Setup dependency **Ant**: taruh `jasperreports-6.21.5.jar` + semua `lib/*.jar` dari distribusi Jasper 6.21.5 + `mysql-connector-j-8.3.0.jar` ke folder `lib/`, daftarkan via Project Properties → Libraries.
- `database/schema.sql` + `seed.sql` (jalankan di MySQL).
- `KoneksiDB`, `RupiahFormat`, `Validasi`, `PaginationHelper`.
- `MainFrame` (MDI: `JDesktopPane` + menu bar Master/Transaksi/Laporan) + `Main.java`.
- **Form referensi `FrmJabatan`** (pasangan `.form` + `.java`) lengkap CRUD + pagination → **minta owner validasi di NetBeans**.

**M1 — Master**
- `FrmTunjangan` (CRUD + pagination).
- Mapping `jabatan_tunjangan` di `FrmJabatan` (panel "Atur Tunjangan").
- `FrmKaryawan` (CRUD + pagination + combo jabatan).

**M2 — Transaksi**
- `FrmAbsensi` → `FrmCuti` (auto `lama_cuti`, ubah status) → `FrmPenggajian` (rumus gaji otomatis + potongan dari Alpha).

**M3 — Laporan (Jasper)**
- Generate 4 `.jrxml` + helper render + 4 form laporan (filter + Tampilkan + Export PDF).

**M4 — Polish**
- Format Rupiah/tanggal konsisten, handling error, ikon/menu rapi, `README.md` (cara setup DB, jalanin, lokasi `.jrxml`).

---

## 12. Acceptance Criteria (Definition of Done)

- [ ] Semua **3 master + 3 transaksi + 4 laporan** berfungsi.
- [ ] CRUD master & transaksi jalan (tambah/ubah/hapus/cari) tanpa error.
- [ ] **Setiap tabel master & transaksi berpaginasi** (tombol First/Prev/Next/Last, label halaman, search reset ke hal. 1) — **bukan** load all rows.
- [ ] **Semua tabel laporan dirender JasperReports** dan **bisa export PDF**; Jasper **tidak** dipakai di luar laporan.
- [ ] **Setiap form punya file `.form`** dan layout dibangun via GUI Builder (tidak ada kode layout di luar blok guarded). `FrmJabatan` referensi terbukti bisa dibuka di NetBeans.
- [ ] Penggajian menghitung `gaji_pokok + total_tunjangan − potongan` dengan benar; potongan dari jumlah Alpha; tidak bisa dobel per periode.
- [ ] Cuti menghitung `lama_cuti` otomatis; absensi mencegah duplikat per tanggal.
- [ ] DAO pakai `PreparedStatement` + `try-with-resources`; error tampil via `JOptionPane`.
- [ ] `schema.sql` + `seed.sql` tersedia; aplikasi jalan end-to-end dengan data dummy.
- [ ] `README.md` berisi langkah setup & menjalankan.

---

## 13. Catatan & Pengembangan Opsional

- **Login/role** (Admin/HRD) — bisa ditambah sebagai modul tambahan jika diminta.
- **Slip gaji PDF** per karyawan (Jasper) — opsional di modul penggajian.
- **Export Excel** untuk data master — opsional.
- **Soft delete** karyawan: alih-alih hapus fisik, set `status='Non-Aktif'` agar histori absensi/penggajian tetap utuh (disarankan; saat ini FK absensi/penggajian `ON DELETE CASCADE` — pertimbangkan diganti `RESTRICT` jika ingin mencegah kehilangan histori).
- **Konstanta** seperti `RATE_POTONGAN_ALPHA` sebaiknya ditaruh di satu tempat (mis. `config.properties` atau kelas konstanta) agar mudah diubah.

---

### Keputusan teknis (sudah dikonfirmasi owner)
1. Database: **MySQL** (DB `db_karyawan`). ✅
2. Build: **Ant** (NetBeans Ant Java Application) — JAR manual di `lib/`. ✅
3. Relasi **`jabatan_tunjangan` (many-to-many)** + **rumus gaji** di 8.3 dipakai. ✅
4. Target **JDK 21 (LTS)**; **JasperReports 6.21.5** (bukan 7.x, demi kompatibilitas Ant & format `.jrxml`). ✅

> Jika di tengah jalan ada JAR Jasper yang kurang (`ClassNotFoundException` / `NoClassDefFoundError`), tambahkan JAR yang relevan dari folder `lib/` distribusi Jasper — jangan ganti versi library.
