package com.iespl.gisgraphy;

public class LatLng {
	private String lat;
	private String lng;
	private String location;
	private String locationCode;
	private String distance;
	
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	
public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLocationCode() {
		return locationCode;
	}
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
	
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getDistance() {
		return distance;
	}
	
	//	private String lng;
	public LatLng(String lat, String lng,String location ,String locationCode,String distance) {
		super();
		this.lat = lat;
		this.lng = lng;
		this.location = location;
		this.locationCode = locationCode;
		this.distance = distance;
	}
	
	

}
