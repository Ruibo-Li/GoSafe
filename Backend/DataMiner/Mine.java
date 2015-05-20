package DataMiner;

import java.util.*;
import java.io.*;

import DBManager.QueryCrimeRequest;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;

public class Mine {
    public static void main(String[] args) throws Exception {
    	String inputfile = "INTEGRATED-DATASET.csv";
    	File outfile = new File(inputfile); 
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(outfile)));
    	QueryCrimeRequest client = new QueryCrimeRequest();
    	JSONArray ja = client.getCrimes();
    	for(int i=0;i<ja.length();i++) {
    		JSONObject jo = ja.getJSONObject(i);
    		Iterator <String> it = jo.keys();
    		int curlen = jo.length();
    		int j = 0;
    		while(it.hasNext()) {
    			String key = it.next();
    			String cur = jo.getString(key);
    			//System.out.println(key);
    			if(key.equals("formatted_address")) {
    				String a = cur.replaceAll(", ", "-");
    				writer.write(a);
    			}
    			else if(key.equals("crime_time")) {
    				int time = Integer.parseInt(cur.split(":")[0]);
    	    		String newTime = "";
    	    		if(time>=5 && time<=12)
    	    			newTime = "Time: Morning";
    	    		else if(time>=12 && time<=17)
    	    			newTime = "Time: Afternoon";
    	    		else if(time>=17 && time<=21)
    	    			newTime = "Time: Evening";
    	    		else
    	    			newTime = "Time: Night";
    	    		writer.write(newTime);
    			}
    			else if(key.equals("crime_date")) {
    				String newDate = "Month: " + cur.split("-")[1];
    				writer.write(newDate);
    			}
    			else
    				writer.write(cur);
    			if(j!=curlen-1)
    				writer.write(",");
    			j++;
    		}
    		if(i!=ja.length()-1)
    			writer.write('\n');
    	}
    	
        String outputfile = "output.txt";
        double threshold = 0.003;
        double conf = 0;       
        FrequentItems fi = new FrequentItems(inputfile);
        fi.setOutputPath(outputfile);
        if(threshold>=0 && threshold<=1) fi.setThreshold(threshold);
        if(conf>=0 && conf<=1) fi.setMinConf(conf);
        fi.findFrequentPairs().findFrequentAll();
        fi.findRules();
        JSONArray results = fi.getJsonrules();
        String[] crimes = {"RAPE", "SEXUAL ASSAULT", "ROBBERY", "ASSAULT", "BURGLARY", "MOTOR VEHICLE THEFT", "THEFT", "HOMICIDE", "OTHER"};
        HashSet <String> hs = new HashSet <String> (Arrays.asList(crimes));
        for(int i=0;i<results.length();i++) {
        	JSONObject jo = results.getJSONObject(i);
        	if(hs.contains(jo.getString("rhs"))) {
        		System.out.println(jo);
        	}
        }
        System.out.println("Program safely terminated! Thanks!");
        
    }
}
