#!/bin/sh

sudo killall -9 run_start.sh

Xvfb :1 &
export DISPLAY=:1

java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=4000 -cp woodie/build/classes:woodie/lib/* core.Main
