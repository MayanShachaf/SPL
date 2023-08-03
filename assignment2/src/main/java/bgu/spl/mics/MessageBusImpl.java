package bgu.spl.mics;

import bgu.spl.mics.application.objects.Data;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.HashMap.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */

// Using HashMap -to match each "Q" to specific MS(saw in the forum)
public class MessageBusImpl implements MessageBus {

	// each MicroService has his own queue
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> microServicesQueue;
	// each Message have the MicroService that can handle it
	private ConcurrentHashMap<Class<? extends Message>, LinkedBlockingQueue<MicroService>> registerToTypeMessage;
	// each Event will update at the Future his result
	private ConcurrentHashMap<Event, Future> eventFutureDic;
	// object that is lock for the messages
	Object lock;

	@Override
	public boolean isMicroServiceRegistred(MicroService m) {
		return microServicesQueue.containsKey(m);
	}

	@Override
	public <T> boolean isSubscribeToEventT(Class<? extends Event<T>> type, MicroService m) {
		if(registerToTypeMessage.containsKey(type))
			return registerToTypeMessage.get(type).contains(m);
		else
			return false;
	}

	@Override
	public boolean isSubscribeToBroadcastT(Class<? extends Broadcast> type, MicroService m) {
		if (registerToTypeMessage.containsKey(type)&&!registerToTypeMessage.get(type).isEmpty()&&registerToTypeMessage.get(type).contains(m))
			return true;
		else
			return false;
	}

	private MessageBusImpl() {
		microServicesQueue = new ConcurrentHashMap<>();
		registerToTypeMessage = new ConcurrentHashMap<>();
		eventFutureDic = new ConcurrentHashMap<>();
		lock = new Object();
	}

	private static class instanceOfMessageBus {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	public static MessageBusImpl getInstance() {
		return instanceOfMessageBus.instance;
	}

	@Override
	/**
	 * @param type m
	 * @pre  Message bus can't assign Event<T> type to m
	 * @post Message bus can assign Event<T> type to m
	 */

	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {//Banana
		synchronized (this) {// while updating the hash map we want to lock to make sure that there is only one queue for each type
			if(isMicroServiceRegistred(m)){
				if(registerToTypeMessage.get(type) == null) {
					registerToTypeMessage.put(type, new LinkedBlockingQueue<MicroService>());
					registerToTypeMessage.get(type).add(m);
				}
				else if(!registerToTypeMessage.get(type).contains(m))
					registerToTypeMessage.get(type).add(m);
			}
		}
	}

	@Override
	/**
	 * @param type m
	 * @pre Message bus can't send Broadcast<T> type to m
	 * @post Message bus wiil send Broadcst<T> type to m
	 */

	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (this) { // while updating the hash map we want to lock to make sure that there is only one queue for each type
			if(isMicroServiceRegistred(m) ) {
				//if (!registerToTypeMessage.containsKey(type)){
				if (registerToTypeMessage.get(type) == null) {
					registerToTypeMessage.put(type, new LinkedBlockingQueue<MicroService>());
					registerToTypeMessage.get(type).add(m);
				}
				else if(!registerToTypeMessage.get(type).contains(m)) {
					registerToTypeMessage.get(type).add(m);
				}
			}
		}
	}

	@Override
	/**
	 * @param e result
	 * @pre e!=null , result!=null
	 * @post m.complete(e,result) done
	 */
	public <T> void complete(Event<T> e, T result) {
		synchronized (e) { // while resolving the Future we want to lock to make sure the result update only once
			if (eventFutureDic.get(e) != null & result !=null) {
				eventFutureDic.get(e).resolve(result);
			}
		}
	}

	@Override
	/**
	 * @param b
	 * @pre b!=null
	 * @post check if b added for each microSerrvice that subscribe to b
	 */
	public void sendBroadcast(Broadcast b) {
		// check if there is a broadcast b and if he has MicroServices that register
		if(registerToTypeMessage.containsKey(b.getClass()) ){
			LinkedBlockingQueue<MicroService> myList = registerToTypeMessage.get(b.getClass());
			if(!myList.isEmpty()){
				for(MicroService m: myList)
					if(isMicroServiceRegistred(m))
						try{
							microServicesQueue.get(m).put(b);
							//System.out.println(" sent bro");
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (NullPointerException e) {

						}
			}

		}
	}
	
	@Override
	/**
	 * @param e
	 * @pre e!=null
	 */
	public <T> Future<T> sendEvent(Event<T> e) {
		boolean done = false;
		Future<T> myFuture = new Future<T>();
		if ( registerToTypeMessage.get(e.getClass())==null && registerToTypeMessage.get(e.getClass()).isEmpty() ) {
			return null;
		}
		MicroService m1;
		eventFutureDic.put(e, myFuture);
		do {
			synchronized (e.getClass()) { // we want that the MS m1 will not be unregister while this action
				m1 = registerToTypeMessage.get(e.getClass()).peek();
				//boolean added = false;
				//while(!added)
				registerToTypeMessage.get(e.getClass()).add(m1);
				registerToTypeMessage.get(e.getClass()).poll();
			}
			try {
				done = microServicesQueue.get(m1).offer(e);
				//System.out.println("add event to Q");
			}
			catch (NullPointerException err) {
				System.out.println("The MicroService unregister while we done this operation");
			}
		} while(!done);
		return myFuture;
	}


	@Override
	/**
	 * @param m
	 * @pre isMicroServiceRegistred(m)==false
	 * @post isMicroServiceRegistred(m)==true
	 */
	public void register(MicroService m) {
		while (!isMicroServiceRegistred(m)) // add a new queue to m if not exist
				microServicesQueue.put(m,new LinkedBlockingQueue<Message>());
	}

	@Override
	/**
	 * @param m
	 * @pre isMicroServiceRegistred(m)==true
	 * @post isMicroServiceRegistred(m)==false
	 */
	public void unregister(MicroService m) {
		if(isMicroServiceRegistred(m)) {
			for (LinkedBlockingQueue<MicroService> microServiceQueue: registerToTypeMessage.values()) {
				if(microServiceQueue.contains(m))
					microServiceQueue.remove(m);
			}
            microServicesQueue.remove(m); // remove the queue from the HashMpa
		}
	}

	@Override
	/**
	 * @param m
	 * @pre isMicroServiceRegistred(m)==true
	 * @post number of messeges in queue -= 1
	 */
	public Message awaitMessage(MicroService m) throws InterruptedException {
		synchronized (lock) {
			if (!isMicroServiceRegistred(m))
				throw new IllegalStateException("MicroService is not register");
			if (microServicesQueue.containsKey(m)) {
				while (microServicesQueue.get(m).isEmpty()) {
						lock.wait(1);
				}
				return microServicesQueue.get(m).remove();
			}
		}
		/*	Message message = null;
			try {
				message =
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return message;
*/
		return null;
	}

	public ConcurrentHashMap<Event, Future> getEventFutureDic() {
		return eventFutureDic;
	}

}