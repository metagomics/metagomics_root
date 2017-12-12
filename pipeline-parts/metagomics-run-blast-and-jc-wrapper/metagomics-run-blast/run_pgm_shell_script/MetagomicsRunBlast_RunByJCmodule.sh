#!/bin/bash

# "$@"  is all command line arguments
 
path_of_script=`dirname $0`

java -Drun_dir=`pwd` -jar ${path_of_script}/MetagomicsRunBlast.jar \
 --config=${path_of_script}/Metagomics_RunBlast_Config.properties   \
  "$@" \
 > z_program_MetagomicsRunBlast.jar_sysout.txt \
 2> z_program_MetagomicsRunBlast.jar_syserr.txt

