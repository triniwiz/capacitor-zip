import Foundation
import Capacitor
import SSZipArchive

@objc(ZipPlugin)
public class ZipPlugin: CAPPlugin {

    @objc func zip(_ call: CAPPluginCall){
        var source = call.getString("source") ?? ""
        let destination = call.getString("destination") ?? ""
        let keepParent = call.getBool("overwrite") ?? true
        let password = call.getString("password") ?? nil

        if(source.contains("_capacitor_")){
            source = source.replacingOccurrences(of: "_capacitor_", with: "file://")
        }

        var progress = 0;
        let completed = SSZipArchive.createZipFile(atPath: source, withContentsOfDirectory: destination, keepParentDirectory: keepParent, compressionLevel: -1, password: password, aes: true, progressHandler: { (entryNumber, entriesTotal) in
            if (entriesTotal > 0) {
                let percent = entryNumber / entriesTotal * 100;
                if (percent != progress) {
                    progress = Int(percent);
                    call.success([
                        "status": "progressing",
                        "progress": progress
                        ])
                }
            }
        })

        if(completed){
            call.success([
                "status": "completed"
                ])
        } else{
            call.error("Error creating zip file.")
        }
    }


    @objc func unZip(_ call: CAPPluginCall){
        var source = call.getString("source") ?? ""
        let dest = call.getString("destination") ?? ""
        let overwrite = call.getBool("overwrite") ?? true
        let password = call.getString("password") ?? nil
        var progress = 0;

        if(source.contains("_capacitor_")){
            source = source.replacingOccurrences(of: "_capacitor_", with: "file://")
        }

        SSZipArchive.unzipFile(atPath: source, toDestination: dest, overwrite: overwrite, password: password, progressHandler: {(entry, zipFileInfo, entryNumber, entriesTotal) in
            if (entriesTotal > 0) {
                let percent = entryNumber / entriesTotal * 100;
                if (percent != progress) {
                    progress = Int(percent);
                    call.success([
                        "status": "progressing",
                        "progress": progress
                        ])
                }
            }

        } , completionHandler: { (path, succeeded, err) in

            if(succeeded){
                call.success([
                    "status": "completed"
                    ])
            }else{
                call.error(err?.localizedDescription ?? "Unknown error")
            }
        })
    }
}
