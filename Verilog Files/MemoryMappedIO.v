module MemoryMappedIO(clk, writeEn, addr, in, out);
	parameter address;
	parameter signalWidth;
	
	input clk, writeEn;
	input[31:0] in, addr;
	output reg [signalWidth-1:0] out;
	
	always @(posedge clk)
		if (writeEn & (addr == address)) 
			out <= in[signalWidth-1:0];
	
endmodule