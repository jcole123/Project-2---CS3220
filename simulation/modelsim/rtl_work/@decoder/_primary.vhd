library verilog;
use verilog.vl_types.all;
entity Decoder is
    generic(
        OP1_ALUR        : vl_logic_vector(0 to 3) := (Hi0, Hi0, Hi0, Hi0);
        OP1_ALUI        : vl_logic_vector(0 to 3) := (Hi1, Hi0, Hi0, Hi0);
        OP1_CMPR        : vl_logic_vector(0 to 3) := (Hi0, Hi0, Hi1, Hi0);
        OP1_CMPI        : vl_logic_vector(0 to 3) := (Hi1, Hi0, Hi1, Hi0);
        OP1_BCOND       : vl_logic_vector(0 to 3) := (Hi0, Hi1, Hi1, Hi0);
        OP1_SW          : vl_logic_vector(0 to 3) := (Hi0, Hi1, Hi0, Hi1);
        OP1_LW          : vl_logic_vector(0 to 3) := (Hi1, Hi0, Hi0, Hi1);
        OP1_JAL         : vl_logic_vector(0 to 3) := (Hi1, Hi0, Hi1, Hi1);
        ALU_ADD         : vl_logic_vector(0 to 4) := (Hi0, Hi0, Hi0, Hi0, Hi0);
        REG_SEL_RS1_FIRST_ARG: vl_logic := Hi0;
        REG_SEL_RS1_SECOND_ARG: vl_logic := Hi1;
        ARG_SEL_REGISTER: vl_logic_vector(0 to 1) := (Hi0, Hi0);
        ARG_SEL_IMMEDIATE: vl_logic_vector(0 to 1) := (Hi0, Hi1);
        ARG_SEL_IMM_SHIFT: vl_logic_vector(0 to 1) := (Hi1, Hi0);
        RD_ALU          : vl_logic_vector(0 to 1) := (Hi0, Hi0);
        RD_MEM          : vl_logic_vector(0 to 1) := (Hi0, Hi1);
        RD_PC           : vl_logic_vector(0 to 1) := (Hi1, Hi0);
        PC_NEXT         : vl_logic_vector(0 to 1) := (Hi0, Hi0);
        PC_IMM          : vl_logic_vector(0 to 1) := (Hi0, Hi1);
        PC_ALU          : vl_logic_vector(0 to 1) := (Hi1, Hi0)
    );
    port(
        opcode          : in     vl_logic_vector(3 downto 0);
        func            : in     vl_logic_vector(3 downto 0);
        aluVal          : in     vl_logic_vector(3 downto 0);
        regSel          : out    vl_logic;
        regWrSel        : out    vl_logic_vector(1 downto 0);
        argSel          : out    vl_logic_vector(1 downto 0);
        aluSel          : out    vl_logic_vector(4 downto 0);
        pcSel           : out    vl_logic_vector(1 downto 0);
        regEn           : out    vl_logic;
        memEn           : out    vl_logic
    );
    attribute mti_svvh_generic_type : integer;
    attribute mti_svvh_generic_type of OP1_ALUR : constant is 1;
    attribute mti_svvh_generic_type of OP1_ALUI : constant is 1;
    attribute mti_svvh_generic_type of OP1_CMPR : constant is 1;
    attribute mti_svvh_generic_type of OP1_CMPI : constant is 1;
    attribute mti_svvh_generic_type of OP1_BCOND : constant is 1;
    attribute mti_svvh_generic_type of OP1_SW : constant is 1;
    attribute mti_svvh_generic_type of OP1_LW : constant is 1;
    attribute mti_svvh_generic_type of OP1_JAL : constant is 1;
    attribute mti_svvh_generic_type of ALU_ADD : constant is 1;
    attribute mti_svvh_generic_type of REG_SEL_RS1_FIRST_ARG : constant is 1;
    attribute mti_svvh_generic_type of REG_SEL_RS1_SECOND_ARG : constant is 1;
    attribute mti_svvh_generic_type of ARG_SEL_REGISTER : constant is 1;
    attribute mti_svvh_generic_type of ARG_SEL_IMMEDIATE : constant is 1;
    attribute mti_svvh_generic_type of ARG_SEL_IMM_SHIFT : constant is 1;
    attribute mti_svvh_generic_type of RD_ALU : constant is 1;
    attribute mti_svvh_generic_type of RD_MEM : constant is 1;
    attribute mti_svvh_generic_type of RD_PC : constant is 1;
    attribute mti_svvh_generic_type of PC_NEXT : constant is 1;
    attribute mti_svvh_generic_type of PC_IMM : constant is 1;
    attribute mti_svvh_generic_type of PC_ALU : constant is 1;
end Decoder;
