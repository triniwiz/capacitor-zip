import { WebPlugin } from '@capacitor/core';
import { IZip, ZipOptions, UnZipOptions } from './definitions';

export class ZipPluginWeb extends WebPlugin implements IZip {
  constructor() {
    super({
      name: 'ZipPlugin',
      platforms: ['web']
    });
  }

  zip(options: ZipOptions): Promise<any> {
    console.log(options);
    return Promise.resolve({});
  }
  unZip(options: UnZipOptions): Promise<any> {
    console.log(options);
    return Promise.resolve({});
  }
}

const ZipPlugin = new ZipPluginWeb();

export { ZipPlugin };
