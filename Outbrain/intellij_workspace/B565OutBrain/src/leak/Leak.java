package leak;

/**
 * Created by Ethan on 12/6/16.
 */


/**
 *
 * Should be?: the document_id  in promoted_content is the document the ad_id landing on.
 * So use page_views to check if the particular documents were visited by particular user, then the user clicked that ad
 */

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map;

/**
 * Compose the data from events.csv, promoted_content.csv, click_test.csv
 */

public class Leak {

    public void loadLeakObj(String expandedClickData, Map<String, ArrayList<Integer>> hm, ArrayList<LeakObj> lkolist) throws IOException{
        FileInputStream inputStream = null;
        Scanner sc = null;

        try {
            inputStream = new FileInputStream(expandedClickData);
            sc = new Scanner(inputStream);
            sc.nextLine();//skip the title
            int index = 0;
            while (sc.hasNextLine()) {


                String line = sc.nextLine();
                String[]  split=line.split(",");
                //display_id,ad_id.uuid,promote_document_id,timestamp

                LeakObj lko = new LeakObj();
                lko.display_id = Integer.parseInt(split[0]);
                lko.ad_id =   Integer.parseInt(split[1]);
                lko.uuid =    split[2];
                lko.promoted_document_id =  Integer.parseInt(split[3]);
                lko.timestamp = Long.parseLong(split[4]);
                /*try{
                    lko.platform = split[5];
                }catch(Exception e){
                    e.printStackTrace();
                }
                try {
                    lko.geo_location = split[6];
                }catch(Exception e){
                    e.printStackTrace();
                }*/
                lkolist.add(lko);

                String key = lko.uuid+"_"+lko.promoted_document_id;

                if(! hm.containsKey(key)) {
                    ArrayList<Integer> indexlist= new ArrayList<>();
                    indexlist.add(index);
                    hm.put(key, indexlist);
                }else{
                    hm.get(key).add(index);
                }
                index ++;

                if (index % 1000000 == 0)
                    System.out.println("load :" +index  );
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



    public void composeLeak(String pageViewPath, String expandedClickTestDataPath, String outputPath) throws IOException{
        ArrayList<LeakObj> lkolist = new ArrayList<>();
        Map<String, ArrayList<Integer>> hm = new HashMap<>();

        loadLeakObj(expandedClickTestDataPath, hm, lkolist);



        FileInputStream inputStream = null;
        Scanner sc = null;
        int count = 0;
        try {
            inputStream = new FileInputStream(pageViewPath);
            sc = new Scanner(inputStream);
            String title = sc.nextLine();//skip the title
            System.out.println(title);
            int numOfTrue=0;
            ArrayList<Integer> toRemoveIndex = new ArrayList<>();

            while (sc.hasNextLine()) {
                count++;

                String line = sc.nextLine();
                String[] split = line.split(",");

                String uuid  =  split[0];
                String doc_id = split[1];
                long timestamp = Long.parseLong(split[2]);

                /*String platform ="";
                try{
                    platform= split[3];
                }catch(Exception e){
                    e.printStackTrace();
                }

                String geo_location="";
                try {
                    geo_location = split[4];
                }catch(Exception e){
                    e.printStackTrace();
                }*/
                int traffic_source = 0;
                try {
                    traffic_source = Integer.parseInt(split[5]);
                }catch(Exception e){
                    e.printStackTrace();
                }

                String user_doc = uuid+"_"+doc_id;

                if(!hm.containsKey(user_doc)){
                    continue;
                }else{
                    ArrayList<Integer> indexlist = hm.get(user_doc);
                    toRemoveIndex.clear();

                    for(int i=0; i<indexlist.size(); i++){
                        int index = indexlist.get(i);
                        //because page_view recorded users' visiting pages.
                        //if the user visited this doc (landing page of the ad), maybe he clicked the ad
                        //here, can add more fine control. like in a certain time range. Tried with 10 mins, worse
                        // add other restriction: platform, location,traffic_source
                        //tried with traffic_source==1, zero improvement
                        if( (timestamp - lkolist.get(index).timestamp > 0)
                          //      && lkolist.get(index).platform.equals(platform)
                          //      && lkolist.get(index).geo_location.equals(geo_location)
                                && (traffic_source == 1)){
                            lkolist.get(index).clicked = true;
                            toRemoveIndex.add(index);
                            //System.out.println(lkolist.get(index).toString());
                            numOfTrue++;
                        }
                    }
                    indexlist.removeAll(toRemoveIndex);
                }

                if (count % 1000000 == 0)
                    System.out.println(count + ": " + uuid+": "+doc_id);

            }

            System.out.println("number of TRUE:"+numOfTrue);
            if (sc.ioException() != null ) {
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
        writeLeak(lkolist, outputPath);

    }

    public void writeLeak(ArrayList<LeakObj> lkolist, String outputPath) throws  IOException{


        File outputFile = new File(outputPath);
        if(outputFile.exists()){
            outputFile.delete();
        }
        outputFile.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));


        int count = 0;
        try {
            for(int i=0; i<lkolist.size(); i++){
                count ++;

                LeakObj lko = lkolist.get(i);

                out.write(lko.display_id+","+lko.ad_id+","+lko.clicked+ "\n");

                if (count % 1000000 == 0)
                    System.out.println(count + ": " +lko.display_id);

            }

        } finally {
            out.flush();
            out.close();
        }

    }


    public static void main(String[] args) throws  IOException{


        if( args.length != 3){
            System.out.println("usage: pageViewPath, expandedClickTestDataPath, outputPath");
        }else {
            String pageViewPath = args[0];
            String expandedClickTestDataPath = args[1];
            String outputPath = args[2];

            Leak leak = new Leak();
            leak.composeLeak(pageViewPath, expandedClickTestDataPath, outputPath);
        }

    }

}
