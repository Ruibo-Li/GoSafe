import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;


class Coordinate {
	public String latitude;
	public String longitude;
	
	public Coordinate(String latitude, String longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public String toString() {
		return String.format("(%s, %s)", latitude, longitude);
	}
}


public class Geocoding {

	private JSONObject result;
	private final String UTF_8 = Charset.forName("UTF-8").name();
	private final String key = "AIzaSyDY-QZyhcNf_YnWf4PwMg-89vJoFYpyKNc";
    private final String url = "https://maps.googleapis.com/maps/api/geocode/json";
	
    public Geocoding(String address) throws MalformedURLException, IOException, JSONException {
    	String query = String.format("key=%s&address=%s", 
    			URLEncoder.encode(key, UTF_8), URLEncoder.encode(address, UTF_8));
    	URLConnection connection = new URL(url + "?" + query).openConnection();
    	connection.setRequestProperty("Accept-Charset", UTF_8);
    	BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder response = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null)
			response.append(line);
		reader.close();
        result = new JSONObject(response.toString()).getJSONArray("results").getJSONObject(0);
    }
    
    public Coordinate getCoordinate() throws JSONException {
    	JSONObject coordinate = result.getJSONObject("geometry").getJSONObject("location");
    	return new Coordinate(coordinate.getString("lat"), coordinate.getString("lng"));
    }
    
    public String getFormattedAddress() throws JSONException {
    	return result.getString("formatted_address").toUpperCase();
    }
    
    public String getBorough() throws JSONException {
    	String borough = "";
    	JSONArray addressComponents = result.getJSONArray("address_components");
    	for (int i = 0; i < addressComponents.length(); i++) {
    		String type = addressComponents.getJSONObject(i).getJSONArray("types").getString(0);
    		if (type.equalsIgnoreCase("locality"))
    			borough = addressComponents.getJSONObject(i).getString("long_name");
    		if (type.contains("sublocality")) {
    			borough = addressComponents.getJSONObject(i).getString("long_name");
    			break;
    		}
    	}
    	return borough.toUpperCase();
    }
    
    public String getZipcode() throws JSONException {
    	String zipcode = "";
    	JSONArray addressComponents = result.getJSONArray("address_components");
    	for (int i = 0; i < addressComponents.length(); i++) {
    		String type = addressComponents.getJSONObject(i).getJSONArray("types").getString(0);
    		if (type.equalsIgnoreCase("postal_code")) {
    			zipcode = addressComponents.getJSONObject(i).getString("long_name");
    			break;
    		}
    	}
    	return zipcode;	
    }

}