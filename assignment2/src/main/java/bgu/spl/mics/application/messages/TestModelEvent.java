package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Message;
import bgu.spl.mics.application.objects.Model;

public class TestModelEvent implements Event {
    private Model m;

    public TestModelEvent(Model model){
        m = model;
    }

    public Model getM() {
        return m;
    }
}
