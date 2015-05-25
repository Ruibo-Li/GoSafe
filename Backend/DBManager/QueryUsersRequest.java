package DBManager;

import java.text.ParseException;
import java.util.Iterator;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

public class QueryUsersRequest {

	private Table table;
	private AmazonDynamoDBClient dynamo;
	
	public QueryUsersRequest() throws JSONException {
		AWSCredentials credentials = new BasicAWSCredentials("AKIAJOEIWOBW6JQAF3IA", "2uxZjNVpKGOKT/KBZn32Y7jFXjT+l76X72Gnwa8R");
		dynamo = new AmazonDynamoDBClient(credentials);
		dynamo.setEndpoint("dynamodb.us-west-2.amazonaws.com");
		table = new DynamoDB(dynamo).getTable("users");
	}
	
	public JSONArray getUsers(String zipcode) throws JSONException, ParseException {
		JSONArray users = new JSONArray();
		ScanSpec spec = new ScanSpec();
		spec.withProjectionExpression("first_name, last_name, address, zipcode, email");
		ItemCollection<ScanOutcome> item = table.scan(spec);
		Iterator<Item> iter = item.iterator();
		while (iter.hasNext()) {
			Item user = iter.next();
			if (user.getString("zipcode") != null && user.getString("zipcode").equals(zipcode))
				users.put(new JSONObject(user.toJSON()));
		}
		return users;
	}
	
	public String getZipCode(String id) {
		GetItemSpec spec = new GetItemSpec()
			.withPrimaryKey("id", id)
			.withProjectionExpression("zipcode");
		Item item = table.getItem(spec);
		return item == null ? null : item.getString("zipcode");
	}
	
	public void shutdown() {
		dynamo.shutdown();
	}
	
}
