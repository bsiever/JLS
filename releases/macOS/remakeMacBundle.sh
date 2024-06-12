#!/bin/sh

jpackage --type app-image --app-version 4.4 --copyright ""  --description "J(ava) Logic Simulator" \
    --icon  resources/JLS.icns  \
    --resource-dir resources \
    --mac-package-identifier "info.siever.JLS" \
    --mac-package-name "JLS" \
    --file-associations associations.properties \
    --runtime-image ./jre/universal/jdk-21.0.3+9-jre/Contents/jre \
    --input contents \
    --main-jar JLS.jar \
    --main-class jls.JLS \
    --mac-sign \
    --mac-app-category education



# jpackage --type dmg --app-version 4.4 --copyright ""  --description "J(ava) Logic Simulator" \
#     --resource-dir resources \
#     --mac-package-identifier "info.siever.JLS" \
#     --mac-package-name "JLS" \
#     --file-associations ./mac_associations.properties \
#     --runtime-image ./jre/universal/jdk-21.0.3+9-jre/Contents/jre \
#     --input contents \
#     --main-jar JLS.jar \
#     --main-class jls.JLS \
#     --mac-sign \
#     --mac-app-category education \
#     --mac-app-store \
#     --license-file ../../LICENSE

