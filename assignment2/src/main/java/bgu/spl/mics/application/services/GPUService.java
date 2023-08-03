package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private GPU gpu;
    private ConcurrentLinkedQueue<Event> waitingEvent;

    public GPUService(String name1,GPU g1) {
        super(name1);
        gpu = g1;
        waitingEvent = new ConcurrentLinkedQueue<Event>();
        MessageBusImpl.getInstance().register(this);
    }
    //public void continuetrain(){
    //        gpu.tellClusterToSendBack();
   // }
    public void tickTime() {
        gpu.increaseNumberOfticks();
        if(gpu.getModel()!=null&&gpu.getmodelDividedToDB()) {
           // gpu.tellClusterToSendBack();
            gpu.tellClusterToSendBack();
            //gpu.trainModel();
            if(gpu.getDBtosent() > 0)
                gpu.sendBatchesToCluster();
        }
        if(gpu.getFinishTrainModel()){//do complete for train model event
            Event next_event = waitingEvent.poll();
            if(next_event!=null){
                complete(next_event, gpu.getModel().getResults());
            }
        }
    }

    @Override
    protected void initialize() {
        // sign up for the TrainModelEvent and TestModelEvent
        subscribeEvent(TrainModelEvent.class,event -> {
            gpu.startTrainModel(event.getM());
            waitingEvent.add(event);

        });
        subscribeEvent(TestModelEvent.class, event -> {
            gpu.setModel(event.getM());
            Model.Results r =gpu.getResult();
            complete(event,r);
        });
        subscribeBroadcast(TickBroadcast.class,callback->{
            tickTime();
        });

        subscribeBroadcast(TerminateBroadcast.class, callback->{
            terminate();
        });
    }
}
