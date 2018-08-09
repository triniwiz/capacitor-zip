declare global {
  interface PluginRegistry {
    ZipPlugin?: IZip;
  }
}

export interface IZip {
  zip(options: ZipOptions, progress?: Function): Promise<any>;

  unZip(options: UnZipOptions, progress?: Function): Promise<any>;
}

export interface ZipOptions {
  source: string;
  destination: string;
  keepParent: boolean;
  password?: string;
}

export interface UnZipOptions {
  source: string;
  destination: string;
  overwrite?: boolean;
  password?: string;
}
