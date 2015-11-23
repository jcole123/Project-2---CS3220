module DataMemory(clk, addr, dataBus, writeEn);
	// Basic paremters
	parameter MEM_INIT_FILE;
	parameter DMEM_PC_BITS_HI;
	parameter DMEM_PC_BITS_LO;
	
	parameter ADDR_BIT_WIDTH = 11;
	parameter DATA_BIT_WIDTH = 32;
	parameter N_WORDS = (1 << ADDR_BIT_WIDTH);
	
	input clk, writeEn;
	input[DATA_BIT_WIDTH - 1: 0] addr;
	inout[DATA_BIT_WIDTH - 1: 0] dataBus;
	
	// Synchronize the address on positive edge clock cycles
	reg[DATA_BIT_WIDTH - 1: 0] addrReg;
	always @(negedge clk) addrReg <= addr;
	
	// Write to the memory
	always @(negedge clk) begin
		if (writeEn & isMemEn)
			data[addr[DMEM_PC_BITS_HI - 1:DMEM_PC_BITS_LO]] <= dataBus;
	end
	
	// Determine if the address applies to this module
	wire isMemEn;
	assign isMemEn = (addr[DATA_BIT_WIDTH-1:DMEM_PC_BITS_HI] == 0);

	// Initialize the memory
	(* ram_init_file = MEM_INIT_FILE *)
	reg[DATA_BIT_WIDTH - 1: 0] data[0: N_WORDS - 1];
	
	// Write to the bus only if the address corresponds to this module and if the processor does not have writeEn high.
	assign dataBus = isMemEn & ~writeEn ? data[addrReg[DMEM_PC_BITS_HI-1:DMEM_PC_BITS_LO]]: {DATA_BIT_WIDTH{1'bz}};

endmodule
