module ALU(out, in1, in2, aluSel);
	input[4:0] aluSel;
	input signed [31:0] in1, in2;
	output reg signed [31:0] out;
	
	always @(*) begin
		case (aluSel)
			5'b00000: out <= in1 + in2;
			5'b00001: out <= in1 - in2;
			5'b00100: out <= in1 & in2;
			5'b00101: out <= in1 | in2;
			5'b00110: out <= in1 ^ in2;
			5'b01011: out <= {in2[15:0], 16'h0000};
			5'b01100: out <= ~ (in1 & in2);
			5'b01101: out <= ~ (in1 | in2);
			5'b01110: out <= ~ (in1 ^ in2);
			
			5'b10000: out <= (in1 == 0);
			5'b10001: out <= (in1 == in2);
			5'b10010: out <= (in1 < in2);
			5'b10011: out <= (in1 <= in2);
			
			5'b10101: out <= (in1 == 0);
			5'b10110: out <= (in1 < 0);
			5'b10111: out <= (in1 <= 0);
			
			5'b11000: out <= (in1 != 0);
			5'b11001: out <= (in1 != in2);
			5'b11010: out <= (in1 >= in2);
			5'b11011: out <= (in1 > in2);
			
			5'b11101: out <= (in1 != 0);
			5'b11110: out <= (in1 > 0);
			5'b11111: out <= (in1 >= 0);

			default: out <= 0;
		endcase	
	end
		
  
endmodule