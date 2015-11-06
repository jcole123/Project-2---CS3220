library verilog;
use verilog.vl_types.all;
entity AsyncRegister is
    port(
        clk             : in     vl_logic;
        RD              : in     vl_logic_vector(3 downto 0);
        RS1             : in     vl_logic_vector(3 downto 0);
        RS2             : in     vl_logic_vector(3 downto 0);
        REG_IN          : in     vl_logic_vector(31 downto 0);
        WR_EN           : in     vl_logic;
        DATA1           : out    vl_logic_vector(31 downto 0);
        DATA2           : out    vl_logic_vector(31 downto 0)
    );
end AsyncRegister;
