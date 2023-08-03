package bgu.spl.mics.application;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static java.lang.Integer.parseInt;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */

// check how to initilaize conference, init the services
public class CRMSRunner {

    public static void main(String[] args) throws IOException, ParseException {
        // parsing file "JSONExample.json"
        Object obj = new JSONParser().parse(new FileReader(args[0]));

        // typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;
        List<Student> studentsList = new ArrayList<>();;
        List<String> gpus= (List<String>) jo.get("GPUS");
        List cpus= (List) jo.get("CPUS");
        //List<Integer> cpus= (List<Integer>) jo.get("CPUS");
        List<ConfrenceInformation> confrenceInformations = new ArrayList<>();
        List<CPUService> cpuServiceList = new ArrayList<>();
        List<GPUService> gpuServiceList = new ArrayList<>();
        List<StudentService> studentServiceList = new ArrayList<>();
        List<ConferenceService> conferenceServiceList = new ArrayList<>();
        String student_name ="";
        String student_department = "";
        String model_name = "";
        String Data_type = "";
        String student_status = "";
        int Data_size = 0;
        String name_conference;
        int date_conference;
        int TickTime = 0;
        int Duration = 0;
        int counter = 0;
        Iterator iter1 = null;
        Cluster cluster = Cluster.getInstance();
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        // getting Students
        for (JSONObject student1 : (List<JSONObject>) jo.get("Students")) {
            student_name= student1.get("name").toString();
            student_department = student1.get("department").toString();
            student_status = student1.get("status").toString();
            Student student = new Student(student_name, student_department, student_status);
            JSONArray models = (JSONArray) student1.get("models");
            Iterator iter2 = models.iterator();
            while (iter2.hasNext()){//GET NEXT MODEL
                iter1 = ((Map) iter2.next()).entrySet().iterator();
                while (iter1.hasNext()) {
                    Map.Entry pair = (Map.Entry) iter1.next();
                    if(pair.getKey().toString().equals("name"))
                        model_name = pair.getValue().toString();
                    else if(pair.getKey().toString().equals("type"))
                        Data_type = pair.getValue().toString();
                    else if(pair.getKey().toString().equals("size"))
                        Data_size = parseInt( pair.getValue().toString());
                }
                Data data1 = new Data(Data_type, Data_size);
                Model model = new Model(model_name, data1, student);
                student.setModels(model);
                student.setModelsOutput(model);
            }
            // number of zazu : 100000000
            studentsList.add(student);
            StudentService studentService = new StudentService("StudentService"+counter , student);
            counter++;
            studentServiceList.add(studentService);
        }
        // add gpus to the cluster
        counter = 0;
        for(String g: gpus){
            GPU g1 = new GPU(g);
            cluster.setGPUs(g1);
            GPUService gpuService = new GPUService("GPUService"+counter , g1);
            counter++;
            gpuServiceList.add(gpuService);
        }
        // add cpus to the cluster
        counter = 0;
        for (Object c : cpus) {
            CPU c1 = new CPU(parseInt(c.toString()));
            cluster.setCPUs(c1);
            CPUService cpuService = new CPUService("CPUService"+counter , c1);
            counter++;
            cpuServiceList.add(cpuService);
        }
        JSONArray conferences = (JSONArray) jo.get("Conferences");
        Iterator<JSONObject> iterator = conferences.iterator();
        counter = 0;
        while (iterator.hasNext()) {
            JSONObject conference1 = iterator.next();
            name_conference = conference1.get("name").toString();
            date_conference = parseInt(conference1.get("date").toString());
            ConfrenceInformation confrenceInformation = new ConfrenceInformation(name_conference, date_conference);
            confrenceInformations.add(confrenceInformation);
            ConferenceService conferenceService1 = new ConferenceService("ConferenceService" + counter, confrenceInformation);
            conferenceServiceList.add(conferenceService1);
        }
            /*
            for (JSONObject conference1 : (List<JSONObject>) jo.get("Conferences"))
            name_conference = (String) conference1.get(0)
        date_conference = (int)conference1.get(1);
            confrenceInformations.add(new ConfrenceInformation(name_conference, date_conference));
        }
        */
        TickTime = parseInt(jo.get("TickTime").toString());
        Duration = parseInt(jo.get("Duration").toString());
        TimeService timeService = new TimeService(TickTime, Duration);
        for(GPUService gpuService: gpuServiceList) {
            Thread threadGPUService = new Thread(gpuService);
            threadGPUService.start();
        }
        //Thread threadGPUService1 = new Thread(gpuServiceList.get(0));
        //threadGPUService1.start();
//        Thread threadGPUService = new Thread(gpuServiceList.get(0));
//        threadGPUService.start();
        /*Thread threadCPUService1 = new Thread(cpuServiceList.get(0));
        threadCPUService1.start();
        Thread threadCPUService2 = new Thread(cpuServiceList.get(1));
        threadCPUService2.start();*/
        for(CPUService cpuService: cpuServiceList) {
            Thread threadCPUService = new Thread(cpuService);
            threadCPUService.start();

        }
        for(ConferenceService conferenceService: conferenceServiceList) {
            Thread threadConferenceService = new Thread(conferenceService);
            threadConferenceService.start();
        }

        for(StudentService studentService: studentServiceList) {
            Thread threadStudentService = new Thread(studentService);
            threadStudentService.start();
        }
        Thread threadTimeService = new Thread(timeService);
        threadTimeService.start();
        try {
           // System.out.println("start joined timeservice");

            threadTimeService.join();
           // System.out.println("Joined timeservice");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       // System.out.println("Starting create output file");

        // Output file
        String needToParse ="";
        for(StudentService studentService: studentServiceList)
            needToParse += studentService.getStudent().toString();
        needToParse += cluster.toString();
        for(ConferenceService conference: conferenceServiceList)
            needToParse += conference.getMyConferenceInf().toString();
        File output = new File(args[1]);
        try {
            output.createNewFile();
            FileWriter myWrite = new FileWriter(output);
            myWrite.write(needToParse);
            myWrite.close();
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }
}
