package bgu.spl.mics.application.services;

import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;

import javax.xml.transform.Result;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConferenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    private ConfrenceInformation MyConferenceInf = null;
    private int tickTime;
    public ConferenceService(String name, ConfrenceInformation confrenceInformation) {
        super(name);
        MyConferenceInf = confrenceInformation;
        tickTime = 0;
        MessageBusImpl.getInstance().register(this);
    }
    public void tickTime() {
        tickTime++;
        //System.out.println("get into the tick time of confrence service");
        if(tickTime == MyConferenceInf.getDate()) {
            PublishConferenceBroadcast b = new PublishConferenceBroadcast(getName(),MyConferenceInf.getSuccessfulModelsNames());
            sendBroadcast(b);
           // System.out.println("sent PublishConferenceBroadcast");
            terminate();
        }
       // System.out.println("My conference got ticktime " + tickTime);
    }

    public ConfrenceInformation getMyConferenceInf() {
        return MyConferenceInf;
    }
    @Override
    protected void initialize() {
        subscribeEvent(PublishResultsEvent.class, event -> {
            MyConferenceInf.addModel(event.getM());
            complete(event, event.getM());
        });
        subscribeBroadcast(TickBroadcast.class, Broadcast -> {
           // System.out.println("conference do tick");
            tickTime();
        });
        subscribeBroadcast(TerminateBroadcast.class, callback->{
            terminate();
        });
    }
}

