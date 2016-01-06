.PHONY: build

BSIZE=11
HEX_DEBUG=5
GAMES=10
TIME=15000
OPPONENT=s87654321.Stroj_OldPanda

export HEX_DEBUG

build:
	javac -encoding UTF-8 -sourcepath src -d build/classes @javadat.txt
	
all: build run

vsself: build
	java -Xmx500m -cp build/classes ogrodje.Hex\
		-s $(BSIZE)\
		-i\
		-1 s63150020.Stroj_mn\
		-2 s63150020.Stroj_mn\
		-d local/dnevnik.txt -n $(GAMES) -t $(TIME)\
		-z 1 -zz 1000

run: build
	java -Xmx500m -cp build/classes ogrodje.Hex\
		-s $(BSIZE)\
		-i\
		-1 s63150020.Stroj_mn\
		-2 $(OPPONENT)\
		-d local/dnevnik.txt -n $(GAMES) -t $(TIME)\
		-z 1 -zz 1000

human: build
	java -Xmx500m -cp build/classes ogrodje.Hex\
		-s $(BSIZE)\
		-i\
		-1 s63150020.Stroj_mn\
		-d local/dnevnik.txt -n $(GAMES) -t $(TIME)

clean:
	rm -r build/
	mkdir -p build/classes
