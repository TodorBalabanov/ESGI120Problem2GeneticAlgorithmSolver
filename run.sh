#!/bin/bash
cd bin
java -cp ".:../lib/*" eu.veldsoft.esgi120.p2.Main <../data/in07.txt >out_$(date "+%Y%m%d%H%M%S").txt
