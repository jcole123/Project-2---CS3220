library verilog;
use verilog.vl_types.all;
entity SCProcController is
    generic(
        DBITS           : integer := 32;
        INST_SIZE       : integer := 4;
        INST_BIT_WIDTH  : integer := 32;
        START_PC        : integer := 64;
        REG_INDEX_BIT_WIDTH: integer := 4;
        ADDR_KEY        : vl_logic_vector(31 downto 0) := (Hi1, Hi1, Hi1, Hi1, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi1, Hi0, Hi0, Hi0, Hi0);
        ADDR_SW         : vl_logic_vector(31 downto 0) := (Hi1, Hi1, Hi1, Hi1, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi1, Hi0, Hi1, Hi0, Hi0);
        ADDR_HEX        : vl_logic_vector(31 downto 0) := (Hi1, Hi1, Hi1, Hi1, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0);
        ADDR_LEDR       : vl_logic_vector(31 downto 0) := (Hi1, Hi1, Hi1, Hi1, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi1, Hi0, Hi0);
        ADDR_LEDG       : vl_logic_vector(31 downto 0) := (Hi1, Hi1, Hi1, Hi1, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi0, Hi1, Hi0, Hi0, Hi0);
        IMEM_INIT_FILE  : string  := "Sorter2.mif";
        IMEM_ADDR_BIT_WIDTH: integer := 11;
        IMEM_DATA_BIT_WIDTH: vl_notype;
        IMEM_PC_BITS_HI : vl_notype;
        IMEM_PC_BITS_LO : integer := 2;
        DMEMADDRBITS    : integer := 13;
        DMEMWORDBITS    : integer := 2;
        DMEMWORDS       : integer := 2048
    );
    port(
        SW              : in     vl_logic_vector(9 downto 0);
        KEY             : in     vl_logic_vector(3 downto 0);
        LEDR            : out    vl_logic_vector(9 downto 0);
        LEDG            : out    vl_logic_vector(7 downto 0);
        HEX0            : out    vl_logic_vector(6 downto 0);
        HEX1            : out    vl_logic_vector(6 downto 0);
        HEX2            : out    vl_logic_vector(6 downto 0);
        HEX3            : out    vl_logic_vector(6 downto 0);
        CLOCK_50        : in     vl_logic
    );
    attribute mti_svvh_generic_type : integer;
    attribute mti_svvh_generic_type of DBITS : constant is 1;
    attribute mti_svvh_generic_type of INST_SIZE : constant is 1;
    attribute mti_svvh_generic_type of INST_BIT_WIDTH : constant is 1;
    attribute mti_svvh_generic_type of START_PC : constant is 1;
    attribute mti_svvh_generic_type of REG_INDEX_BIT_WIDTH : constant is 1;
    attribute mti_svvh_generic_type of ADDR_KEY : constant is 1;
    attribute mti_svvh_generic_type of ADDR_SW : constant is 1;
    attribute mti_svvh_generic_type of ADDR_HEX : constant is 1;
    attribute mti_svvh_generic_type of ADDR_LEDR : constant is 1;
    attribute mti_svvh_generic_type of ADDR_LEDG : constant is 1;
    attribute mti_svvh_generic_type of IMEM_INIT_FILE : constant is 1;
    attribute mti_svvh_generic_type of IMEM_ADDR_BIT_WIDTH : constant is 1;
    attribute mti_svvh_generic_type of IMEM_DATA_BIT_WIDTH : constant is 3;
    attribute mti_svvh_generic_type of IMEM_PC_BITS_HI : constant is 3;
    attribute mti_svvh_generic_type of IMEM_PC_BITS_LO : constant is 1;
    attribute mti_svvh_generic_type of DMEMADDRBITS : constant is 1;
    attribute mti_svvh_generic_type of DMEMWORDBITS : constant is 1;
    attribute mti_svvh_generic_type of DMEMWORDS : constant is 1;
end SCProcController;
