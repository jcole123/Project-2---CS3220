library verilog;
use verilog.vl_types.all;
entity FourPortMux is
    generic(
        WIDTH           : integer := 32
    );
    port(
        sel             : in     vl_logic_vector(1 downto 0);
        in0             : in     vl_logic_vector;
        in1             : in     vl_logic_vector;
        in2             : in     vl_logic_vector;
        in3             : in     vl_logic_vector;
        \out\           : out    vl_logic_vector
    );
    attribute mti_svvh_generic_type : integer;
    attribute mti_svvh_generic_type of WIDTH : constant is 1;
end FourPortMux;
