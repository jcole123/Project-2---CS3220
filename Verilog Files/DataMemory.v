module DataMemory(clk, addr, dataOut, dataIn, writeEn);
	parameter MEM_INIT_FILE;
	parameter ADDR_BIT_WIDTH = 11;
	parameter DATA_BIT_WIDTH = 32;
	parameter N_WORDS = (1 << ADDR_BIT_WIDTH);
	
	input clk, writeEn;
	input[ADDR_BIT_WIDTH - 1: 0] addr;
	input[DATA_BIT_WIDTH - 1: 0] dataIn;
	
	output[DATA_BIT_WIDTH - 1: 0] dataOut;

	(* ram_init_file = MEM_INIT_FILE *)
	reg[DATA_BIT_WIDTH - 1: 0] data[0: N_WORDS - 1];
	
	reg[ADDR_BIT_WIDTH - 1: 0] addrReg;
		
	always @(posedge clk) begin
		addrReg <= addr;
		if (writeEn)
			data[addr] <= dataIn;
	end
	assign dataOut = data[addrReg];
endmodule
