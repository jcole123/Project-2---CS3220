module MemoryMappedIO(clk, writeEn, addr, bus, device);
	// The address of the data register
	parameter ADDRESS_DATA;
	
	// The width of the output device
	parameter OUTPUT_WIDTH;
	
	input clk, writeEn;
	input[31:0]	addr;
	inout[31:0] bus;
	output reg [OUTPUT_WIDTH-1:0] device;
	
	// Determine if the address is for this device
	wire isData;
	assign isData = addr == ADDRESS_DATA;
	
	// If reading from the output, place the output on the bus
	assign bus = (isData&~writeEn)?{{(32-OUTPUT_WIDTH){1'b0}},device}:{32{1'bz}};
	
	always @(negedge clk)
		if (isData & writeEn)
			device <= bus[OUTPUT_WIDTH-1:0];
endmodule