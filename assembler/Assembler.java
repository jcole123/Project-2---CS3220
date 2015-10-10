import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.nio.file.Files;

/*
 * TODO: Handle  .WORD TODO: Support immediate instructions and fix regular instructions
 *  Enter filename as argument: Ex. java assembler simple.a32
 */

/*
 * The labels table is now working. The integer has byte-addressed addreses, so
 * they need to be truncated later. I'll do that. I did rewrite a lot of the
 * code you had because, for instance, your loop assumed code began at line 16
 * and that all lines are continuous (no intermediate orig keywords).
 * 
 * I'm using the origin keyword to find the starting address (or jumps). Right
 * now, the code I've written extracts all labels and calculates their
 * appropiate values. See the end of the console printout. I'm also placing
 * .NAME constants in the same table; that may change if I think it's not going
 * to work.
 * 
 * I'll get the translation working tomorrow.
 * 
 * Note that before translating, all pseudocodes need to be replaced with their
 * equivalents. I'll try to get that running tomorrow as well.
 */
public class Assembler {	
	String header = "WIDTH=32;\nDEPTH=2048;\nADDRESS_RADIX=HEX;\nDATA_RADIX=HEX;\nCONTENT BEGIN\n";
	static HashMap<String, Integer> table;

	private HashMap<String, Integer> labels = new HashMap<String, Integer>();
	
	private static final Map<String, Integer> ALU_CMP_R = new HashMap<>();
	private static final Map<String, Integer> ALU_CMP_I = new HashMap<>();
	private static final Map<String, Integer> LW_SW = new HashMap<>();
	private static final Map<String, Integer> BRANCH_2ARG = new HashMap<>();
	private static final Map<String, Integer> BRANCH_3ARG = new HashMap<>();
	private static final HashMap<String, Integer> REGISTER = new HashMap<>();
	
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

		LW_SW.put("SW", 0x05);
		LW_SW.put("LW", 0x09);
		
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
		
