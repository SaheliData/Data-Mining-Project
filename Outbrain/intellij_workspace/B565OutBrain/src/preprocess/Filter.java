package preprocess;

/**
 * Created by Ethan on 12/5/16.
 */

import java.io.*;
import java.util.*;

/**
 * page_views.csv data is to big. But some information is not userful.
 * Use uuid and document_id in events.csv to filter the page_views.csv
 * for later use.
 *
 * dec 6: maybe have misunderstanding.
 * Check Leak class
 */
public class Filter {


    public void loadEvents(String path, Set<String> uidDocSet)throws IOException {
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
                String[] split=line.split(",");

                String uuid = split[1];
                String docId = split[2];
                String user_doc = uuid+"_"+docId;

                if(!uidDocSet.contains(user_doc)){
                    uidDocSet.add(user_doc);
                }

                if (count % 1000000 == 0)
                    System.out.println(count+": load event: docId=" +uuid + "; docId=" + docId);
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
            System.out.println("events loaded");
        }
    }


    public void process(String eventPath, String pageViewPath, String outputPath) throws  IOException{
        Set<String> uidDocSet = new  HashSet<String> ();

        loadEvents(eventPath, uidDocSet);


        File outputFile = new File(outputPath);
        if(outputFile.exists()){
            outputFile.delete();
        }
        outputFile.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));


        FileInputStream inputStream = null;
        Scanner sc = null;
        int count = 0;
        try {
            inputStream = new FileInputStream(pageViewPath);
            sc = new Scanner(inputStream);
            String title = sc.nextLine();//skip the title
            System.out.println(title);
            out.write(title + "\n");

            while (sc.hasNextLine()) {
                count++;

                String line = sc.nextLine();
                String[] split = line.split(",");

                String uuid  =  split[0];
                String doc_id = split[1];
                String user_doc = uuid+"_"+doc_id;
                if(!uidDocSet.contains(user_doc)){
                    continue;
                }

                out.write(line + "\n");

                if (count % 1000000 == 0)
                    System.out.println(count + ": " + uuid+": "+doc_id);

            }

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
            out.flush();
            out.close();
        }

    }



    public static void main(String args[]) throws IOException{


        String eventPath="";
        String pageViewPath="";
        String outputPath="";
        if(args.length !=3) {
           //  eventPath = "/Users/Ethan/Downloads/outbraindata/events.csv";
           //  pageViewPath = "/Users/Ethan/Downloads/outbraindata/page_views.csv";
           //  outputPath = "/Users/Ethan/Downloads/outbraindata/page_views_filtered_uuid_doc_in_event.csv";
            System.out.println("usage: eventPath, pageViewPath, outputPath");
            System.exit(-1);
        }else{
            eventPath = args[0];
            pageViewPath = args[1];
            outputPath = args[2];
        }

        Filter filter = new Filter();
        filter.process(eventPath, pageViewPath, outputPath);
    }

}
