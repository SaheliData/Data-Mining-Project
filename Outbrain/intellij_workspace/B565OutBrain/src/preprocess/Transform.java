package preprocess;

/**
 * Created by Ethan on 11/20/16.
 */

import java.io.*;
import java.util.Scanner;

/**
 * transform original data to specific format used by libFFM
 */
public class Transform {


    public void transform(int fieldID, String inputPath, String outputPath, String title)throws IOException{


        FileInputStream inputStream = null;
        Scanner sc = null;
        File outputFile = new File(outputPath);
        if(outputFile.exists()){
            outputFile.delete();
        }
        outputFile.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

        int count = 0;
            try {
                inputStream = new FileInputStream(inputPath);
                sc = new Scanner(inputStream);
                sc.nextLine();//skip the title

                String lastDocId = "";
                String lastRecord = title;
                String currentRecord ="";
                while (sc.hasNextLine()) {
                    count++;
                   // if(count > 3) break;
                    String line = sc.nextLine();
                    String[] split = line.split(",");
                    String docId = split[0];

                    if(!docId.equals(lastDocId)){
                        currentRecord = docId+"\t"+fieldID+":" + split[1]+":"+split[2];
                        out.write(lastRecord+"\n");
                        lastDocId = docId;
                        lastRecord = currentRecord;
                    }else{
                        currentRecord += " "+fieldID+":" + split[1]+":"+split[2];
                        lastRecord = currentRecord;
                    }

                    if (count % 100000 == 0)
                        System.out.println(count + ": " + docId);
                }
                out.write(lastRecord+"\n");

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
                out.flush();
                out.close();
            }
    }

    public void transform(int field)throws  IOException{
        if(field == 0) {
            String title = "document_id\t(field:topic_id:confidence_level)";
            transform(field, "/Users/Ethan/Downloads/outbraindata/documents_topics.csv",
                "/Users/Ethan/Downloads/outbraindata/documents_topics_ffm.csv",
                title);

        }
        else if(field == 1) {
            String title = "document_id\t(field:cat_id:confidence_level)";
            transform(field, "/Users/Ethan/Downloads/outbraindata/documents_categories_compressed.csv",
                    "/Users/Ethan/Downloads/outbraindata/documents_categories_ffm.csv",
                    title);
        }
    }
    public static void main(String[] args)throws IOException{
        Transform tran = new Transform();
        tran.transform(0);//FOR doc_topics
        tran.transform(1);//For doc_cat
    }
}
