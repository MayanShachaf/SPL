package bgu.spl.mics.application.messages;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.Event;

public class TrainModelEvent implements Event<Model> {
    private Model m;
    public TrainModelEvent(Model model){
        m = model;
    }

    public Model getM() {
        return m;
    }

}
