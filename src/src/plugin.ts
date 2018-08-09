import { Plugins } from '@capacitor/core';
import { IZip, UnZipOptions, ZipOptions } from './definitions';
const { ZipPlugin } = Plugins;

export class Zip implements IZip {
  public zip(options: ZipOptions, progress: Function): Promise<any> {
    return new Promise((resolve, reject) => {
      ZipPlugin.zip(options, (data: any, error: any) => {
        if (!error) {
          if (data.status === 'progressing') {
            if (progress) {
              progress({
                value: data.progress
              });
            }
          } else if (data.status === 'completed') {
            resolve({
              value: data.path
            });
          }
        } else {
          reject(error);
        }
      });
    });
  }
  public unZip(options: UnZipOptions, progress: Function): Promise<any> {
    return new Promise((resolve, reject) => {
      ZipPlugin.unZip(options, (data: any, error: any) => {
        if (!error) {
          if (data.status === 'progressing') {
            if (progress) {
              progress({
                value: data.progress
              });
            }
          } else if (data.status === 'completed') {
            resolve({
              value: data.path
            });
          }
        } else {
          reject(error);
        }
      });
    });
  }
}