		REGISTER.put("A0", 0);
		REGISTER.put("A1", 1);
		REGISTER.put("A2", 2);
		REGISTER.put("A3", 3);
		REGISTER.put("RV", 3);
		REGISTER.put("T0", 4);
		REGISTER.put("T1", 5);
		REGISTER.put("S0", 6);
		REGISTER.put("S1", 7);
		REGISTER.put("S2", 8);
		REGISTER.put("GP", 12);
		REGISTER.put("FP", 13);
		REGISTER.put("SP", 14);
		REGISTER.put("RA", 15);
		for (int i = 0; i < 16; i++)
			REGISTER.put("R"+i, i);
	}

	public static void main(String[] args) {

		// Check if the required arguments have been provided
		if (args.length != 1) {
			System.err.println("Please select an input file.");
			System.err.println("Syntax: java Assembler sample.a32");
			return;
		}

		// Check if the input file exists
		File asmFile = new File(args[0]);
		if (!asmFile.exists()) {
			System.err.println("The file " + args[0] + "does not exist");
		}
		
		init();
		
		Assembler test = new Assembler();	
		test.assemble(asmFile);
		
		

		//test.readFile(args[0]);
	}

	public static void init() {
		table = new HashMap<String, Integer>();
		// ALU-R
		table.put("AND", 0x40);
		table.put("XOR", 0x60);
		table.put("ADD", 0x00);
		table.put("SUB", 0x10);
		table.put("OR", 0x50);
		table.put("NAND", 0xc0);
		table.put("NOR", 0xd0);
		table.put("XNOR", 0xe0);
		// ALU-I
		table.put("ADDI", 0x08);
		table.put("SUBI", 0x18);
		table.put("ANDI", 0x48);
		table.put("ORI", 0x58);
		table.put("XORI", 0x68);
		table.put("NANDI", 0xc8);
		table.put("NORI", 0xd8);
		table.put("XNORI", 0xe8);
		table.put("MVHI", 0xb8);
		// Load/Store
		table.put("SW", 0x05);
		table.put("LW", 0x09);
		// CMP-R
		table.put("F", 0x02);
		table.put("EQ", 0x12);
		table.put("LT", 0x22);
		table.put("LTE", 0x32);
		table.put("T", 0x82);
		table.put("NE", 0x92);
		table.put("GTE", 0xa2);
		table.put("GT", 0xb2);
		// CMP-I
		table.put("FI", 0x0a);
		table.put("EQI", 0x1a);
		table.put("LTI", 0x2a);
		table.put("LTEI", 0x3a);
		table.put("TI", 0x8a);
		table.put("NEI", 0x9a);
		table.put("GTEI", 0xaa);
		table.put("GTI", 0xba);
		// BRANCH
		table.put("BF", 0x06);
		table.put("BEQ", 0x16);
		table.put("BLT", 0x26);
		table.put("BLTE", 0x36);
		table.put("BEQZ", 0x56);
		table.put("BLTZ", 0x66);
		table.put("BLTEZ", 0x76);
		table.put("BT", 0x86);
		table.put("BNE", 0x96);
		table.put("BGTE", 0xa6);
		table.put("BGT", 0xb6);
		table.put("BNEZ", 0xd6);
		table.put("BGTEZ", 0xe6);
		table.put("BGTZ", 0xf6);
		table.put("JAL", 0x0b);
		// TODO: Pseudo ops
	}

	public void assemble(File input){
		try {
			// Read the file
			List<String> asmCode = readFile(input);
			
			// Parse the .NAME lines into a new list
			parseNames(asmCode);
			
			// Replace any pseudocodes with their equivalents
			replacePseudoCodes(asmCode);

			// Parse any labels into the labels map
			parseLabels(asmCode);
			
			// Translate the code
			List<String> compiledCode = translateCode(asmCode);
			
			// Debug statements
			compiledCode.forEach(l->System.out.println(l));
			System.out.println();
			System.out.println("-----------------------------------");
			System.out.println("-----------Labels------------------");
			System.out.println("-----------------------------------");
			System.out.println();
			labels.forEach((key, value) -> System.out.println(key + ":0x" + String.format("%08X", value)));
			
		} catch (IOException e) {
			System.err.println("The file " + input.getName() + " could not be read.");
		}
	}
	
	/**
	 * Iterates through the list, replacing any pseudocodes with its
	 * corresponding instruction
	 * 
	 * @param code The input code
	 */
	private void replacePseudoCodes(List<String> code) {
		ListIterator<String> i = code.listIterator();

		Pattern patternBR = Pattern.compile("BR (\\w++)");
		Pattern patternNOT = Pattern.compile("NOT (\\w++),(\\w++)");
		Pattern patternBLE = Pattern.compile("BLE (\\w++),(\\w++),(\\w++)");
		Pattern patternBGE = Pattern.compile("BGE (\\w++),(\\w++),(\\w++)");
		Pattern patternCALL = Pattern.compile("CALL (\\w++\\(\\w++\\))");
		Pattern patternJMP = Pattern.compile("JMP (\\w++\\(\\w++\\))");

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
				address = parseLiteral(origMatcher.group(1));
				continue;
			}
			
			// Divide the code into the opcode and the parameters.
			String[] tmpArr = codeLine.split(" ", 2);
			String opcode = tmpArr[0];
			String params = tmpArr[1];
			
			// Add the comment line
			String comment = String.format("-- @ 0x%08x : %-8s %s", address, opcode, params);
			compiledCode.add(comment);
			
			String tmp = null;

			if (ALU_CMP_R.containsKey(opcode)) {
				tmp = translate(codeLine);
			} else if (ALU_CMP_I.containsKey(opcode)) {
				tmp = String.format("%08x", translateAluCmpI(opcode, params));
			} else if (LW_SW.containsKey(opcode)) {
				// TODO
			} else if (BRANCH_3ARG.containsKey(opcode)) {
				tmp = String.format("%08x", translateBranch3Arg(opcode, params, address));
			} else if (BRANCH_2ARG.containsKey(opcode)) {
				tmp = String.format("%08x", translateBranch2Arg(opcode, params, address));
			}else if (opcode.equals("MVHI")){
				// TODO
			} else if (opcode.equals(".WORD")) {
				// TODO
			} else if (opcode.equals("JAL")){
				// TODO
			} else {
				throw new UnsupportedOperationException("The opcode " + opcode + " is not supported");
			}

			String compiledLine = String.format("%08x : %s", address >> 2, tmp);
			compiledCode.add(compiledLine);

			address += Integer.BYTES;
		}
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
	private List<String> readFile(File file) throws IOException {
		// Load the assembly file into a new list.
		List<String> text = Files.readAllLines(file.toPath());

		text.replaceAll(line -> {
			// Convert multiple spaces and tabs into a single space
			// Convert any lower case elements to upper case
			line = line.replaceAll("\\s++", " ").toUpperCase();

			// Remove leading and trailing spaces and comments
			line = line.replaceAll(";.++", "").trim();
			
			// Remove spaces before and after equal signs, commas, parenthesis,
			// and colons.
			return line.replaceAll("(?<=[()=,]) | (?=[()=,])", "");
		});

		// Remove all blank lines
		text.removeIf(line -> line.isEmpty());
		return text;
	}

	
	/**
	 * Parses a list of .NAME strings, adding the relevant entries to the
	 * reference Map and removing them from the source list.
	 * 
	 * @param code
	 *            The source code, with the .NAME declaration
	 */
	private void parseNames(List<String> code) {
		Pattern pattern = Pattern.compile(".NAME ([^=]++)=([^=]++)");
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
	 * Parses a string into an integer, selecting the correct Radix for the
	 * conversion as required. Strings are assumed to be decimal unless they are
	 * preceded by a 0x or ended by a h
	 * 
	 * @param literal
	 *            The string literal to parse
	 * @return The value as an integer
	 */
	private int parseLiteral(String literal) {
		Pattern immediates = Pattern.compile("(0X|X)?([\\dA-F\\-]++)(h)?");
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
		if (matcher.group(1) != null || matcher.group(3) != null) {
			return (int)(Long.valueOf(matcher.group(2), 16) & 0xFFFFFFFF);
		} else {
			return (int)(Long.valueOf(matcher.group(2)) & 0xFFFFFFFF);
		}
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
		Pattern labelPattern = Pattern.compile("([^:]++):");
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
				labels.put(labelMatcher.group(1), address);
				i.remove();

			} else {
				// If the line is an instruction, increment the address as
				// required.
				address = address + Integer.BYTES;
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
		if (!REGISTER.containsKey(paramArray[0]) || !REGISTER.containsKey(paramArray[1])) {
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
		compiledLine |= REGISTER.get(paramArray[0]) << 28;
		
		// Add the source register
		compiledLine |= REGISTER.get(paramArray[1]) << 24;
		
		//Add the immediate
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
		if (!REGISTER.containsKey(paramArray[0]) || !REGISTER.containsKey(paramArray[1])) {
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
				immediate = labels.get(paramArray[2]) - address - 4;
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
		compiledLine |= REGISTER.get(paramArray[1]) << 28;

		// Add the first register
		compiledLine |= REGISTER.get(paramArray[0]) << 24;

		// Add the immediate
		compiledLine |= (immediate & 0xFFFF) << 8;

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
		if (!REGISTER.containsKey(paramArray[0])) {
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
				immediate = labels.get(paramArray[1]) - address - 4;
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
		compiledLine |= REGISTER.get(paramArray[0]) << 28;

		// Add the immediate
		compiledLine |= (immediate & 0xFFFF) << 8;

		return compiledLine;
	}
	
	public void readFile(String fileName) {
		int line = 16;
		int comment;
		try {
			FileReader base = new FileReader(fileName);
			BufferedReader br = new BufferedReader(base);
			FileWriter out = new FileWriter(fileName.substring(0, fileName.indexOf(".")) + ".mif");
			String s;
			String count;
			out.write(header);
			while ((s = br.readLine()) != null) {
				s = s.toUpperCase();
				s = s.trim();
				comment = s.indexOf(";");
				// remove any comments
				if (comment != -1) s = s.substring(0, comment);
				s = s + "\n";
				if (s.charAt(0) != ';' && s.charAt(0) != '.' && s.length() > 2) {
					String temp = translate(s);
					// System.out.println(temp);
					out.append("-- @ 0x" + String.format("%08X", line << 2) + " : " + String.format("%-7s", s)
							+ String.format("%08X", line) + " : " + temp);
					line++;
				}
			}
			base.close();
			out.close();
		} catch (Exception E) {
			System.out.println(E.getMessage());
		}
	}

	/*
	 * Given a string containing an opcode/registers/imm, convert to string of
	 * bytes Currently only working for ALU-R and CMP-R
	 */
	public String translate(String temp) {
		String[] args = temp.split(",");

		String translation = "";
		// should never happen
		if (args == null) {
			System.out.println("error");
			return "ERROR";
		}
		// Split op and first register
		String[] operation = args[0].split("\\s+");
		// remove whitespace so string can be processed in hashmap
		for (int i = 0; i < args.length; i++)
			args[i] = args[i].trim();
		String rd = Integer.toHexString(REGISTER.get(operation[1]));
		String rs1 = Integer.toHexString(REGISTER.get(args[1]));
		String opcode = String.format("%02X", table.get(operation[0]));
		// Padding
		String zero = "000"; // 12 bits
		if (REGISTER.get(args[2]) != null) {
			String rs2 = Integer.toHexString(REGISTER.get(args[2]));
			translation = rd + rs1 + rs2 + zero + opcode;
		} else {
			int imm = Integer.parseInt(args[2]);
			translation = rd + rs1 + String.format("%04X", imm) + opcode;
		}
		return translation;
	}
}
