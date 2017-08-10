REM It's created only for unit testing in Win
SET CURRENTDIR="%cd%"
SET KINDLE_HOME="%2"
echo "Running kindlegen.exe for %1. Current folder %CURRENTDIR%" >>logs/kindle.log

%KINDLE_HOME%\kindlegen.exe %CURRENTDIR%\%1 -c1 -c0 -locale ru -verbose >>logs/conversion.out

