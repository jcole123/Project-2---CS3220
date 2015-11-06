library verilog;
use verilog.vl_types.all;
entity MemoryMappedIO is
    generic(
        address         : vl_notype;
        signalWidth     : vl_notype
    );
    port(
        clk             : in     vl_logic;
        writeEn         : in     vl_logic;
        addr            : in     vl_logic_vector(31 downto 0);
        \in\            : in     vl_logic_vector(31 downto 0);
        \out\           : out    vl_logic_vector
    );
    attribute mti_svvh_generic_type : integer;
    attribute mti_svvh_generic_type of address : constant is 5;
    attribute mti_svvh_generic_type of signalWidth : constant is 5;
end MemoryMappedIO;
