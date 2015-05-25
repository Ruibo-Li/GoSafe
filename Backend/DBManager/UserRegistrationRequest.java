package DBManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import APIServices.Geocoding;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

public class UserRegistrationRequest {

	private Table table;
	private AmazonDynamoDBClient dynamo;
	private final HashSet<String> attributes = 
			new HashSet<String>(Arrays.asList(new String[] {"id", "first_name", "last_name", "email", "address"}));
	
	public UserRegistrationRequest() {
		AWSCredentials credentials = new BasicAWSCredentials("AKIAJOEIWOBW6JQAF3IA", "2uxZjNVpKGOKT/KBZn32Y7jFXjT+l76X72Gnwa8R");
		dynamo = new AmazonDynamoDBClient(credentials);
		dynamo.setEndpoint("dynamodb.us-west-2.amazonaws.com");
		table = new DynamoDB(dynamo).getTable("users");
	}
	
	public String register(JSONObject user) throws JSONException {
		
		if (exists(user.getString("id")))
			return "User exists";
		
		Item item = new Item();
		Iterator<?> iter = user.keys();
		while (iter.hasNext()) {
			String attribute = (String) iter.next();
			if (!attributes.contains(attribute))
				return String.format("Error: <%s> is not a valid attribute.", attribute);
			if (attribute.equals("id")) {
				item.withPrimaryKey("id", user.getString("id"));
				continue;
			}
			item.withString(attribute, user.getString(attribute));
		}
		table.putItem(item);
		return "Success";
	}

	public String updateAddress(String id, String address) {
		try {
			GetItemSpec spec = new GetItemSpec()
				.withPrimaryKey("id", id)
				.withProjectionExpression("id, first_name, last_name, email, address");
			Item item = table.getItem(spec);
			Geocoding geocoding = new Geocoding(address);
			item.withString("address", geocoding.getFormattedAddress())
				.withString("zipcode", geocoding.getZipcode());
			table.putItem(item);
			return "Success";
		} catch (Exception e) {
			return e.getMessage();
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
