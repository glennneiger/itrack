
import java.sql.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import sun.misc.BASE64Decoder;

public class alert_module {
		   
   public static void get_escalation_detail()
   {
		System.out.println("IN GET ESCALATION DETAIL");
		String current_path = "",strLine1="";
		String imei_db="",imei_local="",imei_db_file="";
		boolean imei_db_matched = false;			
		
		try{					
			String temp_path = alert_variables.temp_path;
			//System.out.println("temp_path="+temp_path);
			String xml_file_temp;		
			File folder_temp = new File(temp_path);
			File[] listOfFiles_temp = folder_temp.listFiles();

			//System.out.println("listfilelen="+listOfFiles_temp.length);
			for (int t = 0; t < listOfFiles_temp.length; t++)
			{
				if (listOfFiles_temp[t].isFile()) 
				{
					xml_file_temp = listOfFiles_temp[t].getName();		/*****GET WITH EXTENSION .XML *****/
					//System.out.println("xml_file_temp="+xml_file_temp);
					String[] temp;
					String delimiter = "_";
					String[] temp2;
					String delimiter2 = "\\.";
					
					temp = xml_file_temp.split(delimiter);
					//System.out.println("Temp[0]="+temp[0]);
					if(temp[0].equals("escalation"))
					{																
						temp2 = temp[1].split(delimiter2);
						
						imei_db = temp2[0];
						//System.out.println("imei_db="+imei_db);		
						
						alert_variables.assigned_imei.put(imei_db, imei_db);						
						alert_variables.alert_halt1_start_flag.put(imei_db,false);
						alert_variables.alert_halt2_start_flag.put(imei_db,false);
						alert_variables.alert_movement_flag.put(imei_db,false);
						alert_variables.alert_nogps_flag.put(imei_db,false);
						alert_variables.alert_battery_disconnected_flag.put(imei_db,false);
						alert_variables.alert_exited_region_flag.put(imei_db,false);
												
						//alert_variables.vehicle_name.put(imei_db,"");
						alert_variables.imei.put(imei_db,"");
						alert_variables.datetime.put(imei_db,"");
						alert_variables.sts.put(imei_db,"");
						alert_variables.lat.put(imei_db,0.0);
						alert_variables.lng.put(imei_db,0.0);
						alert_variables.speed.put(imei_db,0.0f);
						alert_variables.io1.put(imei_db,0.0f);
						alert_variables.sup_v.put(imei_db,0.0f);

						alert_variables.region_code.put(imei_db,"");
						alert_variables.region_name.put(imei_db,"");
						alert_variables.region_coord.put(imei_db,"");
																								
						get_device_info(imei_db);		/********** GET ALERT DEVICE INFORMATION *************/							
					}
				}
			}			
		}catch (Exception e) { 
			System.out.println("EXCEPTION IN GETTING ASSIGNED TEMP FILE IMEI:"+e.getMessage()); 
		}
		
		//####### READ TRIP DETAIL
		read_trip_detail();
		//########################
		
		//######## STORE CURRENT TIME
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
		Date now = new Date();
		String current_time = sdfDate.format(now);
		alert_variables.last_datetime = current_time;
		//########################
	}
		
		
	//public static void write_final_alert_data(String imei, String DateTime, String ServerTS, String lat, String lng, String Speed, String io_value1, String io_value2, String io_value3, String io_value4, String io_value5, String io_value6, String io_value7, String io_value8, String SupplyVoltage)
	public static void write_final_alert_data(String imei, String DateTime, String ServerTS, String lat, String lng, String Speed, String io_value1, String SupplyVoltage)
	{					
		//System.out.println("WriteFinalAlert");
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
		Date now = new Date();
		String current_time = sdfDate.format(now);
		
		//System.out.println("current_time="+current_time+" ,alert_variables.last_datetime="+alert_variables.last_datetime);
		if(calculateTimeDiff(alert_variables.last_datetime,current_time)>30)
		{
			read_trip_detail();
			alert_variables.last_datetime = current_time;
		}
		
		try {
				//System.out.println("alert_variables.assigned_imei.get(imei)="+alert_variables.assigned_imei.get(imei)+" ,alert_variables.trip_status.get(imei)="+alert_variables.trip_status.get(imei));				
				if( (alert_variables.assigned_imei.get(imei)!=null) && (alert_variables.trip_status.get(imei)!=null))
				{										
					//System.out.println("AssignedIME-OK");
					//alert_variables.imei.put(imei,imei) = vserial;
					alert_variables.datetime.put(imei, DateTime);
					alert_variables.sts.put(imei, ServerTS);
					lat = lat.substring(0,lat.length()-1);
					lng = lng.substring(0,lng.length()-1);
					//System.out.println("Lat="+lat+" ,Lng="+lng);
					
					alert_variables.lat.put(imei, Double.parseDouble(lat));
					alert_variables.lng.put(imei, Double.parseDouble(lng));
					
					alert_variables.speed.put(imei, Float.parseFloat(Speed));
					alert_variables.io1.put(imei, Float.parseFloat(io_value1));					
					alert_variables.sup_v.put(imei, Float.parseFloat(SupplyVoltage));
					//System.out.println("Debug2");
					//#### CALL FINAL PROCESS ALERTS #########//									
					System.out.println("alert_variables.trip_startdate.get(imei)="+alert_variables.trip_startdate.get(imei)+" ,alert_variables.sts.get(imei)="+alert_variables.sts.get(imei));	
					if(calculateTimeDiff(alert_variables.trip_startdate.get(imei),alert_variables.sts.get(imei)) > 0)
					{					
						process_alerts(imei);
						//#########################################					
						//System.out.println("BEFOE GET DEVICE INFO: av.imei="+imei+" ,av.datetime="+alert_variables.datetime.get(imei)+" ,av.sts="+alert_variables.sts.get(imei)+" ,av.lat="+alert_variables.lat.get(imei)+" ,av.lng="+alert_variables.lat.get(imei)+" ,av.speed="+alert_variables.speed.get(imei)+" ,av.io1="+alert_variables.io1.get(imei)+" ,av.io2="+alert_variables.io2.get(imei)+" ,av.io3.get(imei)="+alert_variables.io3.get(imei)+" ,av.io4="+alert_variables.io4.get(imei)+" ,av.io5="+alert_variables.io5.get(imei)+" ,av.io6="+alert_variables.io6.get(imei)+" ,av.io7="+alert_variables.io7.get(imei)+" ,av.io8="+alert_variables.io8.get(imei)+" ,av.sup_v="+alert_variables.sup_v.get(imei));
						System.out.println("AFTER PROCESS ALERT");
					}
				}
			}
			catch(Exception e) {
					System.out.println("EXCEPTION IN READING -THE LINE OF LAST LOCATION XML FILE:"+e.getMessage());
				}
		//########### LAST LOCATION FILE READING CLOSED ##########################//			
	}	
	
	
	/**** GET DEVICE INFO - METHOD BODY **********/
	public static void get_device_info(String imei)
	{
		System.out.println("**IN DEVICE INFO");
		String Command;
		String alertName="";
		String normal_variable_file="", normal_variable_path ="", escalation_file = "", escalation_path = "";
		String landmark_file ="", landmark_path = "", region_file ="", region_path = "";
		int i=0;
		String[] temp;
											
		//CHECK IF TEMPORARY VARIABLES FILE EXISTS		
		escalation_file = "escalation_"+imei; 		//#### MULTIPLE ENTREIS
		escalation_path = alert_variables.root_dir+"/temp_variables_itc/"+escalation_file+".xml";
		
		landmark_file = "landmark_"+alert_variables.account_id; 			//#### MULTIPLE ENTREIS
		landmark_path = alert_variables.root_dir+"/temp_variables_itc/"+landmark_file+".xml";

		region_file = "region_"+imei;				//#### MULTIPLE ENTREIS
		region_path = alert_variables.root_dir+"/temp_variables_itc/"+region_file+".xml";
															
		//System.out.println("EscalationPath="+escalation_path);
		get_alert_configuration(normal_variable_path, escalation_path, landmark_path, region_path, imei);  	/****** CALL ALERT CONFIGURATION METHOD ******/
		
	} // METHOD CLOSED
	
	/********* GET ALERT CONFIGURATION - METHOD BODY *************/
	public static void get_alert_configuration(String normal_variable_path, String escalation_path, String landmark_path, String region_path, String imei)		/*** ALERT CONFIGURATION METHOD BODY *******/
	{
		System.out.println("IN GET_ALERT_CONFIGURATION");				
				
		String vehicle_id_tmp="", vehicle_name_tmp="", halt_start_time ="", halt_stop_time="", max_speed="", engine_io_no="", sos_io_no="", door1_io_no="", door2_io_no="", ac_io_no=""; 				
		String nearest_landmark = "";						
				
		//##### READ ESCALATION VARIABLE FILE -SET IT TO ALERT VARIABLES #########/
		escalation_read_set_variables(escalation_path, landmark_path, region_path, imei);
		//##### ---------------------------------------------------------#########/		
	}
				
