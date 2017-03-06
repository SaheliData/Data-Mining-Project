package preprocess;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Created by Ethan on 11/20/16.
 */
public class Extract {


    public void extract(String clicks_test) throws IOException{
        HashSet<Integer> adSet = new HashSet<>();
        FileInputStream inputStream = null;
        Scanner sc = null;
        int count = 0;

        String adIdOutputPath = "/Users/Ethan/Downloads/outbraindata/ad_id_from_click_test.txt";
        File adIdFile = null;
        BufferedWriter adIdout= null;

            adIdFile = new File(adIdOutputPath);
            if (adIdFile.exists()) {
                adIdFile.delete();
            }
            adIdFile.createNewFile();
            adIdout = new BufferedWriter(new FileWriter(adIdFile));

        try {
            inputStream = new FileInputStream(clicks_test);
            sc = new Scanner(inputStream);
            String title = sc.nextLine();//skip the title
            System.out.println(title);

            while (sc.hasNextLine()) {
                count++;

                String line = sc.nextLine();
                String[] split = line.split(",");


                int ad_id = Integer.parseInt(split[1]);
                if(!adSet.contains(ad_id)) {// no duplicate. currently only info about ads. so much redundant
                    adSet.add(ad_id);
                    adIdout.write(ad_id+"\n");
                }
                else{}

                if (count % 1000000 == 0)
                    System.out.println(count + ": " + ad_id);

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

            if(adIdout!=null) {
                adIdout.flush();
                adIdout.close();
            }
        }

    }
    public static void main(String[] args) throws IOException{
        Extract ex = new Extract();
        ex.extract("/Users/Ethan/Downloads/outbraindata/clicks_test.csv");
    }
}
