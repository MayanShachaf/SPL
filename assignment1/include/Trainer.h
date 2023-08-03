#ifndef TRAINER_H_
#define TRAINER_H_

#include <vector>
#include "Customer.h"
#include "Workout.h"

typedef std::pair<int, Workout> OrderPair;

class Trainer{
public:
    explicit Trainer(int t_capacity);
    void addCustomer(Customer* customer);
    void removeCustomer(int id);
    Customer* getCustomer(int id);
    void order(const int customer_id, const std::vector<int> workout_ids, const std::vector<Workout>& workout_options);
    void openTrainer();
    void closeTrainer();
    void closeALLTrainers();
    int getId() const;
    int getCapacity() const;
    bool isOpen();
    std::vector<Customer*>& getCustomers();
    std::vector<OrderPair>& getOrders();
    int getSalary();
    void setTrainerId(int _id);
    bool freeSpot(); // return true if the trainer have a avialble spots , else return false
    int getNumOfCust();

    void clear();
    //----------------rule of 5---------------------//
    virtual ~Trainer(); //destructor
    Trainer(const Trainer &other); //copy constructor
    Trainer(Trainer &&other); //move constructor
    Trainer& operator=(const Trainer &other); //copy assignment operator
    Trainer& operator=(Trainer &&other); //move assignment operator
    //----------------------------------------------//

private:
    int id;
    int capacity;
    bool open;
    std::vector<Customer*> customersList;
    std::vector<OrderPair> orderList; //A list of pairs for each order for the trainer - (customer_id, Workout)
    int salary;
    int numOfCust; // Save the current number of customers at the trainer
};


#endif