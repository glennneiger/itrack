package com.hdfc.report;

import java.util.ArrayList;

import com.hdfc.utils.utility_class;
import com.iespl.gisgraphy.LatLng;

public class report_night_movement {		//night travel
	
	public static int halt_flag =0,firstdata_flag=0, start_point_display =0;
	public static long date_secs1 = 0, date_secs2 =0, starttime =0, stoptime =0, halt_dur =0;
	public static String xml_date_latest ="1900-00-00 00:00:00", datetime_ref ="", datetime_cr ="", arrivale_time ="", depature_time ="", datetime_S ="", datetime_E = "", datetime_travel_start = "", last_time="", last_time1 ="", speeed_data_valid_time = "", datetime_travel_end ="";
	public static int firstdata_flag_travel =0, firstdata_flag_halt =0;
	public static double lat_ref =0.0, lng_ref =0.0, lat_cr =0.0, lng_cr =0.0, lat_E=0.0, lng_E=0.0, CurrentLat = 0.0, CurrentLong = 0.0, LastLat = 0.0, LastLong = 0.0, lat_S=0.0, lng_S=0.0, latlast =0.0, lnglast = 0.0, max_speed=0.0, lat_travel_start =0.0, lng_travel_start = 0.0, distance_incriment =0.0;
	public static boolean haltFlag = false;
	public static double xml_date_latest_sec = 0.0, device_time_sec =0.0, startdate_sec =0.0, enddate_sec =0.0, tmp_time_diff=0.0, tmp_time_diff1=0.0, distance1=0.0, tmpdiff=0.0;
	public static double tmp_speed=0.0, tmp_speed1=0.0, distance_travel=0.0, distance_total=0.0, distance_incrimenttotal=0.0, datetime_diff=0.0, lat_travel_end=0.0, lng_travel_end=0.0;
	public static double night_sec1 = 0.0, night_sec2 = 0.0, night_sec3 = 0.0, night_sec4 = 0.0;
	
