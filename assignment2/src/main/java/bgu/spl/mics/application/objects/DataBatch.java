package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private Data data;
    private int start_index;
    private GPU myGpu;

    public DataBatch(Data d1, int start_idx, GPU g){
        data = d1;
        start_index = start_idx;
        myGpu = g;
    }
    public GPU getMyGpu(){
       return myGpu;
    }
    public Data getData() {
        return data;
    }

    public int getStart_index() {
        return start_index;
    }
}
