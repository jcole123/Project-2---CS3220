module Project2(SW,KEY,LEDR,LEDG,HEX0,HEX1,HEX2,HEX3,CLOCK_50);
	input  [9:0] SW;
	input  [3:0] KEY;
	input  CLOCK_50;
	output [9:0] LEDR;
	output [7:0] LEDG;
	output [6:0] HEX0,HEX1,HEX2,HEX3;
 
	parameter DBITS         				= 32;
	parameter INST_SIZE      			 	= 32'd4;
	parameter INST_BIT_WIDTH				= 32;
	parameter START_PC       			 	= 32'h40;
	parameter REG_INDEX_BIT_WIDTH 		= 4;
	parameter ADDR_KEY  					 	= 32'hF0000010;
	parameter ADDR_SW   					 	= 32'hF0000014;
	parameter ADDR_HEX  					 	= 32'hF0000000;
	parameter ADDR_LEDR 					 	= 32'hF0000004;
	parameter ADDR_LEDG 					 	= 32'hF0000008;
  
	parameter IMEM_INIT_FILE				= "Sorter2.mif";
	parameter IMEM_ADDR_BIT_WIDTH 		= 11;
	parameter IMEM_DATA_BIT_WIDTH 		= INST_BIT_WIDTH;
	parameter IMEM_PC_BITS_HI     		= IMEM_ADDR_BIT_WIDTH + 2;
	parameter IMEM_PC_BITS_LO     		= 2;
  
	parameter DMEMADDRBITS 				 	= 13;
	parameter DMEMWORDBITS				 	= 2;
	parameter DMEMWORDS					 	= 2048;
    
	//PLL, clock genration, and reset generation
	wire clk, lock;
  
	//Pll pll(.inclk0(CLOCK_50), .c0(clk), .locked(lock));
	PLL	PLL_inst (.inclk0 (CLOCK_50),.c0 (clk),.locked (lock));
	wire reset = ~lock;
  
	// Create PC and its logic
	wire pcWrtEn = 1'b1;
	wire[DBITS - 1: 0] pcIn;
	wire[DBITS - 1: 0] pcOut;
  
	// This PC instantiation is your starting point
	Register #(.BIT_WIDTH(DBITS), .RESET_VALUE(START_PC)) pc (clk, reset, pcWrtEn, pcIn, pcOut);

	// Create instruction memeory
	wire[IMEM_DATA_BIT_WIDTH - 1: 0] instWord;
	InstMemory #(IMEM_INIT_FILE, IMEM_ADDR_BIT_WIDTH, IMEM_DATA_BIT_WIDTH) instMem (pcOut[IMEM_PC_BITS_HI - 1: IMEM_PC_BITS_LO], instWord);
  
	// Put the code for getting opcode1, rd, rs, rt, imm, etc. here
	wire[REG_INDEX_BIT_WIDTH - 1:0] RS1, RS2;
	TwoPortMux #(8) regMux(regSel, instWord[31:24], instWord[27:20], {RS1, RS2});
  
	// Extend the immediate
	wire[15:0] immSmall;
	wire[DBITS - 1:0] immExt;
	assign immSmall = instWord[23:8];
	SignExtension #(16,32) extender(immSmall, immExt);
  
	// Create the registers
	wire[REG_INDEX_BIT_WIDTH - 1:0] RD;
	wire[DBITS - 1:0] DATA1, DATA2, REG_IN;
	assign RD = instWord[31:28];
	AsyncRegister registers(clk, RD, RS1, RS2, REG_IN, REG_EN, DATA1, DATA2);

	// Switch between the immediate and DATA2
	wire[DBITS - 1:0] ARG2;
	TwoPortMux #(DBITS) argMux(argSel, DATA2, immExt, ARG2);
  
	// Create ALU unit
	wire[DBITS - 1:0] ALU_OUT;
	ALU alu(ALU_OUT, DATA1, ARG2, aluSel);

	// Initialize the data memory
	wire[DBITS - 1:0] MEM_OUT_TMP;
	DataMemory #(IMEM_INIT_FILE) dataMem (clk, ALU_OUT[IMEM_PC_BITS_HI - 1: IMEM_PC_BITS_LO], MEM_OUT_TMP, DATA2, MEM_EN);
   
	// Memory Mapped Inputs
	reg [DBITS-1:0] MEM_OUT;
	always @(*) begin
		if (ALU_OUT == ADDR_KEY)
			MEM_OUT <= {28'h0, ~KEY};
		else if (ALU_OUT == ADDR_SW)
			MEM_OUT <= {22'h0, SW};
		else
			MEM_OUT <= MEM_OUT_TMP;
	end
	
	
	// Memory Mapped Outputs
	MemoryMappedIO #(ADDR_HEX, 16) ioHex(clk, MEM_EN, ALU_OUT, DATA2, HEXTMP);
	MemoryMappedIO #(ADDR_LEDR, 10) ioLedR(clk, MEM_EN, ALU_OUT, DATA2, LEDR);
	MemoryMappedIO #(ADDR_LEDG, 8) ioLedG(clk, MEM_EN, ALU_OUT, DATA2, LEDG);
	
	// Convert the binary to 7-seg
	wire[15:0] HEXTMP;
	SevenSeg hexConv3(HEXTMP[15:12], HEX3);
	SevenSeg hexConv2(HEXTMP[11:8], HEX2);
	SevenSeg hexConv1(HEXTMP[7:4], HEX1);
	SevenSeg hexConv0(HEXTMP[3:0], HEX0);
  
	// Mux to switch register input between the ALU, the PC counter, and the memory
	FourPortMux #(DBITS) regInMux(regWrSel, ALU_OUT, MEM_OUT, nextPC, 0, REG_IN);
  
	// Generate the next program counter
	wire[DBITS - 1:0] nextPC, nextBranch;
	assign nextPC = pcOut + 4;
	assign nextBranch = nextPC + (immExt << 2);
	FourPortMux #(DBITS) pcMux(pcSel, nextPC, nextBranch, (immExt << 2) + DATA1, 0, pcIn);
    
	// Control logic
	wire regSel;
	wire[1:0] regWrSel;
	wire argSel;
	wire REG_EN;
	wire MEM_EN;
	wire[4:0] aluSel;
	wire[1:0] pcSel;
	Decoder decoder(instWord[3:0], instWord[7:4], ALU_OUT[0], regSel, regWrSel, argSel, aluSel, pcSel, REG_EN, MEM_EN);
	
endmodule