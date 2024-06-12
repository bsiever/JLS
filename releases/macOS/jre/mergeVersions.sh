#!/bin/bash

# From https://incenp.org/notes/2023/universal-java-app-on-macos.html 

die() {
    echo "${0##*/}: $@" >&2
    exit 1
}

version=$(ls OpenJDK*-jre_x64_mac_hotspot_*.tar.gz | sed -E "s,OpenJDK.+-jre_x64_mac_hotspot_(.+).tar.gz,\1,")
[ -n "$version" ] || die "Cannot identify JRE version"

[ -f OpenJDK*-jre_aarch64_mac_hotspot_$version.tar.gz ] || die "Missing corresponding arm64 JRE"

echo "Extracting native JREs..."
rm -rf x86_64 arm64
mkdir x86_64 arm64
(cd x86_64
 tar xf ../OpenJDK*-jre_x64_mac_hotspot_$version.tar.gz)
(cd arm64
 tar xf ../OpenJDK*-jre_aarch64_mac_hotspot_$version.tar.gz)

echo "Creating universal JRE..."
rm -rf universal
mkdir universal
find arm64 -type f | while read arm_file ; do
    noarch_file=${arm_file#arm64/}
    mkdir -p universal/${noarch_file%/*}
    if file $arm_file | grep "Mach-O.\+arm64" ; then
        # Create universal binary from both x86_64 and arm64
        lipo -create -output universal/$noarch_file x86_64/$noarch_file $arm_file
        if file $arm_file | grep executable ; then
            chmod 755 universal/$noarch_file
        fi
    else
        # Not a file with binary code, copy it as it is
        cp $arm_file universal/$noarch_file
    fi
done

echo "Packaging the JRE..."
(cd universal/jdk*/Contents
 rm -rf Info.plist MacOS _CodeSignature
 mv Home jre)
jar --create --file jre.os-x-$version.jar -C universal/jdk*/Contents .