    //###### FINAL ARRAY
    public static ArrayList<String> IMEI_No = new ArrayList<String>();    
    public static ArrayList<String> ServerTime = new ArrayList<String>();
    public static ArrayList<Double> AvgSpeed = new ArrayList<Double>();
    public static ArrayList<Double> Distance = new ArrayList<Double>();
    public static ArrayList<Double> MaxSpeed = new ArrayList<Double>();
    public static ArrayList<String> StartDeviceTime = new ArrayList<String>();
    public static ArrayList<String> EndDeviceTime = new ArrayList<String>();   
    //public static ArrayList<String> TravelTime = new ArrayList<String>();
    public static ArrayList<Integer> TravelDuration = new ArrayList<Integer>();
    public static ArrayList<Double> StartLatitude = new ArrayList<Double>();
    public static ArrayList<Double> EndLatitude = new ArrayList<Double>();
    public static ArrayList<Double> StartLongitude = new ArrayList<Double>();
    public static ArrayList<Double> EndLongitude = new ArrayList<Double>();
    public static ArrayList<LatLng> StartlatLngObj = new ArrayList<LatLng>();
    public static ArrayList<LatLng> EndlatLngObj = new ArrayList<LatLng>();
    public static ArrayList<String> StartlocationId = new ArrayList<String>();
    public static ArrayList<String> EndlocationId = new ArrayList<String>();
    public static ArrayList<String> Startlocation = new ArrayList<String>();
    public static ArrayList<String> Endlocation = new ArrayList<String>(); 

    
	public static void action_report_travel(String imei, String device_time, String sts, String startdate, String enddate, double datetime_threshold, double lat, double lng, double speed, int data_size, int record_count) {

		if(device_time!=null) {

			xml_date_latest_sec = utility_class.get_seconds(xml_date_latest);
			device_time_sec = utility_class.get_seconds(device_time);
			startdate_sec = utility_class.get_seconds(startdate);
			enddate_sec = utility_class.get_seconds(enddate);
			String[] temp_date = device_time.split(" ");
			String night_time1 = temp_date[0]+" 23:00:00";
			String night_time2 = temp_date[0]+" 23:59:59";
			String night_time3 = temp_date[0]+" 00:00:00";
			String night_time4 = temp_date[0]+" 04:00:00";
						
			night_sec1 = utility_class.get_seconds(night_time1);
			night_sec2 = utility_class.get_seconds(night_time2);
			night_sec3 = utility_class.get_seconds(night_time3);
			night_sec4 = utility_class.get_seconds(night_time4);
						
			
			if( ( ((device_time_sec >= night_sec1) && (device_time_sec <= night_sec2)) || ((device_time_sec >= night_sec3) && (device_time_sec <= night_sec4)) ) && ((device_time_sec >= startdate_sec) && (device_time_sec <= enddate_sec)) ) {
				
				//System.out.println("device_time="+device_time+" ,night_time1="+night_time1+" ,night_time2="+night_time2+", night_time3="+night_time3+" ,night_time4="+night_time4);
				
				if(firstdata_flag==0) {                                
					firstdata_flag = 1;
					haltFlag= true;
					distance_travel=0;                                    
	
					lat_S = lat;
					lng_S = lng;
					datetime_S = device_time;
					datetime_travel_start = datetime_S;              		
					lat_travel_start = lat_S;
					lng_travel_start = lng_S;       
					start_point_display =0;                  
					last_time1 = device_time;
					latlast = lat;
					lnglast =  lng;  
					max_speed	=0.0;								
				}           	              	
				else {           
					lat_E = lat;
					lng_E = lng; 
					datetime_E = device_time; 					
					distance_incriment = utility_class.calculateDistance(lat_S, lng_S, lat_E, lng_E);
					tmp_time_diff1 = utility_class.get_seconds(device_time) - utility_class.get_seconds(last_time1) / 3600;               
					
					distance1 = utility_class.calculateDistance(latlast, lnglast, lat_E, lng_E);	
					if(!last_time.equals("")) {										 
						tmp_time_diff = (utility_class.get_seconds(device_time) - utility_class.get_seconds(last_time)) / 3600;
					} else {
						distance1 = 0;					 
						tmp_time_diff = utility_class.get_seconds(device_time);
					}
					
					if(tmp_time_diff1>0) {
						tmp_speed = distance_incriment / tmp_time_diff;
						tmp_speed1 = (distance1) / tmp_time_diff1;
					}
					else {
						tmp_speed1 = 1000.0; //very high value
					}
					                                               
					if(tmp_speed<300.0) {
						speeed_data_valid_time = device_time;
					}
					
					if(!last_time.equals("")) {
						tmp_time_diff = (utility_class.get_seconds(device_time) - utility_class.get_seconds(last_time)) / 3600;
					} else {
						tmp_time_diff = utility_class.get_seconds(device_time);
					}
					
					
					if(!speeed_data_valid_time.equals("")) {
						tmpdiff = utility_class.get_seconds(device_time) - utility_class.get_seconds(speeed_data_valid_time);
					} else {
						tmpdiff = utility_class.get_seconds(device_time);
					}
					
					if(tmpdiff >300.0) {
						lat_S = lat_E;
						lng_S = lng_E;
						last_time = device_time;
					}

					last_time1 = device_time;
					latlast = lat_E;
					lnglast =  lng_E;
					//echo"maxspeed=".$max_speed."speed=".$speed."<br>";
					if(max_speed < speed) {
						max_speed = speed;
					}
					
													
					if(tmp_speed<300.0 && tmp_speed1<300.0 && distance_incriment>0.1 && tmp_time_diff>0.0 && tmp_time_diff1>0) {
						if(haltFlag) {
							datetime_travel_start = datetime_E;
							lat_travel_start = lat_E;
							lng_travel_start = lng_E;
							distance_travel = 0.0;
							distance_total = 0.0;
							max_speed = 0.0;
							haltFlag = false;
						}
						distance_total += distance_incriment;
						distance_travel += distance_incriment;
						lat_S = lat_E;
						lng_S = lng_E;
						datetime_S = datetime_E;
						
						//$distance_incrimenttotal += $distance_incriment;
						// echo $datetime_E . " -- " . $lat_E .",". $lng_E . "\tDelta Distance = " . $distance_incriment . "\tTotal Distance = " . $distance_total . "\n";
					}
										
					datetime_diff = utility_class.get_seconds(datetime_E) - utility_class.get_seconds(datetime_S);
					        
					if((datetime_diff > datetime_threshold) && (!haltFlag)) {
						
						datetime_travel_end = datetime_S;
					
						//echo "start_date1=".$datetime_travel_start."end_date1=".$datetime_travel_end."<br>";
						lat_travel_end = lat_S;
						lng_travel_end = lng_S;
						newTravel(imei, datetime_travel_start, datetime_travel_end, distance_travel, lat_travel_start, lng_travel_start, lat_travel_end, lng_travel_end, distance_travel,max_speed,sts);
						haltFlag = true;
						//j=0;
					}
					
					if(record_count == data_size) {
						
						if(haltFlag==false) {
							datetime_travel_end = datetime_E;
							lat_travel_end = lat_S;
							lng_travel_end = lng_S;
							newTravel(imei, datetime_travel_start, datetime_travel_end, distance_travel, lat_travel_start, lng_travel_start, lat_travel_end, lng_travel_end, distance_travel,max_speed,sts);
						}
					}
				}
			} 
		}
	}
	
	
	public static void newTravel(String imei, String datetime_S, String datetime_E, double distance, double lat_travel_start, double lng_travel_start, double lat_travel_end, double lng_travel_end, double distance_travel, double max_speed, String sts)
	{
		double travel_dur =  utility_class.get_seconds(datetime_E) - utility_class.get_seconds(datetime_S);
		int travel_duration = (int) travel_dur; 
		//hms = secondsToTime(travel_dur);
	//	String travel_time = utility_class.get_hms((long)travel_dur);
		//travel_time = hms[h].":".hms[m].":".hms[s];
		//echo "avg_speed=".$distance_travel."travel_dur=".$travel_dur."<br>";
		double avg_speed = 0.0;
		if(avg_speed > 0.0) {
			avg_speed = distance_travel/(travel_dur/3600);
			distance_travel = utility_class.roundTwoDecimals(distance_travel);
			avg_speed = utility_class.roundTwoDecimals(avg_speed);
		}
		//echo "avg_speed=".$avg_speed."<br>";
		if(max_speed < avg_speed)
		{
			max_speed = avg_speed;
		}
		//System.out.println("imei="+imei+" ,datetime_S="+datetime_S+" ,datetime_E="+datetime_E+", lat_travel_start="+lat_travel_start+" ,lng_travel_start="+lng_travel_start+" ,lat_travel_end="+lat_travel_end+" ,lng_travel_end="+lng_travel_end+" ,distance_travel="+distance_travel+" ,travel_dur="+travel_dur+" ,max_speed="+max_speed+" ,avg_speed="+avg_speed);
		
		IMEI_No.add(imei);		
		ServerTime.add(sts);
		AvgSpeed.add(avg_speed);
		Distance.add(distance_travel);
		MaxSpeed.add(max_speed);
		StartDeviceTime.add(datetime_S);
		EndDeviceTime.add(datetime_E);
		StartLatitude.add(lat_travel_start);
		StartLongitude.add(lng_travel_start);
		//TravelTime.add(travel_time);
		TravelDuration.add(travel_duration);
		LatLng tmpobj1 = new LatLng(Double.toString(lat_travel_start), Double.toString(lng_travel_start),"","","","");
		StartlatLngObj.add(tmpobj1);
		//StartlocationId.add();
		//Startlocation.add();
		EndLatitude.add(lat_travel_end);
		EndLongitude.add(lng_travel_end);
		LatLng tmpobj2 = new LatLng(Double.toString(lat_travel_end), Double.toString(lng_travel_end),"","","","");
		StartlatLngObj.add(tmpobj2);
		//EndlocationId.add();
		//Endlocation.add();		
	}
	
	
	public static void set_variables() {
		halt_flag =0; firstdata_flag=0; start_point_display =0;
		date_secs1 = 0; date_secs2 =0; starttime =0; stoptime =0; halt_dur =0;
		xml_date_latest ="1900-00-00 00:00:00"; datetime_ref =""; datetime_cr =""; arrivale_time =""; depature_time =""; datetime_S =""; datetime_E = ""; datetime_travel_start = ""; last_time=""; last_time1 =""; speeed_data_valid_time = ""; datetime_travel_end ="";
		firstdata_flag_travel =0; firstdata_flag_halt =0;
		lat_ref =0.0; lng_ref =0.0; lat_cr =0.0; lng_cr =0.0; lat_E=0.0; lng_E=0.0; CurrentLat = 0.0; CurrentLong = 0.0; LastLat = 0.0; LastLong = 0.0; lat_S=0.0; lng_S=0.0; latlast =0.0; lnglast = 0.0; max_speed=0.0; lat_travel_start =0.0; lng_travel_start = 0.0; distance_incriment =0.0;
		haltFlag = false;
		xml_date_latest_sec = 0.0; device_time_sec =0.0; startdate_sec =0.0; enddate_sec =0.0; tmp_time_diff=0.0; tmp_time_diff1=0.0; distance1=0.0; tmpdiff=0.0;
		tmp_speed=0.0; tmp_speed1=0.0; distance_travel=0.0; distance_total=0.0; distance_incrimenttotal=0.0; datetime_diff=0.0; lat_travel_end=0.0; lng_travel_end=0.0;
		
	    //###### FINAL ARRAY
	    IMEI_No = new ArrayList<String>();    
	    ServerTime = new ArrayList<String>();
	    AvgSpeed = new ArrayList<Double>();
	    Distance = new ArrayList<Double>();
	    MaxSpeed = new ArrayList<Double>();
	    StartDeviceTime = new ArrayList<String>();
	    EndDeviceTime = new ArrayList<String>();   
	    //TravelTime = new ArrayList<String>();
	    TravelDuration = new ArrayList<Integer>();
	    StartLatitude = new ArrayList<Double>();
	    EndLatitude = new ArrayList<Double>();
	    StartLongitude = new ArrayList<Double>();
	    EndLongitude = new ArrayList<Double>();
	    StartlatLngObj = new ArrayList<LatLng>();
	    EndlatLngObj = new ArrayList<LatLng>();
	    StartlocationId = new ArrayList<String>();
	    EndlocationId = new ArrayList<String>();
	    Startlocation = new ArrayList<String>();
	    Endlocation = new ArrayList<String>();		
	}
}
