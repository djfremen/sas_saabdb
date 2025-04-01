@echo off
set DRIVER_PATH="C:\Program Files (x86)\GlobalTIS\transbase\tbjre\lib\tbjdbc.jar"

echo Compiling Java program...
javac -cp %DRIVER_PATH% src\ExportTransbaseTableWithParams.java

echo Running program to export data...
java -cp %DRIVER_PATH%;src ExportTransbaseTableWithParams %*

echo Done!
pause 