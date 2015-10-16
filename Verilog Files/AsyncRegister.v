module AsyncRegister(clk, RD, RS1, RS2, REG_IN, WR_EN, DATA1, DATA2);
	input clk, WR_EN;
	input[3:0] RD, RS1, RS2;
	input[31:0] REG_IN;
	reg[31:0] registers[15:0];
	output reg[31:0] DATA1, DATA2;
	
	// Read asynchronously
	always @(RS1, RS2) begin
		DATA1 <= registers[RS1];
		DATA2 <= registers[RS2];
	end
	
	// Write synchronously
	always @(posedge clk)
		if (WR_EN) registers[RD] <= REG_IN;	
endmodule