package kr.ac.snu.bi.sensorcollector.data;

public class Venue {

	public String name;
	public String category;
	public double latitude;
	public double longitude;

    public boolean isLatLongEmpty(){
        return latitude == 0 || longitude == 0;
    }
}
