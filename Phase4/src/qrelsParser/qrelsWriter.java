package qrelsParser;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner; // Import the Scanner class to read text files

public class qrelsWriter {
    public static void main(String[] args) {
        try {
            File myObj = new File("src\\qrelsParser\\LISARJ.NUM");

            /*Cleaning previous if needed*/
            new FileWriter("trec_eval\\LISAQRELS.test", false).close();

            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNext()) {
                int refID = myReader.nextInt();
                int refCount = myReader.nextInt();
                ArrayList<Integer> ref = new ArrayList<>();
                while(refCount>0){
                    ref.add(myReader.nextInt());
                    refCount--;
                }
                writeQrels(refID, ref);
            }
            System.out.println("Successfully wrote to the file " + "LISAQRELS.test");
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeQrels(int refID, ArrayList<Integer> ref) {
        try {
            FileWriter myWriter = new FileWriter("trec_eval\\LISAQRELS.test", true);
            for(int i=0; i<ref.size(); i++){
                myWriter.write(refID + "\t" + "0" + "\t" + ref.get(i) + "\t" + "1" + "\n");
            }
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}