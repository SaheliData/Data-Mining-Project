package leak;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Ethan on 12/6/16.
 */
public class FFMwithLeak {


    public void combine(String leakPath, String ffmResultPath, String outputPath) throws IOException{


        File outputFile = new File(outputPath);
        if(outputFile.exists()){
            outputFile.delete();
        }
        outputFile.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));


        FileInputStream ffmInputStream = null;
        FileInputStream leakInputStream = null;
        Scanner ffmSC = null;
        Scanner leakSC = null;
        int count = 0;
        try {
            ffmInputStream = new FileInputStream(ffmResultPath);
            leakInputStream = new FileInputStream(leakPath);
            ffmSC = new Scanner(ffmInputStream);
            leakSC = new Scanner(leakInputStream);

            while (ffmSC.hasNextLine()) {
                count++;

                String line = ffmSC.nextLine();
                String[] split = line.split(",");

                String leak = leakSC.nextLine();
                boolean isLeak = Boolean.parseBoolean(leak.split(",")[2]);

                //if the record is leaked, just make the score 1
                if(isLeak){
                    int score = 1;
                    String outputLine = split[0]+","+split[1]+","+score;
                    out.write(outputLine+"\n");
                }else{
                    out.write(line + "\n");
                }

                if (count % 1000000 == 0)
                    System.out.println(count + ": " + split[0]);

            }

            if (ffmSC.ioException() != null || leakSC.ioException()!=null) {
                throw ffmSC.ioException();
            }
        } finally {
            if (ffmInputStream != null) {
                ffmInputStream.close();
            }
            if(leakInputStream!=null){
                leakInputStream.close();
            }
            if (ffmSC != null) {
                ffmSC.close();
            }
            if(leakSC!=null){
                leakSC.close();
            }
            out.flush();
            out.close();

        }


    }



    public static void main(String[] args) throws  IOException{
        FFMwithLeak fleak = new FFMwithLeak();

        //test in local
        fleak.combine("/Users/Ethan/Downloads/outbraindata/leak.csv",
                "/Users/Ethan/Downloads/outbraindata/test_topic_cat_adver_camp_ffm.predict_combined.csv",
                "/Users/Ethan/Downloads/outbraindata/leak_add_to_test_topic_cat_adver_camp_ffm_predict_combined.csv");



        if( args.length != 3){
            System.out.println("usage:  leakPath,  ffmResultPath, outputPath");
            System.exit(-1);
        }else {

            String leakPath = args[0];
            String ffmResultPath = args[1];
            String outputPath = args[2];

            fleak.combine( leakPath,  ffmResultPath, outputPath);

        }

    }

}
