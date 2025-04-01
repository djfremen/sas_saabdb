# Transbase Database Export Tool (Python Version)

This tool uses the official Transbase Python driver to export data from your Transbase database to CSV files.

## Prerequisites

- Python 3.9 or later
- Pip (Python package manager)

## Quick Setup

1. Run `export_transbase.bat` to install the required dependencies and export the default table.

## Usage

### Basic Usage

Simply run the batch file:
```
export_transbase.bat
```

This will attempt to export the `sas_saabdb` table using the default connection settings:
- Host: localhost
- Port: 5024
- Database: sas_saabdb
- Username: tbadmin
- Password: gds

### Advanced Usage

You can specify custom parameters:

```
export_transbase.bat --host hostname --port port --db database --user username --pass password --table tablename --output filename.csv
```

### Parameters

- `--host`: Database server hostname (default: localhost)
- `--port`: Database server port (default: 5024)
- `--db`: Database name (default: sas_saabdb)
- `--user`: Username (default: tbadmin)
- `--pass`: Password (default: gds)
- `--table`: Table to export (default: sas_saabdb)
- `--output`: Output CSV filename (default: tablename_export.csv)

### Examples

Export a specific table:
```
export_transbase.bat --table another_table
```

Connect to a different server:
```
export_transbase.bat --host 192.168.1.100 --port 8324
```

## Troubleshooting

1. If you get an error about missing the Transbase driver, try running:
   ```
   pip install transbase && install_tci
   ```

2. If you're having connection issues, verify:
   - Database server is running
   - Connection information (host, port, credentials) is correct
   - The table exists and you have proper access permissions

## About the Transbase Python Driver

This tool uses the official Transbase Python driver, which is available on [GitHub](https://github.com/TransactionSoftwareGmbH/transbase-python) and [PyPI](https://pypi.org/project/transbase/).

The driver implements the Python database API v2.0 (PEP-249) and uses Transbase/TCI (Transbase C/C++ call interface). 