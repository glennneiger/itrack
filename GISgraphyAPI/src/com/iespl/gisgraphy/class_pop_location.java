package com.iespl.gisgraphy;
import java.util.ArrayList;
import java.util.regex.*;
import java.sql.*;

public class class_pop_location {
	String lat;
	String lng;
	String code;
	String loc_name="";
	String loc_code="";
	
	private ArrayList<LatLng >  latlngData = new ArrayList<LatLng>();
	
	public ArrayList<LatLng> getLatlngData() {
		return latlngData;
	}
	gis_connection gis_con = new gis_connection();
	
	//========pop by latlng array
	public class_pop_location(ArrayList<LatLng> latlngArray,int radius){		
		if(gis_con.connection !=null){
			//System.out.println("You made it, take control your database now!");
			//Create a Statement class to execute the SQL statement
		    try {
				Statement stmt = gis_con.connection.createStatement();
				
				
				for(LatLng data : latlngArray){
					//System.out.println("data : "+data.getLat());
					//System.out.println("data : "+data.getLng());
					lat=data.getLat();
					lng=data.getLng();								
					double lat_minus= Double.parseDouble(lat)-1;
					double lat_plus= Double.parseDouble(lat)+1;
					double lng_minus= Double.parseDouble(lng)-1;
					double lng_plus= Double.parseDouble(lng)+1;
					//Execute the SQL statement and get the results in a Resultset
					String query="SELECT  name,featureid,CAST (st_distance_sphere(location, st_setsrid(st_makepoint("+lng+","+lat+"),4326)) AS INT) AS d  FROM gisfeature WHERE location && 'BOX3D("+lng_minus+" "+lat_minus+","+lng_plus+" "+lat_plus+")'::box3d  and name!='' ORDER BY location <-> st_setsrid(st_makepoint("+lng+","+lat+"), 4326) LIMIT 1";
					//System.out.println(query);
					ResultSet rs1 = stmt.executeQuery(query);
					// Iterate through the ResultSet, displaying two values
					// for each row using the getString method
					String get_radius="";
					String gisfeature_name="";
					if(rs1.next()){
						//System.out.println("Name= " + rs.getString("name") + " Code= " + rs.getString("featureId"));
						get_radius=rs1.getString("d");
						loc_name=rs1.getString("name");
						loc_code=rs1.getString("featureid");
						
						if(Double.parseDouble(get_radius) <= radius)
						{
							LatLng item = new LatLng(lat, lng, loc_name, loc_code,get_radius,gisfeature_name);
							latlngData.add(item);
						}
						else
						{
							if(Double.parseDouble(get_radius) < 5000)
							{
								LatLng item = new LatLng(lat, lng, loc_name, loc_code,get_radius,gisfeature_name);
								latlngData.add(item);
							}
							else
							{
								LatLng item = new LatLng(lat, lng, "-", "-","-","-");
								latlngData.add(item);
							}
						}
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else{
			System.out.print("Failed to make connection");
		}
	}
	
	//========pop by code array
	public class_pop_location(ArrayList<LatLng> codeArray){
		
		if(gis_con.connection !=null){
			//System.out.println("You made it, take control your database now!");
			//Create a Statement class to execute the SQL statement
		    try {
				Statement stmt = gis_con.connection.createStatement();
				
				
				for(LatLng data : codeArray){
					//System.out.println("data : "+data.getLat());
					//System.out.println("data : "+data.getLng());
					code=data.getLocationCode();
												
			
					//Execute the SQL statement and get the results in a Resultset
					
					String query="SELECT name,astext(location) as lnglat,featureid FROM gisfeature where featureid="+code;
					//System.out.println(query);
					ResultSet rs1 = stmt.executeQuery(query);
					// Iterate through the ResultSet, displaying two values
					// for each row using the getString method
			 
					while (rs1.next()){
						
						loc_name=rs1.getString("name");
				    	loc_code=rs1.getString("featureid");
				    	String lnglat="";
				    	lnglat=rs1.getString("lnglat");
				    	//System.out.println(lnglat);
				    
				    	
				    	// String to split. 			    	
				    	String[] temp1;
				    	// delimiter 
				    	String delimiter1 = " ";
				    	String delimiter2 = "("; 
				    	String delimiter3 = ")";
				    	// given string will be split by the argument delimiter provided. 
				    	temp1 = lnglat.split(delimiter1);
				    	// print substrings 
				    	
				    	String[] lng1;String[] lat1;
				    	//System.out.println(temp1[0]+" & "+temp1[1]);			    
				    	String lng2=temp1[0];
				    	lng1=lng2.split("\\(");  //(lng at 1
				    	lng=lng1[1];
				    	String lat2=temp1[1];
				    	lat1=lat2.split("\\)");  //(lat 0
				    	lat=lat1[0];
						
						LatLng item = new LatLng(lat, lng, loc_name, loc_code,"-","-");
						latlngData.add(item);
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else{
			System.out.print("Failed to make connection");
		}
	}

	public ArrayList<String> Data(){
		ArrayList<String> values = new ArrayList<String>();
		String final_data=String.valueOf(lat)+":"+String.valueOf(lng)+":"+loc_name+":"+loc_code;
		//values.add(String.valueOf(lat));
		//values.add(String.valueOf(lng));
		values.add(final_data);
		return values;
	}	
}
