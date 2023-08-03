# All Targets
all: bin/Studio

# Tool invocations
# Executable "Studio" depends on the files main.o Studio.o Action.o Trainer.o Workout.o Customer.o
bin/Studio: bin/main.o bin/Studio.o bin/Action.o bin/Customer.o bin/Workout.o bin/Trainer.o
	@echo 'Building target: Studio'
	@echo 'Invoking: C++ Linker'
	g++ -o bin/Studio bin/main.o bin/Studio.o bin/Action.o bin/Customer.o bin/Workout.o bin/Trainer.o
	@echo 'Finished building target: Studio'

# Depends on the source and header files
bin/main.o: src/main.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/main.o src/main.cpp

bin/Studio.o: src/Studio.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Studio.o src/Studio.cpp

bin/Action.o: src/Action.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Action.o src/Action.cpp

bin/Customer.o: src/Customer.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Customer.o src/Customer.cpp

bin/Workout.o: src/Workout.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Workout.o src/Workout.cpp

bin/Trainer.o: src/Trainer.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Trainer.o src/Trainer.cpp

#Clean the build directory
clean:
	rm -f bin/*
	@echo 'Cleaning /bin directory'
