package bgu.spl.mics.application.objects;


import bgu.spl.mics.Event;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.*;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private Vector<CPU> CPUs;
	private Vector<GPU> GPUs;
	//private Vector<CPU> freeCPUs;
	private ConcurrentHashMap<GPU, BlockingDeque<DataBatch>> myData;
	/**
     * Retrieves the single instance of this class.
     */
	private static class SingletonHolder {
		private static Cluster instance = new Cluster();
	}

	private Cluster() {
		CPUs = new Vector<CPU>();
		GPUs = new Vector<GPU>();
	//	freeCPUs = new Vector<CPU>();
		myData = new ConcurrentHashMap<GPU,BlockingDeque<DataBatch>>();
	}

	public static Cluster getInstance() {
		return SingletonHolder.instance;
	}

    public void sendToCpu(DataBatch d1) {
		CPU c;
		/*if(!freeCPUs.isEmpty()) {
			c = freeCPUs.remove(0);
		}
		else {*/
		synchronized (CPUs) {
			c = CPUs.remove(0);
			CPUs.addElement(c);
			c.setNewData(d1);
		}
		//}
		//System.out.println("send to cpu");

	}
	public void SetProcessedData(DataBatch d1) {
		//System.out.println("cpu set processed in cluster");
		synchronized (d1) {
			boolean ok = false;
			while (!ok)
				ok = myData.get(d1.getMyGpu()).add(d1);
		}
	}
	public void SetProcessedData3(DataBatch d1) {
		boolean ok = false;
		ok = d1.getMyGpu().addToVRAM(d1);
		if(!ok) myData.get(d1.getMyGpu()).add(d1);

	}

	public void SetProcessedData5(DataBatch d1) {
		boolean ok = false;
		boolean addMyData = false;
		GPU myGpu = d1.getMyGpu();
		synchronized (myGpu) {
			//first add to db
			while ((!addMyData)) {
				addMyData = myData.get(myGpu).add(d1);
			}
			//second check if can added to vram and do it while can
			do {
				DataBatch d2 = myData.get(myGpu).peek();
				ok = d1.getMyGpu().addToVRAM(d2);
				if (ok) myData.get(myGpu).pop();
			} while (ok && myData.get(myGpu).size() != 0);

		}
	}

	public void sendToGpu(GPU gpu) {
		synchronized (gpu) {
			if (!myData.get(gpu).isEmpty()) {
				DataBatch d = myData.get(gpu).peek();
				boolean add = gpu.addToVRAM(d);
				if (add)
					myData.get(gpu).poll();
				//System.out.println("gpu take back");
			}
		}
	}
	public Vector<CPU> getCPUs(){
		return CPUs;
	}

	public Vector<GPU> getGPUs(){
		return GPUs;
	}

	public void setCPUs(CPU cpu) {
		CPUs.addElement(cpu);
	}
	public void setGPUs(GPU gpu) {
		GPUs.addElement(gpu);
		myData.put(gpu, new LinkedBlockingDeque<DataBatch>());
	}

	public String toString() {
		int myGPUs_timeUsed = 0;
		int myCPUs_timeUsed = 0;
		int process_CPUs = 0;
		String returnString = "GPU time used: \n";
		for(GPU gpu: GPUs) {
			returnString += gpu.toString();
			myGPUs_timeUsed += gpu.getUseTime();
		}
		returnString += "GPUs Time Used: " + myGPUs_timeUsed + "\n";
		returnString += "CPU time used: \n";
		for(CPU cpu: CPUs) {
			returnString += cpu.toString();
			myCPUs_timeUsed += cpu.getUseTime();
			process_CPUs += cpu.getProcessData();
		}
		returnString += "CPUs Time Used: " + myCPUs_timeUsed + "\n";
		returnString += "CPUs Process Data: " + process_CPUs + "\n";
		return returnString;
	}
}
