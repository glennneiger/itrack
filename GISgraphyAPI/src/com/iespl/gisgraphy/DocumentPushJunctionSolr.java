package com.iespl.gisgraphy;

public class DocumentPushJunctionSolr {
	private String lat;
	private String lng;
	
	private String location;	
	private String name;
	private String source;	
	private String countrycode;	
	private String countryflagurl;
	private String countryname;
	private String featureid;
	private String fullyqualifiedname;
	private String featureclass;
	private String featurecode;
	private String placetype;
	private String timezone;
	private String googlemapurl;
	private String yahoomapurl;
	
	
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
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}	

	public void setCountrycode(String countrycode) {
		this.countrycode = countrycode;
	}
	public String getCountrycode() {
		return countrycode;
	}

	public void setCountryflagurl(String countryflagurl) {
		this.countryflagurl = countryflagurl;
	}
	public String getCountryflagurl() {
		return countryflagurl;
	}
	public void setCountryname(String countryname) {
		this.countryname = countryname;
	}
	public String getCountryname() {
		return countryname;
	}
	public void setFeatureid(String featureid) {
		this.featureid = featureid;
	}
	public String getFeatureid() {
		return featureid;
	}
	
	
	public void setPlacetype(String placetype) {
		this.placetype = placetype;
	}
	public String getPlacetype() {
		return placetype;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setYahoomapurl(String yahoomapurl) {
		this.yahoomapurl = yahoomapurl;
	}
	public String getYahoomapurl() {
		return yahoomapurl;
	}
	public void setGooglemapurl(String googlemapurl) {
		this.googlemapurl = googlemapurl;
	}
	public String getGooglemapurl() {
		return googlemapurl;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getSource() {
		return source;
	}
	public void setFullyqualifiedname(String fullyqualifiedname) {
		this.fullyqualifiedname = fullyqualifiedname;
	}
	public String getFullyqualifiedname() {
		return fullyqualifiedname;
	}
	public void setFeatureclass(String featureclass) {
		this.featureclass = featureclass;
	}
	public String getFeatureclass() {
		return featureclass;
	}
	public void setFeaturecode(String featurecode) {
		this.featurecode = featurecode;
	}
	public String getFeaturecode() {
		return featurecode;
	}

	//======Getting and setting data for solr=========//
	public DocumentPushJunctionSolr(String countrycode,String countryflagurl,String featureid,String countryname,String lat,String lng,String name,String fullyqualifiedname,String featureclass,String featurecode,String placetype,String timezone,String googlemapurl,String yahoomapurl)
	{
		super();
		this.countrycode=countrycode;
		this.countryflagurl=countryflagurl;
		this.featureid=featureid;
		this.countryname=countryname;
					
		this.lat = lat;		
		this.lng = lng;	
		this.name=name;
		this.fullyqualifiedname=fullyqualifiedname;	
		
		this.featureclass=featureclass;
		this.featurecode=featurecode;
		this.placetype=placetype;
		this.timezone=timezone;
		this.googlemapurl=googlemapurl;
		this.yahoomapurl=yahoomapurl;
		
		
	}
	
	
	
	

}
