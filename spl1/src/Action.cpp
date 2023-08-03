#include <Studio.h>
#include <vector>
#include <Action.h>

extern Studio* backup;

ActionStatus BaseAction::getStatus() const {
    return status;
}

BaseAction::BaseAction(): errorMsg(""),status(){

}

void BaseAction::complete() {
    status = COMPLETED;

}

void BaseAction::error(std::string errorMsg) {
    status = ERROR;
    this->errorMsg = errorMsg;
}

std::string BaseAction::getErrorMsg() const {
    return errorMsg;
}

BaseAction *BaseAction::clone() {
    return nullptr;
}


OpenTrainer::OpenTrainer(int id, std::vector<Customer *> &customersList): BaseAction(),trainerId(id),customers(customersList),MyString(){

}

void OpenTrainer::act(Studio &studio) {
    size_t listSize;
    if(studio.getTrainer(trainerId)== nullptr||studio.getTrainer(trainerId)->isOpen()) {
        error("Trainer does not exist or is not open");
        for(Customer *c : customers)
            delete c;
        std::cout<< "Error: " + this->getErrorMsg()<<std::endl;
        studio.addAction(this);
        customers.clear();
        return;
    }
    else {
        int myCapacity = studio.getTrainer(trainerId)->getCapacity();
        int customerSize = customers.size();
        if(myCapacity < customerSize)
            listSize = studio.getTrainer(trainerId)->getCapacity();
        else
            listSize = customers.size();
        for(size_t i=0; i<listSize; i++){
            studio.getTrainer(trainerId)->addCustomer(customers[i]);
            MyString += customers[i]->toString() + " ";
        }
        for(size_t i=listSize; i<customers.size();i++)
            delete customers[i];
        studio.getTrainer(trainerId)->openTrainer();
        complete();
    }
    studio.addAction(this);
}

std::string OpenTrainer::toString() const {
    std::string openString = "open " + std::to_string(this->trainerId) + " ";
    openString += MyString;
    if(this->getStatus()==ERROR)
        openString +="Error: "+ this->getErrorMsg();
    else
        openString+= "Completed";
    return openString;
}

int OpenTrainer::getTrainerId() {
    return trainerId;
}

std::vector<Customer *> OpenTrainer::getCustomerList() {
    return customers;
}

OpenTrainer *OpenTrainer::clone() {
    OpenTrainer *act1 = new OpenTrainer(this->trainerId, this->customers);
    act1->MyString = this->MyString;
    if(this->getStatus()==COMPLETED)
        act1->complete();
    else
        act1->error(this->getErrorMsg());
    return act1;
}

std::string OpenTrainer::getSecretString() {
    return this->MyString;
}

Order::Order(int id): BaseAction(),trainerId(id) {

}

void Order::act(Studio &studio) {
    if(studio.getTrainer(trainerId)== nullptr ||!studio.getTrainer(trainerId)->isOpen()){
        error("Trainer does not exist or is not open");
        std::cout<< "Error: " + this->getErrorMsg()<<std::endl;
    }
    else {
        bool exist = false;
        int myCapacitySize = studio.getTrainer(trainerId)->getOrders().size();
        for(int i=0; !exist && i<myCapacitySize; i++) {
            if(studio.getTrainer(trainerId)->getOrders()[i].first != -1)
                exist = true;
        }
        if(!exist){
            int myCustomersSize = studio.getTrainer(trainerId)->getCustomers().size();
            for(int i=0; i<myCustomersSize; i++) {
                std::vector<int> workoutsOfCustomer = studio.getTrainer(trainerId)->getCustomers()[i]->order(studio.getWorkoutOptions());
                if(workoutsOfCustomer.size()!=0)
                    studio.getTrainer(trainerId)->order(studio.getTrainer(trainerId)->getCustomers()[i]->getId(), workoutsOfCustomer, studio.getWorkoutOptions());
            }
        }
        int myOrdersSize = studio.getTrainer(trainerId)->getOrders().size();
        for(int i=0; i<myOrdersSize; i++){
            if(studio.getTrainer(trainerId)->getOrders()[i].first != -1)
                std::cout<<studio.getTrainer(trainerId)->getCustomer(studio.getTrainer(trainerId)->getOrders()[i].first)->getName() + " Is Doing " + studio.getTrainer(trainerId)->getOrders()[i].second.getName() <<std::endl;
        }
        complete();
    }
    studio.addAction(this);
}

std::string Order::toString() const {
    std::string orderString = "order " + std::to_string(this->trainerId) + " ";
    if(this->getStatus()==ERROR)
        orderString +="Error: "+ this->getErrorMsg();
    else
        orderString+= "Completed";
    return orderString;
}

int Order::getTrainerId() {
    return trainerId;
}

Order *Order::clone() {
    Order *act1 = new Order(this->trainerId);;
    if(this->getStatus()==COMPLETED)
        act1->complete();
    else
        act1->error(this->getErrorMsg());
    return act1;
}

MoveCustomer::MoveCustomer(int src, int dst, int customerId): BaseAction(),srcTrainer(src),dstTrainer(dst),id(customerId){

}

