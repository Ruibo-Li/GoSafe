import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

public class QueryCrimeRequest {

	private Table table;
	private AmazonDynamoDBClient dynamo;
	
	public QueryCrimeRequest() {
		AWSCredentials credentials = new BasicAWSCredentials("AKIAJOEIWOBW6JQAF3IA", "2uxZjNVpKGOKT/KBZn32Y7jFXjT+l76X72Gnwa8R");
		dynamo = new AmazonDynamoDBClient(credentials);
		dynamo.setEndpoint("dynamodb.us-west-2.amazonaws.com");
		table = new DynamoDB(dynamo).getTable("crimes");
	}
	
	public JSONArray getCrimes() throws JSONException, ParseException {
		return getCrimes(null);
	}
	
	public JSONArray getCrimes(JSONObject query) throws JSONException, ParseException {
		JSONArray crimes = new JSONArray();
		ScanSpec spec = new ScanSpec();
		spec.withProjectionExpression("crime_date, crime_time, crime_type, formatted_address, latitude, longitude, description, zipcode, borough");
		ItemCollection<ScanOutcome> item = table.scan(spec);
		Iterator<Item> iter = item.iterator();
		while (iter.hasNext()) {
			Item crime = iter.next();
			if (!matches(crime, query))
				continue;
			crimes.put(new JSONObject(crime.toJSON()));
		}
		return crimes;
	}
	
	public boolean matches(Item crime, JSONObject query) throws JSONException, ParseException {
		
		if (query == null)
			return true;
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
		Date crimeDate = dateFormatter.parse(crime.getString("crime_date"));
		Date crimeTime = timeFormatter.parse(crime.getString("crime_time"));
		
		if (query.has("zipcode") && !query.getString("zipcode").equalsIgnoreCase(crime.getString("zipcode")))
			return false;
		if (query.has("borough") && !query.getString("borough").equalsIgnoreCase(crime.getString("borough")))
			return false;
		if (query.has("start_date")) {
			Date startDate = dateFormatter.parse(query.getString("start_date"));
			if (crimeDate.before(startDate))
				return false;
		}
		if (query.has("end_date")) {
			Date endDate = dateFormatter.parse(query.getString("end_date"));
			if (crimeDate.after(endDate))
				return false;
		}
		if (query.has("start_time")) {
			Date startTime = timeFormatter.parse(query.getString("start_time"));
			if (crimeTime.before(startTime))
				return false;
		}
		if (query.has("end_time")) {
			Date endTime = timeFormatter.parse(query.getString("end_time"));
			if (crimeTime.after(endTime))
				return false;
		}
		if (query.has("crime_type") && !query.getString("crime_type").contains(crime.getString("crime_type")))
			return false;
		
		return true;
		
	}
	
	public void shutdown() {
		dynamo.shutdown();
	}
	
}
