package com.iespl.gisgraphy;
import java.util.ArrayList;
import java.util.regex.*;
import java.sql.*;

public class class_pop_road_location {
	String lat;
	String lng;
	String code;
	String road_name="";
	String road_code="";
	
	private ArrayList<LatLng >  latlngData = new ArrayList<LatLng>();
	
	public ArrayList<LatLng> getLatlngData() {
		return latlngData;
	}
	gis_connection gis_con = new gis_connection();
	
	//========pop by latlng array
	public class_pop_road_location(ArrayList<LatLng> latlngArray,int radius){		
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
					String query="SELECT id, name, astext(location) as lnglat,gid, isin,CAST (st_distance_sphere(shape, st_setsrid(st_makepoint("+lng+","+lat+"),4326)) AS INT) AS d FROM openstreetmap WHERE shape && 'BOX3D("+lng_minus+" "+lat_minus+","+lng_plus+" "+lat_plus+")'::box3d  and name!='' ORDER BY shape <-> st_setsrid(st_makepoint("+lng+","+lat+"), 4326)  LIMIT 1";
					//System.out.println(query);
					ResultSet rs1 = stmt.executeQuery(query);
					// Iterate through the ResultSet, displaying two values
					// for each row using the getString method
					String get_radius="";
					String gisfeature_name="";
					if(rs1.next()){
						//System.out.println("Name= " + rs.getString("name") + " Code= " + rs.getString("featureId"));
						get_radius=rs1.getString("d");
						road_name=rs1.getString("name");
						road_code=rs1.getString("gid");
						//getting location
						String query_loc="SELECT  name,CAST (st_distance_sphere(location, st_setsrid(st_makepoint("+lng+","+lat+"),4326)) AS INT) AS d_loc  FROM gisfeature WHERE location && 'BOX3D("+lng_minus+" "+lat_minus+","+lng_plus+" "+lat_plus+")'::box3d  and name!='' ORDER BY location <-> st_setsrid(st_makepoint("+lng+","+lat+"), 4326) LIMIT 1";
						ResultSet rs_loc = stmt.executeQuery(query_loc);
						
						if(rs_loc.next())						
						{
							//System.out.println("vvvvvvvvvvvvv="+rs_loc.getString("d_loc"));
							if(Double.parseDouble(rs_loc.getString("d_loc")) <= 200)
							{
								gisfeature_name=rs_loc.getString("name");
							}
						}
						if(Double.parseDouble(get_radius) <= radius)
						{
							LatLng item = new LatLng(lat, lng, road_name, road_code,get_radius,gisfeature_name);
							latlngData.add(item);
						}
						else
						{
							if(Double.parseDouble(get_radius) < 5000)
							{
								LatLng item = new LatLng(lat, lng, road_name, road_code,get_radius,gisfeature_name);
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
	
	

	public ArrayList<String> Data(){
		ArrayList<String> values = new ArrayList<String>();
		String final_data=String.valueOf(lat)+":"+String.valueOf(lng)+":"+road_name+":"+road_code;
		//values.add(String.valueOf(lat));
		//values.add(String.valueOf(lng));
		values.add(final_data);
		return values;
	}	
}
