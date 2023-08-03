package bgu.spl.mics.application.objects;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    public enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Model model;
    private Cluster cluster;
    private Deque<DataBatch> VRAM; // Save all the processed DataBatch that came from the Cluster and need to be trained
    private boolean finishTrainModel;
    private int maxDB; // How much process data batches I can hold in VRAM
    private int counterDB; // How much data batches is Trained
    private int unprocessDB; // How much data batch in data (when is 0 - all the data processed and train)
    private int clock; // The number of ticks that happen
    private int freeTicks; // The number of ticks that is not use
    private boolean modelDividedToDB;
    private int useTime;
    private AtomicInteger stoargeLeftVRAM;
    private AtomicInteger DBtosent;
    private  Stack<DataBatch> dataBatchCollection;
    public GPU(String type1){
        setType(type1);
        model = null;
        cluster = Cluster.getInstance();
        if(type1 == "RTX3090"){
            this.stoargeLeftVRAM = new AtomicInteger(32);
            maxDB = 32;
            DBtosent = new AtomicInteger(32);
        }
        else if(type1 == "RTX2080"){
            maxDB = 16;
            this.stoargeLeftVRAM = new AtomicInteger(16);
            DBtosent = new AtomicInteger(16);
        }
        else{
            maxDB = 8;
            this.stoargeLeftVRAM = new AtomicInteger(8);
            DBtosent = new AtomicInteger(8);
        }

        counterDB = 0;
        unprocessDB = 0;
        finishTrainModel = false;
        clock = 0;
        freeTicks = 0;
        VRAM = new ConcurrentLinkedDeque<DataBatch>();
        modelDividedToDB = false;
        useTime = 0;
        dataBatchCollection = new Stack<DataBatch>();


    }
    public void updateAtomic() {
        if (type.equals("RTX3090")) {
            this.stoargeLeftVRAM = new AtomicInteger(32);
            DBtosent = new AtomicInteger(32);
        } else if (type.equals("RTX2080")) {
            this.stoargeLeftVRAM = new AtomicInteger(16);
            DBtosent = new AtomicInteger(16);
        } else {
            this.stoargeLeftVRAM = new AtomicInteger(8);
            DBtosent = new AtomicInteger(8);
        }
    }

    public boolean addToVRAM (DataBatch d){// check if there is space left in VRAM and add it and decraese conter
            if (stoargeLeftVRAM.intValue() > 0) {
                stoargeLeftVRAM.decrementAndGet();
                VRAM.add(d);
                return true;
            } else return false;
    }
    public void addToVRAM1(DataBatch d){// check if there is space left in VRAM and add it and decraese conter
            stoargeLeftVRAM.decrementAndGet();
            VRAM.add(d);
    }
    /**
     * @pre model.data != nulls
     * @post A collection of data batches (1000 samples for each)
     */
    public void divideToBatches(){
        model.setStatus("Training");
        Data unprocessedData = model.getData();
       // System.out.println("GPU getting dviding to batch");
        synchronized (unprocessedData) { // we don't want that two bata will blend so we need to look it
            if (unprocessedData!=null){
                int size = (unprocessedData.getSize())/1000;
                for (int i = 0; i < size ; i++)
                    dataBatchCollection.add(new DataBatch(unprocessedData, 1000 * (i),this));
                unprocessDB = size;
              //  System.out.println("GPU send batch to cluster");
                sendBatchesToCluster();
                modelDividedToDB = true;
            }
        }
    }

    /**
     * @pre db1 != null
     * @post the cluster get the data
     */
    public void sendBatchesToCluster(){
        DataBatch dataToSend = null;
        int num = DBtosent.intValue();
        while (!dataBatchCollection.isEmpty() &&  num>0){
            dataToSend = dataBatchCollection.pop();
            DBtosent.decrementAndGet();
            //  System.out.println("gpu Send db to  cluster");
            Cluster.getInstance().sendToCpu(dataToSend);
            num --;
        }

    }

    /*public boolean freeSpotAtGpu() {
        if(getCounterDB() < getMaxDB()) {
            return true;
        }
        return false;
    }*/
    /**
     * @pre db1 not processed
     * @post db1 processed
     */
    public void tellClusterToSendBack(){
       if(stoargeLeftVRAM.intValue() > 0 )
           cluster.sendToGpu(this);
        trainModel();
      //  System.out.println("I'm telling cluster");
    }
    public void getProcessedDataFromCluster(DataBatch d1){
       // VRAM.addElement(d1);
       // counterDB++;
      //  trainModel();
    }



    /**
     * @pre model not train with data
     * @post model trained with data + data save in VRAM
     */
    public void trainModel() {
        if (!VRAM.isEmpty()) {
            if (type.toString().equals("RTX3090")) {
                if (freeTicks >= 1 && !VRAM.isEmpty()) {
                    freeTicks -= 1;
                    VRAM.poll();
                    // counterDB--;
                    stoargeLeftVRAM.incrementAndGet();
                    DBtosent.incrementAndGet();
                    unprocessDB--;
                    useTime += 1;
                }
            } else if (type.toString().equals("RTX2080")) {
               if (freeTicks >= 2 && !VRAM.isEmpty()) {
                    freeTicks -= 2;
                    VRAM.remove(0);
                    // counterDB--;
                    stoargeLeftVRAM.incrementAndGet();
                    DBtosent.incrementAndGet();
                    unprocessDB--;
                    useTime += 2;
                }
            } else {
                if (freeTicks >= 4 && !VRAM.isEmpty()) {
                    freeTicks -= 4;
                    VRAM.remove(0);
                    //counterDB--;
                    stoargeLeftVRAM.incrementAndGet();
                    DBtosent.incrementAndGet();
                    unprocessDB--;
                    useTime += 4;
                }
            }
            if (unprocessDB == 0) {
                 model.setStatus("Trained");
                if(model.getStudent().getModelsOutput().contains(model)) {
                    model.getStudent().getModelsOutput().remove(model);
                    model.getStudent().setModelsOutput(model);
                }
                else
                    model.getStudent().setModelsOutput(model);
                model.getStudent().setTest(model);
                finish();
            }
        }
    }
        //Take databatch from the VRAM if we can train this databatch



    /**
     * @pre event future object !isDone()
     * @post event future object !isDone() and result is resolved
     */
    public void finish(){
        modelDividedToDB = false;
        finishTrainModel = true;
        updateAtomic();
    }

    public Model.Results getResult() {
        Random random = new Random();
        double pro = random.nextDouble();
        if(getModel().getStudent().getStatus().equals(Student.Degree.MSc) ) {
            if (pro <= 0.6) {
                model.setResults("Good");
                model.setStatus("Tested");
                model.getStudent().setPublish(model);
                model.getStudent().removeModelsOutput(model);
                model.getStudent().setModelsOutput(model);
                return Model.Results.Good;
            }
            else {
                model.setResults("Bad");
                model.setStatus("Tested");
                model.getStudent().setPublish(model);
                model.getStudent().removeModelsOutput(model);
                model.getStudent().setModelsOutput(model);
                return Model.Results.Bad;
            }
        }
        if(getModel().getStudent().getStatus().equals(Student.Degree.PhD)){
            if(pro <= 0.8) {
                model.setResults("Good");
                model.setStatus("Tested");
                model.getStudent().setPublish(model);
                model.getStudent().removeModelsOutput(model);
                model.getStudent().setModelsOutput(model);
                return Model.Results.Good;
            }
            else {
                model.setResults("Bad");
                model.setStatus("Tested");
                model.getStudent().setPublish(model);
                model.getStudent().removeModelsOutput(model);
                model.getStudent().setModelsOutput(model);
                return Model.Results.Bad;
            }
        }
       // model = null;
        return null;
    }

    public void startTrainModel(Model m){
        model = m;
        divideToBatches();
    }
    public void setModel(Model model1){
        model = model1;
    }
    public void setType(String type1) {
        if(type1.equals("RTX3090"))
            type = Type.RTX3090;
        if(type1.equals( "RTX2080"))
            type = Type.RTX2080;
        if(type1.equals("GTX1080"))
            type = Type.GTX1080;
    }
    public boolean getFinishTrainModel(){
        return finishTrainModel;
    }
    public int getMaxDB(){
        return maxDB;
    }

    public int getCounterDB(){
        return counterDB;
    }
    public void increaseNumberOfticks(){
        clock++;
        freeTicks++;
    }

    public int getDBtosent() {
        return DBtosent.intValue();
    }

    public Model getModel() {
        return model;
    }
    public boolean getmodelDividedToDB(){
        return modelDividedToDB;
    }

    public int getClock() {
        return clock;
    }

    public int getUseTime() {
        return useTime;
    }

    public int getFreeTicks() {
        return freeTicks;
    }

    public String toString() {
        return "GPU type: " + type + ", Time used: " + useTime + "\n";
    }
}

