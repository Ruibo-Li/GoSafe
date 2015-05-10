import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

public class ImportCrimeRequest {

	private Table table;
	private AmazonDynamoDBClient dynamo;
	
	public ImportCrimeRequest() {
		AWSCredentialsProvider credentialsProvider = new ClasspathPropertiesFileCredentialsProvider();
		dynamo = new AmazonDynamoDBClient(credentialsProvider);
		dynamo.setEndpoint("dynamodb.us-west-2.amazonaws.com");
		table = new DynamoDB(dynamo).getTable("crimes");
	}
	
	public void importCrime(JSONObject data) throws JSONException {
		Item item = new Item();
		item.withPrimaryKey("id", data.getString("id"))
			.withString("crime_date", data.getString("crime_date"))
			.withString("crime_time", data.getString("crime_time"))
			.withString("address", data.getString("address"))
			.withString("zipcode", data.getString("zipcode"))
			.withString("latitude", data.getString("latitude"))
			.withString("longitude", data.getString("longitude"))
			.withString("crime_type", data.getString("crime_type"))
			.withString("borough", data.getString("borough"))
			.withString("description", data.getString("description"));
		table.putItem(item);
	}
	
	public void shutdown() {
		dynamo.shutdown();
	}
}
