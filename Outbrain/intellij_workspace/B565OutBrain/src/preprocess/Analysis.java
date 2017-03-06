package preprocess;

/**
 * Created by Ethan on 11/20/16.
 */

import sun.reflect.annotation.ExceptionProxy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * To detect how many unique topics in documents_topics.csv; And to identify if it can be compressed.
 * similar for documents_categories.csv  documents_entities.csv
 */
public class Analysis {

    public void analyze (String[] paths) throws IOException{
        Map<Integer, Integer> hm = new HashMap<Integer, Integer>();
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        FileInputStream inputStream = null;
        Scanner sc = null;
        int count = 0;
        for(String path : paths) {
            try {

                inputStream = new FileInputStream(path);
                sc = new Scanner(inputStream);
                sc.nextLine();//skip the title
                while (sc.hasNextLine()) {
                    count++;

                    String line = sc.nextLine();
                    String[] split = line.split(",");

                    int key=0;
                    try{
                        key =Integer.parseInt(split[2]);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        continue;
                    }
                    if (count % 100000 == 0)
                        System.out.println(count + ": " + key);
                    if (hm.containsKey(key)) {
                        hm.put(key, hm.get(key) + 1);
                    } else {
                        hm.put(key, 1);
                        if (key > max) {
                            max = key;
                        }
                        if (key < min) {
                            min = key;
                        }
                    }
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


        System.out.println("total number: "+ hm.size());
        System.out.println("min : "+ min);
        System.out.println("max : "+ max );

    }


    public static void main(String[] args) throws  IOException{
        Analysis ana = new Analysis();
        String doc_cat_path = "/Users/Ethan/Downloads/outbraindata/documents_categories.csv";
        String doc_topic_path = "/Users/Ethan/Downloads/outbraindata/documents_topics.csv";
        // String doc_entities_path = "/Users/Ethan/Downloads/outbraindata/documents_entities.csv";
        String promoted_content = "/Users/Ethan/Downloads/outbraindata/promoted_content.csv";
        String clicks_train = "/Users/Ethan/Downloads/outbraindata/clicks_train.csv";
        String clicks_test = "/Users/Ethan/Downloads/outbraindata/clicks_test.csv";
        String doc_meta = "/Users/Ethan/Downloads/outbraindata/documents_meta.csv";
        String[] paths = new String[]{doc_meta};
        ana.analyze(paths);
    }
}


/**
 * file                       total      min    max       notes*
 * ------------------------------------------------------------------------------
 * documents_topics           300        0      299       don't need compressed
 * documents_categories       97         1000   2100      should be compressed
 * documents_entities         1326009                     too many. skip this.
 * documents id in doc_topics 2495423    1      2999333
 * advertiser_id              4385       2      4532
 * campaign_id                34675      1      35554
 * ad_id in promoted_content  559583     1      573098
 * ad_id in clicks_train      478950     1      548019
 * ad_id in clicks_test       381385     1      567073
 * ad_id in train+test        544300     1      567073
 * clicks_train               87141732
 * clicks_test                32225163
 * doc_id in doc_meta         2999334    1      2999334
 * source_id                  14394      1      14404   many NA
 * publisher_id               1259       2      1263    more NA
 *

 */

/**For FFM:
 *
 * Field          Field_ID         Feature_Range
 * ------------------------------------------------------
 * doc_topic         0               [0,299]
 * doc_cat           1               [300, 399]
 * advertiser        2               [400, 4799]
 * campaign_id       3               [4799+1, 4799+35554]
 *
 */
