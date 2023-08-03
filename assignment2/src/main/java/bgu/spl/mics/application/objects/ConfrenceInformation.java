package bgu.spl.mics.application.objects;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    private ArrayList<Model> successfulModels = new ArrayList<Model>();
    public ConfrenceInformation(String name1, int date1) {
        name = name1;
        date = date1;
    }

    public void addModel(Model model){
        successfulModels.add(model);
    }
    public ArrayList<Model> getSuccessfulModelsNames(){
        return successfulModels;
    }
    public int getDate() {
        return date;
    }
    public String toString() {
        String result = "Conference name: " + name + ", Time of publication: " + date + "\n";
        for(Model model: successfulModels)
            result += model.getName() +"\n";
        return result;
    }
}
