#include "Trainer.h"
#include <vector>
#include <iostream>

void Trainer::clear() {
    if (!customersList.empty()) {
        for (auto  *c: customersList) {
            delete c;
        }
    }
    customersList.clear();
    orderList.clear();
    id = -1;
    capacity = -1;
    open = false;
    salary = -1;
    numOfCust = -1;
}

//----------------rule of 5---------------------//

// Destructor
Trainer::~Trainer() {
    clear();
}

// Copy Constructor
Trainer::Trainer(const Trainer &other):id(other.id),capacity(other.capacity),open(other.open),customersList(),orderList(),salary(other.salary),numOfCust(other.numOfCust){
    for (Customer *c: other.customersList) {
        customersList.push_back(c->clone());
    }
    for (const auto & i : other.orderList) {
        OrderPair p = std::make_pair(i.first, i.second);
        orderList.push_back(p);
    }
}

// Move Constructor
Trainer::Trainer(Trainer &&other): id(other.id),capacity(other.capacity),open(other.open),customersList(),orderList(),salary(other.salary),numOfCust(other.numOfCust) {
    for (Customer *c : other.customersList) {
        customersList.push_back(c);
    }
    for (OrderPair order : other.orderList) {
        orderList.push_back(std::make_pair(order.first,order.second.copy_workout()));
    }
    other.customersList.clear();
    other.id = -1;
    other.capacity = -1;
    other.open = false;
    other.salary = -1;
    other.numOfCust = -1;
}

// Assignment Operator
Trainer &Trainer::operator=(const Trainer &other) {
    if(this != &other) {
        clear();
        this->capacity = other.capacity;
        this->id = other.id;
        this->open = other.open;
        for (Customer *c : other.customersList) {
            customersList.push_back(c);
        }
        for (OrderPair order : other.orderList) {
            orderList.push_back(std::make_pair(order.first,order.second.copy_workout()));
        }
        salary = other.salary;
        numOfCust = other.numOfCust;
    }
    return *this;
}

// Move Assignment Operator
Trainer &Trainer::operator=(Trainer &&other) {
    if(this != &other) {
        clear();
        this->capacity = other.capacity;
        this->id = other.id;
        this->open = other.open;
        for (Customer *c : other.customersList) {
            customersList.push_back(c);
        }
        for (OrderPair order : other.orderList) {
            orderList.push_back(std::make_pair(order.first,order.second.copy_workout()));
        }
        salary = other.salary;
        numOfCust = other.numOfCust;
    }
    other.id = -1;
    other.capacity = -1;
    other.open = false;
    other.salary = -1;
    other.numOfCust = -1;
    return *this;
}

//----------------------------------------------//



Trainer::Trainer(int t_capacity): id(-1),capacity(t_capacity),open(false),customersList(),orderList(),salary(0),numOfCust(0) {

}

void Trainer::addCustomer(Customer *customer) {
    std::vector<Customer *> customer_vector;
    int num= customer->getId();
    bool found = false;
    if(numOfCust == 0)
        customer_vector.push_back(customer);
    else {
        for(Customer *c: customersList) {
            int idCustList = c->getId();
            if (idCustList < num) {
                customer_vector.push_back(c);
            }
            if (idCustList > num && !found) {
                customer_vector.push_back(customer);
                found = true;
            }
            if (found) {
                customer_vector.push_back(c);
            }
        }
        if (!found) {
            customer_vector.push_back(customer);
        }
    }
    this->customersList=customer_vector;
    numOfCust++;
}

void Trainer::removeCustomer(int id) {
    std::vector<Customer *> customer_vector;
    for (auto &i: orderList) {
        if (i.first == id) {
            this->salary -= i.second.getPrice();
            i.first = -1;
        }
    }
    numOfCust--;

    for (Customer *c: customersList) {
        if (c->getId() == id) {
            for (Customer *other: customersList) {
                if (other != c) {
                    customer_vector.push_back(other);
                }
            }
            break;
        }
    }
    this->customersList=customer_vector;

}



Customer *Trainer::getCustomer(int id) {
    for (Customer *c: customersList)
        if (c->getId() == id)
            return c;
    return nullptr;
}

void Trainer::order(const int customer_id, const std::vector<int> workout_ids, const std::vector<Workout>& workout_options) {
    std::vector<Workout> workoutList;
    for (int workout_id : workout_ids) {
        orderList.push_back(std::make_pair(customer_id, workout_options[workout_id]));

        salary += workout_options[workout_id].getPrice();
    }
}

void Trainer::openTrainer() {
    open = true;
}

void Trainer::closeTrainer() {
    open = false;
    if (!customersList.empty()){
        for(Customer *c : customersList)
            delete c;
    }
    customersList.clear();
    orderList.clear();
}
void Trainer::closeALLTrainers() {
    open = false;
    for(Customer *c : customersList)
        delete c;
    customersList.clear();
    orderList.clear();

}


int Trainer::getId() const {
    return id;
}

int Trainer::getCapacity() const {
    return capacity;
}

bool Trainer::isOpen() {
    return open;
}

std::vector<Customer *> &Trainer::getCustomers() {
    return customersList;
}

std::vector<OrderPair> &Trainer::getOrders() {
    return orderList;
}

int Trainer::getSalary() {
    return salary;
}

bool Trainer::freeSpot() {
    return numOfCust<capacity;
}

void Trainer::setTrainerId(int _id) {
    id = _id;
}

int Trainer::getNumOfCust() {
    return numOfCust;
}


























