
  Pod::Spec.new do |s|
    s.name = 'CapacitorZip'
    s.version = '0.0.2'
    s.summary = 'Zip plugin'
    s.license = 'MIT'
    s.homepage = 'https://github.com/triniwiz/capacitor-zip'
    s.author = 'Osei Fortune'
    s.source = { :git => 'https://github.com/triniwiz/capacitor-zip', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '10.0'
    s.dependency 'Capacitor'
    s.dependency 'SSZipArchive'
  end
