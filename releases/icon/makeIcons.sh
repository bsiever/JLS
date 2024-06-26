#!/bin/sh -x

# SVG conversion to ico 

mogrify -format ico -density 1200 -background transparent -trim -resize 256x256 -gravity center -extent 256x256 -define icon:auto-resize *.svg
mv *.ico icons/


# https://gist.github.com/adriansr/1da9b18a8076b0f8a977a5eea0ae41ef

# SVG conversions to ICNS
set -e

SIZES="
16,16x16
32,16x16@2x
32,32x32
64,32x32@2x
128,128x128
256,128x128@2x
256,256x256
512,256x256@2x
512,512x512
1024,512x512@2x
"

FILES=`ls *.svg`
for SVG in $FILES
do
  echo "Processing $f file..."
  # take action on each file. $f store current file name
  BASE=$(basename "$SVG" | sed 's/\.[^\.]*$//')
    ICONSET="$BASE.iconset"
    mkdir -p "icons/$ICONSET"
    for PARAMS in $SIZES; do
        SIZE=$(echo $PARAMS | cut -d, -f1)
        LABEL=$(echo $PARAMS | cut -d, -f2)
        svg2png -w $SIZE -h $SIZE "$SVG" "icons/$ICONSET"/icon_$LABEL.png || true
    done

    iconutil -c icns "icons/$ICONSET" || true
    rm -rf "$ICONSET"
done