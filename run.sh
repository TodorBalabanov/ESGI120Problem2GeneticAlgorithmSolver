#!/bin/bash
cd bin
java eu.veldsoft.esgi120.p2.Main <../data/in06.txt >out_$(date "+%Y_%m_%d_%H_%M_%S").txt
