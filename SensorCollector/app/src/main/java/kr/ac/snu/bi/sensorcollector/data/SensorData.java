package kr.ac.snu.bi.sensorcollector.data;

public class SensorData {
	
	public long time;
	
	public double accX; // m/s^2
	public double accY;
	public double accZ;
	
	public double azimuth; // degree
	public double pitch;
	public double roll;
	
	public double rotX; // rad/s
	public double rotY;
	public double rotZ;
	
	
	public void roundData(){
		accX = round(accX);
		accY = round(accY);
		accZ = round(accZ);
		azimuth = round(azimuth);
		pitch = round(pitch);
		roll = round(roll);
		rotX = round(rotX);
		rotY = round(rotY);
		rotZ = round(rotZ);
	}
	
	public static double round(double v){
		return Math.round(v * 1000000.0) / 1000000.0;
	}
	
}
