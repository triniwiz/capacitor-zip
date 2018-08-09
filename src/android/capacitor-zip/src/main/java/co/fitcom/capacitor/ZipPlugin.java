package co.fitcom.capacitor;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;


@NativePlugin()
public class ZipPlugin extends Plugin {

    @PluginMethod(returnType = PluginMethod.RETURN_CALLBACK)
    public void zip(PluginCall call) {

        String source = call.getString("source", "");
        String destination = call.getString("destination", "");
        Boolean overwrite = call.getBoolean("overwrite", true);
        String password = call.getString("password");

        if (source.contains("_capacitor_")) {
            source = source.replace("_capacitor_", "file://");
        }

        File archive = new File(source);
        if (!archive.exists()) {
            call.reject("File does not exist, invalid archive path: " + archive.getAbsolutePath());
        }

        try {
            ZipFile zipFile = new ZipFile(archive);
            if (zipFile.isEncrypted() && !password.equals("")) {
                zipFile.setPassword(password);
            }

            zipFile.extractAll(destination);
            zipFile.setRunInThread(true);
            ProgressMonitor monitor = zipFile.getProgressMonitor();
            int progress = 0;
            JSObject statusObject = new JSObject();
            while (monitor.getState() == ProgressMonitor.STATE_BUSY) {
                progress = monitor.getPercentDone();
                statusObject.put("status", "progressing");
                statusObject.put("progress", progress);
                statusObject.put("completed", false);
                call.success(statusObject);
            }

            int result = monitor.getResult();

            switch (result) {
                case ProgressMonitor.RESULT_SUCCESS:
                    JSObject object = new JSObject();
                    object.put("status", "completed");
                    call.success(object);
                    break;
                case ProgressMonitor.RESULT_ERROR:
                    call.error(monitor.getException().getMessage());
                    break;
                case ProgressMonitor.RESULT_CANCELLED:
                    call.error("Cancelled");
            }
        } catch (ZipException e) {
            call.error(e.getLocalizedMessage());
        }

    }

    @PluginMethod(returnType = PluginMethod.RETURN_CALLBACK)
    public void unZip(PluginCall call) {
        String source = call.getString("source", "");
        String destination = call.getString("destination", "");
        Boolean overwrite = call.getBoolean("overwrite", true);
        String password = call.getString("password");

        File folder = new File(source);
        if (!folder.exists()) {
            call.reject("Folder does not exist, invalid folder path: " + folder.getAbsolutePath());
        }

        File dest = new File(destination);

        if (overwrite && dest.exists()) {
            Boolean deleted = dest.delete();
        }

        try {
            ZipFile zipFile = new ZipFile(dest);
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            zipFile.createZipFileFromFolder(folder, parameters, false, 0);
            zipFile.setRunInThread(true);
            int progress = 0;
            JSObject statusObject = new JSObject();
            if (!password.isEmpty()) {
                zipFile.setPassword(password);
            }
            ProgressMonitor monitor = zipFile.getProgressMonitor();

            while (monitor.getState() == ProgressMonitor.STATE_BUSY) {
                progress = monitor.getPercentDone();
                statusObject.put("status", "progressing");
                statusObject.put("progress", progress);
                statusObject.put("completed", false);
                call.success(statusObject);
            }


            int result = monitor.getResult();
            switch (result) {
                case ProgressMonitor.RESULT_SUCCESS:
                    JSObject object = new JSObject();
                    object.put("status", "completed");
                    call.success(object);
                    break;
                case ProgressMonitor.RESULT_ERROR:
                    call.error(monitor.getException().getMessage());
                    break;
                case ProgressMonitor.RESULT_CANCELLED:
                    call.error("cancelled");
            }

        } catch (ZipException e) {
            e.printStackTrace();
        }

    }
}
