package preprocess;

import java.io.*;
import java.util.Scanner;

/**
 * Created by Ethan on 11/20/16.
 */
public class Submission {


    public void combine(String clicks_test, String test_predict, String submission_output) throws IOException{

        FileInputStream inputStream = null;
        Scanner sc = null;
        FileInputStream predictStream = null;
        Scanner predictSC = null;


        File submissionFile = null;
        BufferedWriter submissionOut= null;

        submissionFile = new File(submission_output);
        if (submissionFile.exists()) {
            submissionFile.delete();
        }
        submissionFile.createNewFile();
        submissionOut = new BufferedWriter(new FileWriter(submissionFile));

        int count = 0;
        try {
            inputStream = new FileInputStream(clicks_test);
            sc = new Scanner(inputStream);

            predictStream = new FileInputStream(test_predict);
            predictSC = new Scanner(predictStream);

            sc.nextLine();//skip title
            int last_display_id = -1;

            while (sc.hasNext()){

                String click_test_line = sc.nextLine();
                String predict_line = predictSC.nextLine();
                String newLine = click_test_line+","+predict_line;
                submissionOut.write(newLine+"\n");
                if(count %1000000 == 0){
                    System.out.println(count+": "+click_test_line+": "+predict_line);
                }
                count++;
            }

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                inputStream.close();
            }

            if(predictStream!=null){
                predictStream.close();
            }

            if(sc!=null)sc.close();
            if(predictSC!=null)predictSC.close();
            if(submissionOut!=null)submissionOut.close();
        }


    }

    public static void main(String[] args)throws IOException{
        Submission sub = new Submission();
        sub.combine(
                "/Users/Ethan/Downloads/outbraindata/clicks_test.csv",
                "/Users/Ethan/Downloads/outbraindata/test_topic_cat_adver_camp_ffm.predict_output.csv",
                "/Users/Ethan/Downloads/outbraindata/test_topic_cat_adver_camp_ffm.predict_combined.csv"
        );
    }

}
