import java.util.*;
import java.io.*;


/*
 * TODO: Handle lables, .ORIG, .WORD, and .NAME
 * TODO: Support to immediate and instructions pertaining to them
 * Enter filename as argument: Ex. java assembler simple.a32
 */ 
public class assembler {
	String header = "WIDTH=32;\nDEPTH=2048;\nADDRESS_RADIX=HEX;\nDATA_RADIX=HEX;\nCONTENT BEGIN\n";
	static HashMap<String,Integer> table;
	static HashMap<String,Integer> registers;
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
		//CMP-R
		table.put("SW", 0x05);
		table.put("LW", 0x09);
		table.put("BEQ", 0x16);
		 
	}
	public void readFile(String fileName) { 
		int line = 16;
		int comment;
		try {
		FileReader base = new FileReader(fileName);
		BufferedReader br = new BufferedReader(base);
		FileWriter out = new FileWriter("output.txt");
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
		//should never happen
		if(args == null) 
			return "ERROR";
		//Split op and first register
		String[] operation = args[0].split("\\s+");
		//remove whitespace so string can be processed in hashmap
		args[2] = args[2].trim();
		String rd = Integer.toHexString(registers.get(operation[1]));
		String rs1 = Integer.toHexString(registers.get(args[1]));
		String rs2 = Integer.toHexString(registers.get(args[2]));
		//Padding
		String zero = "000"; //12 bits
		String opcode = String.format("%02X", table.get(operation[0]));
		
		return rd + rs1 + rs2 + zero + opcode + "\n";
	}
	
}
	
	
