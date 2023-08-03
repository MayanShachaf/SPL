package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private long tickTime;
	private int duration;
	private int numOfTics;

	public TimeService(long tickTime1, int duration1) {
		super("Time");
		tickTime = tickTime1;
		duration = duration1;
		numOfTics = 0;
		MessageBusImpl.getInstance().register(this);
	}

	protected void initialize() {
		Timer t = new Timer();
		TerminateBroadcast terminateBroadcast = new TerminateBroadcast();
		TimerTask clock=new TimerTask() {
			public void run() {
				int i = 1;
				if ((duration - numOfTics) > 0) {
					TickBroadcast b = new TickBroadcast(i);
					i++;
					sendBroadcast(b);
					numOfTics ++;

				}
				if((duration - numOfTics) == 0) {
					//System.out.println("Terminate Time Service");
					sendBroadcast(terminateBroadcast);
					t.cancel();
					terminate();
				}
			}
		};
		t.schedule(clock, 0, tickTime);
	}
}
