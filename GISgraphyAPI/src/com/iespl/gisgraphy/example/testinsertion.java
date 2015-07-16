package com.iespl.gisgraphy.example;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.iespl.gisgraphy.*;
public class testinsertion {
	
	public static void main(String[] argv){
		gis_connection gis_con = new gis_connection();
		if(gis_con.connection !=null){
			try{
				Statement stmt = gis_con.connection.createStatement();
				String query="INSERT into road (id,name,location,source,featureid)values(5,'testloc5','SRID=4326;POINT(80.23 24.89)','PERSONAL',1005 )";
			    //System.out.println("SQL="+query);
				int okInsert=stmt.executeUpdate(query);
				//System.out.println(okInsert);
				if(okInsert==1)
				{
					System.out.println("Added Successfully");
				}
				stmt.close();				
				gis_con.connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
