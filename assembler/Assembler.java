import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts an assembly file into an Altera .mif compatible file.
 * 
 * @author Michael Chen & Justin Cole
 *
 */
public class Assembler {	
	
	private static final Map<String, Integer> ALU_CMP_I = new HashMap<>();
	private static final Map<String, Integer> ALU_CMP_R = new HashMap<>();
	private static final Map<String, Integer> BRANCH_2ARG = new HashMap<>();
	private static final Map<String, Integer> BRANCH_3ARG = new HashMap<>();
	
	private static final int DEPTH = 2048;
	private static final String FOOTER = "END;";
	private static final String HEADER = "WIDTH=32;\nDEPTH=" + DEPTH
			+ ";\nADDRESS_RADIX=HEX;\nDATA_RADIX=HEX;\nCONTENT BEGIN";
	
	private static final Map<String, Integer> REGISTERS = new HashMap<>();
	
	static {
		ALU_CMP_R.put("AND", 0x40);
		ALU_CMP_R.put("XOR", 0x60);
		ALU_CMP_R.put("ADD", 0x00);
		ALU_CMP_R.put("SUB", 0x10);
		ALU_CMP_R.put("OR", 0x50);
		ALU_CMP_R.put("NAND", 0xc0);
		ALU_CMP_R.put("NOR", 0xd0);
		ALU_CMP_R.put("XNOR", 0xe0);

		ALU_CMP_I.put("ADDI", 0x08);
		ALU_CMP_I.put("SUBI", 0x18);
		ALU_CMP_I.put("ANDI", 0x48);
		ALU_CMP_I.put("ORI", 0x58);
		ALU_CMP_I.put("XORI", 0x68);
		ALU_CMP_I.put("NANDI", 0xc8);
		ALU_CMP_I.put("NORI", 0xd8);
		ALU_CMP_I.put("XNORI", 0xe8);
		
		ALU_CMP_R.put("F", 0x02);
		ALU_CMP_R.put("EQ", 0x12);
		ALU_CMP_R.put("LT", 0x22);
		ALU_CMP_R.put("LTE", 0x32);
		ALU_CMP_R.put("T", 0x82);
		ALU_CMP_R.put("NE", 0x92);
		ALU_CMP_R.put("GTE", 0xa2);
		ALU_CMP_R.put("GT", 0xb2);
		
		ALU_CMP_I.put("FI", 0x0a);
		ALU_CMP_I.put("EQI", 0x1a);
		ALU_CMP_I.put("LTI", 0x2a);
		ALU_CMP_I.put("LTEI", 0x3a);
		ALU_CMP_I.put("TI", 0x8a);
		ALU_CMP_I.put("NEI", 0x9a);
		ALU_CMP_I.put("GTEI", 0xaa);
		ALU_CMP_I.put("GTI", 0xba);
		
		BRANCH_3ARG.put("BF", 0x06);
		BRANCH_3ARG.put("BEQ", 0x16);
		BRANCH_3ARG.put("BLT", 0x26);
		BRANCH_3ARG.put("BLTE", 0x36);
		
		BRANCH_3ARG.put("BT", 0x86);
		BRANCH_3ARG.put("BNE", 0x96);
		BRANCH_3ARG.put("BGTE", 0xa6);
		BRANCH_3ARG.put("BGT", 0xb6);
		
		BRANCH_2ARG.put("BEQZ", 0x56);
		BRANCH_2ARG.put("BLTZ", 0x66);
		BRANCH_2ARG.put("BLTEZ", 0x76);
		
		BRANCH_2ARG.put("BNEZ", 0xd6);
		BRANCH_2ARG.put("BGTEZ", 0xe6);
		BRANCH_2ARG.put("BGTZ", 0xf6);
		
		REGISTERS.put("A0", 0);
		REGISTERS.put("A1", 1);
		REGISTERS.put("A2", 2);
		REGISTERS.put("A3", 3);
		REGISTERS.put("RV", 3);
		REGISTERS.put("T0", 4);
		REGISTERS.put("T1", 5);
		REGISTERS.put("S0", 6);
		REGISTERS.put("S1", 7);
		REGISTERS.put("S2", 8);
		REGISTERS.put("GP", 12);
		REGISTERS.put("FP", 13);
		REGISTERS.put("SP", 14);
		REGISTERS.put("RA", 15);
		for (int i = 0; i < 16; i++)
			REGISTERS.put("R"+i, i);
	}
	
