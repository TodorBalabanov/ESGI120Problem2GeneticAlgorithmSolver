#!/bin/bash
cd bin
java eu.veldsoft.esgi120.p2.Main <../data/in06.txt >out_$(date "+%Y%m%d%H%M%S").txt
