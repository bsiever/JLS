# JLS: Java Logic Simulator


# JAR
 
In Eclipse:  
1. File>Export
2. Java>Runnable JAR
3. Select destination / etc. 


# Packaging

https://docs.oracle.com/en/java/javase/19/jpackage/packaging-overview.html 


# Icon

https://cloudconvert.com/  svg to ico 
https://xconvertx.com/convert/svg_to_icns/








# macOS

Ok...


1. Did the process to create a multi-platform version of jre at https://incenp.org/notes/2023/universal-java-app-on-macos.html 

2. Created certificates for both Application and Installer and installed both to login keychain. 

3. Did jpackage to create app:
```
jpackage --type app-image --app-version 4.4 --copyright ""  --description "J(ava) Logic Simulator" \
    --icon  /Users/bsiever/git/JLS/macOS/icon/JLSicon.icns  \
    --mac-package-identifier "info.siever.JLS"  --mac-package-name "JLS" \
    --file-associations /Users/bsiever/git/JLS/macOS/mac_associations.properties \
    --runtime-image /Users/bsiever/git/JLS/macOS/jre/universal/jdk-21.0.3+9-jre/Contents/jre \
    --input contents --main-jar JLS.jar --main-class jls.JLS \
    --mac-sign 

```

Trying to open .jpackage.xml and failing. 
It's using: /Users/bsiever/Library/Containers/info.siever.JLS/Data 

Bundle:
```
jpackage --type app-image --app-version 4.3 --copyright ""  --description "J(ava) Logic Simulator" \
    --icon  /Users/bsiever/git/JLS/macOS/icon/icon.icns  \
    --mac-package-identifier "info.siever.JLS"  --mac-package-name "JLS" \
    --file-associations /Users/bsiever/git/JLS/macOS/associations.info \
    --runtime-image /Users/bsiever/git/JLS/macOS/jre/universal/jdk-21.0.3+9-jre/Contents/jre \
    --input contents --main-jar JLS.jar --main-class jls.JLS \
    --mac-sign  --mac-app-category education 
```

--mac-app-store --license-file LICENSE  --type dmg



See https://munier.perso.univ-pau.fr/temp/jdk-7u45-apidocs/technotes/guides/jweb/packagingAppsForMac.html 

See: https://incenp.org/notes/2023/universal-java-app-on-macos.html
And modifications in: https://forum.vassalengine.org/t/how-to-bundle-java-in-a-universal-macos-app/77991 

Download latest OpenJDK JREs with hotspot: https://adoptium.net/temurin/releases/?os=mac 

Info.plist based on https://gist.github.com/joaopms/359919334292cfbb96fb 



jpackage --type app-image -i contents --main-class jls.JLS --main-jar JLS.jar --runtime-image /Users/bsiever/git/JLS/macOS/jre/universal/jdk-21.0.3+9-jre/Contents/jre 




--name JLS --type app-image --mac-package-identifier "info.siever.JLS" --mac-package-name "JLS" \
  --file-associations /Users/bsiever/git/JLS/macOS/associations.info \
  \





jpackage --name JLS --type app-image --mac-package-identifier "info.siever.JLS" --mac-package-name "JLS" \
  --file-associations /Users/bsiever/git/JLS/macOS/associations.info \
  --runtime-image /Users/bsiever/git/JLS/macOS/jre/universal/jdk-21.0.3+9-jre/Contents/jre \
  -i contents --main-class jls.JLS --main-jar JLS.jar 
  
  
  
   --icon  /Users/bsiever/git/JLS/macOS/icon/icon.icns



jpackage --type app-image --app-version 4.3 --copyright ""  --description "J(ava) Logic Simulator" \
    --icon  /Users/bsiever/git/JLS/macOS/icon/icon.icns  \
    --mac-package-identifier "info.siever.JLS"  --mac-package-name "JLS" \
    --file-associations /Users/bsiever/git/JLS/macOS/associations.info \
    --runtime-image /Users/bsiever/git/JLS/macOS/jre/universal/jdk-21.0.3+9-jre/Contents/jre \
    --input contents --main-jar JLS.jar --main-class jls.JLS \
    --mac-sign --mac-app-store --mac-app-category education  --verbose
    
    
    
    --license-file ./LICENSE \
    --app-image .
   
   
   
   --about-url "https://github.com/bsiever/JLS"