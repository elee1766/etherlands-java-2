all: subsystem biomeFinder

subsystem:
	cd src && $(MAKE)

biomeFinder: biomeFinder.c
	gcc -o biomeFinder biomeFinder.c .\src\libcubiomes.a -fwrapv -lm