package DataMiner;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by szeyiu on 4/24/15.
 */
public class WordID {
    static List<String> id2Word = new ArrayList<String>();
    static HashMap<String, Integer> word2Id = new HashMap<String, Integer>();
    static int size = 0;
    static void load(String input) throws IOException {
        File fin = new File(input);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fin)));
        String line = reader.readLine();
        while(line!=null){
            String[] words = split(line);//line.split(",");
            for(int i=0; i<words.length; ++i){
                String formatWord = words[i].trim();
                if(!word2Id.containsKey(formatWord)){
                    id2Word.add(formatWord);
                    word2Id.put(formatWord, id2Word.size()-1);
                }
            }
            line = reader.readLine();
        }
        reader.close();
        size = id2Word.size();
    }
    static int getId(String word){
        word = word.trim();
        if(!word2Id.containsKey(word)) return -1;
        else return word2Id.get(word);
    }
    static String getWord(int id){
        if(id>size-1) return null;
        return id2Word.get(id);
    }
    static String[] split(String str){
        List<String> strlst = new ArrayList<String>();
        boolean isQuote = false;
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<str.length(); ++i){
            if(str.charAt(i)=='\"'){
                if(i<str.length()-1 && str.charAt(i+1)=='\"'){
                    sb.append("\"");
                    ++i;
                } else {
                    isQuote = !isQuote;
                }
            } else if(str.charAt(i)==','){
                if(isQuote){
                    sb.append(str.charAt(i));
                } else {
                    strlst.add(sb.toString());
                    sb = new StringBuilder();
                }
            } else {
                sb.append(str.charAt(i));
            }
        }
        if(!isQuote && sb.length()>0){
            strlst.add(sb.toString());
        }
        String[] rst = new String[strlst.size()];
        for(int i=0; i<strlst.size(); ++i){
            rst[i] = strlst.get(i);
        }
        return rst;
    }
/*    public static void main(String[] args){
        String str =  "East Tremont Ave ,Anthony Ave,E 176 St,WB,Commercial,15 - 50 - 1*50,15 - 100 - 0*100,15 - 300 - 0*300,16 - 50 - 1*50,16 - 100 - 0*100,16 - 300 - 0*300,17 - 50 - 1*50,17 - 100 - 0*100,17 - 300 - 0*300,18 - 50 - 0*50,18 - 100 - 0*100,18 - 300 - 0*300,19 - 50 - 0*50,19 - 100 - 0*100,19 - 300 - 0*300,20 - 50 - 0*50,20 - 100 - 0*100,20 - 300 - 0*300,21 - 50 - 0*50,21 - 100 - 0*100,21 - 300 - 0*300,22 - 50 - 0*50,22 - 100 - 0*100,22 - 300 - 0*300,23 - 50 - 0*50,23 - 100 - 0*100,23 - 300 - 0*300";
        String[] rst = split(str);
        for(int i=0; i<rst.length; ++i){
            System.out.println(rst[i]);
        }
    }*/
}
