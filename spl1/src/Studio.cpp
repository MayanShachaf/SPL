#include <fstream>
#include <string>
#include <iostream>
#include "Studio.h"

using namespace std;


void Studio::clear() {
    for( auto *t :trainers)
        delete t;
    for(auto *base:actionsLog)
        delete base;
   actionsLog.clear();
   trainers.clear();
   workout_options.clear();
   open = false;
}
//----------------rule of 5---------------------//
//destructor
Studio::~Studio() {
    clear();
}
//copy constructor
Studio::Studio(const Studio &other):open(other.open) ,trainers(), workout_options(), actionsLog(){

    for(auto trainer : other.trainers) {
        auto *t = new Trainer(*trainer);
        this->trainers.push_back(t);
    }

    for(const auto & workout_option : other.workout_options){
        this->workout_options.push_back(workout_option.copy_workout());
    }

    for(auto i : other.actionsLog){
        this->actionsLog.push_back(i->clone());
    }
}

//move constructor
Studio::Studio(Studio &&other)
:open(other.open), trainers(other.trainers),workout_options(other.workout_options),actionsLog(other.actionsLog){
    other.trainers = std::vector<Trainer*>();
    other.actionsLog = std::vector<BaseAction*>();
    other.open= false;
}

//copy assignment operator
Studio &Studio::operator=(const Studio &other) {
  //check for "self assignment"
  if(this==&other){
      return *this;
  }
    clear();
    for(auto trainer : other.trainers) {
        Trainer *t = new Trainer(*trainer);
        this->trainers.push_back(t);
    }
    for(const auto & workout_option : other.workout_options){
        this->workout_options.push_back(workout_option.copy_workout());

    }
    for(auto i : other.actionsLog){
        this->actionsLog.push_back(i->clone());
    }
    open=other.open;
    return *this;

}
//move assignment operator
Studio &Studio::operator=(Studio&& other){
 if(this!=&other){
     clear();
     for(auto & trainer : other.trainers) {
         this->trainers.push_back(trainer);
     }

     for(auto & workout_option : other.workout_options){
         this->workout_options.push_back(workout_option);

     }
     for(auto & i : other.actionsLog){
         this->actionsLog.push_back(i);
     }
     open=other.open;
     other.trainers = std::vector<Trainer*>();
     other.actionsLog = std::vector<BaseAction*>();
     other.open= false;
 }
 return *this;
}
//----------------------------------------------//

static int endString(string line) {
    int found = line.find(',');
    if (found == -1)
        found = line.length();
    return found;
}

static int endStringSpace(string line) {
    int found = line.find(' ');
    if (found == -1)
        found = line.length();
    return found;
}

static string nameOfAction(string line) {
    return line.substr(0, endStringSpace(line));
}


Studio::Studio(const std::string &configFilePath) : open(),trainers(), workout_options(), actionsLog() {
    open = true;
    string myText;
    //Create and open a text file
    ifstream MyReadFile(configFilePath);
    int currentLine = 0;
    int numOfTrainers = 0;
    int workout_id = 0;
    bool first = false , second = false;
    //Use a while loop together with the get line "function" to read the file line by line
    while (getline(MyReadFile, myText)) {

        if(myText.find_first_of('#')!=0 && !myText.empty()){
            if (first & second) {
                int workout_fill = 0;
                int parameter_start = 0; // intilize the capacity
                int parameter_end = endString(myText);
                string name;
                string typeOfWorkout;
                WorkoutType type;
                int price;
                while (workout_fill < 3) {
                    if (workout_fill == 0) {
                        name = myText.substr(parameter_start, parameter_end);
                        myText = myText.substr(parameter_end + 2, myText.length() - parameter_end - 2);
                    }
                    if (workout_fill == 1) {
                        typeOfWorkout = myText.substr(parameter_start, parameter_end);
                        if (typeOfWorkout == "Anaerobic")
                            type = ANAEROBIC;
                        if (typeOfWorkout == "Mixed")
                            type = MIXED;
                        if (typeOfWorkout == "Cardio")
                            type = CARDIO;
                        myText = myText.substr(parameter_start + parameter_end + 2, myText.length());
                    }
                    if (workout_fill == 2) {
                        price = stoi(myText.substr(parameter_start, parameter_end));
                    }
                    if (workout_fill != 2)parameter_end = endString(myText);
                    workout_fill++;
                }
                workout_options.push_back(Workout(workout_id, name, price, type));
                workout_id++;
            }
            if (first & !second) { // this line contain the trainers capacity
                int trainer_id = 0; // trainer id
                //myText +=",";
                while (trainer_id < numOfTrainers) {
                    int char_num = endString(myText);
                    int capacity = stoi(myText.substr(0, char_num));
                    if (trainer_id != numOfTrainers - 1) {
                        myText = myText.substr(char_num + 1, myText.length() - char_num + 1);
                    }
                    Trainer *trainer1 = new Trainer(capacity);
                    trainer1->setTrainerId(trainer_id);
                    trainers.push_back(trainer1);
                    trainer_id++;
                }
                second = true;
            }
            if (!first & !second) { // this line contain the num of trainers
                numOfTrainers = stoi(myText);
                first = true;
            }
        }
        currentLine++;

    }
    //close the file
    MyReadFile.close();

}

