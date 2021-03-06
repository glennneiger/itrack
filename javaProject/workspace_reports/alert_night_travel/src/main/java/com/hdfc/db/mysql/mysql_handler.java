package com.hdfc.db.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hdfc.init.init;

public class mysql_handler {
	

	//############ ALERT STATUS
	public static String getVehicleInformation(connection conn, int account_id)
	{
	   /*conn.stmt = null;
	   //int vehicle_id = 0;
	   //float max_speed = 0.0f;
	   //String device_imei_no ="", vehicle_name="";
	   try{
	      //STEP 2: Register JDBC driver

	      //STEP 4: Execute a query
	//    System.out.println("Selecting data...");
		  conn.stmt = conn.conn.createStatement();

	      String sql;
	      sql = "SELECT DISTINCT vehicle.vehicle_id,vehicle.vehicle_name,vehicle.max_speed,vehicle_assignment.device_imei_no FROM vehicle,vehicle_assignment,"+
	    		  "vehicle_grouping WHERE vehicle.vehicle_id = vehicle_assignment.vehicle_id AND vehicle_assignment.vehicle_id = vehicle_grouping.vehicle_id AND "+
	    		  "vehicle_grouping.account_id="+account_id+" AND vehicle.status=1 AND vehicle_assignment.status=1 AND vehicle_grouping.status=1";
	      //System.out.println("SQL="+sql);
	      ResultSet rs = conn.stmt.executeQuery(sql);

	      //STEP 5: Extract data from result set
	      //init init_var = new init();
	      while(rs.next()){
	         //Retrieve by column name
	    	  //init.vehicle_id.add(rs.getInt("vehicle_id"));
	    	  init.vehicle_name.add(rs.getString("vehicle_name"));
	    	  init.max_speed.add(rs.getFloat("max_speed"));
	    	  init.device_imei_no.add(rs.getString("device_imei_no"));
	         //Display values
	         //System.out.print("device_imei_no=" + rs.getString("device_imei_no"));
	      }
	      //STEP 6: Clean-up environment
	      rs.close();
	      conn.stmt.close();
	      }catch(SQLException se2){}*/

	 	  init.vehicle_name.add("PB03Y7372");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021570411");
		
	 	  init.vehicle_name.add("MH02DN6584");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021569447");

	 	  init.vehicle_name.add("MH01BU3421");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021567698");

	 	  init.vehicle_name.add("MH02BR7310");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021571229");

	 	  init.vehicle_name.add("MH48 S 6248");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021563051");

	 	  init.vehicle_name.add("MH43AB8750");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021564257");

	 	  init.vehicle_name.add("MH02DN8274");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021563374");

	 	  init.vehicle_name.add("MH04DJ6758");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021563481");

	 	  init.vehicle_name.add("MH04GE6952");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021563622");

	 	  init.vehicle_name.add("MH03BS3616");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021564133");

	 	  init.vehicle_name.add("DL3CBC9978");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021571096");

	 	  init.vehicle_name.add("DL5CJ9616");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021569413");

	 	  init.vehicle_name.add("UP14CL2463");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021569173");

	 	  init.vehicle_name.add("up16aw2866");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021569959");

	 	  init.vehicle_name.add("DL.09.CAG.7982");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021571088");

	 	  init.vehicle_name.add("HR51AB9338");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021568787");

	 	  init.vehicle_name.add("UP14CH9882");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021571237");

	 	  init.vehicle_name.add("UP14CJ2833");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021564323");

	 	  init.vehicle_name.add("DL1CS4316");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021562939");

	 	  init.vehicle_name.add("HRCJ6083");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021564000");

	 	  init.vehicle_name.add("UP16AQ5640");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021563739");

	 	  init.vehicle_name.add("DL3CCC4392");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021563747");

	 	  init.vehicle_name.add("HR72B3024");
	 	  init.max_speed.add(80.0f);
	 	  init.device_imei_no.add("865733021564125");
	 	  
	   return null;
	}
	
}
