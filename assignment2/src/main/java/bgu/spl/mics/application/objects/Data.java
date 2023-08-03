package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {

    /**
     * Enum representing the Data type.
     */
    public enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;
    private int size;

    public Data(String type1, int size1){
        setType(type1);
        processed = 0;
        size = size1;
    }

    public Type getType(){
        return type;
    }

    public int getProcessed() {
        return processed;
    }

    public int getSize() {
        return size;
    }

    public void setProcessed() {
        processed++;
    }

    public void setType(String type1) {
        if(type1.equals("images") || type1.equals("Images"))
            type = Type.Images;
        else if(type1.equals("text") || type1.equals("Text"))
            type = Type.Text;
        else if(type1.equals("tabular") || type1.equals("Tabular"))
            type = Type.Tabular;
    }

}
