`timescale 1ns / 1ps

module alu_testbench;

	reg [31:0] a,b;
	wire [31:0] out;
	reg [4:0] sel; 
	parameter ADD = 5'b00000;
	parameter SUB = 5'b00001;
	parameter AND = 5'b00100; 
	parameter OR = 5'b00101;
	parameter XOR = 5'b00110;
	parameter NAND = 5'b01100;
	parameter NOR = 5'b01101;
	parameter XNOR = 5'b01110;
	parameter MVHI = 5'b01011;
	//CMP
	parameter F = 5'b10000;
	parameter EQ = 5'b10001;
	parameter LT = 5'b10010;
	parameter LTE = 5'b10011;
	parameter T = 5'b11000;
	parameter NE = 5'b11001;
	parameter GTE = 5'b11010;
	parameter GT = 5'b11011;
	//EQZ, LTZ, LTEZ, NEZ
	parameter EQZ = 5'b10101;
	parameter LTZ = 5'b10110;
	parameter LTEZ = 5'b10111;
	parameter NEZ = 5'b11101;
	
	ALU alu_t(sel, a, b, out);
	initial begin
		a = 32'h0000000a; //10
		b = 32'h00000006; //6
		sel = ADD;
		#100
		sel = SUB;
		#100
		sel = AND;
		#100
		sel = OR;
		#100 
		sel = XOR;
		#100 
		sel = NAND;
		#100
		sel = NOR;
		#100
		sel = XNOR;
		#100
		sel = MVHI;
		#100
		sel = F;
		#100 
		sel = EQ;
		#100
		sel = LT;
		#100
		sel = LTE;
		#100
		sel = T;
		#100
		sel = NE;
		#100
		sel = GTE;
		#100
		sel = GT;
		#100
		sel = EQZ;
		#100
		sel = LTZ;
		#100
		sel = LTEZ;
		#100
		sel = NEZ;
	end
endmodule