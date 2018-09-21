import { Component, OnInit , NgZone} from '@angular/core';
import { NavController } from 'ionic-angular';
import { Zip } from 'capacitor-zip';
import { FilesystemDirectory, FilesystemEncoding, Plugins } from '@capacitor/core';
import { File } from '@ionic-native/file';

const {Filesystem} = Plugins;

@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage implements OnInit {
  zip: Zip;
  zipProgress: number = 0;
  unzipProgress: number = 0;
  hasFiles: boolean = false;
  hasZip: boolean = false;
  firstData = '';
  secondData = '';
  constructor(public navCtrl: NavController, private file: File,private zone:NgZone) {
    this.zip = new Zip();
  }

  ngOnInit(): void {
    this.zipProgress = 0;
    this.unzipProgress =  0;
    this.checkForFiles();
    this.checkForArchive();
  }


  async checkForArchive(){
    try {
      await Filesystem.stat({
        directory: FilesystemDirectory.Documents,
        path: 'testZip.zip'
      });

      this.zone.run(()=>{this.hasZip = true;});
    } catch (e) {
      console.log('checkForArchive', e);
    }
  }

  async checkForFiles() {
    try {
      await Filesystem.stat({
        directory: FilesystemDirectory.Documents,
        path: 'toZip/first.txt'
      });

      await Filesystem.stat({
        directory: FilesystemDirectory.Documents,
        path: 'toZip/second.txt'
      });

      this.zone.run(()=>{this.hasFiles = true;});
    } catch (e) {
      console.log('checkForFiles', e);
    }
  }

  async createFiles() {
    try {

      await Filesystem.mkdir({
        directory: FilesystemDirectory.Documents,
        path: 'toZip',
        createIntermediateDirectories: true
      });

      await Filesystem.writeFile({
        data: 'First File',
        directory: FilesystemDirectory.Documents,
        encoding: FilesystemEncoding.UTF8,
        path: 'toZip/first.txt'
      });

      await Filesystem.writeFile({
        data: 'Second File',
        directory: FilesystemDirectory.Documents,
        encoding: FilesystemEncoding.UTF8,
        path: 'toZip/second.txt'
      });
      this.zone.run(()=>{
        this.hasFiles = true;
      });
    } catch (e) {
      console.log('createFiles', e);
    }
  }

  async createZip() {
    try {
      await this.zip.zip({
        destination: `${this.file.documentsDirectory}testZip.zip`,
        source: `${this.file.documentsDirectory}toZip`
      }, (progress) => {
        this.zone.run(()=>{this.zipProgress = progress.value;});
      });
      this.zone.run(()=>{
        this.hasZip = true;
      });
    } catch (e) {

    }
  }

  async unzip() {
    try {
      await this.zip.unZip({
        source: `${this.file.documentsDirectory}testZip.zip`,
        destination: `${this.file.documentsDirectory}testZip`,
      }, (progress) => {
        this.unzipProgress = progress.value;
      });

      this.firstData = await this.file.readAsText(this.file.documentsDirectory + 'toZip','first.txt');
      this.secondData = await this.file.readAsText(this.file.documentsDirectory + 'toZip','second.txt');

    } catch (e) {

    }
  }


}
