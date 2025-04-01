import os
import shutil
import glob
import sys

def prepare_files_for_github():
    """
    Prepares the exported CSV files for GitHub upload by:
    1. Creating a csv_exports directory
    2. Copying all CSV files to that directory
    3. Creating a simple CSV inventory file
    """
    print("Preparing files for GitHub upload...")
    
    # Create csv_exports directory if it doesn't exist
    if not os.path.exists('csv_exports'):
        os.mkdir('csv_exports')
        print("Created csv_exports directory")
    
    # Find all CSV files in the current directory
    csv_files = glob.glob('*_export.csv')
    
    if not csv_files:
        print("No CSV export files found. Please run the export tools first.")
        return False
    
    # Copy CSV files to the exports directory
    print(f"Found {len(csv_files)} CSV files to copy:")
    copied_files = []
    
    for file in csv_files:
        print(f"  - {file}")
        dest_path = os.path.join('csv_exports', file)
        try:
            shutil.copy2(file, dest_path)
            file_size = os.path.getsize(dest_path) / (1024 * 1024)  # Size in MB
            copied_files.append((file, file_size))
        except Exception as e:
            print(f"Error copying {file}: {str(e)}")
    
    # Create inventory file
    with open(os.path.join('csv_exports', 'inventory.txt'), 'w') as f:
        f.write("# SAS_SAABDB CSV Export Inventory\n\n")
        f.write("The following files were exported from the Transbase sas_saabdb database:\n\n")
        
        for file, size in copied_files:
            f.write(f"- {file} ({size:.2f} MB)\n")
        
        f.write("\nThese files contain the raw data exported from the Transbase database.\n")
        f.write("For information on how to use these files, see the README.md in the main repository.\n")
    
    print("\nFiles successfully prepared for GitHub upload.")
    print(f"Total CSV files: {len(copied_files)}")
    total_size = sum(size for _, size in copied_files)
    print(f"Total size: {total_size:.2f} MB")
    print("\nNext steps:")
    print("1. Add these files to your Git repository:")
    print("   git add csv_exports/")
    print("2. Commit the changes:")
    print("   git commit -m \"Add CSV exports from sas_saabdb\"")
    print("3. Push to GitHub:")
    print("   git push origin main")
    
    return True

if __name__ == "__main__":
    prepare_files_for_github() 