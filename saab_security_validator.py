import csv
import sqlite3
import os
import time

class SaabSecurityValidator:
    def __init__(self, csv_file='TSECCODE_export.csv', use_database=True):
        """
        Initialize the validator with either direct memory lookup or SQLite database
        
        Args:
            csv_file: Path to the CSV file with security codes
            use_database: If True, load data into SQLite database for better performance with large datasets
        """
        self.csv_file = csv_file
        self.use_database = use_database
        self.security_dict = {}  # For direct memory lookup
        self.db_path = f"{os.path.splitext(csv_file)[0]}.db"
        
        # Load the security data
        self.load_security_data()
    
    def load_security_data(self):
        """Load security data either into memory or SQLite database"""
        start_time = time.time()
        
        if not os.path.exists(self.csv_file):
            print(f"ERROR: CSV file {self.csv_file} not found!")
            return
            
        if self.use_database:
            self._load_to_database()
        else:
            self._load_to_memory()
            
        elapsed = time.time() - start_time
        print(f"Loaded security data in {elapsed:.2f} seconds")
    
    def _load_to_memory(self):
        """Load all security data into memory (faster for lookups but uses more RAM)"""
        print(f"Loading security data from {self.csv_file} into memory...")
        
        try:
            with open(self.csv_file, 'r', newline='', encoding='utf-8') as csvfile:
                reader = csv.reader(csvfile)
                # Read header row
                header = next(reader)
                
                # Determine which columns to use (assuming first column is the key)
                key_col = 0
                
                # Load data
                count = 0
                for row in reader:
                    if not row:
                        continue
                    
                    key = row[key_col]
                    self.security_dict[key] = row
                    count += 1
                    
                    if count % 100000 == 0:
                        print(f"Loaded {count} records...")
                
                print(f"Loaded {count} security records into memory")
        except Exception as e:
            print(f"Error loading data: {str(e)}")
    
    def _load_to_database(self):
        """Load security data into SQLite database (better for very large datasets)"""
        print(f"Loading security data from {self.csv_file} into SQLite database...")
        
        # Check if database already exists
        db_exists = os.path.exists(self.db_path)
        
        # Connect to database
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        if not db_exists:
            # Create table
            try:
                with open(self.csv_file, 'r', newline='', encoding='utf-8') as csvfile:
                    reader = csv.reader(csvfile)
                    header = next(reader)
                    
                    # Create table with columns from CSV header
                    columns = []
                    for i, col in enumerate(header):
                        col_name = col.strip().replace(" ", "_")
                        if i == 0:  # First column is primary key
                            columns.append(f"{col_name} TEXT PRIMARY KEY")
                        else:
                            columns.append(f"{col_name} TEXT")
                    
                    create_table_sql = f"CREATE TABLE IF NOT EXISTS security_data ({', '.join(columns)})"
                    cursor.execute(create_table_sql)
                    
                    # Create SQL for insert
                    placeholders = ', '.join(['?'] * len(header))
                    insert_sql = f"INSERT OR IGNORE INTO security_data VALUES ({placeholders})"
                    
                    # Load data in batches
                    batch_size = 10000
                    batch = []
                    count = 0
                    
                    for row in reader:
                        if not row:
                            continue
                        
                        batch.append(row)
                        count += 1
                        
                        if len(batch) >= batch_size:
                            cursor.executemany(insert_sql, batch)
                            conn.commit()
                            batch = []
                            print(f"Loaded {count} records to database...")
                    
                    # Insert remaining records
                    if batch:
                        cursor.executemany(insert_sql, batch)
                        conn.commit()
                    
                    print(f"Created security database with {count} records")
                    
                    # Create index for faster lookups
                    cursor.execute(f"CREATE INDEX IF NOT EXISTS idx_key ON security_data ({header[0]})")
                    conn.commit()
            except Exception as e:
                print(f"Error creating database: {str(e)}")
                if os.path.exists(self.db_path):
                    os.remove(self.db_path)
        else:
            print(f"Using existing security database: {self.db_path}")
            
        conn.close()
    
    def validate_security_code(self, code):
        """
        Validate if a security code exists
        
        Args:
            code: The security code to validate
            
        Returns:
            tuple: (is_valid, data) where is_valid is a boolean and data contains the record if valid
        """
        if self.use_database:
            return self._validate_code_db(code)
        else:
            return self._validate_code_memory(code)
    
    def validate_vin(self, vin):
        """
        Lookup security data by VIN (may require a full table scan if not indexed)
        
        Args:
            vin: Vehicle Identification Number
            
        Returns:
            list: Records matching the VIN
        """
        if self.use_database:
            return self._lookup_vin_db(vin)
        else:
            return self._lookup_vin_memory(vin)
    
    def _validate_code_memory(self, code):
        """Validate security code using in-memory dictionary"""
        if code in self.security_dict:
            return True, self.security_dict[code]
        return False, None
    
    def _validate_code_db(self, code):
        """Validate security code using SQLite database"""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        try:
            cursor.execute("SELECT * FROM security_data WHERE key = ?", (code,))
            result = cursor.fetchone()
            
            if result:
                return True, result
            return False, None
        except Exception as e:
            print(f"Error validating security code: {str(e)}")
            return False, None
        finally:
            conn.close()
    
    def _lookup_vin_memory(self, vin):
        """Search for VIN in memory dictionary (may be slow for large datasets)"""
        results = []
        search_term = vin.upper()
        
        # Full scan - inefficient but necessary for memory mode
        for key, data in self.security_dict.items():
            data_str = str(data)
            if search_term in data_str:
                results.append(data)
                
        return results
    
    def _lookup_vin_db(self, vin):
        """Search for VIN in SQLite database"""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        try:
            # This assumes we know the column name that might contain VIN data
            # Depending on actual data structure, this query may need adjustment
            cursor.execute("SELECT * FROM security_data WHERE key LIKE ? OR value LIKE ?", 
                          (f"%{vin}%", f"%{vin}%"))
            
            results = cursor.fetchall()
            return results
        except Exception as e:
            print(f"Error looking up VIN: {str(e)}")
            return []
        finally:
            conn.close()


