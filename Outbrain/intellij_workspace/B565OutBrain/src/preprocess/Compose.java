package preprocess;

/**
 * Created by Ethan on 11/20/16.
 */

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

/**
 * compose the final data for FFM
 * add advertiser_id
 */
public class Compose {

    public static final int ADVERTISER_ID_BASE = 398; //min of adver_id is 2. plus 398, to be 400
    public static final int CAMP_ID_BASE = 4799;
    public static final int TYPE_COMMON = 0;
    public static final int TYPE_ADVER = 1;
    public static final int TYPE_COMP = 3;
    public static final int ADVERTISER_FIELD_ID = 2;
    public static final int CAMP_FIELD_ID = 3;




    public void loadSingleFFMdata(String path, Map<Integer, String> hm, int type)throws IOException{
        FileInputStream inputStream = null;
        Scanner sc = null;
        int count = 0;
            try {
                inputStream = new FileInputStream(path);
                sc = new Scanner(inputStream);
                sc.nextLine();//skip the title
                while (sc.hasNextLine()) {
                    count++;

                    String line = sc.nextLine();
                    String[] split=null;
                    if(type == TYPE_COMMON) {
                        split=line.split("\t");
                    }else if (type == TYPE_ADVER || type == TYPE_COMP){
                        split=line.split(",");
                    }

                    int key = Integer.parseInt(split[0]);
                    if(type == TYPE_COMMON) {
                        String val = split[1];
                        hm.put(key, val);
                    }else if (type == TYPE_ADVER){
                        int val = Integer.parseInt(split[3]);
                        val += ADVERTISER_ID_BASE;
                        String valStr = ADVERTISER_FIELD_ID +":"+val +":1";
                        hm.put(key, valStr);
                    }else if(type == TYPE_COMP){
                        int val = Integer.parseInt(split[2]);
                        val += CAMP_ID_BASE;
                        String valStr = CAMP_FIELD_ID +":"+val +":1";
                        hm.put(key, valStr);
                    }

                    if (count % 1000000 == 0)
                        System.out.println("load ffm:" +count + ": " + key);
                }

                if (sc.ioException() != null) {
                    throw sc.ioException();
                }
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (sc != null) {
                    sc.close();
                }
            }
    }

    public void compose(String data, String eventsData, String outputPath, Boolean train)throws  IOException{
        Map<Integer, String> topicsHM = new HashMap<>();
        Map<Integer, String> catHM = new HashMap<>();
        Map<Integer, String> adverHM = new HashMap<>();
        Map<Integer, String> campaignHM = new HashMap<>();
        loadSingleFFMdata("/Users/Ethan/Downloads/outbraindata/documents_topics_ffm.csv",topicsHM, TYPE_COMMON);
        loadSingleFFMdata("/Users/Ethan/Downloads/outbraindata/documents_categories_ffm.csv", catHM, TYPE_COMMON);
        loadSingleFFMdata("/Users/Ethan/Downloads/outbraindata/promoted_content.csv", adverHM, TYPE_ADVER);
        loadSingleFFMdata("/Users/Ethan/Downloads/outbraindata/promoted_content.csv", campaignHM, TYPE_COMP);


        HashSet<Integer> adSet = new HashSet<>();

        File outputFile = new File(outputPath);
        if(outputFile.exists()){
            outputFile.delete();
        }
        outputFile.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));


        FileInputStream inputStream = null;
        FileInputStream eventInputStream = null;
        Scanner sc = null;
        Scanner eventSC = null;
        int count = 0;
        try {
            inputStream = new FileInputStream(data);
            eventInputStream = new FileInputStream(eventsData);
            sc = new Scanner(inputStream);
            eventSC = new Scanner(eventInputStream);
            String title = sc.nextLine();//skip the title
            System.out.println(title);
            eventSC.nextLine();
            int document_id = 0;
            String catStr = "";
            String topicStr = "";
            String adverStr ="";
            String compaignStr = "";
            int last_display_id = -1;
            while (sc.hasNextLine()) {
                count++;

                String line = sc.nextLine();
                String[] split = line.split(",");

                int display_id = Integer.parseInt(split[0]);
                int ad_id = Integer.parseInt(split[1]);

                adverStr = adverHM.get(ad_id);
                compaignStr = campaignHM.get(ad_id);
                if(last_display_id != display_id){
                    while(eventSC.hasNext()){
                        String eventLine = eventSC.nextLine();
                        String[] eventSplit = eventLine.split(",");
                        int event_display_id = Integer.parseInt(eventSplit[0]);
                        if(event_display_id == display_id){
                            document_id =  Integer.parseInt(eventSplit[2]);
                            break;
                        }
                    }
                    catStr = catHM.get(document_id);
                    topicStr = topicsHM.get(document_id);
                    if(topicStr == null) topicStr="";
                    if(catStr == null) catStr="";

                    last_display_id = display_id;
                }

               // if(!catStr.equals("") && !topicStr.equals("")) {
                String outputLine;
                if(train) {
                    outputLine = split[2] + " " + topicStr + " " + catStr + " " + adverStr + " " + compaignStr;
                    out.write(outputLine + "\n");
                }
                else{//test
                       // if(!adSet.contains(ad_id)) {// no duplicate. currently only info about ads. so much redundant
                       //     adSet.add(ad_id);
                            outputLine = topicStr + " " + catStr + " " + adverStr + " " + compaignStr;
                            out.write(outputLine + "\n");
                      //  }
                     //   else{

                     //   }
                    }
//                }

                if (count % 1000000 == 0)
                    System.out.println(count + ": " + display_id);

            }

            if (sc.ioException() != null || eventSC.ioException()!=null) {
                throw sc.ioException();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if(eventInputStream!=null){
                eventInputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
            if(eventSC!=null){
                eventSC.close();
            }
            out.flush();
            out.close();

        }

    }


    public void compose(int mode)throws  IOException{

        if(mode >= 0)
        compose(
                "/Users/Ethan/Downloads/outbraindata/clicks_train.csv",
                "/Users/Ethan/Downloads/outbraindata/events.csv",
                "/Users/Ethan/Downloads/outbraindata/train_topic_cat_adver_camp_ffm.csv",
                true
        );

        if(mode <= 0)
        compose(
                "/Users/Ethan/Downloads/outbraindata/clicks_test.csv",
                "/Users/Ethan/Downloads/outbraindata/events.csv",
                "/Users/Ethan/Downloads/outbraindata/test_topic_cat_adver_camp_ffm.csv",
                false
        );

    }

    public static void main(String[] args)throws  IOException{
        Compose com = new Compose();
        com.compose(-1);
    }
}