	public static void main(String[] args) {

		// Check if the required arguments have been provided
		if (args.length != 1) {
			System.err.println("Please select an input file.");
			System.err.println("Syntax: java Assembler sample.a32");
			return;
		}

		// Check if the input file exists
		File inputFile = new File(args[0]);
		if (!inputFile.exists()) {
			System.err.println("The file " + args[0] + " does not exist");
			return;
		}
				
		Assembler test = new Assembler();
		List<String> assemblyCode;
		
		// Try to read the input file
		try {
			assemblyCode = test.readFile(inputFile);
		} catch (IOException e) {
			System.err.println("The file " + args[0] + " could not be read");
			return;
		}
		
		// Assemble
		List<String> compiledCode = test.assemble(assemblyCode);
		
		// Try to write to the output file
		File outputFile = new File(args[0].substring(0, args[0].lastIndexOf('.')) + ".mif");
		try {
			test.writeFile(outputFile, compiledCode);
		} catch (IOException e) {
			System.err.println("The file " + outputFile.getName() + " could not be written to");
			return;
		}
		
	}

	private HashMap<String, Integer> labels = new HashMap<String, Integer>();

	/**
	 * Converts the assembly code into it's compiled equivalent.
	 * @param asmCode The formatted input assembly code
	 * @return The compiled code without the headers
	 */
	public List<String> assemble(List<String> asmCode) {
		// Parse the .NAME lines into a new list
		parseNames(asmCode);

		// Replace any pseudocodes with their equivalents
		replacePseudoCodes(asmCode);

		// Parse any labels into the labels map
		parseLabels(asmCode);

		// Translate the code
		List<String> compiledCode = translateCode(asmCode);
		
		return compiledCode;
	}
	
	/**
	 * Reads an assembly file and strips non-essential text. Note that no
	 * parsing or validation is performed here.
	 * 
	 * @param file
	 *            The assembly input file
	 * @throws IOException
	 *             If the file cannot be read.
	 */
	public List<String> readFile(File file) throws IOException {
		// Load the assembly file into a new list.
		List<String> asmCode = Files.readAllLines(file.toPath());

		asmCode.replaceAll(line -> {
			// Convert multiple spaces and tabs into a single space
			// Convert any lower case elements to upper case
			line = line.replaceAll("\\s++", " ").toUpperCase();

			// Remove leading and trailing spaces and comments
			line = line.replaceAll(";.++", "").trim();
			
			// Remove spaces before and after equal signs, commas, parenthesis,
			// and colons.
			return line.replaceAll("(?<=[()=,:]) | (?=[()=,:])", "");
		});

		// Remove all blank lines
		asmCode.removeIf(line -> line.isEmpty());
		return asmCode;
	}
	
	/**
	 * Appends the header and the footer to the code and writes the result to
	 * the specified file.
	 * 
	 * @param file
	 *            The destination file
	 * @param code
	 *            The compiled code, without headers
	 * @throws IOException
	 *             If the data cannot be written to the output file
	 */
	public void writeFile(File file, List<String> code) throws IOException{
		code.add(0, HEADER);
		code.add(FOOTER);
		Files.write(file.toPath(), code);
	}

