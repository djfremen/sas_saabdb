@echo off
set JDBC_DRIVER="C:\Program Files (x86)\GlobalTIS\transbase\tbjre\lib\tbjdbc.jar"
echo Using JDBC driver: %JDBC_DRIVER%

echo Compiling Java program...
javac -cp %JDBC_DRIVER% src\ExportAllTables.java

echo Running export program...
java -cp %JDBC_DRIVER%;src ExportAllTables

echo.
echo Done!
pause 