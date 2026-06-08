package com.karyawan.util;

/**
 * Helper pagination reusable. Form cukup memanggil setTotalRows() setelah
 * menghitung jumlah baris, lalu pakai getOffset()/getPageSize() untuk query.
 */
public class PaginationHelper {

    private int currentPage = 1;
    private int pageSize = 10;
    private int totalRows = 0;

    public int getOffset() {
        return (currentPage - 1) * pageSize;
    }

    public int getTotalPages() {
        // minimal 1 halaman walau data kosong
        return Math.max(1, (int) Math.ceil((double) totalRows / pageSize));
    }

    public void setTotalRows(int t) {
        this.totalRows = t;
        // jaga agar currentPage tidak melebihi total halaman (mis. setelah hapus)
        if (currentPage > getTotalPages()) {
            currentPage = getTotalPages();
        }
    }

    public boolean hasNext() {
        return currentPage < getTotalPages();
    }

    public boolean hasPrev() {
        return currentPage > 1;
    }

    public void next() {
        if (hasNext()) {
            currentPage++;
        }
    }

    public void prev() {
        if (hasPrev()) {
            currentPage--;
        }
    }

    public void first() {
        currentPage = 1;
    }

    public void last() {
        currentPage = getTotalPages();
    }

    public void reset() {
        currentPage = 1;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int p) {
        this.currentPage = Math.max(1, p);
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int size) {
        this.pageSize = Math.max(1, size);
        reset();
    }

    public int getTotalRows() {
        return totalRows;
    }

    /** Label "Halaman X dari Y" untuk ditampilkan di form. */
    public String label() {
        return "Halaman " + currentPage + " dari " + getTotalPages();
    }
}
