#ifndef STUDIO_H_
#define STUDIO_H_
#include <string>
#include <vector>
#include "Trainer.h"
#include "Action.h"
#include "Workout.h"




class Studio{		
public:
	Studio();
    Studio(const std::string &configFilePath);
    void start();
    int getNumOfTrainers() const;
    Trainer* getTrainer(int tid);
    std::vector<Trainer*>& getTrainers();
    std::vector<Workout>& getWorkoutOptions();
    const std::vector<BaseAction*>& getActionsLog() const; // Return a reference to the history of actions
    void addAction(BaseAction* action); // Add action to the history
    //Studio* copy() const;
    void clear();
    bool isOpen(); // return if the studio is open

    //----------------rule of 5---------------------//
    virtual ~Studio(); //destructor
    Studio(const Studio &other); //copy constructor
    Studio(Studio &&other); //move constructor
    Studio& operator=(const Studio &other); //copy assignment operator
    Studio& operator=(Studio &&other); //move assignment operator
    //----------------------------------------------//

private:
    bool open;
    std::vector<Trainer*> trainers;
    std::vector<Workout> workout_options;
    std::vector<BaseAction*> actionsLog;
};

#endif