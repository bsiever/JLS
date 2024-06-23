#!/bin/sh

rm -Rf JLS
rm -Rf JLS_windows.zip

rm contents/JLS.jar
cp ../JLS.jar contents


jpackage --type app-image --app-version 4.6 --copyright ""  --description "J(ava) Logic Simulator" \
    --name "JLS" \
    --icon  resources/JLS.ico  \
    --runtime-image ./jre/jdk-21.0.3+9-jre/ \
    --input contents \
    --main-jar JLS.jar \
    --main-class jls.JLS \

tar -cvf JLS_windows.zip JLS

