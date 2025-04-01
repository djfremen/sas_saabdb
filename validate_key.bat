@echo off
setlocal

if "%~1"=="" (
  echo Saab Security Access System Validator
  echo.
  echo Usage: 
  echo   validate_key.bat --code [SECURITY_CODE]
  echo   validate_key.bat --vin [VEHICLE_VIN]
  echo.
  echo Examples:
  echo   validate_key.bat --code 1CM069Q8
  echo   validate_key.bat --vin YS3FB49S531009137
  exit /b 1
)

python saab_security_validator.py %*
pause

endlocal 