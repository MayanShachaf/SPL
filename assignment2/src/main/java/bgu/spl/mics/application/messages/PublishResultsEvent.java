package bgu.spl.mics.application.messages;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.Event;

public class PublishResultsEvent implements Event<Model> {

    private Model m;

    public PublishResultsEvent(Model model) {
        this.m = model;
    }

    public Model getM() {
        return m;
    }
}