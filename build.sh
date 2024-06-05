#!/bin/bash

javac -d ./build -classpath "./" $(find . -name '*.java')
cd build/ && jar cfm tcp-game.jar ../MANIFEST.MF $(find . -name '*.class')

# $1 path to lib game_dir/lib
cp tcp-game.jar $1