@echo off
set DRIVER_PATH="C:\Program Files (x86)\GlobalTIS\transbase\tbjre\lib\tbjdbc.jar"

echo Compiling Java program...
if not exist src mkdir src
javac -cp %DRIVER_PATH% src\ListTransbaseTables.java

echo Running program to list tables...
java -cp %DRIVER_PATH%;src ListTransbaseTables

echo Done!
pause 