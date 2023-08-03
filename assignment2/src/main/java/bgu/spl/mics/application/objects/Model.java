package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    private String name;
    private Data data;
    private Student student;
    private Status status;
    private Results results;

    public enum Status {
        PreTrained, Training, Trained, Tested
    }

    public enum Results {
        None, Good, Bad
    }

    public Model(String name1, Data data1, Student student1){
        name = name1;
        data = data1;
        student = student1;
        status = Status.PreTrained;
        results = Results.None;
    }

    public String getName() {
        return name;
    }

    public Data getData() {
        return data;
    }

    public Student getStudent() {
        return student;
    }

    public Status getStatus() {
        return status;
    }
    public String getStatusString() {
        if(status.equals(Status.Training))
            return "Training";
        else if(status.equals(Status.Trained))
            return "Trained";
        else if(status.equals(Status.Tested))
            return "Tested";
   return  null;
    }
    public Results getResults() {
        return results;
    }

    public void setStatus(String status1){
        if(status1.equals("Training") )
            status = Model.Status.Training;
        else if(status1.equals("Trained"))
            status = Status.Trained;
        else if(status1.equals("Tested"))
            status = Status.Tested;
    }
    public void setResults(String r){
        if(r.equals("Good") )
            results = Results.Good;
        else if(r.equals("Bad"))
            results = Results.Bad;
        else if(r.equals("None"))
            results = Results.None;
    }

    public String toString() {
        String myString = "Name of model: " + name + ", Status of Model: " + status.toString() + ", result: ";
        if(results.equals(Results.Good))
            myString += "Good ";
        else if(results.equals(Results.Bad))
            myString += "Bad ";
        else
            myString += "Not done ";
        return myString;
    }

}