void Studio::start() {
    string input;
    int customer_id =0;
    int backup_id = 0;
    cout << "Studio is now open!" << endl;
    getline(cin,input);
    while(input!= "closeall") {
        int start = 0;
        int trainer_id=-1;
        int char_until_next = endStringSpace(input);//search for space
        if(nameOfAction(input) == "open") {
            input = input.substr(char_until_next+1 , input.length()-char_until_next+1);//cut the name
            char_until_next = endStringSpace(input);//search for space
            trainer_id = stoi(input.substr(0, input.length()-1-char_until_next));//get trainer id fron input
            input = input.substr(char_until_next+1 , input.length()-char_until_next+1);//cut the id from input
            //help - need to clear in the end?
            std::vector<Customer *> customersList;
            while(input.length() != 0) {
                string name = "";
                string type = "";
                char_until_next = (endString(input));//search for next ","
                name = input.substr(start,char_until_next);//
                input = input.substr(char_until_next+1,input.length()-char_until_next+1);//cut name from input
                char_until_next = endStringSpace(input);
                type = input.substr(0,char_until_next);
                int input_find = input.find(",");
                if(input_find != -1)
                    input = input.substr(char_until_next+1,input.length()-char_until_next+1);//cut type from input
                else
                    input="";
                if(type=="swt"){
                    customersList.push_back(new SweatyCustomer(name,customer_id));
                }
                if (type=="chp"){
                    customersList.push_back(new CheapCustomer(name,customer_id));
                }
                if(type=="mcl"){
                    customersList.push_back(new HeavyMuscleCustomer(name,customer_id));
                }
                if(type=="fbd"){
                    customersList.push_back(new FullBodyCustomer(name,customer_id));
                }
                customer_id++;
            }
            (new OpenTrainer(trainer_id, customersList))->act(*this);

        }
        if(nameOfAction(input)=="order"){
            input = input.substr(char_until_next+1 , input.length()-char_until_next+1);//cut the name
            char_until_next = endStringSpace(input);//search for space
            trainer_id = stoi(input.substr(0, input.length()-1-char_until_next));//get trainer id from input
            input = "";
            (new Order(trainer_id))->act(*this);
        }
        if(nameOfAction(input)=="move") {
            input = input.substr(char_until_next+1 , input.length()-char_until_next+1);//cut the name
            char_until_next = endStringSpace(input);//search for space
            trainer_id = stoi(input.substr(0, input.length()-1-char_until_next));//get trainer id from input
            input = input.substr(char_until_next+1 , input.length()-char_until_next+1);//cut the id
            char_until_next = endStringSpace(input);//search for space
            int dest_trainer_id=stoi(input.substr(0, input.length()-1-char_until_next));//get trainer id from input
            input = input.substr(char_until_next+1 , input.length()-char_until_next+1);//cut the id
            char_until_next = endStringSpace(input);//search for space
            customer_id = stoi(input.substr(0, input.length()-1-char_until_next));//cut the id;
            (new MoveCustomer(trainer_id, dest_trainer_id,customer_id))->act(*this);
            input="";
        }
        if (nameOfAction(input)=="close"){
            input = input.substr(char_until_next+1 , input.length()-char_until_next+1);//cut the name
            char_until_next = endStringSpace(input);//search for space
            trainer_id = stoi(input.substr(0, input.length()-1-char_until_next));//get trainer id from input
            (new Close(trainer_id))->act(*this);
            input="";
        }
        if (nameOfAction(input)=="workout_options") {
            (new PrintWorkoutOptions())->act(*this);
            input = "";
        }
        if (nameOfAction(input)=="status") {
            input = input.substr(char_until_next+1 , input.length()-char_until_next+1);//cut the name
            char_until_next = endStringSpace(input);//search for space
            trainer_id = stoi(input.substr(0, input.length()-1-char_until_next));//get trainer id from input
            (new PrintTrainerStatus(trainer_id))->act(*this);
            input = "";
        }
        if (nameOfAction(input)=="log") {
            (new PrintActionsLog())->act(*this);
            input = "";
        }
        if (nameOfAction(input)=="backup") {
            (new BackupStudio())->act(*this);
            input = "";
            backup_id = customer_id;
        }
        if (nameOfAction(input)=="restore") {
            (new RestoreStudio())->act(*this);
            input = "";
            customer_id = backup_id;
        }
        getline(cin,input);//get next line
    }
    if (input=="closeall"){
        (new CloseAll())->act(*this);
        //delete this;
    }
}

Studio::Studio() : open(),trainers(), workout_options(), actionsLog() {
  open=true;
}

int Studio::getNumOfTrainers() const {
    return trainers.size();
}

Trainer *Studio::getTrainer(int tid) {
    for (auto & trainer : trainers)
        if (tid == trainer->getId())
            return trainer;
    return nullptr;
}

std::vector<Workout> &Studio::getWorkoutOptions() {
    return workout_options;
}

std::vector<Trainer *> &Studio::getTrainers() {
    return trainers;
}

const std::vector<BaseAction *> &Studio::getActionsLog() const {
    return actionsLog;
}

void Studio::addAction(BaseAction *action) {
    actionsLog.push_back(action);
}

bool Studio::isOpen() {
    return open;
}





