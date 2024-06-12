#!/bin/sh

jpackage --type app-image --app-version 4.4 --copyright ""  --description "J(ava) Logic Simulator" \
    --icon  ./icon/JLSicon.icns  \
    --mac-package-identifier "info.siever.JLS" \
    --mac-package-name "JLS" \
    --file-associations ./mac_associations.properties \
    --runtime-image ./jre/universal/jdk-21.0.3+9-jre/Contents/jre \
    --input contents \
    --main-jar JLS.jar \
    --main-class jls.JLS \
    --mac-sign 