void MoveCustomer::act(Studio &studio) {
    if(studio.getTrainer(srcTrainer)== nullptr ||
       !studio.getTrainer(srcTrainer)->isOpen() ||
       studio.getTrainer(srcTrainer)->getCustomer(id)== nullptr ||
       studio.getTrainer(dstTrainer)== nullptr ||
       !studio.getTrainer(dstTrainer)->isOpen() ||
       !studio.getTrainer(dstTrainer)->freeSpot()) {
        error("Cannot move customer");
        std::cout<< "Error: " + this->getErrorMsg()<<std::endl;
    }
    else if(srcTrainer == dstTrainer)
        complete();
    else {
        int num=studio.getTrainer(srcTrainer)->getNumOfCust();
        std::vector<int> workout_ids = studio.getTrainer(srcTrainer)->getCustomer(id)->order(studio.getWorkoutOptions());
        studio.getTrainer(dstTrainer)->addCustomer( studio.getTrainer(srcTrainer)->getCustomer(id));//clone?
        studio.getTrainer(dstTrainer)->order(id , workout_ids , studio.getWorkoutOptions());
        studio.getTrainer(srcTrainer)->removeCustomer(id);
        if(num == 1)
            studio.getTrainer(srcTrainer)->closeTrainer();
        complete();
    }
    studio.addAction(this);
}

std::string MoveCustomer::toString() const {
    std::string moveCustString = "move " + std::to_string(this->srcTrainer) + " " + std::to_string(this->dstTrainer) + " " + std::to_string(this->id) + " ";
    if(this->getStatus()==ERROR)
        moveCustString +="Error: "+ this->getErrorMsg();
    else
       moveCustString+= "Completed";
    return moveCustString;
}

int MoveCustomer::getSrcTrainer() {
    return srcTrainer;
}

int MoveCustomer::getDstTrainer() {
    return dstTrainer;
}

int MoveCustomer::getId() {
    return id;
}

MoveCustomer *MoveCustomer::clone() {
    MoveCustomer *act1 =  new MoveCustomer(this->srcTrainer , this->dstTrainer , this->id);
    if(this->getStatus()==COMPLETED)
        act1->complete();
    else
        act1->error(this->getErrorMsg());
    return act1;
}

Close::Close(int id): BaseAction(),trainerId(id) {

}

void Close::act(Studio &studio) {
    if(!studio.getTrainer(trainerId)->isOpen() ||
       studio.getTrainer(trainerId)== nullptr) {
        error("Trainer does not exist or is not open");
        std::cout<< "Error: " + this->getErrorMsg()<<std::endl;
    }
    else {
        studio.getTrainer(trainerId)->closeTrainer();// Help !
        std::cout<<"Trainer " + std::to_string(trainerId) + " closed. Salary " + std::to_string(studio.getTrainer(trainerId)->getSalary()) + "NIS" <<std::endl;
        complete();
    }
    studio.addAction(this);
}

std::string Close::toString() const {
    std::string closeString = "close " + std::to_string(this->trainerId) + " ";
    if(this->getStatus()==ERROR)
        closeString +="Error: "+ this->getErrorMsg();
    else
        closeString+= "Completed";
    return closeString;
}

int Close::getTrainerId() {
    return trainerId;
}

Close *Close::clone() {
    Close *act1 =  new Close(this->trainerId);
    if(this->getStatus()==COMPLETED)
        act1->complete();
    else
        act1->error(this->getErrorMsg());
    return act1;
}

CloseAll::CloseAll(): BaseAction() {

}

void CloseAll::act(Studio &studio) {
    int numOfTrainers = studio.getTrainers().size();
    for(int i=0; i<numOfTrainers;i++){
        if(studio.getTrainers()[i]->isOpen()){
            std::cout<<"Trainer " + std::to_string(studio.getTrainers()[i]->getId()) << " closed. Salary " + std::to_string(studio.getTrainers()[i]->getSalary()) << "NIS" <<std::endl;
            studio.getTrainers()[i]->closeALLTrainers();
        }
        else{
            studio.getTrainers()[i]->getOrders().clear();
            studio.getTrainers()[i]->getCustomers().clear();

        }
    }
    complete();
    studio.addAction(this);


}

std::string CloseAll::toString() const {
    return "closeall Completed";
}

CloseAll *CloseAll::clone() {
    CloseAll *act1 =  new CloseAll();
    if(this->getStatus()==COMPLETED)
        act1->complete();
    else
        act1->error(this->getErrorMsg());
    return act1;
}





PrintWorkoutOptions::PrintWorkoutOptions(): BaseAction() {

}

void PrintWorkoutOptions::act(Studio &studio) {
    int workoutSize = studio.getWorkoutOptions().size();
    for(int i=0; i<workoutSize; i++) {
        int type = studio.getWorkoutOptions()[i].getType();
        std::string typeName;
        if(type == 0)
            typeName = "Anaerobic";
        if(type == 1)
            typeName = "Mixed";
        if(type == 2)
            typeName = "Cardio";
        std::cout<< studio.getWorkoutOptions()[i].getName() + ", " + typeName + ", " +  std::to_string(studio.getWorkoutOptions()[i].getPrice()) <<std::endl;
    }
    complete();
    studio.addAction(this);
}

