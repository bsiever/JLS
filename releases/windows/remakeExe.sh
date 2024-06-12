#!/bin/sh

jpackage --type app-image --app-version 4.4 --copyright ""  --description "J(ava) Logic Simulator" \
    --name "JLS" \
    --icon  resources/JLS.ico  \
    --runtime-image ./jre/jdk-21.0.3+9-jre/ \
    --input contents \
    --main-jar JLS.jar \
    --main-class jls.JLS \