# Example usage for direct command-line validation
if __name__ == "__main__":
    import sys
    
    if len(sys.argv) < 3:
        print("Usage: python saab_security_validator.py [--code CODE | --vin VIN]")
        print("Optional arguments:")
        print("  --csv-file=<file>    : CSV file to use (default: TSECCODE_export.csv)")
        print("  --use-memory         : Use in-memory mode instead of database")
        print("")
        print("Examples:")
        print("  python saab_security_validator.py --code 1CM069Q8")
        print("  python saab_security_validator.py --vin YS3FB49S531009137")
        sys.exit(1)
    
    # Parse command line arguments
    csv_file = 'TSECCODE_export.csv'
    use_database = True
    mode = None
    value = None
    
    for i in range(1, len(sys.argv)):
        arg = sys.argv[i]
        
        if arg == '--code' and i+1 < len(sys.argv):
            mode = 'code'
            value = sys.argv[i+1]
        elif arg == '--vin' and i+1 < len(sys.argv):
            mode = 'vin'
            value = sys.argv[i+1]
        elif arg.startswith('--csv-file='):
            csv_file = arg.split('=')[1]
        elif arg == '--use-memory':
            use_database = False
    
    if not mode or not value:
        print("Error: You must specify either --code or --vin")
        sys.exit(1)
    
    # Create validator
    validator = SaabSecurityValidator(csv_file=csv_file, use_database=use_database)
    
    # Perform lookup
    if mode == 'code':
        print(f"\nValidating security code: {value}")
        is_valid, data = validator.validate_security_code(value)
        
        if is_valid:
            print(f"✓ Security code '{value}' is VALID")
            print(f"Data: {data}")
        else:
            print(f"✗ Security code '{value}' is NOT valid")
    
    elif mode == 'vin':
        print(f"\nLooking up VIN: {value}")
        results = validator.validate_vin(value)
        
        if results:
            print(f"✓ Found {len(results)} records for VIN '{value}':")
            for i, result in enumerate(results):
                print(f"Record {i+1}: {result}")
        else:
            print(f"✗ No records found for VIN '{value}'") 