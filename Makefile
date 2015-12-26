.PHONY: build
BSIZE=11
HEX_DEBUG=5
export HEX_DEBUG

build:
	javac -encoding UTF-8 -sourcepath src -d build/classes @javadat.txt
	
all: build run

run: build
	java -cp build/classes ogrodje.Hex\
		-s $(BSIZE)\
		-i\
		-1 s63150020.Stroj_OrangePanda\
		-2 s12345678.Stroj_Nakljucko\
		-d local/dnevnik.txt -n 10 -t 15000\
		-z 1 -zz 1000

runh: build
	java -cp build/classes ogrodje.Hex\
		-s $(BSIZE)\
		-i\
		-1 s63150020.Stroj_OrangePanda\
		-2 s12345678.Stroj_Nakljucko\
		-d local/dnevnik.txt -n 10 -t 15000\
		-z 1 -zz 1000\
		-b

human: build
	java -cp build/classes ogrodje.Hex\
		-s $(BSIZE)\
		-i\
		-1 s63150020.Stroj_OrangePanda\
		-d local/dnevnik.txt -n 10 -t 15000\
		-z 1 -zz 1000

clean:
	rm -r build/
	mkdir -p build/classes
