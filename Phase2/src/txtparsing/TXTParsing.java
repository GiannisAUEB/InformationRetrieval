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
    public static List<txtparsing.MyDoc> parse(String file, String action) throws Exception {
        try{
            if(action.equals("q")) uniqueID = new ArrayList<>();
            //Parse txt file
            String txt_file = IO.ReadEntireFileIntoAString(file);
            /*Replacing multiple * with just one *, so we can use it as regex later*/
            txt_file = txt_file.replace("********************************************", "*");
            /*Split each file on * */
            String[] docs = new String[0];
            if(action.equals("d")) docs = txt_file.split("\r\n\\*\r\n");
            if(action.equals("q")) docs = txt_file.split("#\r\n");
            //Parse each document from the txt file
            List<txtparsing.MyDoc> parsed_docs= new ArrayList<txtparsing.MyDoc>();
            for (String doc:docs){
                /* Split each doc on double line change or line change + whitespace + line change, both where present in the collection*/
                String[] adoc = new String[0];
                if(action.equals("d")) adoc = doc.split("\r\n\r\n|\r\n\\s+\r\n");
                if(action.equals("q")) adoc = doc.split("\r\n");
                /* ID is splitted with a single line change from the title of the doc*/
                String id = null;
                if(action.equals("d")) id = adoc[0].split("\r\n")[0];
                if(action.equals("q")) id = String.valueOf((Integer.parseInt(adoc[0].split("\r\n")[0])));
                if((!uniqueID.contains(id) && id.startsWith("Document")) || action.equals("q")){
                    uniqueID.add(id);
                    String title = "";
                    if(action.equals("d")){
                        /* We splitted on every line change, but after taking the ID everything else on adoc[0] is the Title of the document*/
                        for(int i=1; i<adoc[0].split("\r\n").length; i++) title = title + " " + adoc[0].split("\r\n")[i];
                        title = title.trim();
                        /* Removing the word 'Document' from the start of every ID*/
                        id = id.replaceAll("[^\\d.]","");
                    }
                    MyDoc mydoc;
                    String text =" ";
                    if(action.equals("d")) {
                        text = adoc[1];
                    }else{
                        for(int i = 1; i< adoc.length; i++){
                            text = text + adoc[i];
                        }
                    }
                    text = text.replaceAll("\\W", " ");
                    mydoc = new MyDoc(id,title,text.trim());
                    parsed_docs.add(mydoc);
                }
            }
            return parsed_docs;
        } catch (Throwable err) {
            err.printStackTrace();
            return null;
        }

    }

}