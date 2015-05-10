import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;


public class Test {
    public static void test(JSONArray inputJson, double threshold, double conf) throws Exception {
        String outputfile = "output.txt";

        FrequentItems fi = new FrequentItems(inputJson);
        fi.setOutputPath(outputfile);
        if(threshold>=0 && threshold<=1) fi.setThreshold(threshold);
        if(conf>=0 && conf<=1) fi.setMinConf(conf);
        fi.findFrequentPairs().findFrequentAll();
        fi.findRules();
        fi.printfile();
        System.out.println("Program safely terminated! Thanks!");
    }
    
    public static JSONArray Mapper(JSONArray inputJson) throws Exception{
    	String[] times = {"Morning","Afternoon","Evening","Night"};
    	JSONArray result = new JSONArray();
    	for(int i=0;i<inputJson.length();i++) {
    		JSONObject cur = inputJson.getJSONObject(i);
    		String date = cur.getString("crime_date");
    		int time = Integer.parseInt(cur.getString("crime_time").split(":")[0]);
    		String newDate = date.split("-")[1];
    		String newTime = "";
    		if(time>=5 && time<=12)
    			newTime = "Morning";
    		else if(time>=12 && time<=17)
    			newTime = "Afternoon";
    		else if(time>=17 && time<=21)
    			newTime = "Evening";
    		else
    			newTime = "Night";
    		cur.put("crime_date", newDate);
    		cur.put("crime_time", newTime);
    		result.put(cur);
    	}
    	return result;
    }
    
    public static void main(String[] args) throws Exception {
    	JSONArray ja = new JSONArray();
    	/*
    	JSONObject jo1 = new JSONObject();
    	jo1.put("1", "pen");
    	jo1.put("2", "ink");
    	jo1.put("3", "diary");
    	jo1.put("4", "soap");
    	ja.put(jo1);
    	
    	JSONObject jo2 = new JSONObject();
    	jo2.put("1", "pen");
    	jo2.put("2", "ink");
    	jo2.put("3", "diary");
    	ja.put(jo2);
    	
    	JSONObject jo3 = new JSONObject();
    	jo3.put("1", "pen");
    	jo3.put("2", "diary");
    	ja.put(jo3);
    	
    	JSONObject jo4 = new JSONObject();
    	jo4.put("1", "pen");
    	jo4.put("2", "ink");
    	jo4.put("3", "soap");
    	ja.put(jo4);
    	
        test(ja,0.7,0.8);
        */
    	JSONObject jo1 = new JSONObject();
    	jo1.put("id", "1");
    	jo1.put("crime_date", "2015-06-12");
    	jo1.put("crime_time", "13:01");
    	jo1.put("address", "116th st");
    	jo1.put("zipcode", "11111");
    	jo1.put("latitude", "80.122");
    	jo1.put("longitude", "-76.222");
    	jo1.put("crime_type", "theft");
    	jo1.put("borough", "Manhattan");
    	ja.put(jo1);
    	
    	JSONObject jo2 = new JSONObject();
    	jo2.put("id", "2");
    	jo2.put("crime_date", "2015-11-12");
    	jo2.put("crime_time", "01:01");
    	jo2.put("address", "122th st");
    	jo2.put("zipcode", "11101");
    	jo2.put("latitude", "81.122");
    	jo2.put("longitude", "-76.222");
    	jo2.put("crime_type", "theft");
    	jo2.put("borough", "Manhattan");
    	ja.put(jo2);
    	
    	System.out.println(Mapper(ja));
    }
}
