@echo off
setlocal enabledelayedexpansion
setlocal enableextensions
set dir=%~dp0
cd %dir%
set lib=%dir%/lib/javafx-sdk-11_win/lib
echo %lib%
for /f tokens^=2-5^ delims^=.-_^" %%j in ('java -fullversion 2^>^&1') do set "jver=%%j.%%k"
echo java version=%jver%
if %jver% GEQ 11 (
	java --module-path %lib% --add-modules=javafx.controls,javafx.swing,javafx.fxml -Dfile.encoding=utf-8 -jar %dir%/QAUiCase.jar
) else (
	java -Dfile.encoding=utf-8 -jar %dir%/QAUiCase.jar
)