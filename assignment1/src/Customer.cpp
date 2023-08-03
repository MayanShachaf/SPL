#include "Customer.h"
#include <utility>
#include <vector>
#include <algorithm>
#include <limits>

Customer::Customer(std::string c_name, int c_id):name(std::move(c_name)),id(c_id),workout_options(){

}

std::string Customer::getName() const {return name;}

int Customer::getId() const {return id;}


SweatyCustomer::SweatyCustomer(std::string name, int id) : Customer(std::move(name), id),SWT_Order() {

}

std::vector<int> SweatyCustomer::order(const std::vector<Workout> &workout_options){
    for (const auto & workout_option : workout_options) {
        if(workout_option.getType()==2)
             SWT_Order.push_back(workout_option.getId());
    }
    return SWT_Order;
}

std::string SweatyCustomer::toString() const {
    return getName() + ",swt";
}

Customer *SweatyCustomer::clone() {
    auto *customer1 = new SweatyCustomer(getName(), this->getId());
    for(int order : this->getOrders()) {
        customer1->SWT_Order.push_back(order);
    }
    return customer1;
}

std::vector<int> SweatyCustomer::getOrders() {
    return SWT_Order;
}

CheapCustomer::CheapCustomer(std::string name, int id) : Customer(std::move(name), id),CHP_Order() {

}

std::vector<int> CheapCustomer::order(const std::vector<Workout> &workout_options){
    int id = 0 , price = workout_options[0].getPrice();
    for(Workout w:  workout_options){
        if(w.getPrice()<price) {
            id = w.getId();
            price = w.getPrice();
        }
    }
    CHP_Order.push_back(id);
    return CHP_Order;
}

std::string CheapCustomer::toString() const{
    return getName() + ",chp";
}

Customer *CheapCustomer::clone() {
    auto *customer1 = new CheapCustomer(this->getName(),this->getId());
    for(int order : this->getOrders()) {
        customer1->CHP_Order.push_back(order);
    }
    return customer1;
}

std::vector<int> CheapCustomer::getOrders() {
    return CHP_Order;
}

HeavyMuscleCustomer::HeavyMuscleCustomer(std::string name, int id) : Customer(std::move(name), id),HML_Order() {

}

std::vector<int> HeavyMuscleCustomer::order(const std::vector<Workout> &workout_options){
   std::vector<int> order_list = std::vector<int>();
   for(const auto & workout_option : workout_options){
       if(workout_option.getType() == ANAEROBIC)
           order_list.push_back(workout_option.getId());
   }
   std::stable_sort(order_list.begin(), order_list.end(), [&workout_options](int id1, int id2) {
        return (workout_options[id1].getPrice() > workout_options[id2].getPrice());
    });
    return order_list;
}

std::string HeavyMuscleCustomer::toString() const{
    return getName() + ",mcl";
}

Customer *HeavyMuscleCustomer::clone() {
    auto *customer1 = new HeavyMuscleCustomer(this->getName(),this->getId());
    for(int order : this->getOrders()) {
        customer1->HML_Order.push_back(order);
    }
    return customer1;
}

std::vector<int> HeavyMuscleCustomer::getOrders() {
    return HML_Order;
}

FullBodyCustomer::FullBodyCustomer(std::string name, int id) : Customer(name, id),FBD_Order() {
}

std::vector<int> FullBodyCustomer::order(const std::vector<Workout> &workout_options){
    int id0=0 , id1=0 , id2=0;
    double price0=std::numeric_limits<double>::infinity() , price1=0 , price2=std::numeric_limits<double>::infinity();
    for (const auto & workout_option : workout_options) { // after this loop id0 will be with the cheapest id ANAEROBIC workout , id1 will be with the most expensive id MIXED workout , id2 will be with the cheapest id CARDIO workout
        if(workout_option.getType()==0 && workout_option.getPrice()<price0) {
            id0 = workout_option.getId();
            price0 = workout_option.getPrice();
        }
        if(workout_option.getType()==1 && workout_option.getPrice()>price1) {
            id1 = workout_option.getId();
            price1 = workout_option.getPrice();
        }
        if(workout_option.getType()==2 && workout_option.getPrice()<price2) {
            id2 = workout_option.getId();
            price2 = workout_option.getPrice();
        }
    }
    FBD_Order.push_back(id2);
    FBD_Order.push_back(id1);
    FBD_Order.push_back(id0);
    return FBD_Order;
}

std::string FullBodyCustomer::toString() const{
    return getName() + ",fbd";
}

Customer *FullBodyCustomer::clone() {
    auto *customer1 = new FullBodyCustomer(this->getName(),this->getId());
    for(int order : this->getOrders()) {
        customer1->FBD_Order.push_back(order);
    }
    return customer1;
}

std::vector<int> FullBodyCustomer::getOrders() {
    return FBD_Order;
}
