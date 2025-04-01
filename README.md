# SAS_SAABDB Security Access System

This repository provides tools for accessing, downloading, and working with the Saab Security Access System database (`sas_saabdb`).

## What is sas_saabdb?

`sas_saabdb` is the Saab Security Access System database instance within GlobalTIS's Transbase system. It is not a separate tool, but rather a folder/database embedded in GlobalTIS, containing security access data for Saab vehicles.

The naming (`sas_saabdb`) stands for **Security Access System** for Saab database. It contains critical security information including:
- Security codes and algorithms
- Hardware key IDs (hwkId)
- Seed and key algorithm data
- Vehicle information

Within a typical GlobalTIS installation, you'll find it at paths like `C:\GlobalTIS\db\sas\sas_saabdb\`.

## Database Structure

### Security Codes
The database contains security codes that can be used for vehicle operations.

### Seed-Key Algorithms
Contains algorithms and key generation data for secure ECU access.

### Hardware IDs
References to vehicle hardware modules and their unique identifiers.

## Database Exports

The complete extracted database is available in the CSV exports directory of this repository. This makes it possible to work with the security access data without needing direct access to a GlobalTIS installation.

### Available CSV Exports

The main tables exported from the database are:
- RELEASE_export.csv - Version information
- SECVERSION_export.csv - Security version data
- TSECCODE_export.csv - Security codes and algorithms (contains over 2.3 million records)

## Accessing the Original Database

To connect to the original `sas_saabdb` database in a GlobalTIS installation:

1. **Database Information:**
   - **Host:** localhost
   - **Port:** 5024 (typical Transbase port)
   - **Database Name:** sas_saabdb

2. **Authentication Credentials:**
   - **Username:** tbadmin
   - **Password:** dgs

## Exporting from Transbase

The data can be exported from the Transbase database to CSV files using the included tools:

### Using Java Export Tool

1. Run `export_direct.bat` to compile and execute the Java-based export tool.
2. The tool will connect to the database and export each table to a separate CSV file.

## Direct Database Connection

For advanced users, a direct connection to the Transbase database is possible with these parameters:
```
jdbc:transbase://localhost:5024/sas_saabdb
username: tbadmin
password: dgs
```

## Working with the Data

The security access data can be used for:

1. **Security Code Validation** - Verify security codes using the provided validation tool
2. **Seed-Key Calculation** - Verify or calculate key responses for given seeds
3. **ECU Programming Authorization** - Validate access to ECU programming functions
4. **Security Research** - Study the structure of Saab's vehicle security systems

## Validation Tools

The repository includes tools to validate security codes without needing to connect to the original database:

### Security Code Lookup
Use the command-line validator to check if a specific security code exists:
```
validate_key.bat --code [SECURITY_CODE]
```

### VIN-Based Lookup
Search the database for records related to a specific VIN:
```
validate_key.bat --vin [VEHICLE_VIN]
```

## GlobalTIS and Transbase

GlobalTIS is an offline Technical Information System used by GM/Opel/Saab for diagnostics, programming (SPS), and service information. Under the hood, GlobalTIS relies on the **Transbase** database engine (by Transaction Software) to store and manage its data.

The GlobalTIS installation process explicitly asks for Transbase server and kernel port configurations, and the runtime includes Transbase services (e.g., the `tbmux32.exe` process for Transbase/CD) as part of GlobalTIS. The Transbase database engine serves as the backbone for storing security access data.

## Requirements

- Java Development Kit (JDK) 8 or higher (for database export)

## License

This project is provided for educational and research purposes only.

## References

- GlobalTIS uses Transbase as its DB engine (Transbase/CD processes run with GlobalTIS). The installer asks for Transbase port settings, confirming this integration.
- The GlobalTIS installation for Saab includes a directory `sas_saabdb` under `GlobalTIS\db\sas\`, which contains Transbase DB files (e.g. `dbconf.ini`, `tbdsk001`) for Saab's security data.
- For more information, visit: [https://github.com/djfremen/sas_saabdb](https://github.com/djfremen/sas_saabdb) 