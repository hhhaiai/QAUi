#!/bin/sh

dir=$(cd "$(dirname "$0")"; pwd)
echo $dir
cd $dir
lib=$dir/lib/javafx-sdk-11_mac
JAVA_VERSION=`java -version 2>&1 |awk 'NR==1{ gsub(/"/,""); print $3 }'`
echo "java version=${JAVA_VERSION%%.*}($JAVA_VERSION)"
if [ ${JAVA_VERSION%%.*} -ge 11 ]; then 
	java --module-path $lib --add-modules=javafx.controls,javafx.swing,javafx.fxml -Dfile.encoding=utf-8 -jar $dir/QAUiCase.jar
else 
	java -Dfile.encoding=utf-8 -jar $dir/QAUiCase.jar
fi