	/**
	 * Calculates the address of any labels in the code based on the number of
	 * lines of code and .origin labels preceding them. Any labels found will be
	 * added to the label map.
	 * <p>
	 * parseLabels will remove any of the label declarations from the source
	 * list. The origin statements will still be present in the code.
	 * 
	 * @param code
	 *            The input code, with no extra spaces and without any .NAMES
	 *            lines.
	 */
	private void parseLabels(List<String> code) {
		int address = 0;

		// Create the required patterns and matchers
		Pattern origPattern = Pattern.compile(".ORIG (.++)");
		Pattern labelPattern = Pattern.compile("(\\w++):");
		Matcher origMatcher = origPattern.matcher("");
		Matcher labelMatcher = labelPattern.matcher("");

		// Iterate over each line of code
		Iterator<String> i = code.listIterator();
		while (i.hasNext()) {

			String line = i.next();

			origMatcher.reset(line);
			labelMatcher.reset(line);

			if (origMatcher.matches()) {
				// If the line is an origin line, set the address to the
				// requested origin.
				address = parseLiteral(origMatcher.group(1));

			} else if (labelMatcher.matches()) {
				// If the address is a label, add the label and the current
				// address to the label map. Furthermore, remove the label
				// declaration from the source list.
				labels.put(labelMatcher.group(1), address >> 2);
				i.remove();

			} else {
				// If the line is an instruction, increment the address as
				// required.
				address = address + Integer.BYTES;
			}
		}
	}

	/**
	 * Parses a string into an integer, selecting the correct Radix for the
	 * conversion as required. Strings are assumed to be decimal unless they are
	 * preceded by a 0x or ended by a h
	 * 
	 * @param literal
	 *            The string literal to parse
	 * @return The value as an integer
	 */
	private int parseLiteral(String literal) {
		Pattern immediates = Pattern.compile("(0X|X)?(\\-?[\\dA-F]++)?");
		Matcher matcher = immediates.matcher(literal);

		// Check if the immediate is a number
		if (!matcher.matches()) {
			throw new NumberFormatException("The number " + literal + " cannot be parsed into an integer");
		}

		// Check if the number is in hex
		// Otherwise, assume the number is in base 10
		
		// The cast to long and back to integer are neccesary, otherwise, the
		// valueOf will fail to parse String literals whose equivalent integers
		// would be negative.
		if (matcher.group(1) != null) {
			return (int)(Long.valueOf(matcher.group(2), 16) & 0xFFFFFFFF);
		} else {
			return (int)(Long.valueOf(matcher.group(2)) & 0xFFFFFFFF);
		}
	}
	
	/**
	 * Parses a list of .NAME strings, adding the relevant entries to the
	 * reference Map and removing them from the source list.
	 * 
	 * @param code
	 *            The source code, with the .NAME declaration
	 */
	private void parseNames(List<String> code) {
		Pattern pattern = Pattern.compile(".NAME (\\w++)=(\\w++)");
		Matcher matcher = pattern.matcher("");

		code.removeIf(l -> {
			matcher.reset(l);

			// Do not remove anything except .NAME statements
			if (!matcher.matches()) return false;

			String label = matcher.group(1);
			int value = parseLiteral(matcher.group(2));

			// Add the NAME to the label's map
			labels.put(label, value);
			return true;
		});
	}

