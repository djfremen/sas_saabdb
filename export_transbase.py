import sys
import csv
try:
    from transbase import transbase
except ImportError:
    print("Transbase Python driver not found. Installing...")
    print("Please run: pip install transbase")
    print("If that fails, try: pip install transbase && install_tci")
    sys.exit(1)

def export_table_to_csv(host="localhost", port="5024", database="sas_saabdb", 
                       username="tbadmin", password="gds", 
                       table_name="sas_saabdb", output_file=None):
    """Export a table from Transbase database to CSV file"""
    if output_file is None:
        output_file = f"{table_name}_export.csv"
    
    # Build connection string
    conn_string = f"//{host}:{port}/{database}"
    print(f"Connecting to {conn_string}...")
    
    try:
        # Connect to the database
        connection = transbase.connect(conn_string, username, password)
        cursor = connection.cursor()
        
        # Get column names
        cursor.execute(f"SELECT * FROM {table_name} WHERE 1=0")
        columns = [desc[0] for desc in cursor.description]
        
        # Query all data
        print(f"Exporting data from table: {table_name}")
        cursor.execute(f"SELECT * FROM {table_name}")
        cursor.type_cast = True  # Use native Python data types
        
        # Write to CSV
        with open(output_file, 'w', newline='') as csvfile:
            writer = csv.writer(csvfile)
            # Write header
            writer.writerow(columns)
            
            # Write data rows
            row_count = 0
            while True:
                rows = cursor.fetchmany(1000)
                if not rows:
                    break
                    
                for row in rows:
                    writer.writerow(row)
                    row_count += 1
                
                if row_count % 1000 == 0:
                    print(f"Exported {row_count} rows...")
        
        print(f"Export complete. {row_count} rows exported to {output_file}")
        
    except Exception as e:
        print(f"Error: {e}")
        sys.exit(1)
    finally:
        if 'cursor' in locals():
            cursor.close()
        if 'connection' in locals():
            connection.close()

if __name__ == "__main__":
    import argparse
    
    parser = argparse.ArgumentParser(description='Export data from Transbase database to CSV')
    parser.add_argument('--host', default='localhost', help='Database host')
    parser.add_argument('--port', default='5024', help='Database port')
    parser.add_argument('--db', default='sas_saabdb', help='Database name')
    parser.add_argument('--user', default='tbadmin', help='Database username')
    parser.add_argument('--pass', dest='password', default='gds', help='Database password')
    parser.add_argument('--table', default='sas_saabdb', help='Table to export')
    parser.add_argument('--output', help='Output CSV file name')
    
    args = parser.parse_args()
    
    export_table_to_csv(
        host=args.host,
        port=args.port,
        database=args.db,
        username=args.user,
        password=args.password,
        table_name=args.table,
        output_file=args.output
    ) 