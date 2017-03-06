package leak;

/**
 * Created by Ethan on 12/6/16.
 */

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;


/**
 * Compose the data from events.csv, promoted_content.csv, click_test.csv
 */
public class ExpandClickTestData {


    public void loadAdToDoc(String promotedContentPath, Map<Integer, Integer> hm) throws  IOException{
        FileInputStream inputStream = null;
        Scanner sc = null;
        int count = 0;
        try {
            inputStream = new FileInputStream(promotedContentPath);
            sc = new Scanner(inputStream);
            sc.nextLine();//skip the title
            while (sc.hasNextLine()) {
                count++;

                String line = sc.nextLine();
                String[]  split=line.split(",");

                int ad_id = Integer.parseInt(split[0]);

                if(! hm.containsKey(ad_id)) {
                    int doc_id = Integer.parseInt(split[1]);
                    hm.put(ad_id, doc_id);
                }

                if (count % 1000000 == 0)
                    System.out.println("load :" +count + ": " + ad_id );
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



    public void compose(String data, String eventsData, String promotedContentPath, String outputPath)throws IOException {
        Map<Integer, Integer> adDocHm = new HashMap<>();
        loadAdToDoc(promotedContentPath, adDocHm);



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
            int promot_document_id = 0;
            String uuid="";
            long timestamp=0;
            String platform = "";
            String geo_location="";
            int last_display_id = -1;

            out.write("display_id,ad_id.uuid,promote_document_id,timestamp,platform,geo_location\n");

            while (sc.hasNextLine()) {
                count++;

                String line = sc.nextLine();
                String[] split = line.split(",");

                int display_id = Integer.parseInt(split[0]);
                int ad_id = Integer.parseInt(split[1]);
                promot_document_id = adDocHm.get(ad_id);

                if(last_display_id != display_id){
                    while(eventSC.hasNext()){
                        String eventLine = eventSC.nextLine();
                        String[] eventSplit = eventLine.split(",");
                        int event_display_id = Integer.parseInt(eventSplit[0]);
                        if(event_display_id == display_id){
                          //  System.out.println(eventLine);
                            uuid = eventSplit[1];
                            timestamp = Long.parseLong(eventSplit[3]);
                            platform = eventSplit[4];
                            try {
                                geo_location = eventSplit[5];
                            }catch(Exception e){
                                e.printStackTrace();
                                geo_location="";
                            }

                            break;
                        }
                    }

                    last_display_id = display_id;
                }

                String outputLine;
                outputLine = display_id + "," + ad_id + "," + uuid + "," + promot_document_id+","+timestamp+","+platform+","+geo_location;
                out.write(outputLine + "\n");


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



    public static void main(String[] args) throws  IOException{

        //local test

        ExpandClickTestData ectd = new ExpandClickTestData();
        ectd.compose("/Users/Ethan/Downloads/outbraindata/clicks_test.csv",
                "/Users/Ethan/Downloads/outbraindata/events.csv",
                "/Users/Ethan/Downloads/outbraindata/promoted_content.csv",
                "/Users/Ethan/Downloads/outbraindata/click_test_full_expanded.csv");


        if( args.length != 4){
            System.out.println("usage: click_test_path, eventsData,  promotedContentPath,  outputPath");
        }else {
            String data = args[0];
            String eventsData = args[1];
            String promotedContentPath = args[2];
            String outputPath = args[3];

            //ExpandClickTestData ectd = new ExpandClickTestData();
            ectd.compose(data, eventsData, promotedContentPath, outputPath);
        }

    }

}
