package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.CPUService;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private Collection<DataBatch> dataBatchCollection;
    private Cluster cluster;
    private int clock; // The number of ticks that happen
    private int freeTicks; // The number of ticks that is not use
    private int useTime;
    private int processData;

    public CPU(int cores1) {
        cores = cores1;
        dataBatchCollection = new ConcurrentLinkedDeque<DataBatch>();
        cluster = Cluster.getInstance();
        clock = 0;
        freeTicks = 0;
        useTime = 0;
        processData = 0;
    }

    /**
     * @param d
     * @pre d!=null
     * @post d is in the Collection
     */
    public void setNewData(DataBatch d) {
        synchronized (d){
            if(d!=null) {
                dataBatchCollection.add(d);
            }
        }
    }

    public void getData() {//take the data batch from data batch collection and process by type if has ticks
        DataBatch db = ((ConcurrentLinkedDeque<DataBatch>) dataBatchCollection).peekFirst();
        if (db != null) {
            if (db.getData().getType().toString().equals(Data.Type.Images.toString()))
                processImages(db);
            if (db.getData().getType().toString().equals(Data.Type.Text.toString()))
                processText(db);
            if (db.getData().getType().toString().equals(Data.Type.Tabular.toString()))
                processTanular(db);
        }
    }

    /**
     * @param d
     * @pre d(start_index).type == Image
     * @post d is processed
     */
    public void processImages(DataBatch d){
        int TicksForProcess = (32 / getCores()) * 4;
      if(freeTicks>=TicksForProcess && !dataBatchCollection.isEmpty()){
            freeTicks -= TicksForProcess;
            d.getData().setProcessed();
            dataBatchCollection.remove(d);
            sendToCluster(d);
            useTime += TicksForProcess;
            processData ++;
        }
    }

    /**
     * @parm d
     * @pre d(start_index).type == Text
     * @post d is processed
     */
    public void processText(DataBatch d){
        int TicksForProcess = (32 / getCores()) * 2;
        if(freeTicks>=TicksForProcess && !dataBatchCollection.isEmpty()){
            freeTicks -= TicksForProcess;
            d.getData().setProcessed();
            dataBatchCollection.remove(d);
            sendToCluster(d);
            useTime += TicksForProcess;
            processData ++;
        }
    }


    /**
     * @parm d
     * @pre d(start_index).type == Tanular
     * @post d is processed
     */
    public void processTanular(DataBatch d){
        int TicksForProcess = (32 / getCores()) * 1;
        if(freeTicks>=TicksForProcess&& !dataBatchCollection.isEmpty()){
            freeTicks -= TicksForProcess;
            d.getData().setProcessed();
            dataBatchCollection.remove(d);
            sendToCluster(d);
            processData ++;
        }
    }

    /**
     * @pre Collection.size
     * @post Collection.size - 1
     */

    public void sendToCluster(DataBatch d){
        Cluster.getInstance().SetProcessedData(d);
       // System.out.println("cpu finish process 1 db");
    }

    public int getCores() {
        return cores;
    }

    public int getClock() {
        return clock;
    }

    public void increaseNumberOfticks(){
        clock++;
        freeTicks++;
    }

    public int getProcessData() {
        return processData;
    }

    public int getUseTime() {
        return useTime;
    }

    public String toString() {
        return "CPU corse: " + cores + ", Time used: " + useTime + ", The Data that CPU process: " + processData + "\n";
    }

}
