@echo off
echo Installing Transbase Python driver (if not already installed)...
pip install transbase

echo Running data export script...
python export_transbase.py %*

echo.
if %ERRORLEVEL% NEQ 0 (
    echo Export failed. See error message above.
) else (
    echo Export completed successfully.
)

pause 