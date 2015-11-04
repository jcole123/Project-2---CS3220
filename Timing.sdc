## Generated SDC file "Timing.sdc"

## Copyright (C) 1991-2013 Altera Corporation
## Your use of Altera Corporation's design tools, logic functions 
## and other software and tools, and its AMPP partner logic 
## functions, and any output files from any of the foregoing 
## (including device programming or simulation files), and any 
## associated documentation or information are expressly subject 
## to the terms and conditions of the Altera Program License 
## Subscription Agreement, Altera MegaCore Function License 
## Agreement, or other applicable license agreement, including, 
## without limitation, that your use is for the sole purpose of 
## programming logic devices manufactured by Altera and sold by 
## Altera or its authorized distributors.  Please refer to the 
## applicable agreement for further details.


## VENDOR  "Altera"
## PROGRAM "Quartus II"
## VERSION "Version 13.0.1 Build 232 06/12/2013 Service Pack 1 SJ Web Edition"

## DATE    "Wed Nov 04 12:52:46 2015"

##
## DEVICE  "EP2C20F484C7"
##


#**************************************************************
# Time Information
#**************************************************************

set_time_format -unit ns -decimal_places 3



#**************************************************************
# Create Clock
#**************************************************************

create_clock -name {Clock10} -period 20.000 -waveform { 0.000 10.000 } [get_ports { CLOCK_50 }]


#**************************************************************
# Create Generated Clock
#**************************************************************

create_generated_clock -name {PLL_inst|altpll_component|pll|clk[0]} -source [get_pins {PLL_inst|altpll_component|pll|inclk[0]}] -duty_cycle 50.000 -multiply_by 2 -divide_by 5 -master_clock {Clock10} [get_pins { PLL_inst|altpll_component|pll|clk[0] }] 


#**************************************************************
# Set Clock Latency
#**************************************************************



#**************************************************************
# Set Clock Uncertainty
#**************************************************************



#**************************************************************
# Set Input Delay
#**************************************************************

set_input_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {CLOCK_50}]
set_input_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {CLOCK_50}]
set_input_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {KEY[0]}]
set_input_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {KEY[0]}]
set_input_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {KEY[1]}]
set_input_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {KEY[1]}]
set_input_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {KEY[2]}]
set_input_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {KEY[2]}]
set_input_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {KEY[3]}]
set_input_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {KEY[3]}]
set_input_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {SW[0]}]
set_input_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {SW[0]}]
set_input_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {SW[1]}]
set_input_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {SW[1]}]
set_input_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {SW[2]}]
set_input_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {SW[2]}]
set_input_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {SW[3]}]
set_input_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {SW[3]}]
set_input_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {SW[4]}]
set_input_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {SW[4]}]
set_input_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {SW[5]}]
set_input_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {SW[5]}]
set_input_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {SW[6]}]
set_input_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {SW[6]}]
set_input_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {SW[7]}]
set_input_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {SW[7]}]
set_input_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {SW[8]}]
set_input_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {SW[8]}]
set_input_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {SW[9]}]
set_input_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {SW[9]}]


#**************************************************************
# Set Output Delay
#**************************************************************

set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX0[0]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX0[0]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX0[1]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX0[1]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX0[2]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX0[2]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX0[3]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX0[3]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX0[4]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX0[4]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX0[5]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX0[5]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX0[6]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX0[6]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX1[0]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX1[0]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX1[1]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX1[1]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX1[2]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX1[2]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX1[3]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX1[3]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX1[4]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX1[4]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX1[5]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX1[5]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX1[6]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX1[6]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX2[0]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX2[0]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX2[1]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX2[1]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX2[2]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX2[2]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX2[3]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX2[3]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX2[4]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX2[4]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX2[5]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX2[5]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX2[6]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX2[6]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX3[0]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX3[0]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX3[1]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX3[1]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX3[2]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX3[2]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX3[3]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX3[3]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX3[4]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX3[4]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX3[5]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX3[5]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {HEX3[6]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {HEX3[6]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {LEDG[0]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {LEDG[0]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {LEDG[1]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {LEDG[1]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {LEDG[2]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {LEDG[2]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {LEDG[3]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {LEDG[3]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {LEDG[4]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {LEDG[4]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {LEDG[5]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {LEDG[5]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {LEDG[6]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {LEDG[6]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {LEDG[7]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {LEDG[7]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {LEDR[0]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {LEDR[0]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {LEDR[1]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {LEDR[1]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {LEDR[2]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {LEDR[2]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {LEDR[3]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {LEDR[3]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {LEDR[4]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {LEDR[4]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {LEDR[5]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {LEDR[5]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {LEDR[6]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {LEDR[6]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {LEDR[7]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {LEDR[7]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {LEDR[8]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {LEDR[8]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {LEDR[9]}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {LEDR[9]}]
set_output_delay -add_delay -max -clock [get_clocks {Clock10}]  -100.000 [get_ports {~LVDS91p/nCEO~}]
set_output_delay -add_delay -min -clock [get_clocks {Clock10}]  0.000 [get_ports {~LVDS91p/nCEO~}]


#**************************************************************
# Set Clock Groups
#**************************************************************



#**************************************************************
# Set False Path
#**************************************************************



#**************************************************************
# Set Multicycle Path
#**************************************************************



#**************************************************************
# Set Maximum Delay
#**************************************************************



#**************************************************************
# Set Minimum Delay
#**************************************************************



#**************************************************************
# Set Input Transition
#**************************************************************

