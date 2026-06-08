# Folder `lib/` — JAR dependency (diisi manual)

Aplikasi ini butuh JAR berikut **disalin ke folder `lib/` ini**, lalu didaftarkan
di NetBeans via **klik kanan project → Properties → Libraries → Add JAR/Folder**.

## 1. MySQL Connector/J 8.3.0  (WAJIB, untuk semua modul)
- Download: https://dev.mysql.com/downloads/connector/j/  (pilih "Platform Independent")
- Salin file: `mysql-connector-j-8.3.0.jar`

## 2. JasperReports 6.21.5  (WAJIB, untuk modul Laporan)
- Download "JasperReports 6.21.5 project distribution" dari:
  https://sourceforge.net/projects/jasperreports/files/
- Ekstrak, lalu salin ke `lib/`:
  - `dist/jasperreports-6.21.5.jar`
  - **SEMUA** isi `lib/*.jar` dari distribusi Jasper (jangan dipilih-pilih —
    cara teraman menghindari `ClassNotFoundException`). Termasuk antara lain:
    `commons-beanutils`, `commons-collections4`, `commons-digester`,
    `commons-logging`, `groovy-all`, `itext`/`openpdf`.

## Setelah JAR ditaruh
1. NetBeans → kanan project → **Properties → Libraries → Add JAR/Folder** →
   pilih semua `.jar` di folder `lib/`.
2. Build (F11). Bila muncul `ClassNotFoundException`/`NoClassDefFoundError`
   saat runtime, tambahkan JAR terkait dari distribusi Jasper — **jangan ganti versi**.

## Catatan
- Tanpa MySQL Connector, aplikasi gagal konek DB.
- Tanpa JAR Jasper, hanya modul Master & Transaksi yang bisa dipakai;
  modul Laporan akan error saat dibuka.
