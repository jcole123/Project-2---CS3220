module Timer(clk, wrEn, addr, bus);
	parameter TCNT_ADDR = 32'hF0000020;
	parameter TLIM_ADDR = 32'hF0000024;
	parameter TCTL_ADDR = 32'hF0000120;
	
	input wrEn, clk;
	input[31:0] addr;
	inout[31:0] bus;
	
	reg ie, ready, overrun;
	reg[31:0] counter, microcounter, limit;
	
	// Create some control signals
	wire TCNT_EN, TLIM_EN, TCTL_EN;
	assign TCTN_EN = (addr == TCNT_ADDR);
	assign TLIM_EN = (addr == TLIM_ADDR);
	assign TCTL_EN = (addr == TCTL_ADDR);
	
	// Write to the bus if required
	assign bus = TCTN_EN & ~wrEn ? counter : {32{1'bz}};
	assign bus = TLIM_EN & ~wrEn ? limit : {32{1'bz}};
	assign bus = TCTL_EN & ~wrEn ? {{23{1'b0}},ie,5'b0,overrun,1'b0,ready} : {32{1'bz}};
	
	always @(negedge clk) begin
		// Increase the counter if necessary
		microcounter <= microcounter - 1;
		if (microcounter == 0) begin
			microcounter <= 20000; // 20 mhz / 1000 hz
			
			if (counter >= limit - 1) begin
				counter <= 0;
				overrun = overrun | ready;
				ready <= 1;
			end else begin
				counter <= counter + 1;
			end
		end
		
		// Handle writes
		if (TCTN_EN & wrEn) counter <= bus;
		else if (TLIM_EN & wrEn) limit <= bus;
		else if (TCTL_EN & wrEn) begin
			ready <= bus[0] ? ready : 1'b0;
			overrun <= bus[2] ? overrun : 1'b0;
			ie <= bus[8];
		end
	
		// Reset ready on ready
		if (TCTL_EN & ~wrEn) ready <= 1'b0;
	end
	
endmodule