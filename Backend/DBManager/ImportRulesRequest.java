package DBManager;

import java.util.ArrayList;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

public class ImportRulesRequest {

	private AmazonDynamoDBClient dynamo;
	
	public ImportRulesRequest() {
		AWSCredentials credentials = new BasicAWSCredentials("AKIAJOEIWOBW6JQAF3IA", "2uxZjNVpKGOKT/KBZn32Y7jFXjT+l76X72Gnwa8R");
		dynamo = new AmazonDynamoDBClient(credentials);
		dynamo.setEndpoint("dynamodb.us-west-2.amazonaws.com");
	}
	
	private Table createTable() throws InterruptedException {
		DynamoDB dynamoDB = new DynamoDB(dynamo);
		
		try {
			Table table = dynamoDB.getTable("rules");
			table.delete();
			System.out.println("Deleting table...");
			table.waitForDelete();
			System.out.println("Table deleted.");
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		}
		
		ArrayList<AttributeDefinition> attributeDefinitions= new ArrayList<AttributeDefinition>();
		attributeDefinitions.add(new AttributeDefinition().withAttributeName("id").withAttributeType("S"));

		ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
		keySchema.add(new KeySchemaElement().withAttributeName("id").withKeyType(KeyType.HASH));
		
		CreateTableRequest createTableRequest = new CreateTableRequest()
				.withTableName("rules")
				.withKeySchema(keySchema)
				.withAttributeDefinitions(attributeDefinitions)
				.withProvisionedThroughput(new ProvisionedThroughput()
				    .withReadCapacityUnits(5L)
					.withWriteCapacityUnits(5L));

		System.out.println("Creating table...");
		Table table = dynamoDB.createTable(createTableRequest);
		table.waitForActive();
		System.out.println("Table created.");
		
		return table;
	}
	
	public String importRules(JSONArray rules) {
		try {
			Table table = createTable();
			System.out.println(rules.length());
			for (int i = 0; i < rules.length(); i++) {
				JSONObject rule = rules.getJSONObject(i);
				Item item = new Item();
				item.withPrimaryKey("id", Integer.toString(i + 1))
					.with("lhs", rule.getString("lhs").toString())
					.with("rhs", rule.getString("rhs"))
					.with("support", rule.getString("support"))
					.with("confidence", rule.getString("confidence"));
				table.putItem(item);
			}
			return "Success";
		} catch (InterruptedException | JSONException e) {
			return e.getMessage();
		}
	}
	
	public void shutdown() {
		dynamo.shutdown();
	}
	
}
