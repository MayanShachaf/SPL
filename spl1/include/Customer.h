#ifndef CUSTOMER_H_
#define CUSTOMER_H_
#include <vector>
#include <string>
#include "Workout.h"


class Customer{
public:
    Customer(std::string c_name, int c_id);
    virtual std::vector<int> order(const std::vector<Workout> &workout_options)=0;
    virtual std::string toString() const = 0;
    std::string getName() const;
    int getId() const;
    virtual Customer *clone()=0;
    virtual ~Customer()=default;

private:
    const std::string name;
    const int id;
    std::vector<Workout> workout_options;
};


class SweatyCustomer : public Customer {
public:
	SweatyCustomer(std::string name, int id);
    std::vector<int> order(const std::vector<Workout> &workout_options);
    std::string toString() const;
    Customer *clone() override;
    std::vector<int> getOrders();
private:
    std::vector<int> SWT_Order;
};


class CheapCustomer : public Customer {
public:
	CheapCustomer(std::string name, int id);
    std::vector<int> order(const std::vector<Workout> &workout_options);
    std::string toString() const;
    Customer *clone() override;
    std::vector<int> getOrders();
private:
    std::vector<int> CHP_Order;
};


class HeavyMuscleCustomer : public Customer {
public:
	HeavyMuscleCustomer(std::string name, int id);
    std::vector<int> order(const std::vector<Workout> &workout_options);
    std::string toString() const;
    Customer *clone() override;
    std::vector<int> getOrders();
private:
    std::vector<int> HML_Order;
};



class FullBodyCustomer : public Customer {
public:
	FullBodyCustomer(std::string name, int id);
    std::vector<int> order(const std::vector<Workout> &workout_options);
    std::string toString() const;
    Customer *clone() override;
    std::vector<int> getOrders();
private:
    std::vector<int> FBD_Order;

};


#endif