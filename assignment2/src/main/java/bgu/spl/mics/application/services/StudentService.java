package bgu.spl.mics.application.services;
import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.objects.Model.*;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private Student student;
    private Deque<Model> modelsToTest;
    private Deque<Model> modelsToPublish;
    boolean start_process;
    boolean start_test;
    public StudentService(String name, Student s1) {
        super(name);
        student = s1;
        MessageBusImpl.getInstance().register(this);
        start_process = false;
        start_test =false;
    }
    private void testAndPublish() {
       if(!start_process  &&!student.getModels().isEmpty() && student.getTest().isEmpty() && student.getPublish().isEmpty()){
            Model m = student.getModels().pop();
            start_process = true;
            TrainModelEvent e = new TrainModelEvent(m);
            //student.setTest(m);
            sendEvent(e);
        }
       else if(start_process && !student.getTest().isEmpty() && student.getPublish().isEmpty()){
           Model m =student.getTestM();
           if (m.getStatus().equals(Status.Trained)) {
               //student.setPublish(m);
               TestModelEvent e = new TestModelEvent(m);
               sendEvent(e);
               student.removeTest(m);
               start_test = true;
           }
       }
       else  if(start_test &&student.getTest().isEmpty() && !student.getPublish().isEmpty()) {
           Model m = student.getPublishM();
           if (m.getStatus().equals(Status.Tested)) {
               if (m.getResults().equals(Results.Good)) {
                   PublishResultsEvent e = new PublishResultsEvent(m);
                   sendEvent(e);
                   student.removePublish(m);
               }
               if (m.getResults().equals(Results.Bad)) {
                   student.removePublish(m);
               }
               start_process = false;
               start_test = false;
           }

       }

    }


    public Student getStudent() {
        return student;
    }

    @Override
    protected void initialize(){

        // sign up for the conference publication broadcasts
        subscribeBroadcast(PublishConferenceBroadcast.class, broadcast -> {
            for (Model m : broadcast.getModels()) {
              //  System.out.println("found publish conference broadcast");
                if (m.getStudent().getName() != student.getName())
                    student.setPapersRead();
                else {
                    student.setPublications();
                    student.setModelPublish(m);
                }
            }

        });

        subscribeBroadcast(TickBroadcast.class, event -> {
            testAndPublish();
        });
        subscribeBroadcast(TerminateBroadcast.class,callback->{
            terminate();
        });
       //while (!student.getModels().isEmpty()) {
          //  Model m1 = student.getModels().pop();

          //  System.out.println("stu sent train event");

    }
}


