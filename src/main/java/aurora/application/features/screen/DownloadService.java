package aurora.application.features.screen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.application.sourcecode.SourceCodeUtil;
import aurora.security.ResourceNotDefinedException;

public class DownloadService {

    public static CompositeMap createZIP(IObjectRegistry registry, Integer function_id, String filename)
            throws IOException, SAXException, InstantiationException, IllegalAccessException, ClassNotFoundException,
            ParserConfigurationException, SQLException {

        if (registry == null)
            throw new RuntimeException("paramter error. 'registry' can not be null.");
        File webHome = SourceCodeUtil.getWebHome(registry);
        DataSource ds = (DataSource) registry.getInstanceOfType(DataSource.class);

        Connection conn = ds.getConnection();
        Statement stm = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        File sourceFile = new File(webHome, "");
        if (sourceFile == null || !sourceFile.exists())
            throw new ResourceNotDefinedException(".");

        String[] screens = getRegisteredScreens(stm, function_id);
        String[] bms = getRegisteredBM(stm, function_id);
        stm.close();
        conn.close();

        File of = new File(webHome, filename + ".zip");
        if (of.exists())
            of.delete();
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(of);
            zos = new ZipOutputStream(fos);
            byte[] b = new byte[1024 * 8];
            int len = -1;
            for (String s : screens) {
                ZipEntry ze = new ZipEntry(s);
                zos.putNextEntry(ze);
                FileInputStream fis = new FileInputStream(new File(webHome + "/" + s));
                while ((len = fis.read(b)) != -1) {
                    zos.write(b, 0, len);
                }
                fis.close();
            }
            for (String s : bms) {
                String e = "WEB-INF/classes/" + s.replace('.', '/') + ".bm";
                ZipEntry ze = new ZipEntry(e);
                zos.putNextEntry(ze);
                FileInputStream fis = new FileInputStream(new File(webHome + "/" + e));
                while ((len = fis.read(b)) != -1) {
                    zos.write(b, 0, len);
                }
                fis.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (zos != null)
                    zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        of.deleteOnExit();
        return new CompositeMap();
    }

    private static String[] getRegisteredScreens(Statement stm, Integer function_id) throws SQLException {
        String[] scrs = new String[0];
        String sql = "select (select s.service_name from sys_service s"
                + " where s.service_id = f.service_id) service_name from sys_function_service f"
                + " where f.function_id = " + function_id;
        ResultSet rs = stm.executeQuery(sql);
        if (rs.last()) {
            int length = rs.getRow();
            scrs = new String[length];
            rs.first();
            for (int i = 0; i < length; i++) {
                scrs[i] = rs.getString("service_name");
                rs.next();
            }
        }
        return scrs;
    }

    private static String[] getRegisteredBM(Statement stm, Integer function_id) throws SQLException {
        String[] bms = new String[0];
        String sql = "select b.bm_name from sys_function_bm_access b where b.function_id = " + function_id;
        ResultSet rs = stm.executeQuery(sql);
        if (rs.last()) {
            int length = rs.getRow();
            bms = new String[length];
            rs.first();
            for (int i = 0; i < length; i++) {
                bms[i] = rs.getString("bm_name");
                rs.next();
            }
        }
        return bms;
    }
}
