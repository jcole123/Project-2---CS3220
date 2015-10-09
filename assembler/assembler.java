import java.util.*;
import java.io.*;


/*
 * TODO: Handle lables, .ORIG, .WORD, and .NAME
 * TODO: Support immediate instructions 
 * Enter filename as argument: Ex. java assembler simple.a32
 */ 
public class assembler {
	String header = "WIDTH=32;\nDEPTH=2048;\nADDRESS_RADIX=HEX;\nDATA_RADIX=HEX;\nCONTENT BEGIN\n";
	static HashMap<String,Integer> table;
	static HashMap<String,Integer> registers;
	//Hold label address values
	//TODO: Iterate through assembly and store label values before translation
	HashMap<String,Integer> labels = new HashMap<String,Integer>();
	public static void main (String[] args) {
		assembler test = new assembler();
		init();
		test.readFile(args[0]);
	}
	public static void init() {
		registers = new HashMap<String, Integer>();
		registers.put("A0", 0);
		registers.put("A1", 1);
		registers.put("A2", 2);
		registers.put("A3", 3);
		registers.put("RV", 3);
		registers.put("T0", 4);
		registers.put("T1", 5);
		registers.put("S0", 6);
		registers.put("S1", 7);
		registers.put("S2", 8);
		registers.put("GP", 12);
		registers.put("FP", 13);
		registers.put("SP", 14);
		registers.put("RA", 15);
		table = new HashMap<String, Integer>();
		//ALU-R
		table.put("AND", 0x40);
		table.put("XOR", 0x60);
		table.put("ADD", 0x00);
		table.put("SUB", 0x10);
		table.put("OR", 0x50);
		table.put("NAND",0xc0); 
		table.put("NOR", 0xd0);
		table.put("XNOR", 0xe0);
		//ALU-I
		table.put("ADDI", 0x08);
		table.put("SUBI", 0x18);
		table.put("ANDI", 0x48);
		table.put("ORI", 0x58);
		table.put("XORI", 0x68);
		table.put("NANDI", 0xc8);
		table.put("NORI", 0xd8);
		table.put("XNORI", 0xe8);
		table.put("MVHI", 0xb8);
		//Load/Store
		table.put("SW", 0x05);
		table.put("LW", 0x09);
		//CMP-R
		table.put("F", 0x02);
		table.put("EQ", 0x12);
		table.put("LT", 0x22);
		table.put("LTE", 0x32);
		table.put("T", 0x82);
		table.put("NE", 0x92);
		table.put("GTE", 0xa2);
		table.put("GT", 0xb2);
		//CMP-I
		table.put("FI", 0x0a);
		table.put("EQI", 0x1a);
		table.put("LTI", 0x2a);
		table.put("LTEI", 0x3a);
		table.put("TI", 0x8a);
		table.put("NEI", 0x9a);
		table.put("GTEI", 0xaa);
		table.put("GTI", 0xba);
		//BRANCH
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
		//TODO: Pseudo ops
		 
	}
	public void readFile(String fileName) { 
		int line = 16;
		int comment;
		try {
		FileReader base = new FileReader(fileName);
		BufferedReader br = new BufferedReader(base);
		FileWriter out = new FileWriter(fileName.substring(0,fileName.indexOf(".")) + ".mif");
		String s;
		String count;
		out.write(header);
		while((s = br.readLine()) != null) {
			s = s.toUpperCase();
			s = s.trim();
			comment = s.indexOf(";");
			//remove any comments
			if(comment != -1) 
				s = s.substring(0,comment);
			s = s + "\n";
			if(s.charAt(0) != ';' && s.charAt(0) != '.' && s.length() >2 ) {
				String temp = translate(s);
				//System.out.println(temp);
				out.append("-- @ 0x" + String.format("%08X", line<<2) + " : "+ String.format("%-7s",s) + String.format("%08X", line) + " : " + temp);
				line++;
			}
		}
		base.close();
		out.close();
		}
		catch(Exception E) {
			System.out.println(E.getMessage());
		}
	}
	/*
   	 * Given a string containing an opcode/registers/imm, convert to string of bytes
	 * Currently only working for ALU-R and CMP-R
	 */
	public String translate(String temp) {
		String[] args = temp.split(",");

		String translation = "";
		//should never happen
		if(args == null) {
			System.out.println("error");
			return "ERROR";
		}
		//Split op and first register
		String[] operation = args[0].split("\\s+");
		//remove whitespace so string can be processed in hashmap
		for(int i = 0; i < args.length; i++)
			args[i] = args[i].trim();
		String rd = Integer.toHexString(registers.get(operation[1]));
		String rs1 = Integer.toHexString(registers.get(args[1]));
        String opcode = String.format("%02X", table.get(operation[0]));
		//Padding
        String zero = "000"; //12 bits
        if(registers.get(args[2]) != null) {
			String rs2 = Integer.toHexString(registers.get(args[2]));
			translation = rd + rs1 + rs2 + zero + opcode + "\n";
		}
		else {
            int imm = Integer.parseInt(args[2]);
            translation = rd + rs1 + String.format("%04X", imm) + opcode + "\n";
		}
		return translation;
	}
	
}
	
	
