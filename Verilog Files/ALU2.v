module ALU(out, a, b, sel);
	input [4:0] sel;
	input signed [31:0] a, b;
	output reg signed [31:0] out;
	//ALU
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
	
	
	
	always@(*) begin
		case (sel)
			ADD: out <= a + b;
			SUB: out <= a - b;
			AND: out <= a & b;
			OR: out <= a | b;
			XOR: out <= a ^ b;
			MVHI: out <= (b << 16);	
			XNOR: out <= ~(a ^ b);
			NAND: out <= ~(a & b);
			NOR: out <= ~(a | b);
			
			F: out <= 32'h00000000;
			EQ: out <= (a == b);
			LT: out <= (a < b);
			LTE: out <= (a <= b);
			T: out <= 32'h00000001;
			NE: out <= (a != b);
			GTE: out <= (a >= b);
			GT: out <= (a > b);
			
			EQZ: out <= (a == 0);
			LTZ: out <= (a < 0);
			LTEZ: out <= (a <= 0);
			NEZ: out <= (a != 0);
			
			default: out <= 0;
		endcase
	end
endmodule
			
			