package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.GPU;

/**
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {

    private CPU cpu;

    public CPUService(String name, CPU c1) {
        super(name);
        cpu = c1;
        MessageBusImpl.getInstance().register(this);

    }

    public void tickTime() {
       // System.out.println("cpu tick");
        cpu.increaseNumberOfticks();
        cpu.getData();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, Broadcast->{
            tickTime();
        });
        subscribeBroadcast(TerminateBroadcast.class,Broadcast->{
            terminate();
        });
    }
}
