package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	
	/**
	 * This should be the the only public constructor in this class.
	 */
	private T result1;
	boolean done;

	public Future() {
		result1 = null;
		done = false;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * 	       
     */

	/**
	 * @post get the result
	 */
	public synchronized T get() {
		while (!isDone()){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return result1;
	}
	
	/**
     * Resolves the result of this Future object.
     */

	/**
	 * @param result
	 * @post get()==result
	 */
	public synchronized void resolve (T result) {
		result1 = result;
		done = true;
		notifyAll();
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */

	/**
	 * return if resolved
	 */
	public synchronized boolean isDone() {
		return done;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timeout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */

	/**
	 * @post get the result\null
	 */
	public synchronized T get(long timeout, TimeUnit unit) {
		if(!isDone()){
			try {
				wait(unit.toMillis(timeout));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return result1;
	}

}
