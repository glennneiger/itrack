package com.iespl.gisgraphy;
import java.util.ArrayList;
import java.util.regex.*;
import java.sql.*;

public class class_pop_junction {
	String lat;
	String lng;
	String code;
	String churaha_name="";
	String churaha_code="";
	
	private ArrayList<LatLng >  latlngData = new ArrayList<LatLng>();
	
	public ArrayList<LatLng> getLatlngData() {
		return latlngData;
	}
	
	gis_connection gis_con = new gis_connection();
	//========pop by lat lng
	public class_pop_junction(String lt, String lg){
		lat=lt;
		lng=lg;
		//System.out.println("Latitude:"+lat);
		//System.out.println("Longitude:"+lng);		
		/*try{
			Class.forName("org.postgresql.Driver");
		}catch(ClassNotFoundException e){
			//System.out.println("Where is your PostgreSQL JDBC Driver? Included in your library path!");
			e.printStackTrace();
			return;
		}
		//System.out.println("PostgreSQL JDBC Driver Registered!");
		Connection connection=null;
		try{
			//connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/gisgraphy","postgres","neon04$");
			connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5433/gisgraphy","postgres","neon04$");
			//connection = DriverManager.getConnection("jdbc:postgresql://52.74.144.159:5432/gisgraphy","postgres","neon04$");
		}catch(SQLException e){
			//System.out.println("Connection Failed!Check output console");
			e.printStackTrace();
			return;
		}*/
		if(gis_con.connection !=null){
			//System.out.println("You made it, take control your database now!");
			//Create a Statement class to execute the SQL statement
		    try {
				Statement stmt = gis_con.connection.createStatement();
				 //Execute the SQL statement and get the results in a Resultset
				String query="SELECT id, name, astext(location) as lnglat,featureid, CAST (st_distance_sphere(location, st_setsrid(st_makepoint("+lng+","+lat+"),4326)) AS INT) AS d FROM road ORDER BY location <-> st_setsrid(st_makepoint("+lng+","+lat+"), 4326)  LIMIT 1";
			    ResultSet rs1 = stmt.executeQuery(query);
			    // Iterate through the ResultSet, displaying two values
			    // for each row using the getString method
			 
			    while (rs1.next()){
			        //System.out.println("Name= " + rs.getString("name") + " Code= " + rs.getString("featureId"));
			    	churaha_name=rs1.getString("name");
			    	churaha_code=rs1.getString("featureId");
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
	//========pop by latlng array
	public class_pop_junction(ArrayList<LatLng> latlngArray,int radius){
			
		try{
			Class.forName("org.postgresql.Driver");
		}catch(ClassNotFoundException e){
			//System.out.println("Where is your PostgreSQL JDBC Driver? Included in your library path!");
			e.printStackTrace();
			return;
		}
		//System.out.println("PostgreSQL JDBC Driver Registered!");
		Connection connection=null;
		try{
			//connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/gisgraphy","postgres","neon04$");
			connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5433/gisgraphy","postgres","neon04$");
			//connection = DriverManager.getConnection("jdbc:postgresql://52.74.144.159:5432/gisgraphy","postgres","neon04$");
		}catch(SQLException e){
			//System.out.println("Connection Failed!Check output console");
			e.printStackTrace();
			return;
		}
		if(connection !=null){
			//System.out.println("You made it, take control your database now!");
			//Create a Statement class to execute the SQL statement
		    try {
				Statement stmt = connection.createStatement();
				
				
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
					String query="SELECT id, name, astext(location) as lnglat,featureId, CAST (st_distance_sphere(location, st_setsrid(st_makepoint("+lng+","+lat+"),4326)) AS INT) AS d FROM road WHERE location && 'BOX3D("+lng_minus+" "+lat_minus+","+lng_plus+" "+lat_plus+")'::box3d and name!='' ORDER BY location <-> st_setsrid(st_makepoint("+lng+","+lat+"), 4326)  LIMIT 1";
					//String query="SELECT id, name, astext(location) as lnglat,featureId, CAST (st_distance_sphere(location, st_setsrid(st_makepoint("+lng+","+lat+"),4326)) AS INT) AS d FROM road WHERE name!='' ORDER BY location <-> st_setsrid(st_makepoint("+lng+","+lat+"), 4326)  LIMIT 1";
					//String query="SELECT id, name, astext(location) as lnglat,featureId FROM road ORDER BY location <-> st_setsrid(st_makepoint("+lng+","+lat+"), 4326)  LIMIT 1";
					//System.out.println(query);
					//long starttime  = System.currentTimeMillis();
					long starttime  =System.nanoTime();
					ResultSet rs1 = stmt.executeQuery(query);
					//long endtime  = System.currentTimeMillis();
					long endtime  = System.nanoTime();
					//System.out.println("start time latlng to get"+(endtime-starttime));
					// Iterate through the ResultSet, displaying two values
					// for each row using the getString method
					String get_radius="";
					while (rs1.next()){
						//System.out.println("Name= " + rs.getString("name") + " Code= " + rs.getString("featureId"));
						churaha_name=rs1.getString("name");
						churaha_code=rs1.getString("featureId");
						get_radius=rs1.getString("d");
						//System.out.println("radius=:"+get_radius);
						//System.out.println("end time latlng to get"+System.currentTimeMillis());
						if(Double.parseDouble(get_radius) <= radius)
						{
							LatLng item = new LatLng(lat, lng, churaha_name, churaha_code,get_radius,"-");
							latlngData.add(item);
						}
						else
						{
							if(Double.parseDouble(get_radius) < 5000)
							{
								LatLng item = new LatLng(lat, lng, churaha_name, churaha_code,get_radius,"-");
								latlngData.add(item);
							}
							LatLng item = new LatLng(lat, lng, "-", "-","-","-");
							latlngData.add(item);
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
	//===================pop by by code===========
	
	public class_pop_junction(String cde){
		code=cde;
		/*try{
			Class.forName("org.postgresql.Driver");
		}catch(ClassNotFoundException e){
			//System.out.println("Where is your PostgreSQL JDBC Driver? Included in your library path!");
			e.printStackTrace();
			return;
		}
		//System.out.println("PostgreSQL JDBC Driver Registered!");
		Connection connection=null;
		try{
			//connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/gisgraphy","postgres","neon04$");
			connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5433/gisgraphy","postgres","neon04$");
			//connection = DriverManager.getConnection("jdbc:postgresql://52.74.144.159:5432/gisgraphy","postgres","neon04$");
		}catch(SQLException e){
			//System.out.println("Connection Failed!Check output console");
			e.printStackTrace();
			return;
		}*/
		if(gis_con.connection !=null){
			//System.out.println("You made it, take control your database now!");
			//Create a Statement class to execute the SQL statement
		    try {
				Statement stmt = gis_con.connection.createStatement();
				 //Execute the SQL statement and get the results in a Resultset
				String query="SELECT name,astext(location) as lnglat,featureId FROM road where featureid="+code;
			    ResultSet rs1 = stmt.executeQuery(query);
			    // Iterate through the ResultSet, displaying two values
			    // for each row using the getString method
			 
			    while (rs1.next()){
			        //System.out.println("Name= " + rs.getString("name") + " Code= " + rs.getString("featureId"));
			    	churaha_name=rs1.getString("name");
			    	churaha_code=rs1.getString("featureId");
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
	public class_pop_junction(ArrayList<LatLng> codeArray){
			
		/*try{
			Class.forName("org.postgresql.Driver");
		}catch(ClassNotFoundException e){
			//System.out.println("Where is your PostgreSQL JDBC Driver? Included in your library path!");
			e.printStackTrace();
			return;
		}
		//System.out.println("PostgreSQL JDBC Driver Registered!");
		Connection connection=null;
		try{
			//connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/gisgraphy","postgres","neon04$");
			connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5433/gisgraphy","postgres","neon04$");
			//connection = DriverManager.getConnection("jdbc:postgresql://52.74.144.159:5432/gisgraphy","postgres","neon04$");
		}catch(SQLException e){
			//System.out.println("Connection Failed!Check output console");
			e.printStackTrace();
			return;
		}*/
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
					
					String query="SELECT name,astext(location) as lnglat,featureId FROM road where featureid="+code;
					//System.out.println(query);
					ResultSet rs1 = stmt.executeQuery(query);
					// Iterate through the ResultSet, displaying two values
					// for each row using the getString method
			 
					while (rs1.next()){
						
						churaha_name=rs1.getString("name");
				    	churaha_code=rs1.getString("featureId");
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
						
						LatLng item = new LatLng(lat, lng, churaha_name, churaha_code,"-","-");
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
	//===================end pop by by code===========
	
	public ArrayList<String> Data(){
		ArrayList<String> values = new ArrayList<String>();
		String final_data=String.valueOf(lat)+":"+String.valueOf(lng)+":"+churaha_name+":"+churaha_code;
		//values.add(String.valueOf(lat));
		//values.add(String.valueOf(lng));
		values.add(final_data);
		return values;
	}
	
}
