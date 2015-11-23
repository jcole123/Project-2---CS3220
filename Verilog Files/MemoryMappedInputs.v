module MemoryMappedInputs(clk, writeEn, addr, bus, device);
	// The address of the data register
	parameter ADDRESS_DATA;
	
	// The width of the output device
	parameter OUTPUT_WIDTH;
	
	// The address of the control register
	parameter ADDRESS_CTRL = ADDRESS_DATA + 32'h100;
	
	input clk, writeEn;
	input[OUTPUT_WIDTH-1:0] device;
	input[31:0]	addr;
	inout[31:0] bus;
	
	// Determine if this is the targeted device
	wire enData, enCtrl;
	assign enData = (addr == ADDRESS_DATA);
	assign enCtrl = (addr == ADDRESS_CTRL);
	
	// Write the values to the bus
	assign bus = (enData & ~writeEn) ? {{32-OUTPUT_WIDTH{1'b0}},device} : {32{1'bz}};
	assign bus = (enCtrl & ~writeEn) ? {{23{1'b0}},ie,5'b0,overrun,1'b0,ready} : {32{1'bz}};
	
	reg ready, overrun, ie;
	
	// Determine if data has changed
	reg[OUTPUT_WIDTH-1:0] buffer;
	
	// Perform writes in the negative edge
	always @(negedge clk) begin
		// Update the ready and the overflow bits
		if (buffer != device) begin
			buffer <= device;
			overrun <= ready | overrun;
			ready <= 1'b1;
		end
	
		// Handle writes to the control register
		if (enCtrl & writeEn) begin
			overrun <= bus[2] ? overrun : 1'b0;
			ie <= bus[8];
		end
		
		// If reading from data, clear ready
		if (enData & ~writeEn) ready <= 1'b0;
	end
endmodule