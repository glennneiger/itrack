package com.iespl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.sql.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axis.message.MessageElement;
import org.tempuri.GetVehicleDataResponseGetVehicleDataResult;
import org.tempuri.TrackSoapProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ridhiWebServiceClient {
	public static DateFormat sDF = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	public static SimpleDateFormat dDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static String ServerTS = ""; 
	public static String DateTime = "";
	public static String ReceiveDateFormat="";
//	String VehicleID = "";
	public static String DeviceIMEINo = "";
	public static String MsgType = "";
	public static String Version = "";
	public static String Fix = "1";
//	String SendMode = "";
	public static String Latitude = "";
	public static String Longitude = "";
	public static String Altitude = "";
	public static String Speed = "";		
	public static String Signal_Strength = "";
	public static String No_Of_Satellites = "";
	public static String line="",serverdatetime="",devicedatetime="";
//	String CBC = "";
	public static String CellName = "";
//	String min_speed = "";
//	String max_speed = "";
	public static String distance = "";
	public static String strOrig = "";

	public static String io_value1 = "";
	public static String io_value2 = "";
	public static String io_value3 = "";
	public static String io_value4 = "";
	public static String io_value5 = "";
	public static String io_value6 = "";
	public static String io_value7 = "";
	public static String io_value8 = "";
	public static String last_date = "";
	public static ArrayList<String> vehcileNoArr=new ArrayList<String>();
	
	public static String SupplyVoltage = "";
	public static String LogDataPath = "D:\\itrack_vts/xml_data";
	public static String RealDataPath = "D:\\itrack_vts/last_location";
	
	//public static String LogDataPath = "/home/current_data/xml_data";
	//public static String RealDataPath = "/home/current_data/last_location";
	
	public static void main(String[] args)
	{
		ResultSet rs = null;
		Connection connection = null;
		Statement statement = null;
		try 
		{
			String query = "select transporters.name,vehicle_transporter_assignment.device_imei_no,vehicle.vehicle_name from transporters,vehicle_transporter_assignment,vehicle,vehicle_assignment where"+
							" vehicle.vehicle_id=vehicle_assignment.vehicle_id  AND vehicle.status=1 AND vehicle_assignment.status=1"+  
							" AND vehicle_assignment.device_imei_no=vehicle_transporter_assignment.device_imei_no AND "+
							" transporters.name='' and transporters.status=1 and "+
							"transporters.transporter_id=vehicle_transporter_assignment.transporter_id and "+
							"vehicle_transporter_assignment.status=1";
			
			System.out.print(", query: " + query);
			connection = utilMysqlConnectivity.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(query);
			
			int i=0;
			while(rs.next())
			{
				//vehcileNoArr.add(rs.getString("vehicle_name"));
		    }
			vehcileNoArr.add("MH02AQ277");
			System.out.print(", vehcileNoArr: " + vehcileNoArr);
		    rs.close();
			
			
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		System.out.println("BEFORE GET ESCALATION DETAIL");
		alert_module.get_escalation_detail();
		
		System.out.println("INITIALIZATION-1 COMPLETE");		
		PullWebServiceData();
	}
	
	
	public static void PullWebServiceData()
	{
		System.out.println("IN PullWebServiceData");
		TrackSoapProxy webServiceClient = new TrackSoapProxy();	
		try 
		{
			if (!(vehcileNoArr.isEmpty()))
			{
				for(String vehicleNoString : vehcileNoArr)
				{
					GetVehicleDataResponseGetVehicleDataResult  GDVR = webServiceClient.getVehicleData(vehicleNoString,"bnmuerwu90");
					//System.out.println("GDVR "+GDVR.toString());
					MessageElement[] massageElement = GDVR.get_any();
					System.out.println("Element : "+massageElement.toString());
					String response = "";
					for(MessageElement me : massageElement)
					{
						response=me.toString();
						//System.out.println("Element : "+me.toString());
					}
					System.out.println("WebServiceResponse "+response);
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					InputSource is = new InputSource();
					is.setCharacterStream(new StringReader(response));
					Document doc = dBuilder.parse(is);
	
					doc.getDocumentElement().normalize();
					NodeList checkValue = doc.getElementsByTagName("Table");
					Node checkNode=checkValue.item(0);
					Element cElement = (Element) checkNode;
					String Vehicle=cElement.getElementsByTagName("vehicleNo").item(0).getTextContent();
					if(!Vehicle.equals("NA"))
					{
						NodeList nList = doc.getElementsByTagName("Table");
						// System.out.println("Total students: " + nList.getLength());					
						for (int temp = 0; temp < nList.getLength(); temp++) 
						{
							Node nNode = nList.item(temp);
							//System.out.println("\nCurrent Element :" + nNode.getNodeName());
							if (nNode.getNodeType() == Node.ELEMENT_NODE)
							{
								Element eElement = (Element) nNode;
								DeviceIMEINo=eElement.getElementsByTagName("IMEI").item(0).getTextContent();
								//DeviceIMEINo=eElement.getElementsByTagName("VEHICLE_RTO_NO").item(0).getTextContent();						
								ReceiveDateFormat = eElement.getElementsByTagName("DateTime").item(0).getTextContent();
								//System.out.println("ReceiveDateFormat: "+ReceiveDateFormat);
								Date result;
								result = sDF.parse(ReceiveDateFormat.trim());
								String DateTimeLast = "";
								DateTimeLast = dDF.format(result);
								//System.out.println("DateTimeLast: "+DateTimeLast);
								Latitude=eElement.getElementsByTagName("Lat").item(0).getTextContent();
								Longitude=eElement.getElementsByTagName("Lon").item(0).getTextContent();
								//Speed=eElement.getElementsByTagName("VEHICLE_SPEED").item(0).getTextContent();
								Speed="0.0";
												
								write_last_location();
								//CALL ALERT MODULE
								alert_module.write_final_alert_data(DeviceIMEINo, DateTimeLast, serverdatetime, Latitude, Longitude, Speed, "0", "0");
								
								//System.out.println("DateTimeLast:"+DateTimeLast);
								//if(!(DateTimeLast.compareTo(last_date) == 0))
								if(DateTimeLast.compareTo(last_date) > 0)
								{
									createXmlFile();
								}
							}
						}
					}
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		
		try 
		{
			Thread.sleep(60000);
			PullWebServiceData();
		} 
		catch (InterruptedException ie) 
		{
			//Handle exception
		}	
	}
	public static void createXmlFile()
	{	
		//String RealDataPath = "D:\\itrack_vts/xml_vts_java/last_location";		
		//String LogDataPath = "/home/current_data/xml_data";
		//String RealDataPath = "/home/current_data/last_location";

		GregorianCalendar calendar = new GregorianCalendar();
	
		/*Calendar cal = Calendar.getInstance(); // creates calendar
		cal.setTime(new Date()); // sets calendar time/date
		cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
		date=(Date) cal.getTime();*/
		
		//formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
		Date result;
		try 		
		{
			Date date = new Date();
			serverdatetime = dDF.format(date);
			result = sDF.parse(ReceiveDateFormat);			
			DateTime = dDF.format(result);			
		}
		catch (ParseException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	   	   		
		//Statement statement;
		//ResultSet result;
		//result=null;
		//statement=null;
		//ResultSetMetaData rsmd;
		//StringTokenizer st,st4;
	
		float speed_f = 0.F;
		float supv_f = 0.F;
		
		strOrig = Speed;   
		char[] stringArray1;
		stringArray1 = strOrig.toCharArray();
		   
		//display the array    
		for(int index=0; index < stringArray1.length; index++)   
		{
		  if( (stringArray1[index]=='0' || stringArray1[index]=='1'  || stringArray1[index]=='2'  || stringArray1[index]=='3' || stringArray1[index]=='4' || stringArray1[index]=='5' || stringArray1[index]=='6' || stringArray1[index]=='7' || stringArray1[index]=='8' || stringArray1[index]=='9') )
		  {
			//System.out.print(stringArray[index]);
		  }  
		  else
		  {
			stringArray1[index] = '.';
		  }    
		  //System.out.print(stringArray[index]);
		}
		
		
		Speed = new String(stringArray1);
		speed_f = Float.parseFloat(Speed);
		speed_f = (float) (Math.round(speed_f*100.0)/100.0);	
		//int NoofToken,i=0,j=0,k=0;
		int GPSYear,GPSMonth,GPSDay,GPSHr,GPSMin,GPSSec;
		double Latitudetmp=0.0,Longitudetmp=0.0;
		String marker_a1="",marker_a2="";
		String folderDate="",RFID="";
		StringTokenizer st;
//		int SerialNo = 0;
		//String[] data = new String[30];
		//System.out.println(Response);
		//st = new StringTokenizer(Response,";");		//Parsing Input
		//NoofToken = st.countTokens();
		//System.out.println("No of Token="+NoofToken);
//	
		st = new StringTokenizer(serverdatetime," ");
		folderDate = st.nextToken();
		
		String q="\"";
		String mydir1 = LogDataPath;
		boolean success1 = (new File(mydir1 + "/" + folderDate)).mkdir();
		//System.out.println("Success="+success1);							
		 
		 RandomAccessFile raf1 =null;
		// boolean FilehandlerReceived = false;
		 //FileWriteHandler CurrentFileWriteHandler = null;
		 //BufferedWriter out_a1 =null;
		 //BufferedWriter out_a2 =null;
		 String SFile = LogDataPath+"/"+folderDate+"/"+DeviceIMEINo+".xml";
		 try 
		 {
			raf1 = new RandomAccessFile(SFile, "rwd");
		 } 
		 catch (FileNotFoundException e)
		 {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		 long length1=0;
		try 
		{
			length1 = raf1.length();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(length1<10)
		{			
			 try 
			 {
				raf1.seek(0);
				marker_a2 = "<t1>\n<x "+"a="+q+MsgType+q+" b="+q+Version+q+" c="+q+Fix+q+" d="+q+Latitude+q+" e="+q+Longitude+q+" f="+q+speed_f+q+" g="+q+serverdatetime+q+" h="+q+DateTime+q+" i="+q+io_value1+q+" j="+q+io_value2+q+" k="+q+io_value3+q+" l="+q+io_value4+q+" m="+q+io_value5+q+" n="+q+io_value6+q+" o="+q+io_value7+q+" p="+q+io_value8+q+" q="+q+Signal_Strength+q+" r="+q+supv_f+q+"/>\n</t1>";
				raf1.writeBytes(marker_a2);
			 } 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}				
		else
		{
			try 
			{
				raf1.seek(length1-6);										
				//marker_a2 = "\n<x "+"a="+q+MsgType+q+" b="+q+Version+q+" c="+q+Fix+q+" d="+q+Latitude+q+" e="+q+Longitude+q+" f="+q+speed_f+q+" g="+q+serverdatetime+q+" h="+q+DateTime+q+" i="+q+ln_io_value1+q+" j="+q+ln_io_value2+q+" k="+q+ln_io_value3+q+" l="+q+ln_io_value4+q+" m="+q+ln_io_value5+q+" n="+q+ln_io_value6+q+" o="+q+ln_io_value7+q+" p="+q+ln_io_value8+q+" q="+q+Signal_Strength+q+" r="+q+supv_f+q+"/>\n</t1>";
				marker_a2 = "\n<x "+"a="+q+MsgType+q+" b="+q+Version+q+" c="+q+Fix+q+" d="+q+Latitude+q+" e="+q+Longitude+q+" f="+q+speed_f+q+" g="+q+serverdatetime+q+" h="+q+DateTime+q+" i="+q+io_value1+q+" j="+q+io_value2+q+" k="+q+io_value3+q+" l="+q+io_value4+q+" m="+q+io_value5+q+" n="+q+io_value6+q+" o="+q+io_value7+q+" p="+q+io_value8+q+" q="+q+Signal_Strength+q+" r="+q+supv_f+q+"/>\n</t1>";
				//CurrentFileWriteHandler.StrBuf.append(marker_a2);
				//if(((System.currentTimeMillis()-CurrentFileWriteHandler.UpdateTime)>120000) || (CurrentFileWriteHandler.StrBuf.length()>6000))
				{
					//CurrentFileWriteHandler.StrBuf.append("\n</t1>");
					//raf1.writeBytes(CurrentFileWriteHandler.StrBuf.toString());
					//System.out.println("BeforeWrite2");
					raf1.writeBytes(marker_a2);
					//CurrentFileWriteHandler.UpdateTime = System.currentTimeMillis();
					//CurrentFileWriteHandler.StrBuf.setLength(0);
					//System.out.println("AfterWrite2");
				}
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				//System.out.println("FilehandlerReceived1:"+FilehandlerReceived+" "+SFile);
		}
	}
	
	public static void write_last_location()
	//public void write_last_location(String filename, String MsgType, String vserial, String Version, String Fix, String Latitude, String Longitude, String Speed, String serverdatetime, String DateTime, String io_value1, String io_value2, String io_value3, String io_value4, String io_value5, String io_value6, String io_value7, String io_value8, String Signal_Strength, String SupplyVoltage)
	{
		RandomAccessFile out_a2 =null;
		//************WRITE LAST LOCATION FILE **********************
		//String filename = "/var/www/html/itrack_vts/xml_vts/last_location/"+vserial+".xml";
		String marker_a2="", q="\"", xml_date="", xml_last_halt_time="", xml_day_max_speed="", xml_day_max_speed_time="", xml_lat="", xml_lng="";
		String last_halt_time = "", day_max_speed="", day_max_speed_time="", xml_lat_s = "", xml_lng_s = "", Latitude_s="", Longitude_s="";
		float speed_f = 0.F;
		float supv_f = 0.F;
		
		strOrig = Speed;   
		char[] stringArray1;
		stringArray1 = strOrig.toCharArray();
		   
		//display the array    
		for(int index=0; index < stringArray1.length; index++)   
		{
		  if( (stringArray1[index]=='0' || stringArray1[index]=='1'  || stringArray1[index]=='2'  || stringArray1[index]=='3' || stringArray1[index]=='4' || stringArray1[index]=='5' || stringArray1[index]=='6' || stringArray1[index]=='7' || stringArray1[index]=='8' || stringArray1[index]=='9') )
		  {
			//System.out.print(stringArray[index]);
		  }  
		  else
		  {
			stringArray1[index] = '.';
		  }    
		  //System.out.print(stringArray[index]);
		}
		
		
		Speed = new String(stringArray1);
		speed_f = Float.parseFloat(Speed);
		speed_f = (float) (Math.round(speed_f*100.0)/100.0);
		float tmp_speed = 0.0f;

		day_max_speed = Speed;
	
		try {
			Date result;
			result = sDF.parse(ReceiveDateFormat);
			DateTime = dDF.format(result);	
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}			
		
		day_max_speed_time = DateTime; //DEFAULT ASSINGMENT
		Date date_last_loc1=null, date_last_loc2=null, date_servertime2=null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String tmpstr2="";
		//tmpstr2 = "/var/www/html/itrack_vts/xml_vts/last_location/"+vserial+".xml";
		tmpstr2 = RealDataPath+"/"+DeviceIMEINo+".xml";
		
		//ADD ONE HOUR TO SERVERTIME
		int minutesToAdd = 60; // 1 hrs
		
		try
		{
			//System.out.println("Date:"+DateTime+" ,serverdatetime="+serverdatetime);
			date_last_loc2 = (Date) sdf.parse(DateTime); //PARSE DATE LAST LOCATION
			
			Calendar cal = Calendar.getInstance();
			Date date = new Date();
			serverdatetime = dDF.format(date);
			cal.setTime(sdf.parse(serverdatetime));
			cal.add(Calendar.MINUTE, minutesToAdd);
			//System.out.println(cal.getTime());
		
			String TimeStop_Str = sdf.format(cal.getTime());
			//System.out.println(TimeStop_Str);
			date_servertime2 = (Date) sdf.parse(TimeStop_Str); //PARSE SERVER DATETIME
		} 
		catch(Exception e1)
		{
			System.out.println("ErrorLastLocation1="+e1.getMessage());
		}
		
		boolean FileNew = false;
		long FileLength=0;
		String strLine1="";
		String Fix = "1", io_value2 ="0",io_value3="0",io_value4="0",io_value5="0",io_value6="0",io_value7="0",io_value8="0", Signal_Strength="0";
		String io_pwrvolt="";
		try
		{
			out_a2 = new RandomAccessFile(tmpstr2, "rwd");
			//out_a2 = new RandomAccessFile(filename, "rw");
			FileLength = out_a2.length();
			out_a2.seek(0);
			
			if(FileLength<10)
			{
				last_halt_time = DateTime;
				if((date_last_loc2.compareTo(date_servertime2) < 0) && (!Latitude.equals("")) && (!Longitude.equals("")) && (!Latitude.equals("0.0")) && (!Longitude.equals("0.0")) )
				{
					//System.out.println("WriteFile:FirstTime");
					//marker_a2 = "<t1>\n<x "+"a="+q+io_header+q+" b="+q+io_firmware+q+" c="+q+Fix+q+" d="+q+io_lat+q+" e="+q+io_long+q+" f="+q+io_speed+q+" g="+q+serverdatetime+q+" h="+q+DateTime+q+" i="+q+io_value1+q+" j="+q+io_value2+q+" k="+q+io_value3+q+" l="+q+io_value4+q+" m="+q+io_value5+q+" n="+q+io_value6+q+" o="+q+io_value7+q+" p="+q+io_value8+q+" q="+q+Signal_Strength+q+" r="+q+io_pwrvolt+q+" s="+q+day_max_speed+q+" t="+q+day_max_speed_time+q+" u="+q+last_halt_time+q+"/>\n</t1>";
					//marker_a2 = "<\n<x "+"a="+q+MsgType+q+" b="+q+Version+q+" c="+q+Fix+q+" d="+q+Latitude+q+" e="+q+Longitude+q+" f="+q+speed_f+q+" g="+q+serverdatetime+q+" h="+q+DateTime+q+" i="+q+io_value1+q+" j="+q+io_value2+q+" k="+q+io_value3+q+" l="+q+io_value4+q+" m="+q+io_value5+q+" n="+q+io_value6+q+" o="+q+io_value7+q+" p="+q+io_value8+q+" q="+q+Signal_Strength+q+" r="+q+supv_f+q+"/>\n</t1>";
					marker_a2 = "<t1>\n<x "+"a="+q+MsgType+q+" b="+q+Version+q+" c="+q+Fix+q+" d="+q+Latitude+q+" e="+q+Longitude+q+" f="+q+speed_f+q+" g="+q+serverdatetime+q+" h="+q+DateTime+q+" i="+q+io_value1+q+" j="+q+io_value2+q+" k="+q+io_value3+q+" l="+q+io_value4+q+" m="+q+io_value5+q+" n="+q+io_value6+q+" o="+q+io_value7+q+" p="+q+io_value8+q+" q="+q+Signal_Strength+q+" r="+q+supv_f+q+"/>\n</t1>";
					//marker_a2 = "<t1>\n<x "+"a="+q+io_header+q+" z1="+q+io_device_id+q+" z2="+q+io_code+q+" b="+q+io_firmware+q+" h="+q+DateTime+q+" g="+q+serverdatetime+q+" z3="+q+io_cellid+q+" d="+q+io_lat+q+" e="+q+io_long+q+" f="+q+io_speed+q+" z4="+q+io_crs+q+" z5="+q+io_sat+q+" z6="+q+io_gpslock+q+" z7="+q+io_distance+q+" r="+q+io_pwrvolt+q+" i="+q+io_value1+q+" z8="+q+io_mode+q+" z9="+q+io_serial+q+"/>\n</t1>";
					//marker_a2 = "<t1>\n<x "+"a="+q+io_header+q+" b="+q+io_device_id+q+" c="+q+io_code+q+" d="+q+io_firmware+q+" e="+q+DateTime+q+" f="+q+serverdatetime+q+" g="+q+io_cellid+q+" h="+q+io_lat+q+" i="+q+io_long+q+" j="+q+io_speed+q+" k="+q+io_crs+q+" l="+q+io_sat+q+" m="+q+io_gpslock+q+" n="+q+io_distance+q+" o="+q+io_pwrvolt+q+" p="+q+io_value1+q+" q="+q+io_mode+q+" r="+q+io_serial+q+"/>\n</t1>";
				
					out_a2.seek(0);
					out_a2.writeBytes(marker_a2);
				
					//System.out.println("T1");
					//out_a2.close();
				}
			}
			//########## BLANK FILE CLOSED
			else
			{
				while ((strLine1 = out_a2.readLine()) != null)
				{
					int len = strLine1.length();
				
					if(len < 100)
					{
					continue;
					}
					xml_lat = getXmlAttribute(strLine1,"d=\"[^\"]+");
					xml_lng = getXmlAttribute(strLine1,"e=\"[^\"]+");
					xml_date = getXmlAttribute(strLine1,"h=\"[^\"]+");
					last_date = xml_date;
					xml_day_max_speed = getXmlAttribute(strLine1,"s=\"[^\"]+");
					xml_day_max_speed_time = getXmlAttribute(strLine1,"t=\"[^\"]+");
					xml_last_halt_time = getXmlAttribute(strLine1,"u=\"[^\"]+");

					//####### HANDLE EMPTY VARIABELS

					if(Latitude.equals(""))
					{
						Latitude = "0.0";
					}
					if(Longitude.equals(""))
					{
						Longitude = "0.0";
					}
					if(xml_lat.equals(""))
					{
						xml_lat = "0.0";
					}
					if(xml_lng.equals(""))
					{
						xml_lng = "0.0";
					}
					if(xml_day_max_speed.equals(""))
					{
						xml_day_max_speed = "0.0";
					}
					if(Speed.equals(""))
					{
						Speed = "0.0";
					}
					if(xml_day_max_speed_time.equals(""))
					{
						xml_day_max_speed_time = DateTime;
					}
					//############################
				
					//######LAST HALT TIME BLOCK
					Latitude_s = Latitude.substring(0, Latitude.length() - 1);
					Longitude_s = Longitude.substring(0, Longitude.length() - 1);
				
					xml_lat_s = xml_lat.substring(0, xml_lat.length() - 1);
					xml_lng_s = xml_lng.substring(0, xml_lng.length() - 1);
					//System.out.println("One");
					float distance1 = calculateDistance(Float.parseFloat(Latitude_s), Float.parseFloat(xml_lat_s), Float.parseFloat(Longitude_s), Float.parseFloat(xml_lng_s) );
					long time_diff = calculateTimeDiff(DateTime, xml_date); //Seconds
					time_diff = time_diff / 3600;
					//System.out.println("Two");
					//$tmp_time_diff1 = (strtotime($datetime) - strtotime($last_time1)) / 3600;
					if(time_diff>0)
					{
						tmp_speed = distance1 / (float) time_diff;
					}

					//System.out.println("tmp_speed="+tmp_speed+" ,distance="+distance1+" ,time_diff="+time_diff);
					if(tmp_speed>100.0 && distance1>0.1 && time_diff>0)
					{
				
					}
					else
					{
						//##### LAST HALT TIME
						if(Float.parseFloat(Speed) > 10.0)
						{
							last_halt_time = DateTime;
						}
						else
						{
						if(xml_last_halt_time.equals(""))
						{
							last_halt_time = DateTime;
						}
						else
						{
							last_halt_time = xml_last_halt_time;
						}
					}

					//###### DAY MAX SPEED AND TIME
					Float f1 = new Float(xml_day_max_speed);
					double d1 = f1.doubleValue();
				
					Float f2 = new Float(Speed);
					double d2 = f2.doubleValue();

					//System.out.println("xml_day_max_speed="+xml_day_max_speed+", Speed="+Speed);
					//System.out.println("d1="+d1+", d2="+d2);
				
					if(d2 > d1)
					{
						//System.out.println("condition if");
						day_max_speed = Speed;
						day_max_speed_time = DateTime;
					}
					else
					{
						//System.out.println("condition else");
						day_max_speed = xml_day_max_speed;
						day_max_speed_time = xml_day_max_speed_time;
					}

					//## RESET SPEED IF DAY CHANGES
					String[] daytmp1,day1,daytmp2,day2;
					String delimiter1 = " ",delimiter2="-";
					daytmp1 = xml_date.split(delimiter1);
					daytmp2 = DateTime.split(delimiter1);
				
					day1 = daytmp1[0].split(delimiter2);
					day2 = daytmp2[0].split(delimiter2);

					//System.out.println("day1="+day1[2]+" ,day2="+day2[2]);
					if(!(day1[2].equals(day2[2])))
					{
						//System.out.println("IN day1,day2");
						day_max_speed = "0.0";
						day_max_speed_time = DateTime;
						//System.out.println("day1="+day1[2]+" ,day2="+day2[2]);
					}
				}
				try
				{
					date_last_loc1 = (Date) sdf.parse(xml_date); //XML DATETIME
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
				}
				//if( (date_last_loc2.compareTo(date_last_loc1) > 0) && (date_last_loc2.compareTo(date_servertime2) < 0) && (date_last_loc2.compareTo(valid_date_min) > 0) && (date_last_loc2.compareTo(valid_date_max) < 0) )
				if( (date_last_loc2.compareTo(date_last_loc1) > 0) && (date_last_loc2.compareTo(date_servertime2) < 0) && (!Latitude.equals("")) && (!Longitude.equals("")) && (!Latitude.equals("0.0")) && (!Longitude.equals("0.0")) )
				{
					//System.out.println("WRITE TO LAST LOCATION FILE:"+filename);
					//out_a2 = new BufferedWriter(new FileWriter(tmpstr2, false));
					out_a2.seek(0);
					marker_a2 = "<t1>\n<x "+"a="+q+MsgType+q+" b="+q+Version+q+" c="+q+Fix+q+" d="+q+Latitude+q+" e="+q+Longitude+q+" f="+q+speed_f+q+" g="+q+serverdatetime+q+" h="+q+DateTime+q+" i="+q+io_value1+q+" j="+q+io_value2+q+" k="+q+io_value3+q+" l="+q+io_value4+q+" m="+q+io_value5+q+" n="+q+io_value6+q+" o="+q+io_value7+q+" p="+q+io_value8+q+" q="+q+Signal_Strength+q+" r="+q+supv_f+q+"/>\n</t1>";
					marker_a2 = "<t1>\n<x "+"a="+q+MsgType+q+" b="+q+Version+q+" c="+q+Fix+q+" d="+q+Latitude+q+" e="+q+Longitude+q+" f="+q+speed_f+q+" g="+q+serverdatetime+q+" h="+q+DateTime+q+" i="+q+io_value1+q+" j="+q+io_value2+q+" k="+q+io_value3+q+" l="+q+io_value4+q+" m="+q+io_value5+q+" n="+q+io_value6+q+" o="+q+io_value7+q+" p="+q+io_value8+q+" q="+q+Signal_Strength+q+" r="+q+io_pwrvolt+q+" s="+q+day_max_speed+q+" t="+q+day_max_speed_time+q+" u="+q+last_halt_time+q+"/>\n</t1>";
					//marker_a2 = "<t1>\n<x "+"a="+q+io_header+q+" z1="+q+io_device_id+q+" z2="+q+io_code+q+" b="+q+io_firmware+q+" h="+q+DateTime+q+" g="+q+serverdatetime+q+" z3="+q+io_cellid+q+" d="+q+io_lat+q+" e="+q+io_long+q+" f="+q+io_speed+q+" z4="+q+io_crs+q+" z5="+q+io_sat+q+" z6="+q+io_gpslock+q+" z7="+q+io_distance+q+" r="+q+io_pwrvolt+q+" i="+q+io_value1+q+" z8="+q+io_mode+q+" z9="+q+io_serial+q+"/>\n</t1>";
					//marker_a2 = "<t1>\n<x "+"a="+q+io_header+q+" b="+q+io_device_id+q+" c="+q+io_code+q+" d="+q+io_firmware+q+" e="+q+DateTime+q+" f="+q+serverdatetime+q+" g="+q+io_cellid+q+" h="+q+io_lat+q+" i="+q+io_long+q+" j="+q+io_speed+q+" k="+q+io_crs+q+" l="+q+io_sat+q+" m="+q+io_gpslock+q+" n="+q+io_distance+q+" o="+q+io_pwrvolt+q+" p="+q+io_value1+q+" q="+q+io_mode+q+" r="+q+io_serial+q+"/>\n</t1>";
					out_a2.writeBytes(marker_a2);				
					//System.out.println("T2");
					break;
					//out_a2.close();
					//System.out.println(marker_a2);
				}
			}
		}
		out_a2.close();	
	}
	catch (IOException e)
	{
		e.printStackTrace();
		try
		{
			out_a2.close();
		}
		catch (Exception e1)
		{
	
		}
	}	
	}
	public static float calculateDistance(float lat1, float lat2, float lng1, float lng2)
	{
		//System.out.println("In CACL DIST : lat1 : "+lat1+" lng1 : "+lng1+" lat2 : "+lat2 + " lng2 : "+lng2);
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
		Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;
		int meterConversion = 1609;
		return new Float(dist * meterConversion).floatValue(); //KM
	}
	
	public static String getXmlAttribute(String line, String param)
	{
		//System.out.println("In getXmlAttrib: line="+line+" ,param="+param);
		String str1 ="";
		String value ="";
		String[] str2;	
		try 
		{
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
		} 
		catch(Exception e) 
		{ 
			System.out.println("Error in function-Xml Attribute"+e.getMessage());
		}
	
		return value;
	}


	/************* METHOD- CALCULATE TIME DIFFERENCE ************/
	public static long calculateTimeDiff(String time1, String time2)
	{
		//System.out.println("Time1="+time1+" ,Time2="+time2);
		//System.out.println();	
		if(time1.equalsIgnoreCase(""))
		{
			return 600;
		}
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

		//System.out.println("Time in minutes: " + diffMinutes + " minutes.");
		//"yyyy-MM-dd HH:mm:ss"
		long diffSeconds= diffMinutes * 60; //IN SECONDS
		return diffSeconds;
	}
}