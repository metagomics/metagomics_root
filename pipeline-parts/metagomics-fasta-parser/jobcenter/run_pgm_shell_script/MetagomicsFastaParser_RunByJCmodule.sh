#!/bin/bash

# "$@"  is all command line arguments
 
path_of_script=`dirname $0`

java -Drun_dir=`pwd` -jar ${path_of_script}/MetagomicsFastaParser.jar  "$@" \
 > z_program_MetagomicsFastaParser.jar_sysout.txt \
 2> z_program_MetagomicsFastaParser.jar_syserr.txt

