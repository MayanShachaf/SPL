package bgu.spl.mics.application.objects;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {

    /**
     * Enum representing the Degree the student is studying for.
     */
    enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private Stack<Model> modelStack;
    private Vector<Model> modelsOutput;
    private Vector<Model> test;
    private Vector<Model> publish;
    private Vector<Model> modelPublish;

    public Student(String name1, String department1, String degree) {
        name = name1;
        department = department1;
        setStatus(degree);
        publications = 0;
        papersRead = 0;
        modelStack = new Stack<Model>();
        modelsOutput = new Vector<Model>();
        test = new Vector<Model>();
        publish = new Vector<Model>();
        modelPublish = new Vector<Model>();
    }

    public synchronized void setPapersRead() {
        papersRead++;
    }

    public synchronized void setPublications() {
        publications++;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public Degree getStatus() {
        return status;
    }

    public int getPublications() {
        return publications;
    }

    public int getPapersRead() {
        return papersRead;
    }

    public void setModels(Model model) {
        modelStack.push(model);
    }

    public Stack<Model> getModels() {
        return modelStack;
    }

    public void setStatus(String degree) {
        if(degree.equals("PhD") || degree.equals("PHd"))
            status = Degree.PhD;
        if(degree.equals("MsC") || degree.equals("MSc"))
            status = Degree.MSc;
    }
    public void setModelsOutput(Model model){
        modelsOutput.add(model);
    }
    public void removeModelsOutput(Model model){
        modelsOutput.remove(model);
    }
    public Vector<Model> getModelsOutput(){
        return modelsOutput;
    }

    public Vector<Model> getPublish() {
        return publish;
    }

    public Vector<Model> getTest() {
        return test;
    }
    public  Model getTestM(){
        return test.firstElement();
    }
    public  Model getPublishM(){
        return publish.firstElement();
    }
    public void setTest(Model model) {
        test.add(model);
    }

    public void setPublish(Model model) {
        publish.add(model);
    }

    public void removeTest(Model model) {
        test.remove(model);
    }

    public void removePublish(Model model) {
        publish.remove(model);
    }

    public void setModelPublish(Model model) {
        modelPublish.add(model);
    }
    public String toString() {
        String myString = "Student name: " + name + ", Number of papaer read: " + papersRead + ", Status: " + status + "\n";
        for(Model model: modelsOutput){
            if(model.getStudent().getName() == name) {
                myString += model.toString();
                if (modelPublish.contains(model))
                    myString += ", Publish" + "\n";
                else
                    myString += "\n";
            }
        }
        return myString;
    }
}
