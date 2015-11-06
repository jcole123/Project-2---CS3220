transcript on
if {[file exists rtl_work]} {
	vdel -lib rtl_work -all
}
vlib rtl_work
vmap work rtl_work

vlog -vlog01compat -work work +incdir+D:/Dropbox/Fall\ 2015/CS\ 3220/Project-2---CS3220/Verilog\ Files {D:/Dropbox/Fall 2015/CS 3220/Project-2---CS3220/Verilog Files/SCProcController.v}
vlog -vlog01compat -work work +incdir+D:/Dropbox/Fall\ 2015/CS\ 3220/Project-2---CS3220 {D:/Dropbox/Fall 2015/CS 3220/Project-2---CS3220/PLL.v}
vlog -vlog01compat -work work +incdir+D:/Dropbox/Fall\ 2015/CS\ 3220/Project-2---CS3220/Verilog\ Files {D:/Dropbox/Fall 2015/CS 3220/Project-2---CS3220/Verilog Files/ALU2.v}
vlog -vlog01compat -work work +incdir+D:/Dropbox/Fall\ 2015/CS\ 3220/Project-2---CS3220/Verilog\ Files {D:/Dropbox/Fall 2015/CS 3220/Project-2---CS3220/Verilog Files/AsyncRegister.v}
vlog -vlog01compat -work work +incdir+D:/Dropbox/Fall\ 2015/CS\ 3220/Project-2---CS3220/Verilog\ Files {D:/Dropbox/Fall 2015/CS 3220/Project-2---CS3220/Verilog Files/DataMemory.v}
vlog -vlog01compat -work work +incdir+D:/Dropbox/Fall\ 2015/CS\ 3220/Project-2---CS3220/Verilog\ Files {D:/Dropbox/Fall 2015/CS 3220/Project-2---CS3220/Verilog Files/Decoder.v}
vlog -vlog01compat -work work +incdir+D:/Dropbox/Fall\ 2015/CS\ 3220/Project-2---CS3220/Verilog\ Files {D:/Dropbox/Fall 2015/CS 3220/Project-2---CS3220/Verilog Files/FourPortMux.v}
vlog -vlog01compat -work work +incdir+D:/Dropbox/Fall\ 2015/CS\ 3220/Project-2---CS3220/Verilog\ Files {D:/Dropbox/Fall 2015/CS 3220/Project-2---CS3220/Verilog Files/InstMemory.v}
vlog -vlog01compat -work work +incdir+D:/Dropbox/Fall\ 2015/CS\ 3220/Project-2---CS3220/Verilog\ Files {D:/Dropbox/Fall 2015/CS 3220/Project-2---CS3220/Verilog Files/MemoryMappedIO.v}
vlog -vlog01compat -work work +incdir+D:/Dropbox/Fall\ 2015/CS\ 3220/Project-2---CS3220/Verilog\ Files {D:/Dropbox/Fall 2015/CS 3220/Project-2---CS3220/Verilog Files/Register.v}
vlog -vlog01compat -work work +incdir+D:/Dropbox/Fall\ 2015/CS\ 3220/Project-2---CS3220/Verilog\ Files {D:/Dropbox/Fall 2015/CS 3220/Project-2---CS3220/Verilog Files/SevenSeg.v}
vlog -vlog01compat -work work +incdir+D:/Dropbox/Fall\ 2015/CS\ 3220/Project-2---CS3220/Verilog\ Files {D:/Dropbox/Fall 2015/CS 3220/Project-2---CS3220/Verilog Files/SignExtension.v}
vlog -vlog01compat -work work +incdir+D:/Dropbox/Fall\ 2015/CS\ 3220/Project-2---CS3220/Verilog\ Files {D:/Dropbox/Fall 2015/CS 3220/Project-2---CS3220/Verilog Files/TwoPortMux.v}

vlog -vlog01compat -work work +incdir+D:/Dropbox/Fall\ 2015/CS\ 3220/Project-2---CS3220/testbench {D:/Dropbox/Fall 2015/CS 3220/Project-2---CS3220/testbench/SingleCycleProcTest.v}

vsim -t 1ps -L altera_ver -L lpm_ver -L sgate_ver -L altera_mf_ver -L altera_lnsim_ver -L cycloneii_ver -L rtl_work -L work -voptargs="+acc"  SingleCycleProcTest

add wave *
view structure
view signals
run -all
