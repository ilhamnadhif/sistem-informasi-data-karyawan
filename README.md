# Aplikasi Data Karyawan

Aplikasi desktop **Java Swing** (NetBeans Ant Project) untuk mengelola data
karyawan: master data, transaksi absensi/cuti/penggajian, dan laporan PDF
(JasperReports).

Arsitektur berlapis: **Model** (POJO) → **DAO** (JDBC/PreparedStatement) →
**View** (JInternalFrame di dalam MDI `MainFrame`).

---

## 1. Prasyarat

| Komponen | Versi | Catatan |
|----------|-------|---------|
| JDK | 21+ | Project di-set `javac.source/target=25`; turunkan di Project Properties bila JDK Anda lebih lama |
| NetBeans | 17+ | Disarankan, sudah membawa Ant + GUI Builder |
| MySQL Server | 8.x | Database `db_karyawan` |
| MySQL Connector/J | 8.3.0 | Lihat `lib/README.md` |
| JasperReports | 6.21.5 | **Bukan** 7.x. Lihat `lib/README.md` |

---

## 2. Setup Database

Jalankan dua skrip ini (urut) lewat MySQL client / phpMyAdmin / NetBeans Services:

```bash
mysql -u root < database/schema.sql
mysql -u root < database/seed.sql
```

- `schema.sql` — membuat database `db_karyawan` + 7 tabel (jabatan, tunjangan,
  jabatan_tunjangan, karyawan, absensi, cuti, penggajian) beserta FK & unique key.
- `seed.sql` — data dummy untuk pengujian (termasuk baris absensi **Alpha**
  agar potongan gaji bisa diuji).

Kredensial default ada di `src/com/karyawan/util/KoneksiDB.java`
(`USER="root"`, `PASS=""`, `jdbc:mysql://localhost:3306/db_karyawan`). Sesuaikan
bila MySQL Anda memakai password.

---

## 3. Tambahkan JAR Dependency

JAR **tidak disertakan** di repo. Ikuti `lib/README.md`:

1. Salin `mysql-connector-j-8.3.0.jar` ke `lib/`.
2. Salin `jasperreports-6.21.5.jar` + **semua** `lib/*.jar` dari distribusi
   JasperReports 6.21.5 ke `lib/`.
3. NetBeans → klik kanan project → **Properties → Libraries → Add JAR/Folder**
   → pilih semua `.jar` di `lib/`.

---

## 4. Build & Run

- **NetBeans**: buka project → **Build** (F11) → **Run** (F6).
- Main class: `com.karyawan.app.Main`.
- **Ant (CLI)**: `ant clean run` (JAR di `lib/` harus sudah didaftarkan di
  `nbproject/project.properties` / via NetBeans).

> Catatan validasi: lingkungan pembuatan kode ini tidak punya JDK terpasang,
> jadi kompilasi belum diverifikasi lokal. Build pertama harus dilakukan di
> NetBeans setelah JAR ditambahkan.

---

## 5. Struktur Modul

```
src/com/karyawan/
├── app/        Main, MainFrame (MDI JFrame + menu)
├── util/       KoneksiDB, PaginationHelper, RupiahFormat, TanggalFormat, Validasi, Konstanta
├── model/      POJO: Jabatan, Tunjangan, Karyawan, Absensi, Cuti, Penggajian
├── dao/        JDBC DAO (PreparedStatement + try-with-resources)
├── view/
│   ├── master/     FrmJabatan, FrmTunjangan, FrmKaryawan
│   ├── transaksi/  FrmAbsensi, FrmCuti, FrmPenggajian
│   └── laporan/    FrmLaporanKaryawan, FrmLaporanAbsensi, FrmLaporanCuti, FrmLaporanJabatan
└── report/     JasperHelper + template .jrxml
```

Semua tabel master/transaksi memakai **pagination server-side**
(`LIMIT/OFFSET`) dengan tombol First/Prev/Next/Last, label halaman, dan dropdown
ukuran halaman (10/25/50).

---

## 6. Lokasi Template Laporan (.jrxml)

```
src/com/karyawan/report/
├── laporan_karyawan.jrxml
├── laporan_absensi.jrxml
├── laporan_cuti.jrxml
└── laporan_jabatan.jrxml
```

`JasperHelper` mencoba memuat `.jasper` precompiled dari classpath; bila tidak
ada, ia meng-compile `.jrxml` saat runtime. Edit template via **Jaspersoft
Studio** (format JasperReports 6) bila perlu mengubah tata letak laporan.

---

## 7. Catatan Pengembangan

- Form UI dibangun dengan **NetBeans GUI Builder** — pasangan `.form` + `.java`;
  kode layout hanya di blok `initComponents()` yang ter-guard.
- `FrmJabatan` adalah **form referensi**; buka dulu di GUI Builder untuk
  memvalidasi pola `.form` sebelum mengubah form lain.
- Rumus gaji: `gaji_pokok + total_tunjangan − potongan`, dengan
  `potongan = jumlah Alpha × Rp50.000` (`Konstanta.RATE_POTONGAN_ALPHA`).
