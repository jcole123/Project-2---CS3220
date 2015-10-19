module Decoder(opcode, func, aluVal, regSel, regWrSel, argSel, aluSel, pcSel, regEn, memEn);
	parameter OP1_ALUR 					 = 4'b0000;
	parameter OP1_ALUI 					 = 4'b1000;
	parameter OP1_CMPR 					 = 4'b0010;
	parameter OP1_CMPI 					 = 4'b1010;
	parameter OP1_BCOND					 = 4'b0110;
	parameter OP1_SW   					 = 4'b0101;
	parameter OP1_LW   					 = 4'b1001;
	parameter OP1_JAL  					 = 4'b1011;
	
	// Useful ALU functions
	parameter ALU_ADD = 5'b0;
	
	// Sets whether the instruction has RS1 as the first or second argument
	parameter REG_SEL_RS1_FIRST_ARG = 1'b0;
	parameter REG_SEL_RS1_SECOND_ARG = 1'b1;
	
	// Select whether the second argument is an immediate or a register
	parameter ARG_SEL_REGISTER = 2'b00;
	parameter ARG_SEL_IMMEDIATE = 2'b01;
	parameter ARG_SEL_IMM_SHIFT = 2'b10;
	
	// Set whether the destination register should receive a value from the memory of the aluSel
	parameter RD_ALU = 2'b00;
	parameter RD_MEM = 2'b01;
	parameter RD_PC = 2'b10;
	
	// Set the source for the next program counter state
	parameter PC_NEXT = 2'b00;
	parameter PC_IMM = 2'b01;
	parameter PC_ALU = 2'b10;
	
	input[3:0] opcode, func, aluVal;
	output reg[4:0] aluSel;
	output reg[1:0] pcSel, regWrSel, argSel;
	output reg regSel, regEn, memEn;
	
	always @(*) begin
		
		// Set defaults
		aluSel 	<= ALU_ADD;
		pcSel 	<= PC_NEXT;
		regSel 	<= REG_SEL_RS1_FIRST_ARG;
		regWrSel <= RD_ALU;
		argSel 	<= ARG_SEL_REGISTER;
		regEn 	<= 0;
		memEn 	<= 0;
		
		case (opcode)
			OP1_ALUR: begin
				regSel 	<= REG_SEL_RS1_SECOND_ARG;
				argSel 	<= ARG_SEL_REGISTER;
				aluSel 	<= {1'b0, func};
				regWrSel <= RD_ALU;
				regEn 	<= 1'b1;
			end
			OP1_ALUI: begin
				regSel 	<= REG_SEL_RS1_SECOND_ARG;
				argSel 	<= ARG_SEL_IMMEDIATE;
				aluSel 	<= {1'b0, func};
				regWrSel <= RD_ALU;
				regEn 	<= 1'b1;
			end
			OP1_CMPR: begin
				regSel 	<= REG_SEL_RS1_SECOND_ARG;
				argSel 	<= ARG_SEL_REGISTER;
				aluSel 	<= {1'b1, func};
				regWrSel <= RD_ALU;
				regEn 	<= 1'b1;
			end
			OP1_CMPI: begin
				regSel 	<= REG_SEL_RS1_SECOND_ARG;
				argSel 	<= ARG_SEL_IMMEDIATE;
				aluSel 	<= {1'b1, func};
				regWrSel <= RD_ALU;
				regEn 	<= 1'b1;
			end
			OP1_BCOND: begin
				regSel 	<= REG_SEL_RS1_FIRST_ARG;
				argSel 	<= ARG_SEL_REGISTER;
				aluSel 	<= {1'b1, func};
				if (aluVal)
					pcSel 	<= PC_IMM;
			end
			OP1_SW: begin
				regSel 	<= REG_SEL_RS1_FIRST_ARG;
				argSel 	<= ARG_SEL_IMMEDIATE;
				aluSel 	<= ALU_ADD;
				memEn 	<= 1'b1;
			end
			OP1_LW: begin
				regSel 	<= REG_SEL_RS1_SECOND_ARG;
				argSel 	<= ARG_SEL_IMMEDIATE;
				aluSel 	<= ALU_ADD;
				regWrSel <= RD_MEM;
				regEn 	<= 1'b1;
			end
			OP1_JAL: begin
				regSel 	<= REG_SEL_RS1_SECOND_ARG;
				argSel 	<= ARG_SEL_IMM_SHIFT;
				aluSel 	<= ALU_ADD;
				pcSel 	<= PC_ALU;
				regWrSel <= RD_PC;
				regEn 	<= 1'b1;
			end
			default:
				;
		endcase	
	end
endmodule
