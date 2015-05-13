import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;

/**
 * Created by szeyiu on 4/24/15.
 */
public class WordID {
    static List<String> id2Word = new ArrayList<String>();
    static HashMap<String, Integer> word2Id = new HashMap<String, Integer>();
    static int size = 0;

    static void load(JSONArray input) throws Exception {
    	for(int i=0;i<input.length();i++) {
    		JSONObject cur = input.getJSONObject(i);
    		@SuppressWarnings("unchecked")
			Iterator <String> it = cur.keys();
    		while(it.hasNext()) {
    			String key = it.next();
    			String word = cur.getString(key);
                String formatWord = word.trim();
                if(!word2Id.containsKey(formatWord)){
                    id2Word.add(formatWord);
                    word2Id.put(formatWord, id2Word.size()-1);
                }
    		}
    	}
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
}
