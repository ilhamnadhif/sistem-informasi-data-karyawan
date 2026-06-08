package com.karyawan.report;

import com.karyawan.util.KoneksiDB;
import java.awt.Component;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.swing.JRViewer;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Helper render JasperReports: load template dari classpath, fill dengan
 * parameter + koneksi DB, lalu tampilkan via viewer atau export ke PDF.
 */
public class JasperHelper {

    /** Lokasi package resource template laporan. */
    private static final String BASE_PATH = "/com/karyawan/report/";

    private JasperHelper() {
    }

    /**
     * Load template laporan. Coba .jasper precompiled dulu; bila tidak ada,
     * compile .jrxml saat runtime.
     */
    private static JasperReport loadReport(String namaResource) throws JRException {
        // 1) coba .jasper precompiled (lebih ringan, tak butuh compiler)
        InputStream jasperStream = JasperHelper.class.getResourceAsStream(
            BASE_PATH + namaResource + ".jasper");
        if (jasperStream != null) {
            return (JasperReport) JRLoader.loadObject(jasperStream);
        }
        // 2) fallback: compile .jrxml saat runtime
        InputStream jrxmlStream = JasperHelper.class.getResourceAsStream(
            BASE_PATH + namaResource + ".jrxml");
        if (jrxmlStream == null) {
            throw new JRException("Template laporan tidak ditemukan: " + namaResource);
        }
        return JasperCompileManager.compileReport(jrxmlStream);
    }

    /**
     * Fill laporan & tampilkan di JasperViewer.
     *
     * @param namaResource nama file tanpa ekstensi (mis. "laporan_karyawan")
     * @param params       parameter laporan ($P{...})
     */
    public static void tampilkan(String namaResource, Map<String, Object> params)
            throws JRException, SQLException {
        JasperReport jr = loadReport(namaResource);
        Connection conn = KoneksiDB.getConnection();
        JasperPrint print = JasperFillManager.fillReport(jr, params, conn);
        // false = jangan exit aplikasi saat viewer ditutup
        JasperViewer.viewReport(print, false);
    }

    /**
     * Fill laporan lalu tampilkan preview PDF. Viewer dibuka dalam mode
     * "fit page" (satu halaman penuh terlihat); user bisa menyimpan ke file
     * lewat tombol save pada toolbar viewer.
     */
    public static void exportPdf(String namaResource, Map<String, Object> params, Component parent) {
        try {
            JasperReport jr = loadReport(namaResource);
            Connection conn = KoneksiDB.getConnection();
            JasperPrint print = JasperFillManager.fillReport(jr, params, conn);

            JRViewer viewer = new JRViewer(print);
            JFrame frame = new JFrame("Preview Laporan");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.getContentPane().add(viewer);
            frame.setSize(760, 880);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            // set zoom "fit page" setelah viewer punya ukuran nyata
            SwingUtilities.invokeLater(viewer::setFitPageZoomRatio);
        } catch (JRException | SQLException ex) {
            JOptionPane.showMessageDialog(parent,
                "Gagal menampilkan laporan: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
