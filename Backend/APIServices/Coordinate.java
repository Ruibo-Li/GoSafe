package APIServices;

public class Coordinate {
	
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
