module Debouncer(clk, in, out);
	 parameter WIDTH;
	 
	 input clk;
	 input[WIDTH-1:0] in;
	 output reg[WIDTH-1:0] out;
	 
	 reg[9:0] counter;
	 reg[WIDTH-1:0] stable;	 
	 
	 always @(posedge clk) begin
		if (stable == in) begin
			if (counter >= 10'd200000) begin
				out <= stable;
			end else begin
				counter <= counter + 1;
			end
		end else begin
			counter <= 0;
			stable <= in;
		end	 
	 end
endmodule