	/*********** PROCESS ALERT - METHOD BODY **************/
	public static void process_alerts(String imei)
	{		
		System.out.println("In Process Alert");			
		//############## CALL PROCESSES #########################		
	/*	alert_variables.vehicle_name.put(imei, getXmlAttribute(strLine1,"vehicle_name=\"[^\"]+"));
		alert_variables.DFG.put(imei, getXmlAttribute(strLine1,"DFG=\"[^\"]+"));
		alert_variables.S120.put(imei, getXmlAttribute(strLine1,"S120=\"[^\"]+"));
		alert_variables.S30.put(imei, getXmlAttribute(strLine1,"S30=\"[^\"]+"));
		alert_variables.ND.put(imei, getXmlAttribute(strLine1,"ND=\"[^\"]+"));
		alert_variables.RD.put(imei, getXmlAttribute(strLine1,"RD=\"[^\"]+"));
		alert_variables.NG60.put(imei, getXmlAttribute(strLine1,"NG60=\"[^\"]+"));
		alert_variables.DD30.put(imei, getXmlAttribute(strLine1,"DD30=\"[^\"]+"));
		alert_variables.RR.put(imei, getXmlAttribute(strLine1,"RR=\"[^\"]+"));
		alert_variables.FS.put(imei, getXmlAttribute(strLine1,"FS=\"[^\"]+"));
		alert_variables.trip_id.put(imei, getXmlAttribute(strLine1,"trip_id=\"[^\"]+"));
		alert_variables.source_coord.put(imei, getXmlAttribute(strLine1,"source_coord=\"[^\"]+"));
		alert_variables.dest_coord.put(imei, getXmlAttribute(strLine1,"dest_coord=\"[^\"]+"));
		alert_variables.trip_startdate.put(imei, getXmlAttribute(strLine1,"trip_startdate=\"[^\"]+"));
		alert_variables.trip_status.put(imei, trip_status);
		alert_variables.transporter.put(imei, getXmlAttribute(strLine1,"transporter=\"[^\"]+"));
		alert_variables.driver_mobile.put(imei, getXmlAttribute(strLine1,"driver_mobile=\"[^\"]+"));*/
		String q="\"";
				
		/*try{
			if(alert_variables.DFG.get(imei).equals("1"))
			{
				if(alert_variables.alert_exited_region_flag.get(imei))
				{
					//System.out.println(">>EXITED REGION");
					String alert_type = "DFG";
					alert_exited_region_process(imei, alert_type);
				}
			}
			else
			{
				String alert_type = "DFG";
				int exited_region_stop = 0;
				String line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" exited_region_status="+q+exited_region_stop+q+"/>";
				close_alert_db_session(imei, line, alert_type);		
			}
		}catch(Exception e) {System.out.println("Errr:ExitedRegionAlert="+e.getMessage());}*/

		try{
			if(alert_variables.S120.get(imei).equals("1"))
			{
				if(alert_variables.alert_halt1_start_flag.get(imei))
				{
					//System.out.println(">>S120 START");
					String alert_type = "S120";
					alert_halt1_start_process(imei, alert_type);
				}
			}
			else
			{
				//System.out.println(">>S120 STOP");
				String alert_type = "S120";
				int halt1_status = 0;
				String line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" halt1_status="+q+halt1_status+q+"/>";
				close_alert_db_session(imei, line, alert_type);
				
				if(alert_variables.temp_alert_halt1_start.get(imei)!=null)
				{
					line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" halt1_status="+q+halt1_status+q+"/>"; 
					alert_variables.temp_alert_halt1_start.put(imei,null);
					alert_variables.repetitive_alert_halt1_start_time.put(imei,null);
					alert_variables.repetitive_alert_halt1_start_location.put(imei,null);
					alert_variables.repetitive_alert_halt1_start_landmark.put(imei,null);					
				}
			}
		}catch(Exception e) {System.out.println("Errr:Halt1Start_Alert="+e.getMessage());}

		try{
			if(alert_variables.S30.get(imei).equals("1"))
			{				
				if(alert_variables.alert_halt2_start_flag.get(imei))
				{
					//System.out.println(">S30 START");
					String alert_type = "S30";
					alert_halt2_start_process(imei, alert_type);
				}
			}
			else
			{
				//System.out.println(">S30 STOP");
				String alert_type = "S30";
				int halt2_status = 0;
				String line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" halt2_status="+q+halt2_status+q+"/>";
				close_alert_db_session(imei, line, alert_type);
				
				if(alert_variables.temp_alert_halt2_start.get(imei)!=null)
				{
					line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" halt2_status="+q+halt2_status+q+"/>"; 
					alert_variables.temp_alert_halt2_start.put(imei, null);
					alert_variables.repetitive_alert_halt2_start_time.put(imei,null);
					alert_variables.repetitive_alert_halt2_start_location.put(imei,null);
					alert_variables.repetitive_alert_halt2_start_landmark.put(imei,null);						
				}
				
			}
		}catch(Exception e) {System.out.println("Errr:Halt2Start_Alert="+e.getMessage());}
		

		try{
			if(alert_variables.ND.get(imei).equals("1"))
			{
				if(alert_variables.alert_movement_flag.get(imei))
				{
					//System.out.println(">>ND START");
					String alert_type = "ND";
					alert_movement_process(imei, alert_type);
				}
			}
			else
			{
				//System.out.println(">S30 STOP");
				String alert_type = "ND";
				int movement_status = 0;
				String line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" movement_status="+q+movement_status+q+"/>";
				close_alert_db_session(imei, line, alert_type);
				
				if(alert_variables.temp_alert_movement.get(imei)!=null)
				{
					line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" movement_status="+q+movement_status+q+"/>"; 
					alert_variables.temp_alert_movement.put(imei, line);
					alert_variables.repetitive_alert_halt1_start_time.put(imei,null);
					alert_variables.repetitive_alert_halt1_start_location.put(imei,null);
					alert_variables.repetitive_alert_halt1_start_landmark.put(imei,null);				
				}				
			}
		}catch(Exception e) {System.out.println("Errr:Movement_Alert="+e.getMessage());}
		
		
		try{
			if(alert_variables.RD.get(imei).equals("1"))
			{
				if(alert_variables.alert_battery_disconnected_flag.get(imei))
				{
					//System.out.println(">>RD START");
					String alert_type = "RD";
					alert_battery_disconnected_process(imei, alert_type);
				}
			}
			else
			{
				//System.out.println(">>RD STOP");
				String alert_type = "RD";
				int battery_disconnected_status = 0;
				String line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" battery_disconnected_status="+q+battery_disconnected_status+q+"/>";
				close_alert_db_session(imei, line, alert_type);
				
				if(alert_variables.temp_alert_battery_disconnected.get(imei)!=null)
				{
					line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" battery_disconnected_status="+q+battery_disconnected_status+q+"/>"; 
					alert_variables.temp_alert_battery_disconnected.put(imei, line);
					alert_variables.repetitive_alert_battery_disconnected_time.put(imei,null);
					alert_variables.repetitive_alert_battery_disconnected_location.put(imei,null);
					alert_variables.repetitive_alert_battery_disconnected_landmark.put(imei,null);					
				}				
			}
		}catch(Exception e) {System.out.println("Errr:BattrDisConct Alert="+e.getMessage());}
		
		try{
			if(alert_variables.NG60.get(imei).equals("1"))
			{
				if(alert_variables.alert_nogps_flag.get(imei))
				{
					//System.out.println(">>NG60 START");
					String alert_type = "NG60";
					alert_nogps_process(imei, alert_type);
				}
			}
			else
			{
				//System.out.println(">>NG60 STOP");
				String alert_type = "NG60";
				int nogps_status = 0;
				String line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" nogps_status="+q+nogps_status+q+"/>";
				close_alert_db_session(imei, line, alert_type);
				
				if(alert_variables.temp_alert_nogps.get(imei)!=null)
				{
					line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" nogps_status="+q+nogps_status+q+"/>"; 
					alert_variables.temp_alert_nogps.put(imei, line);
					alert_variables.repetitive_alert_nogps_time.put(imei,null);
					alert_variables.repetitive_alert_nogps_location.put(imei,null);
					alert_variables.repetitive_alert_nogps_landmark.put(imei,null);										
				}				
				
			}
		}catch(Exception e) {System.out.println("Errr:NOGPS_Alert="+e.getMessage());}		
	}
	
	
	//###### CLOSE ALERT
	public static void close_alert_db_session(String imei, String line, String alert_type)
	{
		String alert_str = read_database_alert_status(imei, line, alert_type);
		if(alert_str!=null)
		{
			//System.out.println("UPDATE_DB_SOS");
			update_database_alert_status(imei, line, alert_type);
		}
		else
		{
			//System.out.println("INSERT_DB_SOS");
			insert_database_alert_status(imei, line, alert_type);
		}
		System.out.println("After Alert Close Session");
	}
	/************** PROCESS- ALERT EXITED REGION ***************/
	public static void alert_exited_region_process(String imei,String alert_type)
	{
		//System.out.println("IN DOOR-1 OPEN");			//ENGINE IO DOES NOT NEED TO FETCH FROM FILE
		//String alert_type = "exited_region";
		String alert_string = "", folder_date="", current_path ="", current_file="", location="", nearest_landmark="" ,send_msg_path ="", send_msg_date="";
		String[] main_temp;
		String[] temp1;
		String[] temp2;
		String[] temp3;
		String[] region_coord;
		String[] source_coord;
		String[] dest_coord;
		//float engine_io_value = 0.0f;
		//float threshold_distance = 0.300f;	//300 meters
		double threshold_distance = 0.30000;	//300 meters		
		int threshold_time = 2;  //in minutes
		int repetitive_threshold_time = 30;
		String line ="", msg="", exited_region_start="1", exited_region_stop="0";
		String q="\"";
		
		String mobile_no_device="",alert_name_device="",person_name_device="",email_device="";
		String sms_status="",mail_status="",region_lat="",region_lng="",region_name="";
		String strLine1="", prev_imei="", prev_date="", prev_sts ="", prev_lat = "", prev_lng = "", prev_exited_region_status = "", prev_location ="",prev_nearest_landmark="";
		String transporter_name="",driver_mobile="";
		int prev_exited_region_numeric = 0;
		/*//###### GET DB ENGINE IO ###########/
		engine_io_value = get_io(av, "engine");
		//av.engine_io = engine_io;*/
		main_temp = alert_variables.alert_exited_region.get(imei).split("#");
		
		//####### SPLIT ALERT STRING OF DATABASE AND GET INDIVIDUAL VARIABLES #######/
		//System.out.println("Door1_Open_SizeMainTemp="+main_temp.length+", main_temp_string="+alert_variables.alert_door1_open.get(imei));
			
		//System.out.println("DOOR1_OPEN: mobile_no="+mobile_no_device+" ,alert_duration="+alert_duration_device+" ,alert_id="+alert_id_device+" ,alert_name="+alert_name_device+" ,escalation_id="+escalation_id_device+" ,person_name="+person_name_device+" ,email="+email_device+", mail_status="+mail_status+", sms_status="+sms_status);
							
		//########## MAKE NEW DATE FOLDER -IF FOLDER DOES NOT EXISTS ########/
		temp3= alert_variables.sts.get(imei).split(" ");
		send_msg_date = temp3[0];			
		String mydir2 = alert_variables.root_dir+"/send_messages";
		boolean success2 = (new File(mydir2 + "/" + send_msg_date)).mkdir();
		send_msg_path = alert_variables.root_dir+"/send_messages/"+send_msg_date+"/"+imei+".xml";			
		
		//########## CHECK IF VARIABLE EXISTS #########/		
		
		//System.out.println("SizeDoorOpen="+alert_variables.temp_alert_door1_open.get(imei));
		if (alert_variables.temp_alert_exited_region.get(imei)==null) 											/****** CREATE FILE -IF FILE DOES NOT EXIST *********/
		{
			//System.out.println("alert_variables.door1_io_value.get(imei)="+alert_variables.door1_io_value.get(imei));
			String alert_str = read_database_alert_status(imei, alert_string, alert_type);
			if(alert_str==null)
			{
				//######## CREATE CURRENT FILE ###########/
				line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" exited_region_status="+q+exited_region_stop+q+"/>"; 
				alert_variables.temp_alert_exited_region.put(imei, line);
				//write_file_string(current_path,line,"current");	
				//System.out.println("DOOR-1 OPEN: CURRENT CREATED");					
			}
			else
			{
				//######## CREATE CURRENT FILE ###########/
				//line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" door_open_status="+q+door_open_stop+q+"/>"; 
				//System.out.println("ALERT_EXISTS_DB_DOOR1_OPEN="+alert_str);
				alert_variables.temp_alert_exited_region.put(imei, alert_str);
				//write_file_string(current_path,line,"current");	
				//System.out.println("DOOR-1 OPEN: CURRENT CREATED");								
			}
		}
		else													/****** READ FILE -IF FILE EXISTS ********/
		{  
			//System.out.println("HASH_MAP_EXISTS");
			try {
				//##### READ ALL PARAMETERS OF PREVIOUS XML_DATA #####/					
				//strLine1 = read_file_string(current_path);		 /******* GET LINE STRING **********/
				strLine1 = alert_variables.temp_alert_exited_region.get(imei);
				
				//######### GET XML PARAMETERS ###########/
				prev_imei = getXmlAttribute(strLine1,"imei=\"[^\"]+");
				prev_date = getXmlAttribute(strLine1,"datetime=\"[^\"]+");
				prev_sts = getXmlAttribute(strLine1,"sts=\"[^\"]+");
				prev_lat = getXmlAttribute(strLine1,"lat=\"[^\"]+");					
				prev_lng = getXmlAttribute(strLine1,"lng=\"[^\"]+");
				prev_location = getXmlAttribute(strLine1,"location=\"[^\"]+");
				prev_location = getXmlAttribute(strLine1,"nearest_landmark=\"[^\"]+");
				
				prev_exited_region_status = getXmlAttribute(strLine1,"exited_region_status=\"[^\"]+");							
				prev_exited_region_numeric = Integer.parseInt(prev_exited_region_status);
				
				if( (prev_exited_region_numeric == 0) && (!alert_variables.exited_region_reset_flag) )
				{
					line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" exited_region_status="+q+exited_region_stop+q+"/>";
					update_alert_status(line, imei, q, alert_type);
					alert_variables.temp_alert_exited_region.put(imei, strLine1);
					alert_variables.exited_region_reset_flag = true;		
					
					prev_imei = imei;
					prev_date = alert_variables.datetime.get(imei);
					prev_sts = alert_variables.sts.get(imei);
					prev_lat = alert_variables.lat.get(imei).toString();					
					prev_lng = alert_variables.lng.get(imei).toString();
					prev_location = "";
					prev_nearest_landmark = "";											
					prev_exited_region_numeric = 0;												
				}
				
				//System.out.println("prev_door_open_status="+prev_door_open_status+" ,datetime.get(imei)="+alert_variables.datetime.get(imei));				
				//System.out.println("DoorOpenFinal:1=>,alert_variables.sts.get(imei)="+alert_variables.sts.get(imei)+", alert_variables.door1_io_value.get(imei)="+alert_variables.door1_io_value.get(imei));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try{
				if( (calculateTimeDiff(alert_variables.datetime.get(imei),alert_variables.sts.get(imei))<10) && (threshold_time>0) )
				{
					//System.out.println("DoorOpenFinal:2=>prev_sts="+prev_sts+" ,prev_door_status_numeric="+prev_door_status_numeric+" ,alert_variables.door1_io_value.get(imei)="+alert_variables.door1_io_value.get(imei));

					double lat_f = alert_variables.lat.get(imei);
					double lng_f = alert_variables.lng.get(imei);
					
					//######## CHECK TRIP BETWEEN SOURCE AND DESTINATION
					if( (alert_variables.source_coord.get(imei)!="") && (alert_variables.source_coord.get(imei)!="-") && (alert_variables.dest_coord.get(imei)!="") && ((alert_variables.dest_coord.get(imei)!="-")))
					{										
						source_coord = alert_variables.source_coord.get(imei).split(",");
						dest_coord = alert_variables.dest_coord.get(imei).split(",");
						
						Double lat_s = Double.parseDouble(source_coord[0].trim());
						Double lng_s = Double.parseDouble(source_coord[1].trim());
						Double lat_d = Double.parseDouble(dest_coord[0].trim());
						Double lng_d = Double.parseDouble(dest_coord[1].trim());
						double distance_source = calculateDistance(lat_f, lng_f, lat_s, lng_s);
						double distance_dest = calculateDistance(lat_f, lng_f, lat_d, lng_d);
						
						if((distance_source > threshold_distance) && (distance_dest > threshold_distance))
						{					
							if(alert_variables.repetitive_alert_exited_region_time.get(imei)==null)
							{
								alert_variables.repetitive_alert_exited_region_time.put(imei,prev_date);
							}
							
							region_coord = alert_variables.region_coord.get(imei).split(" ");
							region_lat = region_coord[0].trim();
							region_lng = region_coord[1].trim();
							//float lat_g1 = Float.parseFloat(region_lat);
							//float lng_g1 = Float.parseFloat(region_lng);
							
							Double lat_g = Double.parseDouble(region_lat);
							Double lng_g = Double.parseDouble(region_lng);
												
							region_name = alert_variables.region_name.get(imei);
									
							//float lat_f1 = (float) lat_f;
							//float lng_f1 = (float) lng_f;
		
							//double distance1 = calculateDistance(lat_f, lat_g1, lng_f1, lng_g1);
							double distance1 = calculateDistance(lat_f, lng_f, lat_g, lng_g);
									
							if( (calculateTimeDiff(prev_date,alert_variables.datetime.get(imei)) >= threshold_time) && (prev_exited_region_numeric ==0) && (distance1 >= threshold_distance))
							{
								//System.out.println("DoorOpenFinal:3");
								//##### UPDATE CURRENT FILE #######/								
								//########## GET MSG PARAMETERS
								location = get_url_location(lat_f, lng_f);							
								nearest_landmark = get_nearest_landmark(lat_f, lng_f, "landmark");
								if(location==null)
								{
									location = nearest_landmark;
								}
								String time_format_str = get_datetime_format(prev_date);
								String[] time_format_parts = time_format_str.split("#");
								
								long diff_minutes = calculateTimeDiff(prev_date,alert_variables.datetime.get(imei));
								//int t = 80;	//provide in minutes
								long hrs_duration = (long) Math.floor(diff_minutes / 60);
								long mins_duration = diff_minutes % 60;					
								//##################################
		
								//###### UPDATE SEND MESSAGE FILE ######/	
								//msg = "Your -vehicle : "+alert_variables.vehicle_name.get(imei)+": Detour From Geofence :"+alert_variables.region_name.get(imei)+" at "+alert_variables.datetime.get(imei);						
								//line = "\n<marker phone="+q+mobile_no_device+q+" vehicle_id="+q+av.vehicle_id+q+" alertid="+q+alert_id_device+q+" escalationid="+q+escalation_id_device+q+" sts="+q+av.sts+q+" datetime="+q+av.datetime+q+" message="+q+msg+q+" person="+q+person_name_device+q+"/>\n</t1>";					
								//for(int i=0;i<main_temp.length;i++)
								//{
									//alert_string = main_temp[i];
									//temp=split(alert_string,"#");
									//temp1 = alert_string.split("#");
									
									alert_name_device = main_temp[0];
									mobile_no_device = main_temp[1];
									email_device = main_temp[2];
									sms_status = main_temp[3];
									mail_status = main_temp[4];						
									
									//msg = "DFG at "+alert_variables.vehicle_name.get(imei)+": Detour From Geofence :"+alert_variables.region_name.get(imei)+" at "+alert_variables.datetime.get(imei);							
									msg = "DFG  at "+location+":"+alert_variables.vehicle_name.get(imei)+","+alert_variables.transporter_name.get(imei)+" since "+time_format_parts[0]+ "hrs on "+time_format_parts[1]+" "+time_format_parts[2]+" "+time_format_parts[3]+",Total Detour from Geofence is "+hrs_duration+" hrs "+mins_duration+" mins, "+nearest_landmark+","+alert_variables.driver_mobile;
									line = "\n<marker vehicle_name="+q+alert_variables.vehicle_name.get(imei)+q+" alert_type="+q+alert_type+q+" account_id="+q+alert_variables.account_id+q+" phone="+q+mobile_no_device+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" message="+q+msg+q+" trip_id="+q+alert_variables.trip_id.get(imei)+q+" email="+q+email_device+q+" sms_status="+q+sms_status+q+" mail_status="+q+mail_status+q+"/>\n</t1>";						
									write_file_string(send_msg_path,line,"send_msg");
								//}
								
								//alert_variables.repetitive_alert_exited_region_time.put(imei, alert_variables.datetime.get(imei));
								//alert_variables.repetitive_alert_exited_region_location.put(imei, location);
								//alert_variables.repetitive_alert_exited_region_landmark.put(imei, nearest_landmark);
								
								//######## UPDATE DATABASE STATUS
								//line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" exited_region_status="+q+exited_region_start+q+"/>"; 
								line = "<marker imei="+q+imei+q+" lat="+q+prev_lat+q+" lng="+q+prev_lng+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+prev_date+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" exited_region_status="+q+exited_region_start+q+"/>";
								update_alert_status(line, imei, q, alert_type);
								update_database_trigger_log(imei,alert_variables.vehicle_name.get(imei),alert_variables.trip_id.get(imei),alert_type,alert_variables.sts.get(imei),location,nearest_landmark,diff_minutes,prev_date,alert_variables.datetime.get(imei),alert_variables.account_id);
								
								alert_variables.repetitive_alert_exited_region_time.put(imei,alert_variables.datetime.get(imei));
								
								System.out.println("REGION EXITED: SEND OK");							
							}
							
							else if( (distance1 >= threshold_distance) && (prev_exited_region_numeric ==1))	//REPETITVE ALERT GENERATION-EXITED REGION
							{
								if(calculateTimeDiff(alert_variables.repetitive_alert_exited_region_time.get(imei),alert_variables.datetime.get(imei)) >= repetitive_threshold_time)
								{
									//########## GET MSG PARAMETERS
									location = get_url_location(lat_f, lng_f);							
									nearest_landmark = get_nearest_landmark(lat_f, lng_f, "landmark");						
									if(location==null)
									{
										location = nearest_landmark;
									}									
									String time_format_str = get_datetime_format(prev_date);
									String[] time_format_parts = time_format_str.split("#");
									
									long diff_minutes = calculateTimeDiff(prev_date,alert_variables.datetime.get(imei));
									//int t = 80;	//provide in minutes
									long hrs_duration = (long) Math.floor(diff_minutes / 60);
									long mins_duration = diff_minutes % 60;					
									//##################################
		
									//for(int i=0;i<main_temp.length;i++)
									//{
										//alert_string = main_temp[i];
										//System.out.println("AlertStr="+alert_string);
										//temp=split(alert_string,"#");
										//temp1 = alert_string.split("#");
										
										alert_name_device = main_temp[0];
										mobile_no_device = main_temp[1];
										email_device = main_temp[2];
										sms_status = main_temp[3];
										mail_status = main_temp[4];
									
										nearest_landmark = get_nearest_landmark(lat_f, lng_f, "landmark");
										//msg = "Your -vehicle : "+alert_variables.vehicle_name.get(imei)+": is Stopped at "+prev_date+" for more than 2 hours";
										msg = "DFG  at "+location+":"+alert_variables.vehicle_name.get(imei)+","+alert_variables.transporter_name.get(imei)+" since "+time_format_parts[0]+ "hrs on "+time_format_parts[1]+" "+time_format_parts[2]+" "+time_format_parts[3]+",Total Detour from Geofence is "+hrs_duration+" hrs "+mins_duration+" mins"+","+nearest_landmark+","+alert_variables.driver_mobile;
										line = "\n<marker vehicle_name="+q+alert_variables.vehicle_name.get(imei)+q+" alert_type="+q+alert_type+q+" account_id="+q+alert_variables.account_id+q+" phone="+q+mobile_no_device+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" message="+q+msg+q+" trip_id="+q+alert_variables.trip_id.get(imei)+q+" email="+q+email_device+q+" sms_status="+q+sms_status+q+" mail_status="+q+mail_status+q+"/>\n</t1>";
										//System.out.println("line="+line+",send_msg_path="+send_msg_path);
										write_file_string(send_msg_path,line,"send_msg");										
										alert_variables.repetitive_alert_exited_region_time.put(imei, alert_variables.datetime.get(imei));
									//}
									
									//####### UPDATE RE-STATUS
									//line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" exited_region_status="+q+exited_region_start+q+"/>";
									line = "<marker imei="+q+imei+q+" lat="+q+prev_lat+q+" lng="+q+prev_lng+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+prev_date+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" exited_region_status="+q+exited_region_start+q+"/>";
									update_alert_status(line, imei, q, alert_type);
									update_database_trigger_log(imei,alert_variables.vehicle_name.get(imei),alert_variables.trip_id.get(imei),alert_type,alert_variables.sts.get(imei),location,nearest_landmark,diff_minutes,prev_date,alert_variables.datetime.get(imei),alert_variables.account_id);
									
									alert_variables.repetitive_alert_exited_region_time.put(imei,alert_variables.datetime.get(imei));

									System.out.println("REPETITIVE EXITED REGION START: RSEND OK");							
								}					
								else if( (distance1 < threshold_distance) && (prev_exited_region_numeric ==1) )	//####### REPETITIVE ALERT
								{
									//##### UPDATE CURRENT FILE #######/
									line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+prev_location+q+" nearest_landmark="+q+prev_nearest_landmark+q+" exited_region_status="+q+exited_region_stop+q+"/>"; 
									update_alert_status(line, imei, q, alert_type);
									alert_variables.temp_alert_exited_region.put(imei, line);
									alert_variables.repetitive_alert_exited_region_time.put(imei,alert_variables.datetime.get(imei));
									//write_file_string(current_path,line,"current");	
									//System.out.println("DOOR-1 OPEN: FALSE");
								}
								else if(distance1 < threshold_distance)
								{
									line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" exited_region_status="+q+exited_region_stop+q+"/>"; 
									alert_variables.temp_alert_exited_region.put(imei, line);
									alert_variables.repetitive_alert_exited_region_time.put(imei,alert_variables.datetime.get(imei));
								}
							}
						}	// source_dest if closed		
					}
				}
			} 
			catch(Exception e) 
			{ 
				//System.out.println("EXCEPTION-DOOR-1 OPEN ACTIVATED PREV FILE READ:"+e.getMessage()); 
			}										
		}
	}
	
	
	/************** PROCESS- ALERT HALT1 ***************/
	public static void alert_halt1_start_process(String imei, String alert_type)
	{
		//System.out.println("IN HALT1 START");			//ENGINE IO DOES NOT NEED TO FETCH FROM FILE
		//String alert_type = "halt1_start";
		String alert_string = "", folder_date="", current_path ="", current_file="", location="", nearest_landmark="" ,send_msg_path ="", send_msg_date="";
		String[] temp;
		String[] main_temp;
		String[] temp1;
		String[] temp2;
		String[] temp3;
		String[] valid_date_tmp;
		String[] source_coord;
		String[] dest_coord;
		//float engine_io_value = 0.0f;
		int threshold_time = 0;  //in minutes
		int repetitive_threshold_time = 0;
		
		/*if(imei.equalsIgnoreCase("777777"))
		{
			threshold_time = 1;  //in minutes
			repetitive_threshold_time = 1;
		}
		else
		{*/
			threshold_time = 120;  //in minutes
			repetitive_threshold_time = 30;			
		//}
		
		double threshold_distance = 0.30000;	//300 meters
		String line ="", msg="", halt_start="1", halt_stop="0";
		String q="\"";
			
		String mobile_no_device="",alert_name_device="",person_name_device="",email_device="";		
		String sms_status="",mail_status="";						
		String strLine1 ="", prev_imei = "", prev_date = "", prev_sts = "", prev_lat = "", prev_lng = "", prev_halt1_start_status = "", prev_location="", prev_nearest_landmark="";
		String transporter_name="",driver_mobile="";
		int prev_halt1_start_status_numeric = 0;

		/*//###### GET DB ENGINE IO ###########/
		engine_io_value = get_io(av, "engine");
		//av.engine_io = engine_io;*/
		main_temp = alert_variables.alert_halt1_start.get(imei).split("#");
		
		//####### SPLIT ALERT STRING OF DATABASE AND GET INDIVIDUAL VARIABLES #######/
		//System.out.println("Door1_Closed_SizeMainTemp="+main_temp.length);					
		//System.out.println("DOOR-1_CLOSE: mobile_no="+mobile_no_device+" ,alert_duration="+alert_duration_device+" ,alert_id="+alert_id_device+" ,alert_name="+alert_name_device+" ,escalation_id="+escalation_id_device+" ,person_name="+person_name_device+" ,email="+email_device+" ,sms_status="+sms_status+" ,mail_status="+mail_status);
							
		//########## MAKE NEW DATE FOLDER -IF FOLDER DOES NOT EXISTS ########/
		//temp2 = split(av.datetime," ");
		/*temp2 = alert_variables.datetime.get(imei).split(" ");
		folder_date = temp2[0];
		String mydir1 = alert_variables.root_dir+"/current_alerts/ignition_activated";
		boolean success1 = (new File(mydir1 + "/" + folder_date)).mkdir();*/

		temp3= alert_variables.sts.get(imei).split(" ");
		send_msg_date = temp3[0];			
		String mydir2 = alert_variables.root_dir+"/send_messages";
		boolean success2 = (new File(mydir2 + "/" + send_msg_date)).mkdir();
		send_msg_path = alert_variables.root_dir+"/send_messages/"+send_msg_date+"/"+imei+".xml";			
		
		//########## CHECK IF FILE EXISTS #########/		
		/*current_file = mobile_no_device+"_"+alert_variables.imei.get(imei); 			
		current_path = alert_variables.root_dir+"/current_alerts/ignition_activated/"+folder_date+"/"+current_file+".xml";									
		File file = new File(current_path);
		boolean exists = file.exists();*/
		//System.out.println("SizeDoorClose="+alert_variables.temp_alert_door1_closed.get(imei));
		
		//######## CHECK FOR VALID HOURS
		String dateFromat = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
		String xml_date = alert_variables.datetime.get(imei);
		
		valid_date_tmp = xml_date.split(" ");
		//String xml_date = "2014-11-05 17:00:00";
		//String date_range1 = "2014-11-05 12:00:00";
		//String date_range2 = "2014-11-05 16:00:00";
		String date_range1 = valid_date_tmp[0]+" 12:00:00";
		String date_range2 = valid_date_tmp[0]+" 16:00:00";
		//String date_range2 = valid_date_tmp[0]+" 17:00:00";
		boolean valid_time = false;
		try {
			Date date_cur = sdf.parse(xml_date);		
			
			Date date1 = sdf.parse(date_range1);
			Date date2 = sdf.parse(date_range2);
			
			//System.out.println("Halt1::xml_date="+xml_date+" ,date_range1="+date_range1+" ,date_range2="+date_range2);
			
			if(date_cur.after(date1) && date_cur.before(date2))
			{
				//System.out.println("Withing Range");
				valid_time = true;
			}
			else
			{
				//####### IF ALERT TIME IS OVER ###########
				try {
					//##### READ ALL PARAMETERS OF PREVIOUS XML_DATA #####/					
					//System.out.println("Zero");
					//String strLine1;
					//strLine1 = read_file_string(current_path);		 /******* GET LINE STRING **********/
					if(alert_variables.temp_alert_halt1_start.get(imei)!=null)
					{
						strLine1 = alert_variables.temp_alert_halt1_start.get(imei);	
						//System.out.println("One");
						//######### GET XML PARAMETERS ###########/
						prev_imei = getXmlAttribute(strLine1,"imei=\"[^\"]+");
						prev_date = getXmlAttribute(strLine1,"datetime=\"[^\"]+");
						prev_sts = getXmlAttribute(strLine1,"sts=\"[^\"]+");
						prev_lat = getXmlAttribute(strLine1,"lat=\"[^\"]+");					
						prev_lng = getXmlAttribute(strLine1,"lng=\"[^\"]+");
						prev_location = getXmlAttribute(strLine1,"location=\"[^\"]+");
						prev_nearest_landmark = getXmlAttribute(strLine1,"nearest_landmark=\"[^\"]+");
						//System.out.println("Two");
						//String prev_engine_io_value = getXmlAttribute(strLine1,""+engine_io+"\"=\"[^\"]+");
						prev_halt1_start_status = getXmlAttribute(strLine1,"halt1_status=\"[^\"]+");							
						prev_halt1_start_status_numeric = Integer.parseInt(prev_halt1_start_status);														
		
						if(prev_halt1_start_status_numeric==1)
						{
							line = "<marker imei="+q+prev_imei+q+" lat="+q+prev_lat+q+" lng="+q+prev_lng+q+" sts="+q+prev_sts+q+" datetime="+q+prev_date+q+" location="+q+prev_location+q+" nearest_landmark="+q+prev_nearest_landmark+q+" halt1_status="+q+halt_stop+q+"/>";
							update_alert_status(line, imei, q, alert_type);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//######## IF ALERT TIME OVER ENDS
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		//######## VALID HOURS CLOSED
		
		//valid_time = true;
		if(valid_time)
		{
			System.out.println("IN HALT1 START");
			
			if (alert_variables.temp_alert_halt1_start.get(imei)==null)											/****** CREATE FILE -IF FILE DOES NOT EXIST *********/
			{
				//System.out.println("FirstTimeHalt1");
				String alert_str = read_database_alert_status(imei, alert_string, alert_type);
				if(alert_str==null)
				{
					//######## CREATE CURRENT FILE ###########/
					line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" halt1_status="+q+halt_stop+q+"/>"; 
					alert_variables.temp_alert_halt1_start.put(imei, line);
					//write_file_string(current_path,line,"current");	
					System.out.println("HALT1 START: CURRENT CREATED1");					
				}
				else
				{
					//######## CREATE CURRENT FILE ###########/
					//line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" door_close_status="+q+door_close_stop+q+"/>"; 
					//System.out.println("ALERT_EXISTS_DB_DOOR1_CLOSE="+alert_str);
					alert_variables.temp_alert_halt1_start.put(imei, alert_str);
					//write_file_string(current_path,line,"current");	
					System.out.println("HALT1 START: CURRENT CREATED2");								
				}
			}
			else													/****** READ FILE -IF FILE EXISTS ********/
			{  
				//System.out.println("File exists:Halt1_start");
				try {
						//##### READ ALL PARAMETERS OF PREVIOUS XML_DATA #####/					
						//strLine1 = read_file_string(current_path);		 /******* GET LINE STRING **********/
						strLine1 = alert_variables.temp_alert_halt1_start.get(imei);	
						//System.out.println("strLine1="+strLine1);
						//######### GET XML PARAMETERS ###########/
						prev_imei = getXmlAttribute(strLine1,"imei=\"[^\"]+");
						prev_date = getXmlAttribute(strLine1,"datetime=\"[^\"]+");
						prev_sts = getXmlAttribute(strLine1,"sts=\"[^\"]+");
						prev_lat = getXmlAttribute(strLine1,"lat=\"[^\"]+");
						prev_lng = getXmlAttribute(strLine1,"lng=\"[^\"]+");
						prev_location = getXmlAttribute(strLine1,"location=\"[^\"]+");
						prev_nearest_landmark = getXmlAttribute(strLine1,"nearest_landmark=\"[^\"]+");
						
						prev_halt1_start_status = getXmlAttribute(strLine1,"halt1_status=\"[^\"]+");
						prev_halt1_start_status_numeric = Integer.parseInt(prev_halt1_start_status);
						
						if( (prev_halt1_start_status_numeric == 0) && (!alert_variables.halt1_reset_flag) )
						{
							line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" halt1_status="+q+halt_stop+q+"/>";
							update_alert_status(line, imei, q, alert_type);
							alert_variables.temp_alert_halt1_start.put(imei, strLine1);
							alert_variables.halt1_reset_flag = true;
							prev_imei = imei;
							prev_date = alert_variables.datetime.get(imei);
							prev_sts = alert_variables.sts.get(imei);
							prev_lat = alert_variables.lat.get(imei).toString();					
							prev_lng = alert_variables.lng.get(imei).toString();
							prev_location = "";
							prev_nearest_landmark = "";											
							prev_halt1_start_status_numeric = 0;														
						}
						
						//System.out.println("prev_door1_close_status="+prev_door1_close_status+" ,alert_variables.datetime.get(imei)="+alert_variables.datetime.get(imei));
						//System.out.println("prev_halt1_start_status_numeric="+prev_halt1_start_status_numeric);														
						//if(prev_halt1_start_status_numeric==0)
						//System.out.println("HALT1:ELSE:alert_variables.sts.get(imei)="+alert_variables.sts.get(imei)+" ,alert_variables.datetime.get(imei)="+alert_variables.datetime.get(imei)+" ,threshold_time="+threshold_time);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						//System.out.println("Error in Parsing To Integer");
					}
	
				try {
					
					if( (calculateTimeDiff(alert_variables.datetime.get(imei),alert_variables.sts.get(imei))<10) && (threshold_time>0) )
					{					
						double lat_f = alert_variables.lat.get(imei);
						double lng_f = alert_variables.lng.get(imei);
	
						//######## CHECK TRIP BETWEEN SOURCE AND DESTINATION
						System.out.println("alert_variables.source_coord.get(imei)="+alert_variables.source_coord.get(imei));
						
						if( (alert_variables.source_coord.get(imei)!="") && (alert_variables.source_coord.get(imei)!="-") && (alert_variables.dest_coord.get(imei)!="") && ((alert_variables.dest_coord.get(imei)!="-")))
						{
							source_coord = alert_variables.source_coord.get(imei).split(",");
							dest_coord = alert_variables.dest_coord.get(imei).split(",");
							
							Double lat_s = Double.parseDouble(source_coord[0].trim());
							Double lng_s = Double.parseDouble(source_coord[1].trim());
							Double lat_d = Double.parseDouble(dest_coord[0].trim());
							Double lng_d = Double.parseDouble(dest_coord[1].trim());
							double distance_source = calculateDistance(lat_f, lng_f, lat_s, lng_s);
							double distance_dest = calculateDistance(lat_f, lng_f, lat_d, lng_d);
	
						
							if((distance_source > threshold_distance) && (distance_dest > threshold_distance))
							{					
								System.out.println("IN MainAlert:Halt1");
								
								if(alert_variables.repetitive_alert_halt1_start_time.get(imei)==null)
								{
									alert_variables.repetitive_alert_halt1_start_time.put(imei,prev_date);
								}
								
								//double distance1 = calculateDistance(lat_f, lng_f, Double.parseDouble(prev_lat), Double.parseDouble(prev_lng),'K');
								double distance1 = calculateDistance(lat_f, lng_f, Double.parseDouble(prev_lat), Double.parseDouble(prev_lng));
								
								System.out.println("DistanceHalt1="+distance1+" ,prev_date="+prev_date+" ,alert_variables.sts.get(imei)="+alert_variables.sts.get(imei)+" ,prev_halt1_start_status_numeric="+prev_halt1_start_status_numeric);
								//if(prev_halt1_start_status_numeric==0)								
								if( (calculateTimeDiff(prev_date,alert_variables.datetime.get(imei)) >= threshold_time) && (prev_halt1_start_status_numeric ==0) && (distance1 <= threshold_distance)  )					
								{
									System.out.println("IN MainAlert:Halt1-A");

									//########## GET MSG PARAMETERS
									location = get_url_location(lat_f, lng_f);									
									nearest_landmark = get_nearest_landmark(lat_f, lng_f, "landmark");
									if(location==null)
									{
										location = nearest_landmark;
									}

									String time_format_str = get_datetime_format(prev_date);
									String[] time_format_parts = time_format_str.split("#");
									
									long diff_minutes = calculateTimeDiff(prev_date,alert_variables.datetime.get(imei));
									//int t = 80;	//provide in minutes
									long hrs_duration = (long) Math.floor(diff_minutes / 60);
									long mins_duration = diff_minutes % 60;					
									//##################################
									
									//for(int i=0;i<main_temp.length;i++)
									//{
										//alert_string = main_temp[i];
										//System.out.println("AlertStr="+alert_string);
										//temp=split(alert_string,"#");
										//temp1 = alert_string.split(",");
										
										alert_name_device = main_temp[0];
										mobile_no_device = main_temp[1];
										email_device = main_temp[2];
										sms_status = main_temp[3];
										mail_status = main_temp[4];
									
										//msg = "Your -vehicle : "+alert_variables.vehicle_name.get(imei)+": is Stopped at "+prev_date+" for more than 2 hours";
										msg = "S120  at "+location+":"+alert_variables.vehicle_name.get(imei)+","+alert_variables.transporter_name.get(imei)+" stopped since "+time_format_parts[0]+" hrs on "+time_format_parts[1]+" "+time_format_parts[2]+" "+time_format_parts[3]+",Total halt "+hrs_duration+" hrs "+mins_duration+" mins.,"+nearest_landmark+","+alert_variables.driver_mobile.get(imei);
										line = "\n<marker vehicle_name="+q+alert_variables.vehicle_name.get(imei)+q+" alert_type="+q+alert_type+q+" account_id="+q+alert_variables.account_id+q+" phone="+q+mobile_no_device+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" message="+q+msg+q+" trip_id="+q+alert_variables.trip_id.get(imei)+q+" email="+q+email_device+q+" sms_status="+q+sms_status+q+" mail_status="+q+mail_status+q+"/>\n</t1>";
										//System.out.println("line="+line+",send_msg_path="+send_msg_path);
										write_file_string(send_msg_path,line,"send_msg");						
									//}						
									
									//######## UPDATE DATABASE STATUS
									//line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" halt1_status="+q+halt_start+q+"/>";									
									line = "<marker imei="+q+imei+q+" lat="+q+prev_lat+q+" lng="+q+prev_lng+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+prev_date+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" halt1_status="+q+halt_start+q+"/>";
									update_alert_status(line, imei, q, alert_type);
									update_database_trigger_log(imei,alert_variables.vehicle_name.get(imei),alert_variables.trip_id.get(imei),alert_type,alert_variables.sts.get(imei),location,nearest_landmark,diff_minutes,prev_date,alert_variables.datetime.get(imei),alert_variables.account_id);
									
									alert_variables.repetitive_alert_halt1_start_time.put(imei, alert_variables.datetime.get(imei));
									//alert_variables.repetitive_alert_halt1_start_location.put(imei, location);
									//alert_variables.repetitive_alert_halt1_start_landmark.put(imei, nearest_landmark);	
									
									System.out.println("HALT1 START: SEND OK");							
								}
								else if( (distance1 <= threshold_distance) && (prev_halt1_start_status_numeric ==1))	//REPETITVE ALERT GENERATION-HALT1_START
								{
									if(calculateTimeDiff(alert_variables.repetitive_alert_halt1_start_time.get(imei),alert_variables.datetime.get(imei)) >= repetitive_threshold_time)
									{
										//########## GET MSG PARAMETERS
										//String location = get_url_location(lat_f, lng_f);							
										//nearest_landmark = get_nearest_landmark(lat_f, lng_f, "landmark");						
										String time_format_str = get_datetime_format(prev_date);
										String[] time_format_parts = time_format_str.split("#");
										
										long diff_minutes = calculateTimeDiff(prev_date,alert_variables.datetime.get(imei));
										//int t = 80;	//provide in minutes
										long hrs_duration = (long) Math.floor(diff_minutes / 60);
										long mins_duration = diff_minutes % 60;					
										//##################################
										
										//for(int i=0;i<main_temp.length;i++)
										//{
											//alert_string = main_temp[i];
											//System.out.println("AlertStr="+alert_string);
											//temp=split(alert_string,"#");
											//temp1 = alert_string.split("#");
											
											alert_name_device = main_temp[0];
											mobile_no_device = main_temp[1];
											email_device = main_temp[2];
											sms_status = main_temp[3];
											mail_status = main_temp[4];
										
											//msg = "Your -vehicle : "+alert_variables.vehicle_name.get(imei)+": is Stopped at "+prev_date+" for more than 2 hours";
											msg = "S120  at "+prev_location+":"+alert_variables.vehicle_name.get(imei)+","+alert_variables.transporter_name.get(imei)+" stopped since"+time_format_parts[0]+" hrs on "+time_format_parts[1]+" "+time_format_parts[2]+" "+time_format_parts[3]+",Total halt "+hrs_duration+" hrs "+mins_duration+" mins.,"+prev_nearest_landmark+","+alert_variables.driver_mobile.get(imei);
											line = "\n<marker vehicle_name="+q+alert_variables.vehicle_name.get(imei)+q+" alert_type="+q+alert_type+q+" account_id="+q+alert_variables.account_id+q+" phone="+q+mobile_no_device+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" message="+q+msg+q+" trip_id="+q+alert_variables.trip_id.get(imei)+q+" email="+q+email_device+q+" sms_status="+q+sms_status+q+" mail_status="+q+mail_status+q+"/>\n</t1>";
											//System.out.println("line="+line+",send_msg_path="+send_msg_path);
											write_file_string(send_msg_path,line,"send_msg");
											//alert_variables.repetitive_alert_halt1_start_time.put(imei, alert_variables.datetime.get(imei));
										//}
										
										//######## UPDATE STATUS
										//line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+prev_location+q+" nearest_landmark="+q+prev_nearest_landmark+q+" halt1_status="+q+halt_start+q+"/>";
										line = "<marker imei="+q+imei+q+" lat="+q+prev_lat+q+" lng="+q+prev_lng+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+prev_date+q+" location="+q+prev_location+q+" nearest_landmark="+q+prev_nearest_landmark+q+" halt1_status="+q+halt_start+q+"/>";
										update_alert_status(line, imei, q, alert_type);
										update_database_trigger_log(imei,alert_variables.vehicle_name.get(imei),alert_variables.trip_id.get(imei),alert_type,alert_variables.sts.get(imei),prev_location,prev_nearest_landmark,diff_minutes,prev_date,alert_variables.datetime.get(imei),alert_variables.account_id);
										
										alert_variables.repetitive_alert_halt1_start_time.put(imei, alert_variables.datetime.get(imei));
										
										System.out.println("REPETITIVE HALT1 START: RSEND OK");
									}
								}					
								else if( (distance1 > threshold_distance) && (prev_halt1_start_status_numeric ==1) )	//###### REPETITVE
								{
									//System.out.println("ElseIf_Halt1");
									//##### UPDATE STATUS #######/
									line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" halt1_status="+q+halt_stop+q+"/>"; 
									update_alert_status(line, imei, q, alert_type);
									alert_variables.temp_alert_halt1_start.put(imei, line);
									alert_variables.repetitive_alert_halt1_start_time.put(imei, alert_variables.datetime.get(imei));
								}
								else if(distance1 > threshold_distance)
								{
									//System.out.println("ELSE LAST");
									line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" halt1_status="+q+halt_stop+q+"/>"; 
									alert_variables.temp_alert_halt1_start.put(imei, line);
									alert_variables.repetitive_alert_halt1_start_time.put(imei, alert_variables.datetime.get(imei));
								}
							}
						}
					}
				}		
				catch(Exception e) 
				{ 
					System.out.println("EXCEPTION-HALT-1 CLOSED PREV FILE READ:"+e.getMessage()); 
				}
			}
		}
		
		//System.out.println("ALERT_STR_HALT1="+alert_variables.temp_alert_halt1_start.get(imei));
	}
	
	
	/************** PROCESS- ALERT HALT2 ***************/
	public static void alert_halt2_start_process(String imei, String alert_type)
	{
		//System.out.println("IN HALT2");			//ENGINE IO DOES NOT NEED TO FETCH FROM FILE
		//String alert_type = "halt2_start";
		String alert_string = "", folder_date="", current_path ="", current_file="", location="", nearest_landmark="" ,send_msg_path ="", send_msg_date="";
		String[] temp;
		String[] main_temp;
		String[] temp1;
		String[] temp2;
		String[] temp3;
		String[] valid_date_tmp;
		String[] source_coord;
		String[] dest_coord;		
		//float engine_io_value = 0.0f;
		
		int threshold_time = 0;  //in minutes
		int repetitive_threshold_time = 0;

		/*if(imei.equalsIgnoreCase("777777"))
		{
			threshold_time = 1;  //in minutes
			repetitive_threshold_time = 1;
		}
		else
		{*/
			threshold_time = 30;  //in minutes
			repetitive_threshold_time = 30;		
		//}		

		//int repetitive_threshold_time = 1;
		double threshold_distance = 0.30000;	//300 meters
		String line ="", msg="", halt_start="1", halt_stop="0";
		String q="\"";
			
		String mobile_no_device="",alert_name_device="",person_name_device="",email_device="";		
		String sms_status="",mail_status="";
		String strLine1 = "",prev_imei = "",prev_date = "", prev_sts = "", prev_lat = "", prev_lng = "", prev_halt2_start_status = "", prev_location="", prev_nearest_landmark="";
		String transporter_name="",driver_mobile="";
		int prev_halt2_start_status_numeric = 0;
		/*//###### GET DB ENGINE IO ###########/
		engine_io_value = get_io(av, "engine");
		//av.engine_io = engine_io;*/
		main_temp = alert_variables.alert_halt2_start.get(imei).split("#");
		
		//####### SPLIT ALERT STRING OF DATABASE AND GET INDIVIDUAL VARIABLES #######/
		//System.out.println("Door1_Closed_SizeMainTemp="+main_temp.length);
					
		//System.out.println("DOOR-1_CLOSE: mobile_no="+mobile_no_device+" ,alert_duration="+alert_duration_device+" ,alert_id="+alert_id_device+" ,alert_name="+alert_name_device+" ,escalation_id="+escalation_id_device+" ,person_name="+person_name_device+" ,email="+email_device+" ,sms_status="+sms_status+" ,mail_status="+mail_status);
							
		//########## MAKE NEW DATE FOLDER -IF FOLDER DOES NOT EXISTS ########/
		//temp2 = split(av.datetime," ");
		/*temp2 = alert_variables.datetime.get(imei).split(" ");
		folder_date = temp2[0];
		String mydir1 = alert_variables.root_dir+"/current_alerts/ignition_activated";
		boolean success1 = (new File(mydir1 + "/" + folder_date)).mkdir();*/

		temp3= alert_variables.sts.get(imei).split(" ");
		send_msg_date = temp3[0];			
		String mydir2 = alert_variables.root_dir+"/send_messages";
		boolean success2 = (new File(mydir2 + "/" + send_msg_date)).mkdir();
		send_msg_path = alert_variables.root_dir+"/send_messages/"+send_msg_date+"/"+imei+".xml";			
		
		//########## CHECK IF FILE EXISTS #########/		
		/*current_file = mobile_no_device+"_"+alert_variables.imei.get(imei); 			
		current_path = alert_variables.root_dir+"/current_alerts/ignition_activated/"+folder_date+"/"+current_file+".xml";									
		File file = new File(current_path);
		boolean exists = file.exists();*/
		//System.out.println("SizeDoorClose="+alert_variables.temp_alert_door1_closed.get(imei));
		
		//######## CHECK FOR VALID HOURS
		String dateFromat = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
		String xml_date = alert_variables.datetime.get(imei);

		System.out.println("Halt2::XmlDate="+xml_date);
		valid_date_tmp = xml_date.split(" ");
		//String xml_date = "2014-11-05 17:00:00";
		//String date_range1 = "2014-11-05 12:00:00";
		//String date_range2 = "2014-11-05 16:00:00";
		String date_range1 = valid_date_tmp[0]+" 04:00:00";
		String date_range2 = valid_date_tmp[0]+" 12:00:00";
		String date_range3 = valid_date_tmp[0]+" 16:00:00";
		String date_range4 = valid_date_tmp[0]+" 22:00:00";
		
		//System.out.println("D1="+date_range1+" ,D2="+date_range2+" ,D3="+date_range3+" ,D4="+date_range4+" ,xml_date="+xml_date);
		
		boolean valid_time = false;
		try {
			Date date_cur = sdf.parse(xml_date);
			
			Date date1 = sdf.parse(date_range1);
			Date date2 = sdf.parse(date_range2);
			Date date3 = sdf.parse(date_range3);
			Date date4 = sdf.parse(date_range4);			
			
			System.out.println("HALT2::date_cur="+date_cur+" ,date1="+date1+ ", date2="+date2+" ,date3="+date3+" ,date4="+date4);
			if((date_cur.after(date1) && date_cur.before(date2)) || (date_cur.after(date3) && date_cur.before(date4)) )
			{
				//System.out.println("Withing Range Halt2:30mins");
				valid_time = true;
			}			
			else
			{
				//####### IF ALERT TIME IS OVER ###########
				try {
					//##### READ ALL PARAMETERS OF PREVIOUS XML_DATA #####/					
					//System.out.println("Zero");
					//String strLine1;
					//strLine1 = read_file_string(current_path);		 /******* GET LINE STRING **********/
					if(alert_variables.temp_alert_halt2_start.get(imei)!=null)
					{
						strLine1 = alert_variables.temp_alert_halt2_start.get(imei);	
						//System.out.println("One");
						//######### GET XML PARAMETERS ###########/
						prev_imei = getXmlAttribute(strLine1,"imei=\"[^\"]+");
						prev_date = getXmlAttribute(strLine1,"datetime=\"[^\"]+");
						prev_sts = getXmlAttribute(strLine1,"sts=\"[^\"]+");
						prev_lat = getXmlAttribute(strLine1,"lat=\"[^\"]+");					
						prev_lng = getXmlAttribute(strLine1,"lng=\"[^\"]+");
						prev_location = getXmlAttribute(strLine1,"location=\"[^\"]+");
						prev_nearest_landmark = getXmlAttribute(strLine1,"nearest_landmark=\"[^\"]+");
						//System.out.println("Two");
						//String prev_engine_io_value = getXmlAttribute(strLine1,""+engine_io+"\"=\"[^\"]+");
						prev_halt2_start_status = getXmlAttribute(strLine1,"halt2_status=\"[^\"]+");							
						prev_halt2_start_status_numeric = Integer.parseInt(prev_halt2_start_status);														
	
						if(prev_halt2_start_status_numeric==1)
						{
							line = "<marker imei="+q+prev_imei+q+" lat="+q+prev_lat+q+" lng="+q+prev_lng+q+" sts="+q+prev_sts+q+" datetime="+q+prev_date+q+" location="+q+prev_location+q+" nearest_landmark="+q+prev_nearest_landmark+q+" halt2_status="+q+halt_stop+q+"/>";
							update_alert_status(line, imei, q, alert_type);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//######## IF ALERT TIME OVER ENDS
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		//######## VALID HOURS CLOSED
		
		if(valid_time)
		{		
			System.out.println("IN HALT2 START:valid_time");
			
			if(alert_variables.temp_alert_halt2_start.get(imei)==null)											/****** CREATE FILE -IF FILE DOES NOT EXIST *********/
			{
				String alert_str = read_database_alert_status(imei, alert_string, alert_type);
				//System.out.println("IF::STR_HALT2="+alert_str+" ORG:imei="+imei+" ,alert_str="+alert_string+" ,alert_type="+alert_type);
				if(alert_str==null)
				{
					//######## CREATE CURRENT FILE ###########/
					line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" halt2_status="+q+halt_stop+q+"/>"; 
					System.out.println("Line-AHalt1="+line);
					alert_variables.temp_alert_halt2_start.put(imei, line);
					//write_file_string(current_path,line,"current");	
					System.out.println("HALT2 START: CURRENT CREATED-A");		
				}
				else
				{
					//######## CREATE CURRENT FILE ###########/
					//line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" door_close_status="+q+door_close_stop+q+"/>"; 
					//System.out.println("ALERT_EXISTS_DB_DOOR1_CLOSE="+alert_str);
					alert_variables.temp_alert_halt2_start.put(imei, alert_str);
					//write_file_string(current_path,line,"current");	
					System.out.println("HALT2 START: CURRENT CREATED-B");								
				}
			}
			else													/****** READ FILE -IF FILE EXISTS ********/
			{  
				System.out.println("File exists:Halt2_start");
				try {
					//##### READ ALL PARAMETERS OF PREVIOUS XML_DATA #####/					
					//System.out.println("Zero");
					//String strLine1;
					//strLine1 = read_file_string(current_path);		 /******* GET LINE STRING **********/
					strLine1 = alert_variables.temp_alert_halt2_start.get(imei);	
					//System.out.println("One");
					//######### GET XML PARAMETERS ###########/
					prev_imei = getXmlAttribute(strLine1,"imei=\"[^\"]+");
					prev_date = getXmlAttribute(strLine1,"datetime=\"[^\"]+");
					prev_sts = getXmlAttribute(strLine1,"sts=\"[^\"]+");
					prev_lat = getXmlAttribute(strLine1,"lat=\"[^\"]+");					
					prev_lng = getXmlAttribute(strLine1,"lng=\"[^\"]+");
					prev_location = getXmlAttribute(strLine1,"location=\"[^\"]+");
					prev_nearest_landmark = getXmlAttribute(strLine1,"nearest_landmark=\"[^\"]+");
					//System.out.println("Two");
					//String prev_engine_io_value = getXmlAttribute(strLine1,""+engine_io+"\"=\"[^\"]+");
					prev_halt2_start_status = getXmlAttribute(strLine1,"halt2_status=\"[^\"]+");							
					prev_halt2_start_status_numeric = Integer.parseInt(prev_halt2_start_status);
					
					if( (prev_halt2_start_status_numeric == 0) && (!alert_variables.halt2_reset_flag) )
					{
						line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" halt2_status="+q+halt_stop+q+"/>";
						update_alert_status(line, imei, q, alert_type);
						alert_variables.temp_alert_halt2_start.put(imei, strLine1);
						alert_variables.halt2_reset_flag = true;
						prev_imei = imei;
						prev_date = alert_variables.datetime.get(imei);
						prev_sts = alert_variables.sts.get(imei);
						prev_lat = alert_variables.lat.get(imei).toString();					
						prev_lng = alert_variables.lng.get(imei).toString();
						prev_location = "";
						prev_nearest_landmark = "";											
						prev_halt2_start_status_numeric = 0;
					}					
					//System.out.println("prev_door1_close_status="+prev_door1_close_status+" ,alert_variables.datetime.get(imei)="+alert_variables.datetime.get(imei));
					//System.out.println("STS="+alert_variables.sts.get(imei)+" ,DateTime="+alert_variables.datetime.get(imei));
					//System.out.println("ELSE::HALT2:alert_variables.sts.get(imei)="+alert_variables.sts.get(imei)+" ,alert_variables.datetime.get(imei)="+alert_variables.datetime.get(imei)+" ,threshold_time="+threshold_time);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try{	
					System.out.println("STS="+alert_variables.sts.get(imei)+" ,Datetime="+alert_variables.datetime.get(imei)+" ,Diff="+calculateTimeDiff(alert_variables.datetime.get(imei),alert_variables.sts.get(imei)));
					
					if( (calculateTimeDiff(alert_variables.datetime.get(imei),alert_variables.sts.get(imei))<10) && (threshold_time>0) )
					{					
						System.out.println("H2-C1");
						double lat_f = alert_variables.lat.get(imei);
						double lng_f = alert_variables.lng.get(imei);
	
						//######## CHECK TRIP BETWEEN SOURCE AND DESTINATION
						if( (alert_variables.source_coord.get(imei)!="") && (alert_variables.source_coord.get(imei)!="-") && (alert_variables.dest_coord.get(imei)!="") && ((alert_variables.dest_coord.get(imei)!="-")))
						{					
							System.out.println("H2-C2");
							source_coord = alert_variables.source_coord.get(imei).split(",");
							dest_coord = alert_variables.dest_coord.get(imei).split(",");
							
							Double lat_s = Double.parseDouble(source_coord[0].trim());
							Double lng_s = Double.parseDouble(source_coord[1].trim());
							Double lat_d = Double.parseDouble(dest_coord[0].trim());
							Double lng_d = Double.parseDouble(dest_coord[1].trim());
							double distance_source = calculateDistance(lat_f, lng_f, lat_s, lng_s);
							double distance_dest = calculateDistance(lat_f, lng_f, lat_d, lng_d);
							
							if((distance_source > threshold_distance) && (distance_dest > threshold_distance))
							{
								if(alert_variables.repetitive_alert_halt2_start_time.get(imei)==null)
								{
									alert_variables.repetitive_alert_halt2_start_time.put(imei,prev_date);
								}
								
								//double distance1 = calculateDistance(lat_f, lng_f, Double.parseDouble(prev_lat), Double.parseDouble(prev_lng), 'K');
								double distance1 = calculateDistance(lat_f, lng_f, Double.parseDouble(prev_lat), Double.parseDouble(prev_lng));
								
								System.out.println("Distance-Halt2::"+distance1+" ,prev_date="+prev_date+" ,alert_variables.datetime.get(imei)="+alert_variables.datetime.get(imei)+" ,prev_halt2_start_status_numeric="+prev_halt2_start_status_numeric+" ,TimeDiff(prev_date,alert_variables.datetime.get(imei)="+calculateTimeDiff(prev_date,alert_variables.datetime.get(imei))+" ,threshold_time="+threshold_time);
								
								//if( (prev_halt2_start_status_numeric ==0) && (distance1 <= threshold_distance)  )
								if( (calculateTimeDiff(prev_date,alert_variables.datetime.get(imei)) >= threshold_time) && (prev_halt2_start_status_numeric ==0) && (distance1 <= threshold_distance)  )								
								{
									System.out.println("INNER_IF-Halt2-A");
									
									//########## GET MSG PARAMETERS
									location = get_url_location(lat_f, lng_f);
									//String location = "";
									//System.out.println("INNER_IF-3_Halt2");
									nearest_landmark = get_nearest_landmark(lat_f, lng_f, "landmark");
									if(location==null)
									{
										location = nearest_landmark;
									}
									
									//System.out.println("INNER_IF-4_Halt2");
									String time_format_str = get_datetime_format(prev_date);
									String[] time_format_parts = time_format_str.split("#");
									//System.out.println("INNER_IF-5_Halt2");
									
									long diff_minutes = calculateTimeDiff(prev_date,alert_variables.datetime.get(imei));
									//int t = 80;	//provide in minutes
									long hrs_duration = (long) Math.floor(diff_minutes / 60);
									long mins_duration = diff_minutes % 60;	
									//System.out.println("INNER_IF-6_Halt2");
									//##################################
									
									//System.out.println("main_temp.length="+main_temp.length);									
									//for(int i=0;i<main_temp.length;i++)
									//{
										//alert_string = main_temp[i];
										//temp=split(alert_string,"#");
										//temp1 = alert_string.split("#");
										
										alert_name_device = main_temp[0];
										mobile_no_device = main_temp[1];
										email_device = main_temp[2];
										sms_status = main_temp[3];
										mail_status = main_temp[4];
									
										//msg = "Your -vehicle : "+alert_variables.vehicle_name.get(imei)+": is Stopped at "+prev_date+" for more than 30 mins";
										msg = "S30  at "+location+":"+alert_variables.vehicle_name.get(imei)+","+alert_variables.transporter_name.get(imei)+" stopped since "+time_format_parts[0]+" hrs on "+time_format_parts[1]+" "+time_format_parts[2]+" "+time_format_parts[3]+",Total halt "+hrs_duration+" hrs "+mins_duration+" mins.,"+nearest_landmark+","+alert_variables.driver_mobile.get(imei);
										line = "\n<marker vehicle_name="+q+alert_variables.vehicle_name.get(imei)+q+" alert_type="+q+alert_type+q+" account_id="+q+alert_variables.account_id+q+" phone="+q+mobile_no_device+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" message="+q+msg+q+" trip_id="+q+alert_variables.trip_id.get(imei)+q+" email="+q+email_device+q+" sms_status="+q+sms_status+q+" mail_status="+q+mail_status+q+"/>\n</t1>";
									
										write_file_string(send_msg_path,line,"send_msg");
									//}			
									
									//####### UPDATE DB STATUS
									//line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+prev_date+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" halt2_status="+q+halt_start+q+"/>";
									line = "<marker imei="+q+imei+q+" lat="+q+prev_lat+q+" lng="+q+prev_lng+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+prev_date+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" halt2_status="+q+halt_start+q+"/>";
									update_alert_status(line, imei, q, alert_type);
									update_database_trigger_log(imei,alert_variables.vehicle_name.get(imei),alert_variables.trip_id.get(imei),alert_type,alert_variables.sts.get(imei),location,nearest_landmark,diff_minutes,prev_date,alert_variables.datetime.get(imei),alert_variables.account_id);
									
									alert_variables.repetitive_alert_halt2_start_time.put(imei, alert_variables.datetime.get(imei));
									//################## UPDATE CLOSED									
									//alert_variables.repetitive_alert_halt2_start_location.put(imei, location);
									//alert_variables.repetitive_alert_halt2_start_landmark.put(imei, nearest_landmark);	
									
									//System.out.println("HALT2 START: SEND OK");							
								}
								else if( (distance1 <= threshold_distance) && (prev_halt2_start_status_numeric ==1))	//REPETITVE ALERT GENERATION-HALT2_START
								{
									//System.out.println("INNER_IF-2_Halt2-REP, alert_variables.repetitive_alert_halt2_start_time.get(imei)="+alert_variables.repetitive_alert_halt2_start_time.get(imei)+" ,alert_variables.datetime.get(imei)="+alert_variables.datetime.get(imei));
									
									if(calculateTimeDiff(alert_variables.repetitive_alert_halt2_start_time.get(imei),alert_variables.datetime.get(imei)) >= repetitive_threshold_time)
									{							
										//System.out.println("INNER_IF-3_Halt2");
										//########## GET MSG PARAMETERS
										//String location = get_url_location(lat_f, lng_f);										
										//nearest_landmark = get_nearest_landmark(lat_f, lng_f, "landmark");						
										String time_format_str = get_datetime_format(prev_date);
										String[] time_format_parts = time_format_str.split("#");
										
										long diff_minutes = calculateTimeDiff(prev_date,alert_variables.datetime.get(imei));
										//int t = 80;	//provide in minutes
										long hrs_duration = (long) Math.floor(diff_minutes / 60);
										long mins_duration = diff_minutes % 60;					
										//##################################
										
										//for(int i=0;i<main_temp.length;i++)
										//{
											//alert_string = main_temp[i];
											//System.out.println("AlertStr="+alert_string);
											//temp=split(alert_string,"#");
											//temp1 = alert_string.split("#");
											
											alert_name_device = main_temp[0];
											mobile_no_device = main_temp[1];
											email_device = main_temp[2];
											sms_status = main_temp[3];
											mail_status = main_temp[4];
										
											//msg = "Your -vehicle : "+alert_variables.vehicle_name.get(imei)+": is Stopped at "+prev_date+" for more than 2 hours";
											msg = "S30  at "+prev_location+":"+alert_variables.vehicle_name.get(imei)+","+alert_variables.transporter_name.get(imei)+" stopped since"+time_format_parts[0]+" hrs on "+time_format_parts[1]+" "+time_format_parts[2]+" "+time_format_parts[3]+",Total halt "+hrs_duration+" hrs "+mins_duration+" mins.,"+prev_nearest_landmark+","+alert_variables.driver_mobile.get(imei);
											line = "\n<marker vehicle_name="+q+alert_variables.vehicle_name.get(imei)+q+" alert_type="+q+alert_type+q+" account_id="+q+alert_variables.account_id+q+" phone="+q+mobile_no_device+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" message="+q+msg+q+" trip_id="+q+alert_variables.trip_id.get(imei)+q+" email="+q+email_device+q+" sms_status="+q+sms_status+q+" mail_status="+q+mail_status+q+"/>\n</t1>";
											//System.out.println("line="+line+",send_msg_path="+send_msg_path);
											write_file_string(send_msg_path,line,"send_msg");											
										//}
										
										//####### UPDATE VALUES FILE AND DB
										//line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+prev_date+q+" location="+q+prev_location+q+" nearest_landmark="+q+prev_nearest_landmark+q+" halt2_status="+q+halt_start+q+"/>";
										line = "<marker imei="+q+imei+q+" lat="+q+prev_lat+q+" lng="+q+prev_lng+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+prev_date+q+" location="+q+prev_location+q+" nearest_landmark="+q+prev_nearest_landmark+q+" halt2_status="+q+halt_start+q+"/>";
										update_alert_status(line, imei, q, alert_type);
										update_database_trigger_log(imei,alert_variables.vehicle_name.get(imei),alert_variables.trip_id.get(imei),alert_type,alert_variables.sts.get(imei),prev_location,prev_nearest_landmark,diff_minutes,prev_date,alert_variables.datetime.get(imei),alert_variables.account_id);
										
										alert_variables.repetitive_alert_halt2_start_time.put(imei, alert_variables.datetime.get(imei));
										
										System.out.println("REPETITIVE HALT2 START: RSEND OK");							
									}
								}					
								else if( (distance1 > threshold_distance) && (prev_halt2_start_status_numeric ==1) )					
								{
									//System.out.println("INNER_IF-3_Halt2");
									//##### UPDATE CURRENT FILE #######/
									line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" halt2_status="+q+halt_stop+q+"/>"; 
									update_alert_status(line, imei, q, alert_type);
									alert_variables.temp_alert_halt2_start.put(imei, line);
									alert_variables.repetitive_alert_halt2_start_time.put(imei, alert_variables.datetime.get(imei));
									//System.out.println("HALT2: FALSE");							
								}
								else if(distance1 > threshold_distance)
								{
									//System.out.println("INNER_IF-4_Halt2");
									line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" halt2_status="+q+halt_stop+q+"/>"; 
									alert_variables.temp_alert_halt2_start.put(imei, line);
									alert_variables.repetitive_alert_halt2_start_time.put(imei, alert_variables.datetime.get(imei));
								}
							}
						}
					} 
				}
				catch(Exception e) 
				{ 
					//System.out.println("EXCEPTION-DOOR-1 CLOSED PREV FILE READ:"+e.getMessage()); 
				}
			}
		}
		//System.out.println("ALERT_STR_HALT2="+alert_variables.temp_alert_halt2_start.get(imei));
	}
	

	/************** PROCESS- ALERT MOVEMENT ***************/
	public static void alert_movement_process(String imei, String alert_type)
	{
		//System.out.println("IN ND START");			//ENGINE IO DOES NOT NEED TO FETCH FROM FILE
		//String alert_type = "movement";
		String alert_string = "", folder_date="", current_path ="", current_file="", location="", nearest_landmark="" ,send_msg_path ="", send_msg_date="";
		String[] temp;
		String[] main_temp;
		String[] temp1;
		String[] temp2;
		String[] temp3;
		String[] valid_date_tmp;
		String[] source_coord;
		String[] dest_coord;
		//float engine_io_value = 0.0f;
		
		int threshold_time = 0;  //in minutes
		int repetitive_threshold_time = 0;

		if(imei.equalsIgnoreCase("777777"))
		{
			threshold_time = 1;  //in minutes
			repetitive_threshold_time = 1;
		}
		else
		{
			threshold_time = 2;  //in minutes
			repetitive_threshold_time = 30;
		}		

		//int repetitive_threshold_time = 1;
		double threshold_distance = 0.30000;	//300 meters
		String line ="", msg="", movement_start="1", movement_stop="0";
		String q="\"";
			
		String mobile_no_device="",alert_name_device="",person_name_device="",email_device="";		
		String sms_status="",mail_status="";
		String strLine1 = "",prev_imei = "",prev_date = "",prev_sts = "",prev_lat = "",prev_lng = "",prev_movement_status = "", prev_location="", prev_nearest_landmark="";
		String transporter_name="",driver_mobile="";
		int prev_movement_numeric = 0;
		
		main_temp = alert_variables.alert_movement.get(imei).split("#");
		
		temp3= alert_variables.sts.get(imei).split(" ");
		send_msg_date = temp3[0];			
		String mydir2 = alert_variables.root_dir+"/send_messages";
		boolean success2 = (new File(mydir2 + "/" + send_msg_date)).mkdir();
		send_msg_path = alert_variables.root_dir+"/send_messages/"+send_msg_date+"/"+imei+".xml";			
		
		//######## CHECK FOR VALID HOURS
		String dateFromat = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
		String xml_date = alert_variables.datetime.get(imei);
		
		valid_date_tmp = xml_date.split(" ");
		//String xml_date = "2014-11-05 17:00:00";
		//String date_range1 = "2014-11-05 12:00:00";
		//String date_range2 = "2014-11-05 16:00:00";
		String date_range1 = valid_date_tmp[0]+" 22:00:00";
		String date_range2 = valid_date_tmp[0]+" 23:59:59";
		String date_range3 = valid_date_tmp[0]+" 00:00:00";
		String date_range4 = valid_date_tmp[0]+" 04:00:00";	
		//String date_range4 = valid_date_tmp[0]+" 14:00:00";	
		boolean valid_time = false;
		try {
			Date date_cur = sdf.parse(xml_date);
			
			Date date1 = sdf.parse(date_range1);
			Date date2 = sdf.parse(date_range2);
			Date date3 = sdf.parse(date_range3);
			Date date4 = sdf.parse(date_range4);
			//System.out.println("date1="+date1+",date2="+date2+",date3="+date3+", date4="+date4+" ,xml_date="+xml_date);
			
			if((date_cur.after(date1) && date_cur.before(date2))||(date_cur.after(date3) && date_cur.before(date4)))
			{
				//System.out.println("Withing Range:Movement");
				valid_time = true;
			}
			else
			{
				//####### IF ALERT TIME IS OVER ###########
				try {
					//##### READ ALL PARAMETERS OF PREVIOUS XML_DATA #####/					
					//System.out.println("Zero");
					//String strLine1;
					//strLine1 = read_file_string(current_path);		 /******* GET LINE STRING **********/
					if(alert_variables.temp_alert_movement.get(imei)!=null)
					{
						strLine1 = alert_variables.temp_alert_movement.get(imei);	
						
						//######### GET XML PARAMETERS ###########/
						prev_imei = getXmlAttribute(strLine1,"imei=\"[^\"]+");
						prev_date = getXmlAttribute(strLine1,"datetime=\"[^\"]+");
						prev_sts = getXmlAttribute(strLine1,"sts=\"[^\"]+");
						prev_lat = getXmlAttribute(strLine1,"lat=\"[^\"]+");					
						prev_lng = getXmlAttribute(strLine1,"lng=\"[^\"]+");
						prev_location = getXmlAttribute(strLine1,"location=\"[^\"]+");
						prev_nearest_landmark = getXmlAttribute(strLine1,"nearest_landmark=\"[^\"]+");
						//String prev_engine_io_value = getXmlAttribute(strLine1,""+engine_io+"\"=\"[^\"]+");
						prev_movement_status = getXmlAttribute(strLine1,"movement_status=\"[^\"]+");							
						prev_movement_numeric = Integer.parseInt(prev_movement_status);													
		
						if(prev_movement_numeric==1)
						{
							line = "<marker imei="+q+prev_imei+q+" lat="+q+prev_lat+q+" lng="+q+prev_lng+q+" sts="+q+prev_sts+q+" datetime="+q+prev_date+q+" location="+q+prev_location+q+" nearest_landmark="+q+prev_nearest_landmark+q+" movement_status="+q+movement_stop+q+"/>";
							update_alert_status(line, imei, q, alert_type);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//######## IF ALERT TIME OVER ENDS
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		//######## VALID HOURS CLOSED
		
		//valid_time = true;
		if(valid_time)
		{
			System.out.println("IN MOVEMENT");
			
			if (alert_variables.temp_alert_movement.get(imei)==null)											/****** CREATE FILE -IF FILE DOES NOT EXIST *********/
			{
				String alert_str = read_database_alert_status(imei, alert_string, alert_type);
			
				if(alert_str==null)
				{
					//######## CREATE CURRENT FILE ###########/
					line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" movement_status="+q+movement_stop+q+"/>"; 
					alert_variables.temp_alert_movement.put(imei, line);
					//write_file_string(current_path,line,"current");	
					System.out.println("MOVEMENT1 STOP: CURRENT CREATED");					
				}
				else
				{
					//######## CREATE CURRENT FILE ###########/
					//line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" door_close_status="+q+door_close_stop+q+"/>"; 
					//System.out.println("ALERT_EXISTS_DB_DOOR1_CLOSE="+alert_str);
					alert_variables.temp_alert_movement.put(imei, alert_str);
					//write_file_string(current_path,line,"current");	
					System.out.println("MOVEMENT2 STOP: CURRENT CREATED");								
				}
			}
			else													/****** READ FILE -IF FILE EXISTS ********/
			{  
				System.out.println("File exists:Movement");
				try {
					//##### READ ALL PARAMETERS OF PREVIOUS XML_DATA #####/					
					//String strLine1;
					//strLine1 = read_file_string(current_path);		 /******* GET LINE STRING **********/
					strLine1 = alert_variables.temp_alert_movement.get(imei);	
					
					//######### GET XML PARAMETERS ###########/
					prev_imei = getXmlAttribute(strLine1,"imei=\"[^\"]+");
					prev_date = getXmlAttribute(strLine1,"datetime=\"[^\"]+");
					prev_sts = getXmlAttribute(strLine1,"sts=\"[^\"]+");
					prev_lat = getXmlAttribute(strLine1,"lat=\"[^\"]+");					
					prev_lng = getXmlAttribute(strLine1,"lng=\"[^\"]+");
					prev_location = getXmlAttribute(strLine1,"location=\"[^\"]+");
					prev_nearest_landmark = getXmlAttribute(strLine1,"nearest_landmark=\"[^\"]+");
					//String prev_engine_io_value = getXmlAttribute(strLine1,""+engine_io+"\"=\"[^\"]+");
					prev_movement_status = getXmlAttribute(strLine1,"movement_status=\"[^\"]+");							
					prev_movement_numeric = Integer.parseInt(prev_movement_status);
					
					if( (prev_movement_numeric == 0) && (!alert_variables.movement_reset_flag) )
					{
						line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" movement_status="+q+movement_stop+q+"/>";
						update_alert_status(line, imei, q, alert_type);
						alert_variables.temp_alert_movement.put(imei, strLine1);
						alert_variables.movement_reset_flag = true;
						prev_imei = imei;
						prev_date = alert_variables.datetime.get(imei);
						prev_sts = alert_variables.sts.get(imei);
						prev_lat = alert_variables.lat.get(imei).toString();					
						prev_lng = alert_variables.lng.get(imei).toString();
						prev_location = "";
						prev_nearest_landmark = "";											
						prev_movement_numeric = 0;
					}
					//System.out.println("prev_door1_close_status="+prev_door1_close_status+" ,alert_variables.datetime.get(imei)="+alert_variables.datetime.get(imei));
					//COMPARE DATES
					//######## WRITE IF CONDITION SATISFIED ############/
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try{
					if( (calculateTimeDiff(alert_variables.datetime.get(imei),alert_variables.sts.get(imei))<10) && (threshold_time>0) )
					{					
						System.out.println("ND_condition1");
						double lat_f = alert_variables.lat.get(imei);
						double lng_f = alert_variables.lng.get(imei);
	
						//######## CHECK TRIP BETWEEN SOURCE AND DESTINATION
						if( (alert_variables.source_coord.get(imei)!="") && (alert_variables.source_coord.get(imei)!="-") && (alert_variables.dest_coord.get(imei)!="") && ((alert_variables.dest_coord.get(imei)!="-")))
						{										
							source_coord = alert_variables.source_coord.get(imei).split(",");
							dest_coord = alert_variables.dest_coord.get(imei).split(",");
							
							Double lat_s = Double.parseDouble(source_coord[0].trim());
							Double lng_s = Double.parseDouble(source_coord[1].trim());
							Double lat_d = Double.parseDouble(dest_coord[0].trim());
							Double lng_d = Double.parseDouble(dest_coord[1].trim());
							double distance_source = calculateDistance(lat_f, lng_f, lat_s, lng_s);
							double distance_dest = calculateDistance(lat_f, lng_f, lat_d, lng_d);
														
							if((distance_source > threshold_distance) && (distance_dest > threshold_distance))
							{
								if(alert_variables.repetitive_alert_movement_time.get(imei)==null)
								{
									alert_variables.repetitive_alert_movement_time.put(imei,prev_date);
								}
								
								//double distance1 = calculateDistance(lat_f, lng_f, Float.parseFloat(prev_lat), Float.parseFloat(prev_lng), 'K');
								double distance1 = calculateDistance(lat_f, lng_f, Float.parseFloat(prev_lat), Float.parseFloat(prev_lng));
								
								System.out.println("ND_condition2 ,distance="+distance1+" ,prev_sts="+prev_sts+" ,av_sts="+alert_variables.sts.get(imei)+" ,prev_numeric="+prev_movement_numeric);
								
								if( (calculateTimeDiff(prev_date,alert_variables.datetime.get(imei)) >= threshold_time) && (prev_movement_numeric ==0) && (distance1 >= threshold_distance)  )
								{	
									//########## GET MSG PARAMETERS
									location = get_url_location(lat_f, lng_f);									
									nearest_landmark = get_nearest_landmark(lat_f, lng_f, "landmark");
									if(location==null)
									{
										location = nearest_landmark;
									}
									
									String time_format_str = get_datetime_format(prev_date);
									String[] time_format_parts = time_format_str.split("#");
									
									long diff_minutes = calculateTimeDiff(prev_date,alert_variables.datetime.get(imei));
									//int t = 80;	//provide in minutes
									long hrs_duration = (long) Math.floor(diff_minutes / 60);
									long mins_duration = diff_minutes % 60;					
									//##################################
									
									//for(int i=0;i<main_temp.length;i++)
									//{
										//alert_string = main_temp[i];
										//temp=split(alert_string,"#");
										//temp1 = alert_string.split("#");
										
										alert_name_device = main_temp[0];
										mobile_no_device = main_temp[1];
										email_device = main_temp[2];
										sms_status = main_temp[3];
										mail_status = main_temp[4];
									
										//msg = "Your -vehicle : "+alert_variables.vehicle_name.get(imei)+": is Moved at "+alert_variables.datetime.get(imei);
										msg = "ND  at "+location+":"+alert_variables.vehicle_name.get(imei)+","+alert_variables.transporter_name.get(imei)+" Moving since "+time_format_parts[0]+" hrs on "+time_format_parts[1]+" "+time_format_parts[2]+" "+time_format_parts[3]+" Total moving time is "+hrs_duration+" hrs "+mins_duration+" mins."+nearest_landmark+","+alert_variables.driver_mobile.get(imei);
										line = "\n<marker vehicle_name="+q+alert_variables.vehicle_name.get(imei)+q+" alert_type="+q+alert_type+q+" account_id="+q+alert_variables.account_id+q+" phone="+q+mobile_no_device+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" message="+q+msg+q+" trip_id="+q+alert_variables.trip_id.get(imei)+q+" email="+q+email_device+q+" sms_status="+q+sms_status+q+" mail_status="+q+mail_status+q+"/>\n</t1>";
									
										write_file_string(send_msg_path,line,"send_msg");
									//}
									
									//####### UPDATE STATUS
									//line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" movement_status="+q+movement_start+q+"/>";
									line = "<marker imei="+q+imei+q+" lat="+q+prev_lat+q+" lng="+q+prev_lng+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+prev_date+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" movement_status="+q+movement_start+q+"/>";
									update_alert_status(line, imei, q, alert_type);
									update_database_trigger_log(imei,alert_variables.vehicle_name.get(imei),alert_variables.trip_id.get(imei),alert_type,alert_variables.sts.get(imei),location,nearest_landmark,diff_minutes,prev_date,alert_variables.datetime.get(imei),alert_variables.account_id);
									
									alert_variables.repetitive_alert_movement_time.put(imei, alert_variables.datetime.get(imei));
									//alert_variables.repetitive_alert_movement_location.put(imei, location);
									//alert_variables.repetitive_alert_movement_landmark.put(imei, nearest_landmark);	
									
									System.out.println("MOVEMENT: SEND OK");							
								}					
								else if( (distance1 >= threshold_distance) && (prev_movement_numeric ==1))	//REPETITVE ALERT GENERATION-HALT2_START
								{
									//System.out.println("In repetitive:ND, alert_variables.repetitive_alert_movement_time.get(imei)="+alert_variables.repetitive_alert_movement_time.get(imei)+" ,repetitive_threshold_time="+repetitive_threshold_time);
									if(calculateTimeDiff(alert_variables.repetitive_alert_movement_time.get(imei),alert_variables.datetime.get(imei)) >= repetitive_threshold_time)
									{
										//########## GET MSG PARAMETERS
										location = get_url_location(lat_f, lng_f);							
										nearest_landmark = get_nearest_landmark(lat_f, lng_f, "landmark");
										if(location==null)
										{
											location = nearest_landmark;
										}
										
										String time_format_str = get_datetime_format(prev_date);
										String[] time_format_parts = time_format_str.split("#");
										
										long diff_minutes = calculateTimeDiff(prev_date,alert_variables.datetime.get(imei));
										//int t = 80;	//provide in minutes
										long hrs_duration = (long) Math.floor(diff_minutes / 60);
										long mins_duration = diff_minutes % 60;					
										//##################################
										
										//for(int i=0;i<main_temp.length;i++)
										//{
											//alert_string = main_temp[i];
											//System.out.println("AlertStr="+alert_string);
											//temp=split(alert_string,"#");
											//temp1 = alert_string.split("#");
											
											alert_name_device = main_temp[0];
											mobile_no_device = main_temp[1];
											email_device = main_temp[2];
											sms_status = main_temp[3];
											mail_status = main_temp[4];
										
											//msg = "Your -vehicle : "+alert_variables.vehicle_name.get(imei)+": is Stopped at "+prev_date+" for more than 2 hours";
											msg = "ND  at "+location+" :"+alert_variables.vehicle_name.get(imei)+","+alert_variables.transporter_name.get(imei)+" Moving since "+time_format_parts[0]+" hrs on "+time_format_parts[1]+" "+time_format_parts[2]+" "+time_format_parts[3]+" Total moving time is "+hrs_duration+" hrs "+mins_duration+" mins."+nearest_landmark+","+alert_variables.driver_mobile.get(imei);
											line = "\n<marker vehicle_name="+q+alert_variables.vehicle_name.get(imei)+q+" alert_type="+q+alert_type+q+" account_id="+q+alert_variables.account_id+q+" phone="+q+mobile_no_device+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" message="+q+msg+q+" trip_id="+q+alert_variables.trip_id.get(imei)+q+" email="+q+email_device+q+" sms_status="+q+sms_status+q+" mail_status="+q+mail_status+q+"/>\n</t1>";
											//System.out.println("line="+line+",send_msg_path="+send_msg_path);
											write_file_string(send_msg_path,line,"send_msg");											
										//}						
										
										//####### UPDATE STATUS
										//line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+prev_nearest_landmark+q+" movement_status="+q+movement_start+q+"/>";
										line = "<marker imei="+q+imei+q+" lat="+q+prev_lat+q+" lng="+q+prev_lng+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+prev_date+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" movement_status="+q+movement_start+q+"/>";
										update_alert_status(line, imei, q, alert_type);										
										update_database_trigger_log(imei,alert_variables.vehicle_name.get(imei),alert_variables.trip_id.get(imei),alert_type,alert_variables.sts.get(imei),location,nearest_landmark,diff_minutes,prev_date,alert_variables.datetime.get(imei),alert_variables.account_id);
										
										alert_variables.repetitive_alert_movement_time.put(imei, alert_variables.datetime.get(imei));
										
										System.out.println("REPETITIVE ALERT MOVEMENT: RSEND OK");							
									}
								}					
								else if( (distance1 < threshold_distance) && (prev_movement_numeric ==1) )
								{
									//##### UPDATE CURRENT FILE #######/
									line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" movement_status="+q+movement_stop+q+"/>"; 
									update_alert_status(line, imei, q, alert_type);
									alert_variables.temp_alert_movement.put(imei, line);
									alert_variables.repetitive_alert_movement_time.put(imei, alert_variables.datetime.get(imei));
									//System.out.println("ALERT MOVEMENT: FALSE");							
								}
								else if(distance1 < threshold_distance)
								{						
									line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" movement_status="+q+movement_stop+q+"/>";
									alert_variables.temp_alert_movement.put(imei, line);
									alert_variables.repetitive_alert_movement_time.put(imei, alert_variables.datetime.get(imei));
								}
							}
						}
					}
				} 
				catch(Exception e) 
				{ 
					//System.out.println("EXCEPTION-DOOR-1 CLOSED PREV FILE READ:"+e.getMessage()); 
				}
			}
		}
	}
	
	/************** PROCESS- ALERT BATTERY DISCONNECTED ***************/
	public static void alert_battery_disconnected_process(String imei, String alert_type)
	{
		//System.out.println("IN BATTERY DISCONNECTED");			//ENGINE IO DOES NOT NEED TO FETCH FROM FILE
		//String alert_type = "battery_disconnected";
		String alert_string = "", folder_date="", current_path ="", current_file="", location="", nearest_landmark="" ,send_msg_path ="", send_msg_date="";
		String[] main_temp;
		String[] temp1;
		String[] temp2;
		String[] temp3;
		String[] source_coord;
		String[] dest_coord;
		//float engine_io_value = 0.0f;
		float battery_threshold = 9.0f;
		int threshold_time = 2;  //in minutes
		double threshold_distance = 0.30000;	//300 meters
		int repetitive_threshold_time = 30;
		String line ="", msg="", battery_disconnected_start="1", battery_disconnected_stop="0";
		String q="\"";
		String sms_status="",mail_status="";	
		String mobile_no_device="",alert_name_device="",person_name_device="",email_device="";		
		String strLine1="",prev_imei = "", prev_date = "", prev_sts = "",prev_lat = "",prev_lng = "",prev_battery_disconnected_status = "", prev_location="", prev_nearest_landmark="";
		String transporter_name="",driver_mobile="";
		int prev_battery_disconnected_status_numeric = 0;													
						
		/*//###### GET DB ENGINE IO ###########/
		engine_io_value = get_io(av, "engine");
		//av.engine_io = engine_io;*/
		main_temp = alert_variables.alert_battery_disconnected.get(imei).split("#");
		
		//####### SPLIT ALERT STRING OF DATABASE AND GET INDIVIDUAL VARIABLES #######/			
		//System.out.println("BATTERY DISCONNECTED: mobile_no="+mobile_no_device+" ,alert_duration="+alert_duration_device+" ,alert_id="+alert_id_device+" ,alert_name="+alert_name_device+" ,escalation_id="+escalation_id_device+" ,person_name="+person_name_device+" ,email="+email_device);
							
		//########## MAKE NEW DATE FOLDER -IF FOLDER DOES NOT EXISTS ########/
		//temp2 = split(av.datetime," ");
		/*temp2 = alert_variables.datetime.get(imei).split(" ");
		folder_date = temp2[0];
		String mydir1 = alert_variables.root_dir+"/current_alerts/ignition_activated";
		boolean success1 = (new File(mydir1 + "/" + folder_date)).mkdir();*/

		temp3= alert_variables.sts.get(imei).split(" ");
		send_msg_date = temp3[0];			
		String mydir2 = alert_variables.root_dir+"/send_messages";
		boolean success2 = (new File(mydir2 + "/" + send_msg_date)).mkdir();
		send_msg_path = alert_variables.root_dir+"/send_messages/"+send_msg_date+"/"+imei+".xml";			
		
		//########## CHECK IF FILE EXISTS #########/		
		/*current_file = mobile_no_device+"_"+alert_variables.imei.get(imei); 			
		current_path = alert_variables.root_dir+"/current_alerts/ignition_activated/"+folder_date+"/"+current_file+".xml";									
		File file = new File(current_path);
		boolean exists = file.exists();*/
		
		if (alert_variables.temp_alert_battery_disconnected.get(imei)==null)											/****** CREATE FILE -IF FILE DOES NOT EXIST *********/
		{
			String alert_str = read_database_alert_status(imei, alert_string, alert_type);
			if(alert_str==null)
			{
				//######## CREATE CURRENT FILE ###########/
				line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" battery_disconnected_status="+q+battery_disconnected_stop+q+"/>"; 
				alert_variables.temp_alert_battery_disconnected.put(imei, line);
				//write_file_string(current_path,line,"current");	
				//System.out.println("BATTERY DISCONNECTED: CURRENT CREATED");
			}
			else
			{
				//######## CREATE CURRENT FILE ###########/
				//line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" battery_disconnected_status="+q+battery_disconnected_stop+q+"/>"; 
				alert_variables.temp_alert_battery_disconnected.put(imei, alert_str);
				//write_file_string(current_path,line,"current");	
				//System.out.println("BATTERY DISCONNECTED: CURRENT CREATED");				
			}
		}
		else													/****** READ FILE -IF FILE EXISTS ********/
		{  
			//System.out.println("File exists");
			try {
				//##### READ ALL PARAMETERS OF PREVIOUS XML_DATA #####/					
				//String strLine1;
				//strLine1 = read_file_string(current_path);		 /******* GET LINE STRING **********/
				strLine1 = alert_variables.temp_alert_battery_disconnected.get(imei);	
				
				//######### GET XML PARAMETERS ###########/
				prev_imei = getXmlAttribute(strLine1,"imei=\"[^\"]+");
				prev_date = getXmlAttribute(strLine1,"datetime=\"[^\"]+");
				prev_sts = getXmlAttribute(strLine1,"sts=\"[^\"]+");
				prev_lat = getXmlAttribute(strLine1,"lat=\"[^\"]+");					
				prev_lng = getXmlAttribute(strLine1,"lng=\"[^\"]+");
				prev_location = getXmlAttribute(strLine1,"location=\"[^\"]+");
				prev_nearest_landmark = getXmlAttribute(strLine1,"nearest_landmark=\"[^\"]+");
				//String prev_engine_io_value = getXmlAttribute(strLine1,""+engine_io+"\"=\"[^\"]+");
				prev_battery_disconnected_status = getXmlAttribute(strLine1,"battery_disconnected_status=\"[^\"]+");							
				prev_battery_disconnected_status_numeric = Integer.parseInt(prev_battery_disconnected_status);
				
				if( (prev_battery_disconnected_status_numeric == 0) && (!alert_variables.battery_disconnect_reset_flag) )
				{
					line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" battery_disconnected_status="+q+battery_disconnected_stop+q+"/>";
					update_alert_status(line, imei, q, alert_type);
					alert_variables.temp_alert_battery_disconnected.put(imei, strLine1);
					alert_variables.battery_disconnect_reset_flag = true;
					prev_imei = imei;
					prev_date = alert_variables.datetime.get(imei);
					prev_sts = alert_variables.sts.get(imei);
					prev_lat = alert_variables.lat.get(imei).toString();					
					prev_lng = alert_variables.lng.get(imei).toString();
					prev_location = "";
					prev_nearest_landmark = "";											
					prev_battery_disconnected_status_numeric = 0;
				}						
				//System.out.println("prev_date="+prev_date+" ,curr_date="+av.datetime);
				//COMPARE DATES
				//######## WRITE IF CONDITION SATISFIED ############/
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try{
				if( (calculateTimeDiff(alert_variables.datetime.get(imei),alert_variables.sts.get(imei))<10) && (threshold_time>0) )
				{					
					double lat_f = alert_variables.lat.get(imei);
					double lng_f = alert_variables.lng.get(imei);

					//######## CHECK TRIP BETWEEN SOURCE AND DESTINATION
					if( (alert_variables.source_coord.get(imei)!="") && (alert_variables.source_coord.get(imei)!="-") && (alert_variables.dest_coord.get(imei)!="") && ((alert_variables.dest_coord.get(imei)!="-")))
					{										
						source_coord = alert_variables.source_coord.get(imei).split(",");
						dest_coord = alert_variables.dest_coord.get(imei).split(",");
						
						Double lat_s = Double.parseDouble(source_coord[0].trim());
						Double lng_s = Double.parseDouble(source_coord[1].trim());
						Double lat_d = Double.parseDouble(dest_coord[0].trim());
						Double lng_d = Double.parseDouble(dest_coord[1].trim());
						double distance_source = calculateDistance(lat_f, lng_f, lat_s, lng_s);
						double distance_dest = calculateDistance(lat_f, lng_f, lat_d, lng_d);
						
						if((distance_source > threshold_distance) && (distance_dest > threshold_distance))
						{										

							if(alert_variables.repetitive_alert_battery_disconnected_time.get(imei)==null)
							{
								alert_variables.repetitive_alert_battery_disconnected_time.put(imei,prev_date);
							}
							
							if( (calculateTimeDiff(prev_date,alert_variables.datetime.get(imei)) >= threshold_time) && (prev_battery_disconnected_status_numeric ==0) && (Float.compare(alert_variables.sup_v.get(imei), battery_threshold) < 0) )
							{
								//########## GET MSG PARAMETERS
								location = get_url_location(lat_f, lng_f);								
								nearest_landmark = get_nearest_landmark(lat_f, lng_f, "landmark");
								if(location==null)
								{
									location = nearest_landmark;
								}
								
								String time_format_str = get_datetime_format(prev_date);
								String[] time_format_parts = time_format_str.split("#");
								
								long diff_minutes = calculateTimeDiff(prev_date,alert_variables.datetime.get(imei));
								//int t = 80;	//provide in minutes
								long hrs_duration = (long) Math.floor(diff_minutes / 60);
								long mins_duration = diff_minutes % 60;					
								//##################################
								
								//for(int i=0;i<main_temp.length;i++)
								//{
									//alert_string = main_temp[i];
									//temp=split(alert_string,"#");
									//temp1 = alert_string.split("#");
									
									alert_name_device = main_temp[0];
									mobile_no_device = main_temp[1];
									email_device = main_temp[2];
									sms_status = main_temp[3];
									mail_status = main_temp[4];
								
									//msg = "Your vehicle-"+alert_variables.vehicle_name.get(imei)+" Battery DisConnected at "+prev_date;
									msg = "RD  at "+location+":"+alert_variables.vehicle_name.get(imei)+","+alert_variables.transporter_name.get(imei)+" Device removed since "+time_format_parts[0]+" hrs on "+time_format_parts[1]+" "+time_format_parts[2]+" "+time_format_parts[3]+" Total removal time is "+hrs_duration+" hrs "+mins_duration+" mins."+nearest_landmark+","+alert_variables.driver_mobile.get(imei);
									line = "\n<marker vehicle_name="+q+alert_variables.vehicle_name.get(imei)+q+" alert_type="+q+alert_type+q+" account_id="+q+alert_variables.account_id+q+" phone="+q+mobile_no_device+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" message="+q+msg+q+" trip_id="+q+alert_variables.trip_id.get(imei)+q+" email="+q+email_device+q+" sms_status="+q+sms_status+q+" mail_status="+q+mail_status+q+"/>\n</t1>";
								
									write_file_string(send_msg_path,line,"send_msg");									
								//}
								
								//######## UPDATE STATUS
								//line = "<marker imei="+q+alert_variables.imei.get(imei)+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" battery_disconnected_status="+q+battery_disconnected_start+q+"/>";								//alert_variables.repetitive_alert_battery_disconnected_time.put(imei, alert_variables.datetime.get(imei));
								line = "<marker imei="+q+alert_variables.imei.get(imei)+q+" lat="+q+prev_lat+q+" lng="+q+prev_lng+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+prev_date+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" battery_disconnected_status="+q+battery_disconnected_start+q+"/>";								//alert_variables.repetitive_alert_battery_disconnected_time.put(imei, alert_variables.datetime.get(imei));
								update_alert_status(line, imei, q, alert_type);
								update_database_trigger_log(imei,alert_variables.vehicle_name.get(imei),alert_variables.trip_id.get(imei),alert_type,alert_variables.sts.get(imei),location,nearest_landmark,diff_minutes,prev_date,alert_variables.datetime.get(imei),alert_variables.account_id);
								
								alert_variables.repetitive_alert_battery_disconnected_time.put(imei, alert_variables.datetime.get(imei));	//UPDATE TO REPETITVE VARIABLE
								//alert_variables.repetitive_alert_battery_disconnected_location.put(imei, location);
								//alert_variables.repetitive_alert_battery_disconnected_landmark.put(imei, nearest_landmark);	
								
								System.out.println("BATTERY DISCONNECTED: SEND OK");							
							}
							
							else if( (Float.compare(alert_variables.sup_v.get(imei), battery_threshold) < 0) && (prev_battery_disconnected_status_numeric ==1))	//REPETITVE ALERT GENERATION-BATTERY DISCONNECT
							{
								if(calculateTimeDiff(alert_variables.repetitive_alert_battery_disconnected_time.get(imei),alert_variables.datetime.get(imei)) >= repetitive_threshold_time)
								{							
									//########## GET MSG PARAMETERS
									//location = get_url_location(lat_f, lng_f);									
									//nearest_landmark = get_nearest_landmark(lat_f, lng_f, "landmark");						
									String time_format_str = get_datetime_format(prev_date);
									String[] time_format_parts = time_format_str.split("#");
									
									long diff_minutes = calculateTimeDiff(prev_date,alert_variables.datetime.get(imei));
									//int t = 80;	//provide in minutes
									long hrs_duration = (long) Math.floor(diff_minutes / 60);
									long mins_duration = diff_minutes % 60;					
									//##################################
									
									//for(int i=0;i<main_temp.length;i++)
									//{
										//alert_string = main_temp[i];
										//System.out.println("AlertStr="+alert_string);
										//temp=split(alert_string,"#");
										//temp1 = alert_string.split("#");
										
										alert_name_device = main_temp[0];
										mobile_no_device = main_temp[1];
										email_device = main_temp[2];
										sms_status = main_temp[3];
										mail_status = main_temp[4];
									
										//msg = "Your -vehicle : "+alert_variables.vehicle_name.get(imei)+": is Stopped at "+prev_date+" for more than 2 hours";
										msg = "RD  at "+prev_location+":"+alert_variables.vehicle_name.get(imei)+","+alert_variables.transporter_name.get(imei)+" Device removed since "+time_format_parts[0]+" hrs on "+time_format_parts[1]+" "+time_format_parts[2]+" "+time_format_parts[3]+" Total removal time is "+hrs_duration+" hrs "+mins_duration+" mins."+prev_nearest_landmark+","+alert_variables.driver_mobile.get(imei);
										line = "\n<marker vehicle_name="+q+alert_variables.vehicle_name.get(imei)+q+" alert_type="+q+alert_type+q+" account_id="+q+alert_variables.account_id+q+" phone="+q+mobile_no_device+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" message="+q+msg+q+" trip_id="+q+alert_variables.trip_id.get(imei)+q+" email="+q+email_device+q+" sms_status="+q+sms_status+q+" mail_status="+q+mail_status+q+"/>\n</t1>";
										//System.out.println("line="+line+",send_msg_path="+send_msg_path);
										write_file_string(send_msg_path,line,"send_msg");										
									//}
									
									//######## UPDATE RE-STATUS
									//line = "<marker imei="+q+alert_variables.imei.get(imei)+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+prev_location+q+" nearest_landmark="+q+prev_nearest_landmark+q+" battery_disconnected_status="+q+battery_disconnected_start+q+"/>";								//alert_variables.repetitive_alert_battery_disconnected_time.put(imei, alert_variables.datetime.get(imei));
									line = "<marker imei="+q+alert_variables.imei.get(imei)+q+" lat="+q+prev_lat+q+" lng="+q+prev_lng+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+prev_date+q+" location="+q+prev_location+q+" nearest_landmark="+q+prev_nearest_landmark+q+" battery_disconnected_status="+q+battery_disconnected_start+q+"/>";								//alert_variables.repetitive_alert_battery_disconnected_time.put(imei, alert_variables.datetime.get(imei));
									update_alert_status(line, imei, q, alert_type);
									update_database_trigger_log(imei,alert_variables.vehicle_name.get(imei),alert_variables.trip_id.get(imei),alert_type,alert_variables.sts.get(imei),prev_location,prev_nearest_landmark,diff_minutes,prev_date,alert_variables.datetime.get(imei),alert_variables.account_id);
									
									alert_variables.repetitive_alert_battery_disconnected_time.put(imei, alert_variables.datetime.get(imei));
									
									System.out.println("REPETITIVE BATTERY DISCONNECT: RSEND OK");							
								}
							}					
							else if( (Float.compare(alert_variables.sup_v.get(imei), battery_threshold) > 0) && (prev_battery_disconnected_status_numeric ==1) ) 
							{
								//##### UPDATE CURRENT FILE #######/
								line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+prev_location+q+" nearest_landmark="+q+prev_nearest_landmark+q+" battery_disconnected_status="+q+battery_disconnected_stop+q+"/>"; 
								update_alert_status(line, imei, q, alert_type);
								alert_variables.temp_alert_battery_disconnected.put(imei, line);
								alert_variables.repetitive_alert_battery_disconnected_time.put(imei, alert_variables.datetime.get(imei));
								
								//System.out.println("BATTERY DISCONNECTED: FALSE");							
							}
							else if(Float.compare(alert_variables.sup_v.get(imei), battery_threshold) > 0)
							{
								//##### UPDATE CURRENT FILE #######/
								line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" battery_disconnected_status="+q+battery_disconnected_stop+q+"/>"; 
								alert_variables.temp_alert_battery_disconnected.put(imei, line);
								alert_variables.repetitive_alert_battery_disconnected_time.put(imei, alert_variables.datetime.get(imei));
							}
								
						}
					}
				}
			} 
			catch(Exception e) 
			{ 
				//System.out.println("EXCEPTION BATTERY DISCONNECTED PREV FILE READ:"+e.getMessage()); 
			}
			
		} //ELSE CLOSED						
	}
	
	
	/************** PROCESS- ALERT BATTERY DISCONNECTED ***************/
	public static void alert_nogps_process(String imei, String alert_type)
	{
		//System.out.println("IN BATTERY DISCONNECTED");			//ENGINE IO DOES NOT NEED TO FETCH FROM FILE
		//String alert_type = "nogps";
		String alert_string = "", folder_date="", current_path ="", current_file="", location="-", nearest_landmark="-" ,send_msg_path ="", send_msg_date="";
		String[] main_temp;
		String[] temp1;
		String[] temp2;
		String[] temp3;
		String[] source_coord;
		String[] dest_coord;
		
		int threshold_time = 1;  //in minutes
		int repetitive_threshold_time = 1;
		
		if(imei.equalsIgnoreCase("777777"))
		{
			threshold_time = 1;  //in minutes
			repetitive_threshold_time = 1;
		}
		else
		{
			threshold_time = 60;  //in minutes
			repetitive_threshold_time = 30;
		}
		
		double threshold_distance = 0.30000;	//300 meters
		String line ="", msg="", nogps_start="1", nogps_stop="0";
		String q="\"";
		String sms_status="",mail_status="";	
		String mobile_no_device="",alert_name_device="",person_name_device="",email_device="";		
		String strLine1="",prev_imei = "", prev_date = "", prev_sts = "",prev_lat = "",prev_lng = "",prev_nogps_status = "", prev_location="-", prev_nearest_landmark="-";
		String transporter_name="",driver_mobile="";
		int prev_nogps_status_numeric = 0;													
		/*//###### GET DB ENGINE IO ###########/
		engine_io_value = get_io(av, "engine");
		//av.engine_io = engine_io;*/
		main_temp = alert_variables.alert_nogps.get(imei).split("#");
		
		//####### SPLIT ALERT STRING OF DATABASE AND GET INDIVIDUAL VARIABLES #######/
			
		//System.out.println("BATTERY DISCONNECTED: mobile_no="+mobile_no_device+" ,alert_duration="+alert_duration_device+" ,alert_id="+alert_id_device+" ,alert_name="+alert_name_device+" ,escalation_id="+escalation_id_device+" ,person_name="+person_name_device+" ,email="+email_device);
							
		//########## MAKE NEW DATE FOLDER -IF FOLDER DOES NOT EXISTS ########/
		//temp2 = split(av.datetime," ");
		/*temp2 = alert_variables.datetime.get(imei).split(" ");
		folder_date = temp2[0];
		String mydir1 = alert_variables.root_dir+"/current_alerts/ignition_activated";
		boolean success1 = (new File(mydir1 + "/" + folder_date)).mkdir();*/

		temp3= alert_variables.sts.get(imei).split(" ");
		send_msg_date = temp3[0];			
		String mydir2 = alert_variables.root_dir+"/send_messages";
		boolean success2 = (new File(mydir2 + "/" + send_msg_date)).mkdir();
		send_msg_path = alert_variables.root_dir+"/send_messages/"+send_msg_date+"/"+imei+".xml";			
		
		//########## CHECK IF FILE EXISTS #########/		
		/*current_file = mobile_no_device+"_"+alert_variables.imei.get(imei); 			
		current_path = alert_variables.root_dir+"/current_alerts/ignition_activated/"+folder_date+"/"+current_file+".xml";									
		File file = new File(current_path);
		boolean exists = file.exists();*/
		
		if (alert_variables.temp_alert_nogps.get(imei)==null)											/****** CREATE FILE -IF FILE DOES NOT EXIST *********/
		{
			String alert_str = read_database_alert_status(imei, alert_string, alert_type);
			if(alert_str==null)
			{
				//######## CREATE CURRENT FILE ###########/
				line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" nogps_status="+q+nogps_stop+q+"/>"; 
				alert_variables.temp_alert_nogps.put(imei, line);
				//write_file_string(current_path,line,"current");	
				//System.out.println("BATTERY DISCONNECTED: CURRENT CREATED");
			}
			else
			{
				//######## CREATE CURRENT FILE ###########/
				//line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" battery_disconnected_status="+q+battery_disconnected_stop+q+"/>"; 
				alert_variables.temp_alert_nogps.put(imei, alert_str);
				//write_file_string(current_path,line,"current");	
				//System.out.println("BATTERY DISCONNECTED: CURRENT CREATED");				
			}
		}
		else													/****** READ FILE -IF FILE EXISTS ********/
		{  
			//System.out.println("File exists");
			try {
				//##### READ ALL PARAMETERS OF PREVIOUS XML_DATA #####/					
				//String strLine1;
				//strLine1 = read_file_string(current_path);		 /******* GET LINE STRING **********/
				strLine1 = alert_variables.temp_alert_nogps.get(imei);	
				
				//######### GET XML PARAMETERS ###########/
				prev_imei = getXmlAttribute(strLine1,"imei=\"[^\"]+");
				prev_date = getXmlAttribute(strLine1,"datetime=\"[^\"]+");
				prev_sts = getXmlAttribute(strLine1,"sts=\"[^\"]+");
				prev_lat = getXmlAttribute(strLine1,"lat=\"[^\"]+");					
				prev_lng = getXmlAttribute(strLine1,"lng=\"[^\"]+");
				prev_location = getXmlAttribute(strLine1,"location=\"[^\"]+");
				prev_nearest_landmark = getXmlAttribute(strLine1,"nearest_landmark=\"[^\"]+");
				//String prev_engine_io_value = getXmlAttribute(strLine1,""+engine_io+"\"=\"[^\"]+");
				prev_nogps_status = getXmlAttribute(strLine1,"nogps_status=\"[^\"]+");							
				prev_nogps_status_numeric = Integer.parseInt(prev_nogps_status);
				
				if( (prev_nogps_status_numeric == 0) && (!alert_variables.nogps_reset_flag) )
				{
					line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" nogps_status="+q+nogps_stop+q+"/>";
					update_alert_status(line, imei, q, alert_type);
					alert_variables.temp_alert_nogps.put(imei, strLine1);
					alert_variables.nogps_reset_flag = true;
					prev_imei = imei;
					prev_date = alert_variables.datetime.get(imei);
					prev_sts = alert_variables.sts.get(imei);
					prev_lat = alert_variables.lat.get(imei).toString();					
					prev_lng = alert_variables.lng.get(imei).toString();
					prev_location = "";
					prev_nearest_landmark = "";											
					prev_nogps_status_numeric = 0;
				}				
				//System.out.println("prev_date="+prev_date+" ,curr_date="+av.datetime);
				//COMPARE DATES
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try{
				//######## WRITE IF CONDITION SATISFIED ############/									
				if( (calculateTimeDiff(alert_variables.datetime.get(imei),alert_variables.sts.get(imei))<10) && (threshold_time>0) )
				{					
					double lat_f = alert_variables.lat.get(imei);
					double lng_f = alert_variables.lng.get(imei);
					
					
					//######## CHECK TRIP BETWEEN SOURCE AND DESTINATION
					/*if( (alert_variables.source_coord.get(imei)!="") && (alert_variables.source_coord.get(imei)!="-") && (alert_variables.dest_coord.get(imei)!="") && ((alert_variables.dest_coord.get(imei)!="-")))
					{					
						source_coord = alert_variables.source_coord.get(imei).split(",");
						dest_coord = alert_variables.dest_coord.get(imei).split(",");
						
						Double lat_s = Double.parseDouble(source_coord[0].trim());
						Double lng_s = Double.parseDouble(source_coord[1].trim());
						Double lat_d = Double.parseDouble(dest_coord[0].trim());
						Double lng_d = Double.parseDouble(dest_coord[1].trim());
						double distance_source = calculateDistance(lat_f, lng_f, lat_s, lng_s);
						double distance_dest = calculateDistance(lat_f, lng_f, lat_d, lng_d);
						
						if((distance_source > threshold_distance) && (distance_dest > threshold_distance))
						{*/
							if(alert_variables.repetitive_alert_nogps_time.get(imei)==null)
							{
								alert_variables.repetitive_alert_nogps_time.put(imei,prev_date);
							}
							
							if( (calculateTimeDiff(prev_date,alert_variables.datetime.get(imei)) >= threshold_time) && (prev_nogps_status_numeric ==0) && ((prev_lat.equals("")||prev_lat.equals("-"))||(prev_lng.equals("")||prev_lng.equals("-"))) )
							{
								String time_format_str = get_datetime_format(prev_date);
								String[] time_format_parts = time_format_str.split("#");
								
								long diff_minutes = calculateTimeDiff(prev_date,alert_variables.datetime.get(imei));
								//int t = 80;	//provide in minutes
								long hrs_duration = (long) Math.floor(diff_minutes / 60);
								long mins_duration = diff_minutes % 60;					
								//##################################
								
								//for(int i=0;i<main_temp.length;i++)
								//{
									//alert_string = main_temp[i];
									//temp=split(alert_string,"#");
									//temp1 = alert_string.split("#");
									
									alert_name_device = main_temp[0];
									mobile_no_device = main_temp[1];
									email_device = main_temp[2];
									sms_status = main_temp[3];
									mail_status = main_temp[4];
								
									//msg = "Your vehicle-"+alert_variables.vehicle_name.get(imei)+" GPS disconnected at "+prev_date+" for more than 1 hour";
									msg = "NG60  at "+location+":"+alert_variables.vehicle_name.get(imei)+","+alert_variables.transporter_name.get(imei)+" No GPS connectivity since "+time_format_parts[0]+" hrs on "+time_format_parts[2]+" "+time_format_parts[3]+" "+time_format_parts[4]+" Total No GPS connectivity is "+hrs_duration+" hrs "+mins_duration+" mins."+nearest_landmark+","+alert_variables.driver_mobile.get(imei);
									line = "\n<marker vehicle_name="+q+alert_variables.vehicle_name.get(imei)+q+" alert_type="+q+alert_type+q+" account_id="+q+alert_variables.account_id+q+" phone="+q+mobile_no_device+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" message="+q+msg+q+" trip_id="+q+alert_variables.trip_id.get(imei)+q+" email="+q+email_device+q+" sms_status="+q+sms_status+q+" mail_status="+q+mail_status+q+"/>\n</t1>";
								
									write_file_string(send_msg_path,line,"send_msg");
									alert_variables.repetitive_alert_nogps_time.put(imei, alert_variables.sts.get(imei));	//REPETITIVE NO GPS
								//}
								
								//##### UPDATE STATUS #######/
								//line = "<marker imei="+q+alert_variables.imei.get(imei)+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" nogps_status="+q+nogps_start+q+"/>"; 
								line = "<marker imei="+q+alert_variables.imei.get(imei)+q+" lat="+q+prev_lat+q+" lng="+q+prev_lng+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+prev_date+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" nogps_status="+q+nogps_start+q+"/>";
								update_alert_status(line, imei, q, alert_type);
								update_database_trigger_log(imei,alert_variables.vehicle_name.get(imei),alert_variables.trip_id.get(imei),alert_type,alert_variables.sts.get(imei),location,nearest_landmark,diff_minutes,prev_date,alert_variables.datetime.get(imei),alert_variables.account_id);
								
								alert_variables.repetitive_alert_battery_disconnected_time.put(imei, alert_variables.datetime.get(imei));
								//alert_variables.repetitive_alert_battery_disconnected_location.put(imei, location);
								//alert_variables.repetitive_alert_battery_disconnected_landmark.put(imei, nearest_landmark);	
																
								System.out.println("GPS DISCONNECTED: SEND OK");							
							}
							
							else if( ((prev_lat.equals("")||prev_lat.equals("-"))||(prev_lng.equals("")||prev_lng.equals("-"))) && (prev_nogps_status_numeric ==1))	//REPETITVE ALERT GENERATION-BATTERY DISCONNECT
							{
								if(calculateTimeDiff(alert_variables.repetitive_alert_nogps_time.get(imei),alert_variables.datetime.get(imei)) >= repetitive_threshold_time)
								{							
									//########## GET MSG PARAMETERS
									//location = get_url_location(lat_f, lng_f);								
									//nearest_landmark = get_nearest_landmark(lat_f, lng_f, "landmark");						
									String time_format_str = get_datetime_format(prev_date);
									String[] time_format_parts = time_format_str.split("#");
									
									long diff_minutes = calculateTimeDiff(prev_date,alert_variables.datetime.get(imei));
									//int t = 80;	//provide in minutes
									long hrs_duration = (long) Math.floor(diff_minutes / 60);
									long mins_duration = diff_minutes % 60;					
									//##################################
									
									//for(int i=0;i<main_temp.length;i++)
									//{
										//alert_string = main_temp[i];
										//System.out.println("AlertStr="+alert_string);
										//temp=split(alert_string,"#");
										//temp1 = alert_string.split("#");
										
										alert_name_device = main_temp[0];
										mobile_no_device = main_temp[1];
										email_device = main_temp[2];
										sms_status = main_temp[3];
										mail_status = main_temp[4];
									
										//msg = "Your -vehicle : "+alert_variables.vehicle_name.get(imei)+": is Stopped at "+prev_date+" for more than 2 hours";
										msg = "NG60  at "+prev_location+":"+alert_variables.vehicle_name.get(imei)+","+alert_variables.transporter_name.get(imei)+" No GPS connectivity since "+time_format_parts[0]+" hrs on "+time_format_parts[1]+" "+time_format_parts[2]+" "+time_format_parts[3]+" Total No GPS connectivity is "+hrs_duration+" hrs "+mins_duration+" mins."+prev_nearest_landmark+","+alert_variables.driver_mobile.get(imei);
										line = "\n<marker vehicle_name="+q+alert_variables.vehicle_name.get(imei)+q+" alert_type="+q+alert_type+q+" account_id="+q+alert_variables.account_id+q+" phone="+q+mobile_no_device+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" message="+q+msg+q+" trip_id="+q+alert_variables.trip_id.get(imei)+q+" email="+q+email_device+q+" sms_status="+q+sms_status+q+" mail_status="+q+mail_status+q+"/>\n</t1>";
										//System.out.println("line="+line+",send_msg_path="+send_msg_path);
										write_file_string(send_msg_path,line,"send_msg");										
									//}
									
									//##### UPDATE STATUS #######/
									//line = "<marker imei="+q+alert_variables.imei.get(imei)+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+prev_location+q+" nearest_landmark="+q+prev_nearest_landmark+q+" nogps_status="+q+nogps_start+q+"/>"; 
									line = "<marker imei="+q+alert_variables.imei.get(imei)+q+" lat="+q+prev_lat+q+" lng="+q+prev_lng+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+prev_date+q+" location="+q+prev_location+q+" nearest_landmark="+q+prev_nearest_landmark+q+" nogps_status="+q+nogps_start+q+"/>";
									update_alert_status(line, imei, q, alert_type);									
									update_database_trigger_log(imei,alert_variables.vehicle_name.get(imei),alert_variables.trip_id.get(imei),alert_type,alert_variables.sts.get(imei),prev_location,prev_nearest_landmark,diff_minutes,prev_date,alert_variables.datetime.get(imei),alert_variables.account_id);
									
									alert_variables.repetitive_alert_nogps_time.put(imei, alert_variables.datetime.get(imei));
									
									System.out.println("REPETITIVE BATTERY DISCONNECT: RSEND OK");							
								}
							}					
							else if( ( (!prev_lat.equals("")||!prev_lat.equals("-"))||(!prev_lng.equals("")||!prev_lng.equals("-"))) && (prev_nogps_status_numeric ==1) ) 
							{
								//##### UPDATE CURRENT FILE #######/
								line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" nogps_status="+q+nogps_stop+q+"/>"; 
								alert_variables.temp_alert_nogps.put(imei, line);
								//System.out.println("BATTERY DISCONNECTED: FALSE");
								//########## GET MSG PARAMETERS
								if((prev_lat!="") && (prev_lng!=""))
								{									
									if((alert_variables.repetitive_alert_nogps_location.get(imei)==null) && (alert_variables.repetitive_alert_nogps_landmark.get(imei)==null))
									{
										location = get_url_location(lat_f, lng_f);								
										nearest_landmark = get_nearest_landmark(lat_f, lng_f, "landmark");
										if(location==null)
										{
											location = nearest_landmark;
										}
										
										alert_variables.repetitive_alert_nogps_location.put(imei,location);
										alert_variables.repetitive_alert_nogps_landmark.put(imei,nearest_landmark);
										alert_variables.repetitive_alert_nogps_time.put(imei, alert_variables.datetime.get(imei));
									}
								}
								
							}
						}
					//}
				//}
			} 
			catch(Exception e) 
			{ 
				//System.out.println("EXCEPTION BATTERY DISCONNECTED PREV FILE READ:"+e.getMessage()); 
			}
			
		} //ELSE CLOSED						
	}	
	
	
	/************* METHOD- GET XML ATTRIBUTES ************/
	public static String getXmlAttribute(String line, String param)
	{
		String str1 ="";
		String value ="";
		String[] str2;
		
		try {
			Pattern p = Pattern.compile(param);
			Matcher matcher = p.matcher(line);				
			
			while(matcher.find()){
						
				str1 = matcher.group().toString().replace("\"","");
				str2 = str1.split("=");
				//System.out.println(str2[1]);
				value = str2[1];
				//System.out.println("val="+value);
				break;
			}
		} catch(Exception e) { 
			//System.out.println("Line:"+line+" ,Error in function-Xml Attribute:"+e.getMessage());
			}
		
		return value;		
	}	
		
	/************* METHOD- READ CONTENT OF FILE ************/
	public String read_file_content(String current_file)
	{
		String strLine1 ="",file_content = "";
		
		try{
			FileInputStream fstream1 = new FileInputStream(current_file);
			// Get the object of DataInputStream
			DataInputStream in1 = new DataInputStream(fstream1);
			BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));	
			
			while ((strLine1 = br1.readLine()) != null) 
			{																																						
				file_content = file_content + strLine1;							
			}
		
			fstream1.close();
			in1.close();

		} catch(Exception e) {
			//System.out.println("Exception2 in line Read:"+e.getMessage());
			}	
		
		return file_content;
	}	

	
	//############ IF CONTENT EXIST IN FILE
	public static boolean IfExist_xmlAttribute(String line, String param)
	{
		String str1 ="";
		String value ="";
		String[] str2;
		
		try {
			//System.out.println("LINE:"+line+" ,param="+param);
			Pattern p = Pattern.compile(param);
			Matcher matcher = p.matcher(line);				
			
			while(matcher.find()){
						
				//str1 = matcher.group().toString().replace("\"","");
				//str2 = str1.split("=");
				//System.out.println("MATCH str1");
				//value = str2[1];
				//System.out.println("val="+value);
				return true;
				//break;
			}
		} catch(Exception e) { 
			//System.out.println("Error in function-Xml Attribute"+e.getMessage());
			}
		
		return false;		
	}	
	
	/************* METHOD- CALCULATE DISTANCE ************/
	public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
		Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;

		int meterConversion = 1609;

		double meters = new Double(dist * meterConversion).doubleValue();
		return (meters/1000.0);
	}
	
   /*public static double calculateDistance(double lat1, double lon1, double lat2, double lon2, char unit) {
	   double theta = lon1 - lon2;
	   double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
	   dist = Math.acos(dist);
	   dist = rad2deg(dist);
	   dist = dist * 60 * 1.1515;
	   if (unit == 'K') {
	     dist = dist * 1.609344;
	   } else if (unit == 'N') {
	     dist = dist * 0.8684;
	     }
	   return (dist);
	 }
   
   public static double rad2deg(double rad) {
	     return (rad * 180 / Math.PI);
   }
   
   public static double deg2rad(double deg) {
	 return (deg * Math.PI / 180.0);
   }*/	
	
	/************* METHOD- CALCULATE TIME DIFFERENCE ************/
	public static long calculateTimeDiff(String time1, String time2)
	{
		//System.out.println("Time1="+time1+" ,Time2="+time2);
		//System.out.println();		
		/*if(time1.equalsIgnoreCase(""))
		{
			return 600;
		}*/
		
		try{
			int timediff=0;
			String[] temp,temp1,temp2;
			Calendar calendar1 = Calendar.getInstance();
			Calendar calendar2 = Calendar.getInstance();
	
			/*temp=split(time1," ");
			temp1=split(temp[0],"-");
			temp2=split(temp[1],":");*/
			
			temp= time1.split(" ");
			temp1=temp[0].split("-");
			temp2=temp[1].split(":");
			
	
			calendar1.set(Integer.parseInt(temp1[0]), Integer.parseInt(temp1[1]), Integer.parseInt(temp1[2]) , Integer.parseInt(temp2[0]), Integer.parseInt(temp2[1]), Integer.parseInt(temp2[2]) );
	
			/*temp=split(time2," ");
			temp1=split(temp[0],"-");
			temp2=split(temp[1],":");*/
			
			temp=time2.split(" ");
			temp1=temp[0].split("-");
			temp2=temp[1].split(":");
			
	
			calendar2.set(Integer.parseInt(temp1[0]), Integer.parseInt(temp1[1]), Integer.parseInt(temp1[2]) , Integer.parseInt(temp2[0]), Integer.parseInt(temp2[1]), Integer.parseInt(temp2[2]) );
	
			long milliseconds1 = calendar1.getTimeInMillis();
			long milliseconds2 = calendar2.getTimeInMillis();
			long diff = milliseconds2 - milliseconds1;
			long diffMinutes = diff / (60 * 1000);
	
			//System.out.println("Time in minutes2: " + diffMinutes + " minutes.");        		
			//"yyyy-MM-dd HH:mm:ss"
			return diffMinutes;
		}catch(Exception e){System.out.println("Exception in time");}
		
		return 0;
	}

	/************* METHOD- ROUND TO TWO DECIMAL ************/
	public static double roundTwoDecimals(double d) 
	{
    	DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}
	

	
		
	/************* METHOD- WRITE FILE STRING ************/
	public static void write_file_string(String xml_path, String xml_line, String type)
	{
		RandomAccessFile raf1 =null;						// IF TYPE=CURRENT , WHOLE STRING WILL COME
		BufferedWriter out1 =null;							// IF TYPE=SEND_MSG, SECOND HALF STRING WILL COME
		BufferedWriter out2 =null;
		String marker1="";

		//System.out.println("type="+type);
		//System.out.println("xml_path="+xml_path+" ,xml_line="+xml_line+" ,type="+type);
		
		if(type.equalsIgnoreCase("current") || (type.equalsIgnoreCase("temp_variables_itc")))				//## WRITE STRING -ONE TIME 
		{
			try{
			out1 = new BufferedWriter(new FileWriter(xml_path, false));																														
			out1.write(xml_line);
			out1.close();
			}catch(Exception e) {
				//System.out.println("EXCEPTION IN CURRENT FILE WRITE:"+e.getMessage());
				}
		}
		
		else if( (type.equalsIgnoreCase("send_msg")) || (type.equalsIgnoreCase("temp_escalations")) || (type.equalsIgnoreCase("temp_landmarks")) || (type.equalsIgnoreCase("temp_regions")))	//## WRITE STRING -MULTIPLE TIMES
		{			
			try{
				raf1 = new RandomAccessFile(xml_path, "rw");
				long length1 = raf1.length();

				//if(type.equals("send_msg"))
					//System.out.println("###############RAF LENGTH="+raf1.length());
				
				if(length1==0)
				{
					out1 = new BufferedWriter(new FileWriter(xml_path, false));		//CREATE FILE		
					marker1 = "<t1>";
					out1.write(marker1);
					out1.close();
				}	

				else
				{
					//System.out.println("File Length="+raf1.length());
					raf1.setLength(length1 - 6);
				}

				raf1.close();
			}catch(Exception e1){
				//System.out.println("EXCEPTION IN SEND MSG WRITE-1:"+e1.getMessage());
				}		
			
			
			try{
				//System.out.println("xml_path="+xml_path);
				out2 = new BufferedWriter(new FileWriter(xml_path, true));			 // UPDATE FILE															
				out2.write(xml_line);
				out2.close();
			}catch(Exception e2){
				//System.out.println("EXCEPTION IN SEND MSG WRITE-2:"+e2.getMessage());
				}				
		}
	}
		
	/******* READ AND SET ESCALATION VARIABLES - METHOD BODY **************/
	public static void escalation_read_set_variables(String escalation_path, String landmark_path, String region_path, String imei)
	{
		String strLine1 ="";
		String alert_name_tmp = "", person_mobile_tmp = "", alert_duration_tmp = "", alert_id_tmp = "";
		String escalation_id_tmp = "", person_name_tmp = "", email_tmp = "", tmp_string ="", sms_status ="", mail_status ="";
		String landmark_id_tmp = "", landmark_name_tmp = "", landmark_coord_tmp = "", distance_variable_tmp="";
		String transporter_tmp ="",driver_mob_tmp ="";
		
		boolean halt_start_flag = false, halt1_start_flag = false, halt2_start_flag = false, movement_flag=false, nogps_flag=false, halt_stop_flag = false, ignition_activated_flag = false, ignition_deactivated_flag = false,sos_flag = false, over_temperature_flag = false, overspeed_flag = false, battery_connected_flag = false, battery_disconnected_flag = false, door1_open_flag = false, door1_close_flag = false, door2_open_flag = false, door2_close_flag = false, ac_on_flag = false,ac_off_flag = false, entered_region_flag = false, exited_region_flag = false;
		
		collect_data_main.conn_remote = null;		   
		try{
		      //STEP 2: Register JDBC driver
		  try {
			Class.forName("com.mysql.jdbc.Driver");
		  } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
		  //STEP 3: Open a connection
		  	  System.out.println("Connection to Remote database-ok");
		  	collect_data_main.conn_remote = DriverManager.getConnection(collect_data_main.DB_URL_remote,collect_data_main.USER,collect_data_main.PASS);
		 }catch(SQLException se){}
		 //######### DATABASE CONNECTION
		
		 collect_data_main.stmt_remote = null;
		   try{
		      //STEP 2: Register JDBC driver

		      //STEP 4: Execute a query
			  // System.out.println("Selecting data...");
			   collect_data_main.stmt_remote = collect_data_main.conn_remote.createStatement();
		      String sql_remote;
		      sql_remote = "SELECT * FROM escalation_detail_icici";
		      //System.out.println("SQL="+sql);
		      ResultSet rs_remote = collect_data_main.stmt.executeQuery(sql_remote);

  	          String tmp_string_halt_start ="", tmp_string_halt1_start ="", tmp_string_halt2_start ="", tmp_string_movement ="", tmp_string_nogps ="",tmp_string_halt_stop ="", tmp_string_ignition_activated ="", tmp_string_ignition_deactivated ="", tmp_string_sos ="", tmp_string_over_temperature ="", tmp_string_overspeed ="", tmp_string_battery_connected ="", tmp_string_battery_disconnected ="", tmp_string_door1_open ="", tmp_string_door1_closed ="", tmp_string_door2_open ="", tmp_string_door2_closed ="", tmp_string_ac_on ="", tmp_string_ac_off ="", tmp_string_exited_region="";		

		      //STEP 5: Extract data from result set
		      while(rs_remote.next()){
		         //Retrieve by column name		         	        
		         //Display values
		         //System.out.print("Alert_str=" + alert_str);		   	
																					
		         try{						
					//System.out.println(strLine1);
					alert_name_tmp = rs_remote.getString("alert_type");
					person_mobile_tmp = rs_remote.getString("mobile");					
					email_tmp = rs_remote.getString("email");
					sms_status = "1";
					mail_status = "1";
					
					if(person_mobile_tmp.equals(""))
					{
						sms_status = "0";
					}
					if(email_tmp.equals(""))
					{
						mail_status = "0";
						email_tmp = "-";
					}					
						
					/********* STORE IN ARRAY LISTS ********/
					if(alert_name_tmp.equalsIgnoreCase("S120"))
					{
						//####### ALERT HALT START #######/
						//System.out.println("IN Halt1_Start:count");
						halt1_start_flag = true;
						tmp_string_halt1_start = alert_name_tmp+"#"+person_mobile_tmp+"#"+email_tmp+"#"+sms_status+"#"+mail_status;
						System.out.println("\nS120:"+tmp_string_halt1_start);
						alert_variables.alert_halt1_start_flag.put(imei, true);
						alert_variables.alert_halt1_start.put(imei,tmp_string_halt1_start); 
						//System.out.println("TMP_STRING_HALT1_STRING="+tmp_string_halt1_start);
					}
					else if(alert_name_tmp.equalsIgnoreCase("S30"))
					{
						//####### ALERT HALT START #######/
						halt2_start_flag = true;
						tmp_string_halt2_start = alert_name_tmp+"#"+person_mobile_tmp+"#"+email_tmp+"#"+sms_status+"#"+mail_status;
						alert_variables.alert_halt2_start_flag.put(imei, true);
						alert_variables.alert_halt2_start.put(imei,tmp_string_halt2_start); 
					}

					else if(alert_name_tmp.equalsIgnoreCase("ND"))
					{
						/*******ALERT BATTERY DISCONNTED********/
						//System.out.println("Mov1");
						movement_flag = true;
						tmp_string_movement = alert_name_tmp+"#"+person_mobile_tmp+"#"+email_tmp+"#"+sms_status+"#"+mail_status;
						alert_variables.alert_movement_flag.put(imei, true);
						alert_variables.alert_movement.put(imei,tmp_string_movement); 				
					}
					else if(alert_name_tmp.equalsIgnoreCase("RD"))
					{
						/*******ALERT BATTERY DISCONNTED********/
						battery_disconnected_flag = true;
						tmp_string_battery_disconnected = alert_name_tmp+"#"+person_mobile_tmp+"#"+email_tmp+"#"+sms_status+"#"+mail_status;
						alert_variables.alert_battery_disconnected_flag.put(imei, true);
						alert_variables.alert_battery_disconnected.put(imei,tmp_string_battery_disconnected); 						
					}
					
					else if(alert_name_tmp.equalsIgnoreCase("NG60"))
					{
						/*******ALERT NOGPS********/
						//System.out.println("NG1");
						nogps_flag = true;
						tmp_string_nogps = alert_name_tmp+"#"+person_mobile_tmp+"#"+email_tmp+"#"+sms_status+"#"+mail_status;
						alert_variables.alert_nogps_flag.put(imei, true);
						alert_variables.alert_nogps.put(imei,tmp_string_nogps);
					}

					else if(alert_name_tmp.equalsIgnoreCase("DFG"))
					{
						//##### ALERT EXITED REGION #######/						
						exited_region_flag = true;
						tmp_string_exited_region = alert_name_tmp+"#"+person_mobile_tmp+"#"+email_tmp+"#"+sms_status+"#"+mail_status;
						alert_variables.alert_exited_region_flag.put(imei, true);
						alert_variables.alert_exited_region.put(imei,tmp_string_nogps); 										
					}
					
				} catch(Exception e) {
					//System.out.println("EXCEPTION-2 IN READ ESCALATION FILE:"+e.getMessage());
				}
		  } //WHILE CLOSED
		      	      					
		/*} catch(Exception e) {
			//System.out.println("EXCEPTION-1 IN READ ESCALATION FILE:"+e.getMessage());
		}*/			
	  //STEP 6: Clean-up environment
	  rs_remote.close();
	  collect_data_main.stmt_remote.close();
	  collect_data_main.conn_remote.close();
	  }catch(SQLException se2){}	
			  
		//######## VEHICLE GEOFENCE ##########
		try{
			FileInputStream fstream1 = new FileInputStream(region_path);
			DataInputStream in1 = new DataInputStream(fstream1);
			BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));
			String vehicle_geo_name_tmp = "",vehicle_geo_coord_tmp ="";
									
			while ((strLine1 = br1.readLine()) != null) 
			{																																						
				int len = strLine1.length();
				
				if(len < 100)
				{
					continue;
				}													
				try{						
					//System.out.println(strLine1);
					
					vehicle_geo_name_tmp = getXmlAttribute(strLine1,"vehicle_geo_name=\"[^\"]+");
					vehicle_geo_coord_tmp = getXmlAttribute(strLine1,"vehicle_geo_coord=\"[^\"]+");					
					
					alert_variables.region_name.put(imei, vehicle_geo_name_tmp);
					alert_variables.region_coord.put(imei, vehicle_geo_name_tmp);
					
				}catch(Exception e2){}
			}
		}catch(Exception e2){}
	}
	
	/*public static void escalation_read_set_variables(String escalation_path, String landmark_path, String region_path, String imei)
	{
		String strLine1 ="";
		String alert_name_tmp = "", person_mobile_tmp = "", alert_duration_tmp = "", alert_id_tmp = "";
		String escalation_id_tmp = "", person_name_tmp = "", email_tmp = "", tmp_string ="", sms_status ="", mail_status ="";
		String landmark_id_tmp = "", landmark_name_tmp = "", landmark_coord_tmp = "", distance_variable_tmp="";
		String transporter_tmp ="",driver_mob_tmp ="";
		
		boolean halt_start_flag = false, halt1_start_flag = false, halt2_start_flag = false, movement_flag=false, nogps_flag=false, halt_stop_flag = false, ignition_activated_flag = false, ignition_deactivated_flag = false,sos_flag = false, over_temperature_flag = false, overspeed_flag = false, battery_connected_flag = false, battery_disconnected_flag = false, door1_open_flag = false, door1_close_flag = false, door2_open_flag = false, door2_close_flag = false, ac_on_flag = false,ac_off_flag = false, entered_region_flag = false, exited_region_flag = false;
				
		//System.out.println("escalation_path="+escalation_path);
		try{
			FileInputStream fstream1 = new FileInputStream(escalation_path);
			DataInputStream in1 = new DataInputStream(fstream1);
			BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));	
			
			String tmp_string_halt_start ="", tmp_string_halt1_start ="", tmp_string_halt2_start ="", tmp_string_movement ="", tmp_string_nogps ="",tmp_string_halt_stop ="", tmp_string_ignition_activated ="", tmp_string_ignition_deactivated ="", tmp_string_sos ="", tmp_string_over_temperature ="", tmp_string_overspeed ="", tmp_string_battery_connected ="", tmp_string_battery_disconnected ="", tmp_string_door1_open ="", tmp_string_door1_closed ="", tmp_string_door2_open ="", tmp_string_door2_closed ="", tmp_string_ac_on ="", tmp_string_ac_off ="", tmp_string_exited_region="";		
						
			while ((strLine1 = br1.readLine()) != null) 
			{																																						
				int len = strLine1.length();
				
				if(len < 100)
				{
					continue;
				}													
				try{						
					//System.out.println(strLine1);
					
					alert_name_tmp = getXmlAttribute(strLine1,"alert_name=\"[^\"]+");
					person_mobile_tmp = getXmlAttribute(strLine1,"person_mobile=\"[^\"]+");					
					email_tmp = getXmlAttribute(strLine1,"person_email=\"[^\"]+");
					sms_status = getXmlAttribute(strLine1,"sms_status=\"[^\"]+");
					mail_status = getXmlAttribute(strLine1,"mail_status=\"[^\"]+");
					
					if(email_tmp.equalsIgnoreCase(""))
					{
						email_tmp = "-";
					}
						
					//********* STORE IN ARRAY LISTS ********
					if(alert_name_tmp.equalsIgnoreCase("S120"))
					{
						//####### ALERT HALT START #######/
						//System.out.println("IN Halt1_Start:count");
						halt1_start_flag = true;
						tmp_string_halt1_start += alert_name_tmp+"#"+person_mobile_tmp+"#"+email_tmp+"#"+sms_status+"#"+mail_status+"$";
						//System.out.println("TMP_STRING_HALT1_STRING="+tmp_string_halt1_start);
					}
					if(alert_name_tmp.equalsIgnoreCase("S30"))
					{
						//####### ALERT HALT START #######/
						halt2_start_flag = true;
						tmp_string_halt2_start += alert_name_tmp+"#"+person_mobile_tmp+"#"+email_tmp+"#"+sms_status+"#"+mail_status+"$";
					}

					if(alert_name_tmp.equalsIgnoreCase("ND"))
					{
						//*******ALERT BATTERY DISCONNTED********
						//System.out.println("Mov1");
						movement_flag = true;
						tmp_string_movement += alert_name_tmp+"#"+person_mobile_tmp+"#"+email_tmp+"#"+sms_status+"#"+mail_status+"$";
					}
					if(alert_name_tmp.equalsIgnoreCase("RD"))
					{
						///*******ALERT BATTERY DISCONNTED********
						battery_disconnected_flag = true;
						tmp_string_battery_disconnected += alert_name_tmp+"#"+person_mobile_tmp+"#"+email_tmp+"#"+sms_status+"#"+mail_status+"$";
					}
					
					if(alert_name_tmp.equalsIgnoreCase("NG60"))
					{
						//*******ALERT NOGPS********
						//System.out.println("NG1");
						nogps_flag = true;
						tmp_string_nogps += alert_name_tmp+"#"+person_mobile_tmp+"#"+email_tmp+"#"+sms_status+"#"+mail_status+"$";
					}

					if(alert_name_tmp.equalsIgnoreCase("DFG"))
					{
						//##### ALERT EXITED REGION #######/						
						exited_region_flag = true;
						tmp_string_exited_region += alert_name_tmp+"#"+person_mobile_tmp+"#"+email_tmp+"#"+sms_status+"#"+mail_status+"$";						
					}
					
				} catch(Exception e) {
					//System.out.println("EXCEPTION-2 IN READ ESCALATION FILE:"+e.getMessage());
					}
			} //WHILE CLOSED
		
			fstream1.close();
			in1.close();
			
			//####### PUT VALUES AGAINST IMEI						
			if(halt1_start_flag)
			{
				//######## ALERT HALT1 STAR ########/							
				tmp_string_halt1_start = tmp_string_halt1_start.substring(0,tmp_string_halt1_start.length()-1);
				//System.out.println("H1_After="+tmp_string_halt1_start);
				alert_variables.alert_halt1_start_flag.put(imei, true);
				alert_variables.alert_halt1_start.put(imei,tmp_string_halt1_start); 				
			}
			if(halt2_start_flag)
			{
				//######## ALERT HALT2 STAR ########/
				//System.out.println("H2");
				tmp_string_halt2_start = tmp_string_halt2_start.substring(0,tmp_string_halt2_start.length()-1);
				alert_variables.alert_halt2_start_flag.put(imei, true);
				alert_variables.alert_halt2_start.put(imei,tmp_string_halt2_start); 
				//System.out.println("AfterH2");
			}
			//System.out.println("movement_flag="+movement_flag);
			if(movement_flag)
			{
				//######## ALERT MOVEMENT ########/	
				//System.out.println("Mov2");
				tmp_string_movement = tmp_string_movement.substring(0,tmp_string_movement.length()-1);
				alert_variables.alert_movement_flag.put(imei, true);
				alert_variables.alert_movement.put(imei,tmp_string_movement); 				
			}
			if(nogps_flag)
			{
				//######## ALERT NOGPS ########/
				//System.out.println("Nogps");
				tmp_string_nogps = tmp_string_nogps.substring(0,tmp_string_nogps.length()-1);
				alert_variables.alert_nogps_flag.put(imei, true);
				alert_variables.alert_nogps.put(imei,tmp_string_nogps); 				
			}			
			
			if(battery_disconnected_flag)
			{
				//*******ALERT BATTERY DISCONNTED********
				//System.out.println("BattDis");
				tmp_string_battery_disconnected = tmp_string_battery_disconnected.substring(0,tmp_string_battery_disconnected.length()-1);
				alert_variables.alert_battery_disconnected_flag.put(imei, true);
				alert_variables.alert_battery_disconnected.put(imei,tmp_string_battery_disconnected); 
			}
			if(exited_region_flag)
			{
				//######## ALERT EXITED REGION ########/
				System.out.println("Exited Region");
				tmp_string_exited_region = tmp_string_exited_region.substring(0,tmp_string_exited_region.length()-1);
				alert_variables.alert_exited_region_flag.put(imei, true);
				alert_variables.alert_exited_region.put(imei,tmp_string_nogps); 				
			}			

		} catch(Exception e) {
			//System.out.println("EXCEPTION-1 IN READ ESCALATION FILE:"+e.getMessage());
		}
		
		
		//######## VEHICLE GEOFENCE ##########
		try{
			FileInputStream fstream1 = new FileInputStream(region_path);
			DataInputStream in1 = new DataInputStream(fstream1);
			BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));
			String vehicle_geo_name_tmp = "",vehicle_geo_coord_tmp ="";
									
			while ((strLine1 = br1.readLine()) != null) 
			{																																						
				int len = strLine1.length();
				
				if(len < 100)
				{
					continue;
				}													
				try{						
					//System.out.println(strLine1);
					
					vehicle_geo_name_tmp = getXmlAttribute(strLine1,"vehicle_geo_name=\"[^\"]+");
					vehicle_geo_coord_tmp = getXmlAttribute(strLine1,"vehicle_geo_coord=\"[^\"]+");					
					
					alert_variables.region_name.put(imei, vehicle_geo_name_tmp);
					alert_variables.region_coord.put(imei, vehicle_geo_name_tmp);
					
				}catch(Exception e2){}
			}
		}catch(Exception e2){}
	}*/
	
	
	//############ ALERT STATUS
	public static String read_database_alert_status(String imei, String alert_string, String alert_type)
	{		
		collect_data_main.stmt = null;
	   try{
	      //STEP 2: Register JDBC driver

	      //STEP 4: Execute a query
	//      System.out.println("Selecting data...");
		  collect_data_main.stmt = collect_data_main.conn.createStatement();
	      String sql;
	      sql = "SELECT alert_string FROM alert_detail_java WHERE imei='"+imei+"' AND alert_type='"+alert_type+"'";
	      //System.out.println("SQL="+sql);
	      ResultSet rs = collect_data_main.stmt.executeQuery(sql);

	      //STEP 5: Extract data from result set
	      if(rs.next()){
	         //Retrieve by column name
	         String alert_str = rs.getString("alert_string");
	         return alert_str;
	         //Display values
	         //System.out.print("Alert_str=" + alert_str);
	      }
	      //STEP 6: Clean-up environment
	      rs.close();
	      collect_data_main.stmt.close();
	      }catch(SQLException se2){}
	    	  
		return null;
	}
	
	public static void update_database_alert_status(String imei, String alert_string, String alert_type)
	{		
		collect_data_main.stmt = null;
	   try{
	      //STEP 2: Register JDBC driver

	      //STEP 4: Execute a query
	//      System.out.println("Updating data...");
	      try {
	    	  collect_data_main.stmt = collect_data_main.conn.createStatement();
	      } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	      }
	      String sql;
	      sql = "UPDATE alert_detail_java SET alert_string='"+alert_string+"' WHERE imei='"+imei+"' AND alert_type='"+alert_type+"'";
	      collect_data_main.stmt.executeUpdate(sql);
	   }catch(SQLException e){}
	}		   
	
	public static void insert_database_alert_status(String imei, String alert_string, String alert_type)
	{		
		collect_data_main.stmt = null;
	   try{
	      //STEP 2: Register JDBC driver

	      //STEP 4: Execute a query
	    //  System.out.println("Inserting data...");
	      try {
	    	  collect_data_main.stmt = collect_data_main.conn.createStatement();
	      } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	      }
	      String sql;
	      sql = "INSERT INTO alert_detail_java(imei,alert_string,alert_type) values('"+imei+"','"+alert_string+"','"+alert_type+"')";
	      System.out.println(sql);
	      collect_data_main.stmt.executeUpdate(sql);
	   }catch(SQLException e){System.out.println("Error AlertDetailDB:"+e.getMessage());}
	}
	
	
	public static void update_database_trigger_log(String imei, String vehicle_name, String trip_id, String alert_type, String sts, String location, String nearest_landmark, long voilation_time, String start_time, String end_time, int account_id)
	{		
		collect_data_main.stmt = null;
	   try{
	      //STEP 2: Register JDBC driver

	      //STEP 4: Execute a query
	    //  System.out.println("Inserting data...");
	      try {
	    	  collect_data_main.stmt = collect_data_main.conn.createStatement();
	      } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	      }
	      String sql;
	      sql = "INSERT INTO trigger_log(imei,vehicle_name,trip_id,alert_type,sts,location,nearest_landmark,voilation_time,start_time,end_time,account_id) values('"+imei+"','"+vehicle_name+"','"+trip_id+"','"+alert_type+"','"+sts+"','"+location+"','"+nearest_landmark+"','"+voilation_time+"','"+start_time+"','"+end_time+"','"+account_id+"')";
	      System.out.println("SQL="+sql);
	      collect_data_main.stmt.executeUpdate(sql);
	   }catch(SQLException e){System.out.println("Error TriggerDB:"+e.getMessage());}
	}
	
	//########### GET GOOGLE LOCATION
	public static String get_url_location(double lat, double lng)
	{
		try {		
				String Request = "http://www.itracksolution.co.in/src/php/get_url_location2.php?lat="+lat+"&lng="+lng+"";
				//URL my_url = new URL("http://www.placeofjo.blogspot.com/");
				URL my_url = new URL(Request);
				BufferedReader br = new BufferedReader(new InputStreamReader(my_url.openStream()));
				String strTemp = "";
				while(null != (strTemp = br.readLine())){
					//System.out.println(strTemp);
					return strTemp;
				}
		} catch (Exception ex) {ex.printStackTrace();}
		
		return null;
	}
	
	//########### GET DATE TIME FORMAT
	public static String get_datetime_format(String date_str)
	{
		try{
			String[] temp1;
			String delimiter1 = " ";
			String[] temp2;
			String delimiter2 = "-";
			String[] temp3;
			String delimiter3 = ":";
					
			temp1 = date_str.split(delimiter1);
			temp2 = temp1[0].split(delimiter2);
			temp3 = temp1[1].split(delimiter3);
			
			String time1 = temp3[0]+""+temp3[1];
			String day1 = temp2[2];
			String month1 = temp2[1];
			if(month1.equals("01"))
			{
				month1 = "Jan";
			}
			else if(month1.equals("02"))
			{
				month1 = "Feb";
			}
			else if(month1.equals("03"))
			{
				month1 = "Mar";
			}
			else if(month1.equals("04"))
			{
				month1 = "Apr";
			}
			else if(month1.equals("05"))
			{
				month1 = "May";
			}
			else if(month1.equals("06"))
			{
				month1 = "Jun";
			}
			else if(month1.equals("07"))
			{
				month1 = "Jul";
			}
			else if(month1.equals("08"))
			{
				month1 = "Aug";
			}
			else if(month1.equals("09"))
			{
				month1 = "Sep";
			}
			else if(month1.equals("10"))
			{
				month1 = "Oct";
			}
			else if(month1.equals("11"))
			{
				month1 = "Nov";
			}
			else if(month1.equals("12"))
			{
				month1 = "Dec";
			}			
			String year1 = temp2[0];
			year1 = Character.toString(year1.charAt(2))+""+Character.toString(year1.charAt(3));
			String format_str = time1+"#"+day1+"#"+month1+"#"+year1;
			return format_str;
			
		}catch(Exception e){}
		
		return null;			
	}
	
	/************* METHOD- GET NEAREST LANDMARK ************/
	public static String get_nearest_landmark(double lat1, double lng1, String type)
	{
		//System.out.println("IN NEAREST LANDMARK="+landmark_path);
		
		/*double lat1=0.0;
		double lng1=0.0;
		lat1 = av.lat;
		lng1 = av.lng;*/	
		//System.out.println("AV latlng="+lat1+","+lng1);		
		
		String placename1 ="", landmark1="" , lat2="", lng2="";
		String landmark_name1 ="", landmark_coord1 ="", strLine1="";
		int i=0;
		double lowest_dist = 0.0;
		String temp[];				
		
		try{
			FileInputStream fstream1 = new FileInputStream(alert_variables.landmark_path);
			DataInputStream in1 = new DataInputStream(fstream1);
			BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));	
							
			while ((strLine1 = br1.readLine()) != null) 
			{																																						
				int len = strLine1.length();
				
				//System.out.println("LEN="+len);
				
				if(len < 10)
				{
					continue;
				}													
				try{						
					landmark_name1 = getXmlAttribute(strLine1,"landmark_name=\"[^\"]+");
					landmark_coord1 = getXmlAttribute(strLine1,"landmark_coord=\"[^\"]+");
										
					//System.out.println("LANDMARK_NAME="+landmark_name1+" ,landmark_coord="+landmark_coord1);					
					if( !(landmark_name1.equalsIgnoreCase("")) && !(landmark_coord1.equalsIgnoreCase("")))
					{
						//temp=split(landmark_coord1,",");
						landmark_coord1 = landmark_coord1.replaceAll("\\(", "");
						landmark_coord1 = landmark_coord1.replaceAll("\\)", "");
						//System.out.println("#LANDMARK="+landmark_coord1);
						temp=landmark_coord1.split(",");
						lat2 = temp[0].trim();
						lng2 = temp[1].trim();					
					}
					else
					{
						lat2 = "0";
						lng2 = "0";						
					}	       								
					//System.out.println("In nearest landmark :lat1"+lat1+" ,lat2="+lat2+" ,lng1="+lng1+" ,lng2="+lng2);						
					//float distance1 = calculateDistance((float) lat1, Float.parseFloat(lat2), (float) lng1, Float.parseFloat(lng2));				   
					double distance1 = calculateDistance(lat1, lng1, Double.parseDouble(lat2), Double.parseDouble(lng2));
					//System.out.println("Distance1 in nearest landmark :"+distance1);
					
					if(i==0)			//DISTANCE MAY BE ASSUMED WITHIN 1KM BUT HERE WE WILL GET NEAREST
					{
					  lowest_dist = distance1;
					  placename1 = landmark_name1;
					}
					else
					{
						if(distance1 < lowest_dist)
						{
						  lowest_dist = distance1;
						  placename1 = landmark_name1;
						} 
					}
					
					i++;	        											
					
				} catch(Exception e2){System.out.println("EXCEPTION IN LANDMARK READ-2:"+e2.getMessage());}
			} //WHILE CLOSED
			

			if(type.equalsIgnoreCase("common"))
			{
				if(placename1!="" && lowest_dist < 2.0)
				{
					if(lat1==0.0 || lng1 ==0.0)
					{
						landmark1 = "near (gps missing):"+placename1;
					}
					else
					{
						landmark1 = roundTwoDecimals((lowest_dist/1000))+ " km from :"+placename1;
					}				 
				}					
			}
			if(type.equalsIgnoreCase("landmark"))
			{
				if(placename1!="")
				{
					if(lat1==0.0 || lng1 ==0.0)
					{
						landmark1 = "near (gps missing):"+placename1;
					}
					else
					{
						landmark1 = roundTwoDecimals((double)(lowest_dist/1000))+ " km from :"+placename1;
					}				 
				}
			}
		
			fstream1.close();
			in1.close();
		
		} catch(Exception e2){System.out.println("EXCEPTION IN LANDMARK READ-1:"+e2.getMessage());}

		//System.out.println("NEAREST LANDMARK="+landmark1);
		
		return landmark1;
	}
	
	
	/************* METHOD- READ TRIP DETAILL ************/
	public static void read_trip_detail()
	{
		System.out.println("ReadTripDetail");
		String normal_variable_path = alert_variables.root_dir+"/temp_variables_itc/current_trip.xml";
		
		String strLine1 ="";
		
		try{
			FileInputStream fstream1 = new FileInputStream(normal_variable_path);
			// Get the object of DataInputStream
			DataInputStream in1 = new DataInputStream(fstream1);
			BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));	
							
			while ((strLine1 = br1.readLine()) != null) 
			{																																						
				int len = strLine1.length();
				
				if(len < 100)
				{
					continue;
				}													
				try{	
					//return strLine1;					
					String imei = getXmlAttribute(strLine1,"imei=\"[^\"]+");	
					String trip_status = getXmlAttribute(strLine1,"trip_status=\"[^\"]+").trim();
					String trip_id = getXmlAttribute(strLine1,"trip_id=\"[^\"]+");
					
					if(trip_status.equals("1"))
					{
						System.out.println("IF ALERT CLEAR-0");
						
						//if((alert_variables.trip_status.get(imei)!=null) || !(alert_variables.trip_status.get(imei).equals("")))
						if(alert_variables.trip_status.get(imei)!=null)
						{
							System.out.println("ALERT CLEAR-1");
							
							if(!(alert_variables.trip_id.get(imei).equalsIgnoreCase(trip_id)))
							{
								System.out.println("ALERT CLEAR-2");
								String q="\"";
								try{
									String alert_type = "S120";
									int halt1_status = 0;
									String line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" halt1_status="+q+halt1_status+q+"/>";
									close_alert_db_session(imei, line, alert_type);
									
									if(alert_variables.temp_alert_halt1_start.get(imei)!=null)
									{
										line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" halt1_status="+q+halt1_status+q+"/>"; 
										alert_variables.temp_alert_halt1_start.put(imei,null);
										alert_variables.repetitive_alert_halt1_start_time.put(imei,null);
										alert_variables.repetitive_alert_halt1_start_location.put(imei,null);
										alert_variables.repetitive_alert_halt1_start_landmark.put(imei,null);					
									}
								}catch(Exception e){}

								
								try{
									String alert_type = "S30";
									int halt2_status = 0;
									String line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" halt2_status="+q+halt2_status+q+"/>";
									close_alert_db_session(imei, line, alert_type);
									
									if(alert_variables.temp_alert_halt2_start.get(imei)!=null)
									{
										line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" halt2_status="+q+halt2_status+q+"/>"; 
										alert_variables.temp_alert_halt2_start.put(imei, null);
										alert_variables.repetitive_alert_halt2_start_time.put(imei,null);
										alert_variables.repetitive_alert_halt2_start_location.put(imei,null);
										alert_variables.repetitive_alert_halt2_start_landmark.put(imei,null);						
									}
								}catch(Exception e){}


								try{
									String alert_type = "ND";
									int movement_status = 0;
									String line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" movement_status="+q+movement_status+q+"/>";
									close_alert_db_session(imei, line, alert_type);
									
									if(alert_variables.temp_alert_movement.get(imei)!=null)
									{
										line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" movement_status="+q+movement_status+q+"/>"; 
										alert_variables.temp_alert_movement.put(imei, line);
										alert_variables.repetitive_alert_halt1_start_time.put(imei,null);
										alert_variables.repetitive_alert_halt1_start_location.put(imei,null);
										alert_variables.repetitive_alert_halt1_start_landmark.put(imei,null);				
									}
								}catch(Exception e){}



								try{
									String alert_type = "RD";
									int battery_disconnected_status = 0;
									String line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" battery_disconnected_status="+q+battery_disconnected_status+q+"/>";
									close_alert_db_session(imei, line, alert_type);
									
									if(alert_variables.temp_alert_battery_disconnected.get(imei)!=null)
									{
										line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" battery_disconnected_status="+q+battery_disconnected_status+q+"/>"; 
										alert_variables.temp_alert_battery_disconnected.put(imei, line);
										alert_variables.repetitive_alert_battery_disconnected_time.put(imei,null);
										alert_variables.repetitive_alert_battery_disconnected_location.put(imei,null);
										alert_variables.repetitive_alert_battery_disconnected_landmark.put(imei,null);					
									}
								}catch(Exception e){}
								
								
								try{
									String alert_type = "NG60";
									int nogps_status = 0;
									String line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" nogps_status="+q+nogps_status+q+"/>";
									close_alert_db_session(imei, line, alert_type);
									
									if(alert_variables.temp_alert_nogps.get(imei)!=null)
									{
										line = "<marker imei="+q+imei+q+" lat="+q+alert_variables.lat.get(imei)+q+" lng="+q+alert_variables.lng.get(imei)+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+alert_variables.datetime.get(imei)+q+" nogps_status="+q+nogps_status+q+"/>"; 
										alert_variables.temp_alert_nogps.put(imei, line);
										alert_variables.repetitive_alert_nogps_time.put(imei,null);
										alert_variables.repetitive_alert_nogps_location.put(imei,null);
										alert_variables.repetitive_alert_nogps_landmark.put(imei,null);										
									}
								}catch(Exception e){}								
							}
						}
						alert_variables.vehicle_name.put(imei, getXmlAttribute(strLine1,"vehicle_name=\"[^\"]+"));
						alert_variables.DFG.put(imei, getXmlAttribute(strLine1,"DFG=\"[^\"]+"));
						alert_variables.S120.put(imei, getXmlAttribute(strLine1,"S120=\"[^\"]+"));
						alert_variables.S30.put(imei, getXmlAttribute(strLine1,"S30=\"[^\"]+"));
						alert_variables.ND.put(imei, getXmlAttribute(strLine1,"ND=\"[^\"]+"));
						alert_variables.RD.put(imei, getXmlAttribute(strLine1,"RD=\"[^\"]+"));
						alert_variables.NG60.put(imei, getXmlAttribute(strLine1,"NG60=\"[^\"]+"));
						//alert_variables.DD30.put(imei, getXmlAttribute(strLine1,"DD30=\"[^\"]+"));
						//alert_variables.RR.put(imei, getXmlAttribute(strLine1,"RR=\"[^\"]+"));
						//alert_variables.FS.put(imei, getXmlAttribute(strLine1,"FS=\"[^\"]+"));
						alert_variables.trip_id.put(imei, trip_id);
						alert_variables.source_coord.put(imei, getXmlAttribute(strLine1,"source_coord=\"[^\"]+"));
						alert_variables.dest_coord.put(imei, getXmlAttribute(strLine1,"destination_coord=\"[^\"]+"));
						alert_variables.trip_startdate.put(imei, getXmlAttribute(strLine1,"trip_startdate=\"[^\"]+"));
						alert_variables.trip_status.put(imei, trip_status);
						alert_variables.transporter_name.put(imei, getXmlAttribute(strLine1,"transporter_name=\"[^\"]+"));
						alert_variables.transporter_mobile.put(imei, getXmlAttribute(strLine1,"transporter_mobile=\"[^\"]+"));
						alert_variables.driver_name.put(imei, getXmlAttribute(strLine1,"driver_name=\"[^\"]+"));
						alert_variables.driver_mobile.put(imei, getXmlAttribute(strLine1,"driver_mobile=\"[^\"]+"));
						System.out.println("Stored:TripData");
					}
					else
					{
						//System.out.println("ELSE ALERT CLEAR-0");
						
						//if((alert_variables.trip_status.get(imei)!=null) || !(alert_variables.trip_status.get(imei).equals("")))
						if(alert_variables.trip_status.get(imei)!=null)
						{
							alert_variables.vehicle_name.put(imei, "");
							alert_variables.DFG.put(imei, "");
							alert_variables.S120.put(imei, "");
							alert_variables.S30.put(imei, "");
							alert_variables.ND.put(imei, "");
							alert_variables.RD.put(imei, "");
							alert_variables.NG60.put(imei, "");
							//alert_variables.DD30.put(imei, "");
							//alert_variables.RR.put(imei, "");
							//alert_variables.FS.put(imei, "");
							alert_variables.trip_id.put(imei, "");
							alert_variables.source_coord.put(imei, "");
							alert_variables.dest_coord.put(imei, "");
							alert_variables.trip_startdate.put(imei, "");
							alert_variables.trip_status.put(imei, "");
							alert_variables.transporter_name.put(imei, "");
							alert_variables.transporter_mobile.put(imei, "");
							alert_variables.driver_name.put(imei, "");
							alert_variables.driver_mobile.put(imei, "");
							System.out.println("Reset:TripData");
						}						
					}
					//System.out.println("PREV SECTOR="+prev_sector_id);			
				} catch(Exception e) {
					System.out.println("Exception1 in middle of Trip Read file:"+e.getMessage());
				}
			}
		
			fstream1.close();
			in1.close();

		} catch(Exception e) {
			//System.out.println("Exception2 in line Read:"+e.getMessage());
			}	
		
		//return strLine1;
	}
	
	public static void update_alert_status(String line, String imei, String q, String alert_type)
	{
		//####### UPDATE VALUES FILE AND DB
		/*line = "<marker imei="+q+imei+q+" lat="+q+prev_lat+q+" lng="+q+prev_lng+q+" sts="+q+alert_variables.sts.get(imei)+q+" datetime="+q+prev_date+q+" location="+q+location+q+" nearest_landmark="+q+nearest_landmark+q+" halt2_status="+q+halt_start+q+"/>";*/
		
		try{
		if(alert_type.equalsIgnoreCase("DFG"))
		{
			alert_variables.temp_alert_exited_region.put(imei, line);
		}
		else if(alert_type.equalsIgnoreCase("S120"))
		{
			alert_variables.temp_alert_halt1_start.put(imei, line);
		}
		else if(alert_type.equalsIgnoreCase("S30"))
		{
			alert_variables.temp_alert_halt2_start.put(imei, line);
		}
		else if(alert_type.equalsIgnoreCase("ND"))
		{
			alert_variables.temp_alert_movement.put(imei, line);
		}
		else if(alert_type.equalsIgnoreCase("RD"))
		{
			alert_variables.temp_alert_battery_disconnected.put(imei, line);
		}
		else if(alert_type.equalsIgnoreCase("NG60"))
		{
			alert_variables.temp_alert_nogps.put(imei, line);
		}
		
		//######### UPDATE ALERT DATABASE
		String alert_str = read_database_alert_status(imei, line, alert_type);
		if(alert_str!=null)
		{
			System.out.println("UPDATE_DB_HALT2");
			update_database_alert_status(imei, line, alert_type);
		}
		else
		{
			System.out.println("INSERT_DB_HALT2");
			insert_database_alert_status(imei, line, alert_type);
		}
		}catch(Exception E){System.out.println("Exception in DB Write");}
		//################## UPDATE CLOSED
	}	
}

