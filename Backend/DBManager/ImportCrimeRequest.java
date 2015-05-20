package DBManager;

import APIServices.Geocoding;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

public class ImportCrimeRequest {

	private Table table;
	private AmazonDynamoDBClient dynamo;
	private final HashSet<String> attributes = 
			new HashSet<String>(Arrays.asList(new String[] {"id", "crime_date", "crime_time", "address",
					"zipcode", "crime_type", "description"}));
	
	public ImportCrimeRequest() {
		AWSCredentials credentials = new BasicAWSCredentials("AKIAJOEIWOBW6JQAF3IA", "2uxZjNVpKGOKT/KBZn32Y7jFXjT+l76X72Gnwa8R");
		dynamo = new AmazonDynamoDBClient(credentials);
		dynamo.setEndpoint("dynamodb.us-west-2.amazonaws.com");
		table = new DynamoDB(dynamo).getTable("crimes");
	}
	
	public String importCrime(JSONObject crime) throws JSONException {
		Item item = new Item();
		Iterator<?> iter = crime.keys();
		while (iter.hasNext()) {
			String attribute = (String) iter.next();
			if (!attributes.contains(attribute))
				return String.format("Error: <%s> is not a valid attribute.", attribute);
			if (attribute.equals("id")) {
				if (exists(crime.getString(attribute)))
					return "Crime entry exists.";
			} else
				item.withString(attribute, crime.getString(attribute).toUpperCase());
		}
		Geocoding geocoding;
		try {
			if (crime.has("zipcode"))
				geocoding = new Geocoding(crime.getString("address") + ", " + crime.getString("zipcode"));
			else {
				geocoding = new Geocoding(crime.getString("address") + ", NY");
				item.withString("zipcode", geocoding.getZipcode());
			}
			item.withPrimaryKey("id", crime.getString("id"))
				.withString("latitude", geocoding.getCoordinate().latitude)
				.withString("longitude", geocoding.getCoordinate().longitude)
				.withString("formatted_address", geocoding.getFormattedAddress())
				.withString("borough", geocoding.getBorough());
			table.putItem(item);
			return "Success";
		} catch (Exception e) {
			return "Error: " + e.getMessage();
		}
	}
	
	public boolean exists(String id) {
		GetItemSpec spec = new GetItemSpec();
		spec.withPrimaryKey("id", id);
		return table.getItem(spec) != null;
	}
	
	public void shutdown() {
		dynamo.shutdown();
	}

}
