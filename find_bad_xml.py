import os
import sys
import xml.sax

def find_bad_xml_files(directory):
    bad_files = []

    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith('.xml'):
                file_path = os.path.join(root, file)
                try:
                    with open(file_path, 'r', encoding='utf-8') as f:
                        content = f.read()

                    # Try to parse the XML
                    parser = xml.sax.make_parser()
                    parser.setContentHandler(xml.sax.ContentHandler())
                    parser.parse(file_path)
                except Exception as e:
                    bad_files.append((file_path, str(e)))

    return bad_files

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python find_bad_xml.py <directory>")
        sys.exit(1)

    directory = sys.argv[1]
    bad_files = find_bad_xml_files(directory)

    if bad_files:
        print("Found problematic XML files:")
        for file_path, error in bad_files:
            print(f"{file_path}: {error}")
    else:
        print("No problematic XML files found.")