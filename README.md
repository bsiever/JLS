# JLS: Java Logic Simulator

A digital logic simulator, written in Java.

# Releasing 

## Common

1. Update `src/jls/JLSInfo.java`'s `ver`, `release`, and `buildNum`
2. Rebuild and export to Jar (File > Export;  Java > Runnable JAR)
	1. Launch Config `JLS - JLS`
	2. Place it in JLS/releases/JLS.jar
	3. Select "Extract required libraries into generated JAR`
3. Navigate to `releases`

## macOS

Skip to step 3 if not updating the JRE. 

1. Create certificates for Application install to login keychain. (https://developer.apple.com/help/account/create-certificates/create-developer-id-certificates/)
2. Create a multi-platform version of jre (using notes from https://incenp.org/notes/2023/universal-java-app-on-macos.html)
	1. Get latest tgz bundles from Adoptium: https://adoptium.net/temurin/releases/?os=mac and place in jre folder (OpenJDK*-jre_x64_mac_hotspot_*.tar.gz and  OpenJDK.+-jre_x64_mac_hotspot_(.+).tar.gz)
	2. Run `jre/mergeVersions.sh` to extract them and create a merged Universal version 
3. Update `remakeMacBundle.sh` with updates to files, directories, versions. 
   1. Update `app-version` 
   2. If jre has been updated, update `runtime-image` path
4. Run `remakeMacBundle.sh`

## Windows

Login to windows, Academic lab;  Open bash and navigate to H:\git\JLS; `git pull`
Skip to step 3 if not updating the JRE.

1. Get the jre (https://adoptium.net/temurin/releases/), put it in `jre`, and unzip it.  
2. Copy the JLS jar to the contents folder (`cd releases/windows`, `cp ../JLS.jar contents`)
3. Update the `remakeExe.sh` script with version number and any changes to jre directory/name. 
4. Remove old version (remove the JLS director in releases/windows)
5. Run `remakeExe.sh` 

## GitHub 

Create a Tag for most recent commit.
Create a release.
Add .jar and _macOS.zip and .zip from windows to the release. 

## Tagging / Releasing

`git tag -a v4.6.0 -m "Version 4.6 Release"`
`git push origin tag v4.6.0`
Got to GitHub repo @ https://github.com/bsiever/JLS/releases and Draft a new release.
Attach executables.  Need to upload windows (maybe via google drive transfer.  Ugh)

# Misc. notes & debris

* Resources for converting svgs to icons (macOS icons)
  * `brew install svg2png` 
  * Script from https://gist.github.com/adriansr/1da9b18a8076b0f8a977a5eea0ae41ef 
  
  * https://cloudconvert.com/   
  * https://xconvertx.com/convert/svg_to_icns/
* Convert SVGs to ico 
  * `brew install ImageMagick`
  * `mogrify -format ico -density 1200 -background transparent -trim -resize 256x256 -gravity center -extent 256x256 -define icon:auto-resize *.svg`  (https://superuser.com/questions/260047/batch-convert-svg-images-to-desired-size-png-or-ico)

  
* Package specific resources

* [jpackage](https://docs.oracle.com/en/java/javase/19/jpackage/packaging-overview.html) for creating Java packages
   * Mac 
     * Packaging https://munier.perso.univ-pau.fr/temp/jdk-7u45-apidocs/technotes/guides/jweb/packagingAppsForMac.html 
     * Universal binary https://incenp.org/notes/2023/universal-java-app-on-macos.html
     * Modifications in: https://forum.vassalengine.org/t/how-to-bundle-java-in-a-universal-macos-app/77991 

* macOS build / bundle attempts

 ```
jpackage --type app-image --app-version 4.4 --copyright ""  --description "J(ava) Logic Simulator" \
    --icon  /Users/bsiever/git/JLS/macOS/icon/JLSicon.icns  \
    --mac-package-identifier "info.siever.JLS"  --mac-package-name "JLS" \
    --file-associations /Users/bsiever/git/JLS/macOS/mac_associations.properties \
    --runtime-image /Users/bsiever/git/JLS/macOS/jre/universal/jdk-21.0.3+9-jre/Contents/jre \
    --input contents --main-jar JLS.jar --main-class jls.JLS \
    --mac-sign 
```

```
jpackage --type dmg --app-version 4.3 --copyright ""  --description "J(ava) Logic Simulator" \
    --icon  /Users/bsiever/git/JLS/macOS/icon/icon.icns  \
    --mac-package-identifier "info.siever.JLS"  --mac-package-name "JLS" \
    --file-associations /Users/bsiever/git/JLS/macOS/associations.info \
    --runtime-image /Users/bsiever/git/JLS/macOS/jre/universal/jdk-21.0.3+9-jre/Contents/jre \
    --input contents --main-jar JLS.jar --main-class jls.JLS \
    --mac-sign  --mac-app-category education --mac-app-store --license-file LICENSE  --type dmg`    
```
