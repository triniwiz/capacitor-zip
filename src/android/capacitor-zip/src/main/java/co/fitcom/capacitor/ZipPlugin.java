package co.fitcom.capacitor;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.util.List;


@NativePlugin()
public class ZipPlugin extends Plugin {

    @PluginMethod(returnType = PluginMethod.RETURN_CALLBACK)
    public void unZip(PluginCall call) {

        String source = call.getString("source", "");
        String destination = call.getString("destination", "");
        Boolean overwrite = call.getBoolean("overwrite", true);
        String password = call.getString("password");

        if (source.contains("_capacitor_")) {
            source = source.replace("_capacitor_", "");
        }

        if (source.contains("file://")) {
            source = source.replace("file://", "");
        }

        if (destination.contains("_capacitor_")) {
            destination = destination.replace("_capacitor_", "");
        }

        if (destination.contains("file://")) {
            destination = destination.replace("file://", "");
        }

        File archive = new File(source);
        if (!archive.exists()) {
            call.reject("File does not exist, invalid archive path: " + archive.getAbsolutePath());
        }

        try {
            ZipFile zipFile = new ZipFile(archive);
            zipFile.setRunInThread(true);
            if (zipFile.isEncrypted() && !password.equals("")) {
                zipFile.setPassword(password);
            }

            File d = new File(destination);

            if (!d.exists()) {
                d.mkdirs();
            }
            List<FileHeader> fileHeaders = zipFile.getFileHeaders();
            for (FileHeader header : fileHeaders) {
                if (header.isDirectory()) {
                    if (d.exists()) {
                        File f = new File(destination, header.getFileName());
                        f.mkdirs();
                        zipFile.extractFile(header, f.toString());
                    }
                }
            }
            ProgressMonitor monitor = zipFile.getProgressMonitor();
            int progress;
            JSObject statusObject = new JSObject();
            zipFile.extractAll(destination);
            while (monitor.getState() == ProgressMonitor.STATE_BUSY) {
                progress = monitor.getPercentDone();
                statusObject.put("status", "progressing");
                statusObject.put("progress", progress);
                statusObject.put("completed", false);
                call.resolve(statusObject);
            }

            int result = monitor.getResult();
            switch (result) {
                case ProgressMonitor.RESULT_SUCCESS:
                    JSObject object = new JSObject();
                    object.put("status", "completed");
                    object.put("completed", true);
                    object.put("progress", 100);
                    object.put("path", destination);
                    call.resolve(object);
                    break;
                case ProgressMonitor.RESULT_ERROR:
                    call.error(monitor.getException().getMessage());
                    break;
                case ProgressMonitor.RESULT_CANCELLED:
                    call.error("Cancelled");
            }
        } catch (ZipException e) {
            call.error(e.getMessage());
        }

    }

    @PluginMethod(returnType = PluginMethod.RETURN_CALLBACK)
    public void zip(PluginCall call) {
        String source = call.getString("source", "");
        String destination = call.getString("destination", "");
        Boolean overwrite = call.getBoolean("overwrite", true);
        String password = call.getString("password");


        if (source.contains("_capacitor_")) {
            source = source.replace("_capacitor_", "");
        }

        if (source.contains("file://")) {
            source = source.replace("file://", "");
        }

        if (destination.contains("_capacitor_")) {
            destination = destination.replace("_capacitor_", "");
        }

        if (destination.contains("file://")) {
            destination = destination.replace("file://", "");
        }

        File folder = new File(source);
        File dest = new File(destination);

        if (!folder.exists()) {
            call.reject("Folder does not exist, invalid folder path: " + folder.getAbsolutePath());
        }

        if (overwrite && dest.exists()) {
            Boolean deleted = dest.delete();
        }

        try {
            ZipFile zipFile = new ZipFile(dest);
            zipFile.setRunInThread(true);
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            if (!password.isEmpty()) {
                zipFile.setPassword(password);
            }
            zipFile.createZipFileFromFolder(folder, parameters, false, 0);
            int progress;
            JSObject statusObject = new JSObject();
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
            call.error(e.getMessage());
        }

    }
}
