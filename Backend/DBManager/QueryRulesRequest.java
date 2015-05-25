package DBManager;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

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

public class QueryRulesRequest {
	
	private Table table;
	private JSONArray rules;
	private AmazonDynamoDBClient dynamo;
	
	public QueryRulesRequest() throws JSONException {
		AWSCredentials credentials = new BasicAWSCredentials("AKIAJOEIWOBW6JQAF3IA", "2uxZjNVpKGOKT/KBZn32Y7jFXjT+l76X72Gnwa8R");
		dynamo = new AmazonDynamoDBClient(credentials);
		dynamo.setEndpoint("dynamodb.us-west-2.amazonaws.com");
		table = new DynamoDB(dynamo).getTable("rules");
		rules = new JSONArray();
		getRules();
	}
	
	public JSONArray getRules() throws JSONException {
		if (rules.length() == 0) {
			ScanSpec spec = new ScanSpec();
			spec.withProjectionExpression("lhs, rhs, confidence, support");
			ItemCollection<ScanOutcome> item = table.scan(spec);
			Iterator<Item> iter = item.iterator();
			while (iter.hasNext()) {
				Item rule = iter.next();
				rules.put(new JSONObject(rule.toJSON()));
			}
			return rules;
		} else
			return rules;
	}
	
	public JSONArray getRules(String zipcode) throws JSONException {
		JSONArray rules = new JSONArray();
		for (int i = 0; i < this.rules.length(); i++) {
			JSONObject rule = this.rules.getJSONObject(i);
			if (rule.getString("lhs").contains(zipcode)) {
				rules.put(rule);
			}
		}
		return rules;
	}
	
	public void shutdown() {
		dynamo.shutdown();
	}
	
}
