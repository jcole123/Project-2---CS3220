import sys
import os
#Give two files, output any lines from file1 that don't match the same line from file2
def main(fOne, fTwo):
    '''with open(fOne, 'r') as fileOne:
        with open(fTwo,'r') as fileTwo:
            differ = set(fileOne).difference(fileTwo)
    differ.discard('\n') #ignore empty lines
    with open('compare-results.txt', 'w') as output:
        for line in differ:
            if '--' not in line: #ignore comments
                output.write(line)'''

    #Includes line numbers and differences
    f1 = open(fOne, 'r')
    f2 = open(fTwo, 'r')
    fileOne = f1.readlines()
    fileTwo = f2.readlines()
    f1.close()
    f2.close()
    outFile = open('compare-results.txt', 'w')
    line = 0
    empty = 0    
    for i in fileOne:
        if i != fileTwo[line]:
            if '--' not in i:
                outFile.write('line ' + str(line+1) + ': ' + fOne + ': ' + i + fTwo + ': ' + fileTwo[line])
                empty += 1
        line = line + 1 if line+1 < len(fileTwo) else line
    outFile.close()
    if (empty == 0):
        print "Files were an exact match"


if __name__ == "__main__":
   if len(sys.argv) != 3:
        print 'supply the name of two files to compare'
        sys.exit(1)
   main(sys.argv[1], sys.argv[2])