	/**
	 * Iterates through the list, replacing any pseudocodes with its
	 * corresponding instruction
	 * 
	 * @param code The input code
	 */
	private void replacePseudoCodes(List<String> code) {
		ListIterator<String> i = code.listIterator();

		Pattern patternBR = Pattern.compile("BR (\\-?\\w++)");
		Pattern patternNOT = Pattern.compile("NOT (\\w++),(\\w++)");
		Pattern patternBLE = Pattern.compile("BLE (\\w++),(\\w++),(\\-?\\w++)");
		Pattern patternBGE = Pattern.compile("BGE (\\w++),(\\w++),(\\-?\\w++)");
		Pattern patternCALL = Pattern.compile("CALL (\\-?\\w++\\(\\w++\\))");
		Pattern patternJMP = Pattern.compile("JMP (\\-?\\w++\\(\\w++\\))");

		Matcher matcherBR = patternBR.matcher("");
		Matcher matcherNOT = patternNOT.matcher("");
		Matcher matcherBLE = patternBLE.matcher("");
		Matcher matcherBGE = patternBGE.matcher("");
		Matcher matcherCALL = patternCALL.matcher("");
		Matcher matcherJMP = patternJMP.matcher("");

		while (i.hasNext()) {
			String instruction = i.next();

			String opcode = instruction.replaceAll(" .++\\Z", "");
			switch (opcode) {
			case "BR":
				matcherBR.reset(instruction);
				if (!matcherBR.matches())
					throw new IllegalArgumentException("The instruction " + instruction + " is invalid");
				i.set("BEQ R6,R6," + matcherBR.group(1));
				break;
			case "NOT":
				matcherNOT.reset(instruction);
				if (!matcherNOT.matches())
					throw new IllegalArgumentException("The instruction " + instruction + " is invalid");
				i.set("NAND " + matcherNOT.group(1) + "," + matcherNOT.group(2) + "," + matcherNOT.group(2));
				break;
			case "BLE":
				matcherBLE.reset(instruction);
				if (!matcherBLE.matches())
					throw new IllegalArgumentException("The instruction " + instruction + " is invalid");
				i.set("LTE R6," + matcherBLE.group(1) + "," + matcherBLE.group(2));
				i.add("BNEZ R6," + matcherBLE.group(3));
				break;
			case "BGE":
				matcherBGE.reset(instruction);
				if (!matcherBGE.matches())
					throw new IllegalArgumentException("The instruction " + instruction + " is invalid");
				i.set("GTE R6," + matcherBGE.group(1) + "," + matcherBGE.group(2));
				i.add("BNEZ R6," + matcherBGE.group(3));
				break;
			case "CALL":
				matcherCALL.reset(instruction);
				if (!matcherCALL.matches())
					throw new IllegalArgumentException("The instruction " + instruction + " is invalid");
				i.set("JAL RA," + matcherCALL.group(1));
				break;
			case "RET":
				i.set("JAL R9,0(RA)");
				break;
			case "JMP":
				matcherJMP.reset(instruction);
				if (!matcherJMP.matches())
					throw new IllegalArgumentException("The instruction " + instruction + " is invalid");
				i.set("JAL R9," + matcherJMP.group(1));
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * Translates all ALU-I and CMP-I instructions except MVHI into its
	 * corresponding bytecode.
	 * 
	 * @param opcode
	 *            The instruction's name, as a String
	 * @param params
	 *            The comma separated instruction parameters
	 * @return The instruction's bytecode, as a 32-bit integer
	 */
	private int translateAluCmpI(String opcode, String params) {
		String[] paramArray = params.split(",");

		// Check the number of parameters
		if (paramArray.length != 3) {
			throw new IllegalArgumentException("The instruction " + opcode + " " + params + " is illegal");
		}
		
		// Validate the register names
		if (!REGISTERS.containsKey(paramArray[0]) || !REGISTERS.containsKey(paramArray[1])) {
			throw new IllegalArgumentException("The instruction " + opcode + " " + params + " is illegal");
		}
		
		// Parse the immediate
		int immediate = 0;
		try {
			// Try to parse the immediate as a number
			immediate = parseLiteral(paramArray[2]);

			// Check that the immediate is within bounds
			if (immediate < Short.MIN_VALUE || immediate > Short.MAX_VALUE) {
				throw new IllegalArgumentException("The immediate " + paramArray[2] + " is too large.");
			}

		} catch (NumberFormatException e) {
			if (labels.containsKey(paramArray[2])) {
				// If that fails, try to parse it as a label
				immediate = labels.get(paramArray[2]);
				// The immediate bounds are not checked, for labels the lower 16
				// bits are used and the rest is truncated.
			} else {
				throw new IllegalArgumentException("The label " + paramArray[2] + " cannot be found");
			}
		}

		int compiledLine = 0;
		
		// Add the opcode
		compiledLine |= ALU_CMP_I.get(opcode);
		
		// Add the destination register
		compiledLine |= REGISTERS.get(paramArray[0]) << 28;
		
		// Add the source register
		compiledLine |= REGISTERS.get(paramArray[1]) << 24;
		
		//Add the immediate
		compiledLine |= (immediate & 0xFFFF) << 8;
		
		return compiledLine;
	}

	/**
	 * Translates all ALU-R and CMP-R instructions into their corresponding
	 * bytecode.
	 * 
	 * @param opcode
	 *            The instruction's name, as a String
	 * @param params
	 *            The comma separated instruction parameters
	 * @return The instruction's bytecode, as a 32 bit integer
	 */
	private int translateAluCmpR(String opcode, String params){
		String[] paramArray = params.split(",");

		// Check the number of parameters
		if (paramArray.length != 3) {
			throw new IllegalArgumentException("The instruction " + opcode + " " + params + " is illegal");
		}
		
		// Validate the register names
		if (!REGISTERS.containsKey(paramArray[0]) || !REGISTERS.containsKey(paramArray[1])
				|| !REGISTERS.containsKey(paramArray[2])) {
			
			throw new IllegalArgumentException("The instruction " + opcode + " " + params + " is illegal");
		}

		int compiledLine = 0;
		
		// Add the opcode
		compiledLine |= ALU_CMP_R.get(opcode);
		
		// Add the destination register
		compiledLine |= REGISTERS.get(paramArray[0]) << 28;
		
		// Add the source 1 register
		compiledLine |= REGISTERS.get(paramArray[1]) << 24;
		
		// Add the source 2 register
		compiledLine |= REGISTERS.get(paramArray[2]) << 20;
		
		return compiledLine;
	}
	
	/**
	 * Translates all 2-argument branch statements
	 * 
	 * @param opcode
	 *            The instruction name
	 * @param params
	 *            The comma separated instruction parameters
	 * @param address
	 *            The instruction's address
	 * @return The compiled instruction, as a 32-bit integer
	 */
	private int translateBranch2Arg(String opcode, String params, int address){
		String[] paramArray = params.split(",");

		// Check the number of parameters
		if (paramArray.length != 2) {
			throw new IllegalArgumentException("The instruction " + opcode + " " + params + " is illegal");
		}

		// Validate the register names
		if (!REGISTERS.containsKey(paramArray[0])) {
			throw new IllegalArgumentException("The instruction " + opcode + " " + params + " is illegal");
		}

		// Parse the immediate
		int immediate = 0;
		try {
			// Try to parse the immediate as a number
			immediate = parseLiteral(paramArray[1]);
		} catch (NumberFormatException e) {
			if (labels.containsKey(paramArray[1])) {
				// If that fails, try to parse it as a label
				immediate = labels.get(paramArray[1]) - address - 1;
			} else {
				throw new IllegalArgumentException("The label " + paramArray[1] + " cannot be found");
			}
		}
		
		// Check that the immediate is within bounds
		if (immediate < Short.MIN_VALUE || immediate > Short.MAX_VALUE) {
			throw new IllegalArgumentException("The offset on \"" + opcode + params +"\" is too large");
		}

		int compiledLine = 0;

		// Add the opcode
		compiledLine |= BRANCH_2ARG.get(opcode);

		// Add the first register
		compiledLine |= REGISTERS.get(paramArray[0]) << 28;

		// Add the immediate
		compiledLine |= (immediate & 0xFFFF) << 8;

		return compiledLine;
	}
	
	/**
	 * Translates all 3-argument branch statements
	 * 
	 * @param opcode
	 *            The instruction name
	 * @param params
	 *            The instruction comma separated parameters
	 * @param address
	 *            The instruction's address
	 * @return The compiled instruction, as a 32-bit integer
	 */
	private int translateBranch3Arg(String opcode, String params, int address) {
		String[] paramArray = params.split(",");

		// Check the number of parameters
		if (paramArray.length != 3) {
			throw new IllegalArgumentException("The instruction " + opcode + " " + params + " is illegal");
		}

		// Validate the register names
		if (!REGISTERS.containsKey(paramArray[0]) || !REGISTERS.containsKey(paramArray[1])) {
			throw new IllegalArgumentException("The instruction " + opcode + " " + params + " is illegal");
		}

		// Parse the immediate
		int immediate = 0;
		try {
			// Try to parse the immediate as a number
			immediate = parseLiteral(paramArray[2]);
		} catch (NumberFormatException e) {
			if (labels.containsKey(paramArray[2])) {
				// If that fails, try to parse it as a label
				immediate = labels.get(paramArray[2]) - address - 1;
			} else {
				throw new IllegalArgumentException("The label " + paramArray[2] + " cannot be found");
			}
		}
		
		// Check that the immediate is within bounds
		if (immediate < Short.MIN_VALUE || immediate > Short.MAX_VALUE) {
			throw new IllegalArgumentException("The offset on \"" + opcode + params +"\" is too large");
		}

		int compiledLine = 0;

		// Add the opcode
		compiledLine |= BRANCH_3ARG.get(opcode);

		// Add the second register
		compiledLine |= REGISTERS.get(paramArray[0]) << 28;

		// Add the first register
		compiledLine |= REGISTERS.get(paramArray[1]) << 24;

		// Add the immediate
		compiledLine |= (immediate & 0xFFFF) << 8;

		return compiledLine;
	}
	
	/**
	 * Transforms the code from assembly instructions to the binary code
	 * 
	 * @param code
	 *            The source assembly code.
	 */
	private List<String> translateCode(List<String> code) {
		Pattern origPattern = Pattern.compile(".ORIG (.++)");
		Matcher origMatcher = origPattern.matcher("");

		List<String> compiledCode = new ArrayList<>();

		int address = 0;

		for (String codeLine : code) {

			// Handle .ORIG tags separately
			origMatcher.reset(codeLine);
			if (origMatcher.matches()) {
				// Parse the address
				int oldAddress = address;
				address = parseLiteral(origMatcher.group(1)) >> 2;

				// Fill the empty memory with the bytes DEAD
				if (oldAddress > address) {
					throw new UnsupportedOperationException(
							"The assembler does not support origin statements going up");
				} else {
					String deadMemory = String.format("[%08x:%08x] : DEAD;", oldAddress, address - 1);
					compiledCode.add(deadMemory);
				}

			} else {
				// Divide the code into the opcode and the parameters.
				String[] tmpArr = codeLine.split(" ", 2);
				String opcode = tmpArr[0];
				String params = tmpArr[1];

				// Add the comment line
				String comment = String.format("-- @ 0x%08x : %-8s %s", address << 2, opcode, params);
				compiledCode.add(comment);

				int byteCode = 0;

				if (ALU_CMP_R.containsKey(opcode)) {
					byteCode = translateAluCmpR(opcode, params);
				} else if (ALU_CMP_I.containsKey(opcode)) {
					byteCode = translateAluCmpI(opcode, params);
				} else if (BRANCH_3ARG.containsKey(opcode)) {
					byteCode = translateBranch3Arg(opcode, params, address);
				} else if (BRANCH_2ARG.containsKey(opcode)) {
					byteCode = translateBranch2Arg(opcode, params, address);
				} else if (opcode.equals("MVHI")) {
					byteCode = translateMVHI(params);
				} else if (opcode.equals("JAL")) {
					byteCode = translateJAL(params, address);
				} else if (opcode.equals("SW")) {
					byteCode = translateSW(params);
				} else if (opcode.equals("LW")) {
					byteCode = translateLW(params);
				} else if (opcode.equals(".WORD")) {
					byteCode = translateWord(params);
				} else {
					throw new UnsupportedOperationException("The opcode " + opcode + " is not supported");
				}

				String compiledLine = String.format("%08x : %08x;", address, byteCode);
				compiledCode.add(compiledLine);
				address ++;
			}
		}
		
		String deadMemory = String.format("[%08x:%08x] : DEAD;", address, DEPTH - 1);
		compiledCode.add(deadMemory);
		
		return compiledCode;
	}
	
	/**
	 * Translates JAL statements
	 * @param params The comma separated parameters
	 * @param address The current address
	 * @return The compiled bytecode
	 */
	private int translateJAL(String params, int address){
		Pattern jalPattern = Pattern.compile("(\\w++),(\\-?\\w++)\\((\\w++)\\)");
		Matcher jalMatcher = jalPattern.matcher(params);
		
		// Check the number of parameters
		if (!jalMatcher.matches()) {
			throw new IllegalArgumentException("The instruction JAL " + params + " is illegal");
		}

		// Validate the register names
		if (!REGISTERS.containsKey(jalMatcher.group(1)) || !REGISTERS.containsKey(jalMatcher.group(3))) {
			throw new IllegalArgumentException("The instruction JAL "+ params + " is illegal");
		}

		// Parse the immediate
		int immediate = 0;
		try {
			// Try to parse the immediate as a number
			immediate = parseLiteral(jalMatcher.group(2));
		} catch (NumberFormatException e) {
			if (labels.containsKey(jalMatcher.group(2))) {
				// If that fails, try to parse it as a label
				immediate = labels.get(jalMatcher.group(2));
			} else {
				throw new IllegalArgumentException("The label " + jalMatcher.group(2) + " cannot be found");
			}
		}
		
		// Check that the immediate is within bounds
		if (immediate < Short.MIN_VALUE || immediate > Short.MAX_VALUE) {
			throw new IllegalArgumentException("The offset on \"JAL " + params +"\" is too large");
		}

		int compiledLine = 0;

		// Add the opcode
		compiledLine |= 0x0b;

		// Add the second register
		compiledLine |= REGISTERS.get(jalMatcher.group(1)) << 28;

		// Add the first register
		compiledLine |= REGISTERS.get(jalMatcher.group(3)) << 24;

		// Add the immediate
		compiledLine |= (immediate & 0xFFFF) << 8;

		return compiledLine;
	}
	
	/**
	 * Translates load word instructions
	 * 
	 * @param params
	 *            The comma separated parameters
	 * @return The compiled code
	 */
	private int translateLW(String params){
		Pattern lwPattern = Pattern.compile("(\\w++),(\\-?\\w++)\\((\\w++)\\)");
		Matcher lwMatcher = lwPattern.matcher(params);
		
		// Check the number of parameters
		if (!lwMatcher.matches()) {
			throw new IllegalArgumentException("The instruction LW " + params + " is illegal");
		}

		// Validate the register names
		if (!REGISTERS.containsKey(lwMatcher.group(1)) || !REGISTERS.containsKey(lwMatcher.group(3))) {
			throw new IllegalArgumentException("The instruction LW "+ params + " is illegal");
		}

		// Parse the immediate
		int immediate = 0;
		try {
			// Try to parse the immediate as a number
			immediate = parseLiteral(lwMatcher.group(2));

			// Check that the immediate is within bounds
			if (immediate < Short.MIN_VALUE || immediate > Short.MAX_VALUE)
				throw new IllegalArgumentException("The immediate on \"LW " + params + "\" is too large");

		} catch (NumberFormatException e) {
			if (labels.containsKey(lwMatcher.group(2))) {
				// If that fails, try to parse it as a label
				immediate = labels.get(lwMatcher.group(2));
			} else {
				throw new IllegalArgumentException("The label " + lwMatcher.group(2) + " cannot be found");
			}
		}

		int compiledLine = 0;

		// Add the opcode
		compiledLine |= 0x09;

		// Add the destination register
		compiledLine |= REGISTERS.get(lwMatcher.group(1)) << 28;

		// Add the base register
		compiledLine |= REGISTERS.get(lwMatcher.group(3)) << 24;

		// Add the immediate
		compiledLine |= (immediate & 0xFFFF) << 8;

		return compiledLine;
	}
	
	/**
	 * Translates the MVHI statement
	 * @param params The comma separated parameters
	 * @return The corresponding bytecode as a 32 bit integer
	 */
	private int translateMVHI(String params){
		String[] paramArray = params.split(",");

		// Check the number of parameters
		if (paramArray.length != 2) {
			throw new IllegalArgumentException("The instruction MVHI " + params + " is illegal");
		}
		
		// Validate the register names
		if (!REGISTERS.containsKey(paramArray[0])) {
			throw new IllegalArgumentException("The instruction MVHI " + params + " is illegal");
		}
		
		// Parse the immediate
		int immediate = 0;
		try {
			// Try to parse the immediate as a number
			immediate = parseLiteral(paramArray[1]);

			// Check that the immediate is within bounds
			if (immediate < Short.MIN_VALUE || immediate > Short.MAX_VALUE) {
				throw new IllegalArgumentException("The immediate " + paramArray[1] + " is too large.");
			}

		} catch (NumberFormatException e) {
			if (labels.containsKey(paramArray[1])) {
				// If that fails, try to parse it as a label
				immediate = labels.get(paramArray[1]);
				// The immediate bounds are not checked, for labels the lower 16
				// bits are used and the rest is truncated.
			} else {
				throw new IllegalArgumentException("The label " + paramArray[1] + " cannot be found");
			}
		}

		int compiledLine = 0;
		
		// Add the opcode
		compiledLine |= 0xb8;
		
		// Add the destination register
		compiledLine |= REGISTERS.get(paramArray[0]) << 28;
				
		//Add the immediate
		compiledLine |= (immediate & 0xFFFF) << 8;
		
		return compiledLine;
	}
	
	/**
	 * Translates the store word instruction
	 * 
	 * @param params
	 *            The comma separated parameters
	 * @return The compiled instruction
	 */
	private int translateSW(String params){
		Pattern swPattern = Pattern.compile("(\\w++),(\\-?\\w++)\\((\\w++)\\)");
		Matcher swMatcher = swPattern.matcher(params);
		
		// Check the number of parameters
		if (!swMatcher.matches()) {
			throw new IllegalArgumentException("The instruction SW " + params + " is illegal");
		}

		// Validate the register names
		if (!REGISTERS.containsKey(swMatcher.group(1)) || !REGISTERS.containsKey(swMatcher.group(3))) {
			throw new IllegalArgumentException("The instruction SW "+ params + " is illegal");
		}

		// Parse the immediate
		int immediate = 0;
		try {
			// Try to parse the immediate as a number
			immediate = parseLiteral(swMatcher.group(2));
			// Check that the immediate is within bounds
			if (immediate < Short.MIN_VALUE || immediate > Short.MAX_VALUE)
				throw new IllegalArgumentException("The offset on \"SW " + params + "\" is too large");
		} catch (NumberFormatException e) {
			if (labels.containsKey(swMatcher.group(2))) {
				// If that fails, try to parse it as a label
				immediate = labels.get(swMatcher.group(2));
			} else {
				throw new IllegalArgumentException("The label " + swMatcher.group(2) + " cannot be found");
			}
		}

		int compiledLine = 0;

		// Add the opcode
		compiledLine |= 0x05;

		// Add the base register
		compiledLine |= REGISTERS.get(swMatcher.group(3)) << 28;

		// Add the source register
		compiledLine |= REGISTERS.get(swMatcher.group(1)) << 24;

		// Add the immediate
		compiledLine |= (immediate & 0xFFFF) << 8;

		return compiledLine;
	}
	
	/**
	 * Translates a .WORD statement
	 * @param param The parameter
	 * @return The corresponding bytecode
	 */
	private int translateWord(String param) {
		// Parse the bytecode
		int byteCode;
		try {
			// Try to parse the parameter as a number
			byteCode = parseLiteral(param);
		} catch (NumberFormatException e) {
			if (labels.containsKey(param)) {
				// If that fails, try to parse it as a label
				byteCode = labels.get(param);
			} else {
				throw new IllegalArgumentException("The label " + param + " cannot be found");
			}
		}
		return byteCode;
	}
}
