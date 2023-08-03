package bgu.spl.mics.application.messages;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.ArrayList;

public class PublishConferenceBroadcast implements Broadcast{
    private ArrayList<Model> arrayList;
    private String senderName;
    public PublishConferenceBroadcast(String senderName1, ArrayList<Model> modelsNames) {
        this.senderName = senderName1;
        arrayList = modelsNames;

    }
    public String getSenderName() {
        return senderName;
    }
    public  ArrayList<Model> getModels(){
        return  arrayList;
    }
}
