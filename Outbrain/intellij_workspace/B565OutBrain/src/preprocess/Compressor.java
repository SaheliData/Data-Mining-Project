package preprocess;

/**
 * Created by Ethan on 11/20/16.
 */

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * compress documents_categories
 *
 */
public class Compressor {
    public final static int BEGIN_INDEX= 300;

    public void compress (String path, String mapOutPutPath, String compressedOutPutPath) throws IOException {
        Map<Integer, Integer> hm = new TreeMap<Integer, Integer>(
                new Comparator<Integer>() {
                    public int compare(Integer obj1, Integer obj2) {
                        // 降序排序
                        return obj1.compareTo(obj2);
                    }
                });

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
                String[] split = line.split(",");

                int key = Integer.parseInt(split[1]);
                if (count % 100000 == 0)
                    System.out.println(count + ": " + key);
                if (!hm.containsKey(key)) {
                    hm.put(key, 1);
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

        File outputFile = new File(mapOutPutPath);
        if(outputFile.exists()){
            outputFile.delete();
        }
        outputFile.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
        out.write("oldKey\tnewKey\n");
        Set<Integer> keySet = hm.keySet();
        Iterator<Integer> iter = keySet.iterator();
        int newIndex = BEGIN_INDEX;
        while (iter.hasNext()) {
            Integer key = iter.next();
            hm.put(key, newIndex);
            System.out.println(key + ":" + newIndex);
            out.write(key + "\t" + newIndex+"\n");

            newIndex ++;
        }
        out.flush();
        out.close();

        outputCompressed(path, compressedOutPutPath, hm);
    }

    public void outputCompressed(String originalData, String compressedOutput, Map<Integer, Integer> hm) throws IOException{
        FileInputStream inputStream = null;
        Scanner sc = null;
        File outputFile = new File(compressedOutput);
        if(outputFile.exists()){
            outputFile.delete();
        }
        outputFile.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

        int count = 0;
        try {
            inputStream = new FileInputStream(originalData);
            sc = new Scanner(inputStream);
            String title = sc.nextLine();//skip the title
            out.write(title+"\n");
            while (sc.hasNextLine()) {
                count++;

                String line = sc.nextLine();
                String[] split = line.split(",");

                int key = Integer.parseInt(split[1]);
                if (count % 100000 == 0)
                    System.out.println(count + ": " + key);

                int newKey = hm.get(key);
                String newLine = split[0]+","+newKey+","+split[2];
                out.write(newLine+"\n");

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
        out.flush();
        out.close();
    }

    public void compress() throws  IOException{
        compress("/Users/Ethan/Downloads/outbraindata/documents_categories.csv",
                "/Users/Ethan/Downloads/outbraindata/documents_categories_map.csv",
                "/Users/Ethan/Downloads/outbraindata/documents_categories_compressed.csv" );
    }

    public static void main(String[] args) throws IOException{
        Compressor com = new Compressor();
        com.compress();
    }
}
