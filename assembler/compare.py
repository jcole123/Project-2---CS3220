import sys
#Give two files, output any lines from file1 that don't match the same line from file2
def main(fOne, fTwo):
    with open(fOne, 'r') as fileOne:
        with open(fTwo,'r') as fileTwo:
            differ = set(fileOne).difference(fileTwo)
    differ.discard('\n') #ignore empty lines
    with open('compare-results.txt', 'w') as output:
        for line in differ:
            if '--' not in line: #ignore comments
                output.write(line)


if __name__ == "__main__":
   if len(sys.argv) != 3:
        print 'supply the name of two files to compare'
        sys.exit(1)
   main(sys.argv[1], sys.argv[2])
