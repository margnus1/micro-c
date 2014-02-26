## Quick-and-dirty POSIX-only build file. The recommended way to compile is
## using ant.

compile: generate-parser
	mkdir -p build
	javac -sourcepath generated -d build generated/**/*.java generated/*.java

generate-parser: generated
	cd generated/parser; jjtree parser.jjt
	cd generated/parser; javacc parser.jj
	cp -r src/* generated ## jjtree sometimes feels like overwriting

generated:
	mkdir -p generated
	cp -r src/* generated

clean:
	-rm -rf generated build

.PHONY: compile generate-parser clean
