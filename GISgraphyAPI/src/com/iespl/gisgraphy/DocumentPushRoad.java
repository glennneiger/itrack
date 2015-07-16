package com.iespl.gisgraphy;

public class DocumentPushRoad {
	private String lat;
	private String lng;
	private String latlngseries;
	private String location;
	private String name;
	private String shape;
	private String streettype;
	private String length;
	private String onewaytf;
	private String countrycode;
	private String isin;
	private String countryflagurl;
	private String textsearchname;
	private String featureid;
	private String placetype;
	
	
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
	
	
	public void setLatlngseries(String latlngseries) {
		this.latlngseries = latlngseries;
	}
	public String getLatlngseries() {
		return latlngseries;
	}
	public void setStreettype(String streettype) {
		this.streettype = streettype;
	}
	public String getStreettype() {
		return streettype;
	}
	public void setLength(String length) {
		this.length = length;
	}
	public String getLength() {
		return length;
	}
	public void setOnewaytf(String onewaytf) {
		this.onewaytf = onewaytf;
	}
	public String getOnewaytf() {
		return onewaytf;
	}
	public void setCountrycode(String countrycode) {
		this.countrycode = countrycode;
	}
	public String getCountrycode() {
		return countrycode;
	}
	public void setIsin(String isin) {
		this.isin = isin;
	}
	public String getIsin() {
		return isin;
	}
	public void setCountryflagurl(String countryflagurl) {
		this.countryflagurl = countryflagurl;
	}
	public String getCountryflagurl() {
		return countryflagurl;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setShape(String shape) {
		this.shape = shape;
	}
	public String getShape() {
		return shape;
	}
	public void setTextsearchname(String textsearchname) {
		this.textsearchname = textsearchname;
	}
	public String getTextsearchname() {
		return textsearchname;
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
//	private String lng;
	/*public DocumentPushRoad(String countrycode,String isin,String length,String location,String name,String onewaytf,String shape,String streettype,String textsearchname,String countryflagurl, String featureid,String lat, String lng,String latlngseries,String placetype) {
		super();
		this.countrycode=countrycode;
		this.isin=isin;	
		this.length=length;		
		this.location = location;
		this.name=name;
		this.onewaytf=onewaytf;
		this.shape=shape;
		this.streettype=streettype;
		this.textsearchname=textsearchname;
		
		this.countryflagurl=countryflagurl;
		this.featureid=featureid;
		this.lat = lat;
		this.lng = lng;		
		this.latlngseries=latlngseries;
		this.placetype=placetype;		
	}
	*/
	//======Getting and setting data from user=========//
	public DocumentPushRoad(String lat,String lng,String latlngseries,String name,String streettype,String length,String onewaytf,String countrycode,String isin)
	{
		super();
		this.lat = lat;
		this.lng = lng;	
		this.latlngseries=latlngseries;
		this.name=name;
		this.streettype=streettype;
		this.length=length;
		this.onewaytf=onewaytf;		
		this.countrycode=countrycode;		
		this.isin=isin;	
		
	}

	
	
	
	

}