std::string PrintWorkoutOptions::toString() const {
    return "workout_options Completed";
}

PrintWorkoutOptions *PrintWorkoutOptions::clone() {
    PrintWorkoutOptions *act1 =  new PrintWorkoutOptions();
    if(this->getStatus()==COMPLETED)
        act1->complete();
    else
        act1->error(this->getErrorMsg());
    return act1;
}

PrintTrainerStatus::PrintTrainerStatus(int id): BaseAction(),trainerId(id) {

}

void PrintTrainerStatus::act(Studio &studio) {
    if(studio.getTrainer(trainerId)->isOpen()) {
        std::cout << "Trainer " + std::to_string(studio.getTrainer(trainerId)->getId()) + " status: open" << std::endl;
        std::cout << "Customers:" << std::endl;
        int myCustomerSize = studio.getTrainer(trainerId)->getCustomers().size();
        for (int i = 0; i < myCustomerSize; i++)
            std::cout << std::to_string(studio.getTrainer(trainerId)->getCustomers()[i]->getId()) + " " +
                         studio.getTrainer(trainerId)->getCustomers()[i]->getName()  << std::endl;
        std::cout << "Orders:" << std::endl;
        int myOrderSize = studio.getTrainer(trainerId)->getOrders().size();
        for (int i = 0; i < myOrderSize; i++)
            if (studio.getTrainer(trainerId)->getOrders()[i].first !=-1) {
                std::cout << studio.getTrainer(trainerId)->getOrders()[i].second.getName() + " " +
                             std::to_string(studio.getTrainer(trainerId)->getOrders()[i].second.getPrice()) + "NIS " +
                             std::to_string(studio.getTrainer(trainerId)->getOrders()[i].first) << std::endl;
            }
        std::cout<< "Current Trainer's Salary: " + std::to_string(studio.getTrainer(trainerId)->getSalary()) + "NIS" << std::endl;
    }
    else
        std::cout<< "Trainer " + std::to_string(studio.getTrainer(trainerId)->getId()) + " status: closed" <<std::endl;
    complete();
    studio.addAction(this);
}

std::string PrintTrainerStatus::toString() const {
    std::string statusString = "status " + std::to_string(this->trainerId) + " ";
    if(this->getStatus()==ERROR)
        statusString +="Error: "+ this->getErrorMsg();
    else
        statusString+= "Completed";
    return statusString;
}

int PrintTrainerStatus::getTrainerId() {
    return trainerId;
}

PrintTrainerStatus *PrintTrainerStatus::clone() {
    PrintTrainerStatus *act1 =  new PrintTrainerStatus(trainerId);
    if(this->getStatus()==COMPLETED)
        act1->complete();
    else
        act1->error(this->getErrorMsg());
    return act1;
}

PrintActionsLog::PrintActionsLog(): BaseAction() {

}

void PrintActionsLog::act(Studio &studio) {
    for (BaseAction *action: studio.getActionsLog())
        std::cout << action->toString() << std::endl;
    complete();
    studio.addAction(this);
}

std::string PrintActionsLog::toString() const {
    return "log Completed";
}

PrintActionsLog *PrintActionsLog::clone() {
    PrintActionsLog *act1 =  new PrintActionsLog();
    if(this->getStatus()==COMPLETED)
        act1->complete();
    else
        act1->error(this->getErrorMsg());
    return act1;
}

BackupStudio::BackupStudio(): BaseAction() {

}

void BackupStudio::act(Studio &studio) {
    delete backup;
    studio.addAction(this);
    backup = new Studio(studio);
    complete();
}

std::string BackupStudio::toString() const {
    return "backup Completed";
}

BackupStudio *BackupStudio::clone() {
    BackupStudio *act1 =  new BackupStudio();
    if(this->getStatus()==COMPLETED)
        act1->complete();
    else
        act1->error(this->getErrorMsg());
    return act1;
}

RestoreStudio::RestoreStudio(): BaseAction() {

}

void RestoreStudio::act(Studio &studio) {

    if(backup == nullptr) {
        error( "No backup available");
        std::cout<< "Error: " + this->getErrorMsg()<<std::endl;
        studio.addAction(this);
    }
    else {
        complete();
        studio.addAction(this);
        studio = *backup;
    }
}

std::string RestoreStudio::toString() const {
    std::string restorString = "restore ";
    if(this->getStatus()==ERROR)
        restorString+="Error: "+ this->getErrorMsg();
    else
        restorString+= "Completed";
    return restorString;
}

RestoreStudio *RestoreStudio::clone() {
    RestoreStudio *act1 =  new RestoreStudio();
    if(this->getStatus()==COMPLETED)
        act1->complete();
    else
        act1->error(this->getErrorMsg());
    return act1;
}
