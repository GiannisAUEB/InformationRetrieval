package txtparsing;

import utils.IO;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author  Tonia Kyriakopoulou
 */

public class TXTParsing {
    static ArrayList<String> uniqueID = new ArrayList<>();
    public static List<txtparsing.MyDoc> parse(String file) throws Exception {
        try{
            //Parse txt file
            String txt_file = IO.ReadEntireFileIntoAString(file);
            /*Replacing multiple * with just one *, so we can use it as regex later*/
            txt_file = txt_file.replace("********************************************", "*");
            /*Split each file on * */
            String[] docs = txt_file.split("\r\n\\*\r\n");
            int counter=0;
            //Parse each document from the txt file
            List<txtparsing.MyDoc> parsed_docs= new ArrayList<txtparsing.MyDoc>();
            for (String doc:docs){
                /* Split each doc on double line change or line change + whitespace + line change, both where present in the collection*/
                String[] adoc = doc.split("\r\n\r\n|\r\n\\s+\r\n");
                /* ID is splitted with a single line change from the title of the doc*/
                String id = adoc[0].split("\r\n")[0];
                if(!uniqueID.contains(id) && id.startsWith("Document")){
                    uniqueID.add(id);
                    String title = "";
                    /* We splitted on every line change, but after taking the ID everything else on adoc[0] is the Title of the document*/
                    for(int i=1; i<adoc[0].split("\r\n").length; i++) title = title + " " + adoc[0].split("\r\n")[i];

                    title = title.trim();
                    /* Removing the word 'Document' from the start of every ID*/
                    id = id.replaceAll("[^\\d.]","");
                    txtparsing.MyDoc mydoc = new txtparsing.MyDoc(id,title,adoc[1]);
                    counter++;
                    parsed_docs.add(mydoc);
                }
            }
            System.out.println("Read: "+counter + " docs");
            return parsed_docs;
        } catch (Throwable err) {
            err.printStackTrace();
            return null;
        }

    }

}