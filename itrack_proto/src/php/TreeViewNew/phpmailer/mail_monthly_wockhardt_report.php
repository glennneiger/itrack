<?php
	include_once('util_session_variable.php');
	include_once('util_php_mysql_connectivity.php');
	set_time_limit(36000);
	//set_time_limit(0);
	$abspath = "/var/www/html/vts/beta/src/php";
	include_once($abspath."/common_xml_element.php");
	include_once("../get_all_dates_between.php");
	include_once("../sort_xml.php");
	include_once("../calculate_distance.php");
	include_once("../report_title.php");
	include_once("../read_filtered_xml.php");
	include_once("../select_landmark_report.php");
	include_once("../util.hr_min_sec.php");
	//include("get_location.php");

	include_once("../area_violation/check_with_range.php");
	include_once("../area_violation/pointLocation.php");
	include("../user_type_setting.php");
	require_once "../excel_lib/class.writeexcel_workbook.inc.php";
	require_once "../excel_lib/class.writeexcel_worksheet.inc.php";

	$timetmp1 = 0;
	$breakflag = 0;
	/*$current_date = date("Y-m-d");
	$startdate = $current_date." 09:00:00";
	$enddate = $current_date." 18:00:00";*/

	function tempnam_sfx($path, $suffix)
	{
		do
		{
			//$file = $path."/".mt_rand().$suffix;
			$file = $path.$suffix;
			$fp = @fopen($file, 'x');
		}
		while(!$fp);
		fclose($fp);
		return $file;
	}

	//GET PREVIOUS MONTH
	$current_month=date('m');
	//$current_month=1;
	$current_year=date('Y');
	echo "\nCurrentYear=".$current_year;

	if($current_month==1)
	{
		$previous_month=12;
		$previous_year = $current_year -1;
	}
	else
	{
		$previous_month=$current_month-1;
		$previous_year = $current_year;
	}

	//CHANGE TEMPORARILY FOR CURRENT MONTH
	//$previous_month = "7";
	//$previous_year = "2012"; 
	$total_days=date('t',mktime(0,0,0,$previous_month,1,$previous_year));		// TOTAL DAYS IN PREVIOUS MONTH
	//////////////// 
	$vserial_global = array();
	$vname_global = array();
	$vid_global = array();
	$date_global = array(array());
	$daily_dist_global = array(array());
	$daily_halt_global = array(array());
	$total_distance_global = array();
	$total_halt_global = array();
	$daily_dist_tmp =0;
	$daily_halt_tmp =0;
   
	echo "previous_month=".$previous_month." ,previous_year=".$previous_year." ,total_days=".$total_days;
	$user_interval = "30";  
	/*$query_assignment = "SELECT DISTINCT vehicle.vehicle_id,vehicle.vehicle_name FROM vehicle,vehicle_assignment,alert,alert_assignment WHERE ".
						"vehicle.vehicle_id = vehicle_assignment.vehicle_id AND vehicle_assignment.vehicle_id = alert_assignment.vehicle_id AND ".
						"alert_assignment.alert_id = alert.alert_id AND alert.alert_name='visit_report' AND ".
						"vehicle.status=1 AND vehicle_assignment.status=1 AND alert_assignment.status=1"; 
	*/
	
	//########## COMMENT FOR NOW #######################################
	$query1 = "SELECT account_id FROM account WHERE user_id='wockhardt'";
	$result1 = mysql_query($query1,$DbConnection);
	$row1 = mysql_fetch_object($result1);
	$account_id = $row1->account_id;

	$query2 = "SELECT vehicle_id FROM vehicle_grouping WHERE account_id='$account_id' and status=1";
	$result2 = mysql_query($query2,$DbConnection);
	$vehicle_id_str ="";
	while($row2 = mysql_fetch_object($result2))
	{
		$vehicle_id_str = $vehicle_id_str.$row2->vehicle_id.",";
	}
		$vehicle_id_str = substr($vehicle_id_str, 0, -1);
	
	//###################################################################
	//$vehicle_id_str = "1087,1090,1123,1134,1136,1140,1142,1145,1149,1161,1162,2728,2803,2804,2805";
	// $vehicle_id_str = "1091";
	/*
	$query = "SELECT DISTINCT vehicle_assignment.device_imei_no, schedule_assignment.vehicle_id FROM vehicle_assignment, schedule_assignment WHERE ".
			"vehicle_assignment.vehicle_id = schedule_assignment.vehicle_id AND vehicle_assignment.status=1 AND ".
			"vehicle_assignment.vehicle_id IN($vehicle_id_str) AND schedule_assignment.status=1";        
	*/

	//$query = "SELECT DISTINCT vehicle_assignment.device_imei_no FROM vehicle_assignment WHERE vehicle_assignment.status=1 AND vehicle_assignment.vehicle_id IN($vehicle_id_str)";
	//echo "\n".$query;
	//echo "\nDBCon=".$DbConnection;
	$result_assignment = mysql_query($query,$DbConnection);
	$query_assignment = "SELECT DISTINCT vehicle.vehicle_id,vehicle.vehicle_name FROM vehicle,vehicle_assignment WHERE ".
						"vehicle.vehicle_id = vehicle_assignment.vehicle_id AND vehicle_assignment.vehicle_id IN($vehicle_id_str) AND vehicle.status=1 AND vehicle_assignment.status=1";              					
	$result_assignment = mysql_query($query_assignment,$DbConnection);

	$v=0;
	$vehicle_id_tmp ="";
	while($row_assignment = mysql_fetch_object($result_assignment))
	{
		$vehicle_id_a = $row_assignment->vehicle_id;
		$vehicle_name[] = $row_assignment->vehicle_name;

		$query_imei = "SELECT device_imei_no FROM vehicle_assignment WHERE vehicle_id ='$vehicle_id_a' AND status=1";
		//echo "\nquery_imei=".$query_imei;
		$result_imei = mysql_query($query_imei, $DbConnection);
		$row_imei = mysql_fetch_object($result_imei);
		$device_imei_no[] = $row_imei->device_imei_no;
		$vid[] = $vehicle_id_a;
		if($v==0) 
		$vehicle_id_tmp = $vehicle_id_tmp.$vehicle_id_a;
		else
		$vehicle_id_tmp = $vehicle_id_tmp.",".$vehicle_id_a; 
		$v++;     
	}

	$vsize=sizeof($device_imei_no);
	echo "\nvsize=".$vsize;
	if($vsize>0)
	{  
		monthly_wockhardt_report_prev_xml($device_imei_no, $vehicle_name, $vid);
	}

	function monthly_wockhardt_report_prev_xml($vserial, $vname, $vid)
	{
		//echo "\nIn function prev";  
		global $previous_month;
		global $previous_year; 	
		global $total_days;

		global $daily_dist_tmp;
		global $daily_halt_tmp;	

		global $vserial_global;
		global $vname_global;
		global $vid_global;
		global $date_global;
		global $daily_dist_global;
		global $daily_halt_global;   
		global $total_distance_global; 
		global $total_halt_global;               
  	
		if($previous_month<10)
		$previous_month = "0".$previous_month;
		for($i=0;$i<sizeof($vserial);$i++)  
		{  
			$tmp_serial = $i+1;
			echo "\nSERIAL=".$tmp_serial." ,TOTAL=".sizeof($vserial)."\n";
			$vserial_global[$i] = $vserial[$i];
			$vname_global[$i] = $vname[$i];
			$vid_global[$i] = $vid[$i];
			$total_dist_tmp = 0;
			$total_halt_tmp = 0;
			//for($j=0;$j<$total_days;$j++)
			//for($j=27;$j<30;$j++)	//FROM 28 TO 30 JUNE 2012
			//for($j=0;$j<4;$j++)       //FROM 28 TO 30 JUNE 2012
			for($j=0;$j<$total_days;$j++)
			{
				$daily_dist_tmp =0;             //RESET VARIABLES FOR INDIVIDUAL DAY
				$daily_halt_tmp =0;

				$day = $j+1;

				if($j<9)
				//$date = $previous_year."-".$previous_month."-0".$day;
				$date = "2013-10-0".$day;
				else
				//$date = $previous_year."-".$previous_month."-".$day;      
				$date = "2013-10-".$day;
				//$date1 = $date." 09:00:00";
				//$date2 = $date." 18:00:00";
				$date1 = $date." 09:00:00";
				$date2 = $date." 18:00:00";      
				echo "\nIMEI=".$vserial[$i]." ,Date1=".$date1." ,Date2=".$date2;
				get_monthly_wockhardt_report_xml($vserial[$i], $vname[$i], $date1, $date2);
				$date_global[$i][$j] = $date;
				$daily_dist_global[$i][$j] = $daily_dist_tmp;
				$daily_halt_global[$i][$j] = $daily_halt_tmp; 
				$total_dist_tmp += $daily_dist_tmp;
				$total_halt_tmp += $daily_halt_tmp; 
				echo ": daily_dist=".$daily_dist_global[$i][$j]." ,daily_halt=".$daily_halt_global[$i][$j];              
			}     
			$total_distance_global[$i] = $total_dist_tmp; 
			$total_halt_global[$i] = $total_halt_tmp; 
			
			echo "\nTotal_dist=".$total_distance_global[$i]." ,Total_halt=".$total_halt_global[$i];              
		}     
	}

//GET WOCKHARDT XML_DATA
function get_monthly_wockhardt_report_xml($vehicle_serial, $vname, $startdate, $enddate)
{
	//echo "<br>vs=".$vehicle_serial." ,vname=".$vname." ,startdate=".$startdate." ,enddate=".$enddate." ,xmltowrite=".$xmltowrite;
	//echo "\nIn function -wockhardt report xml main";
	global $va,$vb,$vc,$vd,$ve,$vf,$vg,$vh,$vi,$vj,$vk,$vl,$vm,$vn,$vo,$vp,$vq,$vr,$vs,$vt,$vu,$vv,$vw,$vx,$vy,$vz,$vaa,$vab;
	global $old_xml_date;
	
	$fix_tmp = 1;
	$xml_date_latest="1900-00-00 00:00:00";
	$CurrentLat = 0.0;
	$CurrentLong = 0.0;
	$LastLat = 0.0;
	$LastLong = 0.0;
	$firstData = 0;
	$distance =0.0;
	$linetowrite="";
	$firstdata_flag =0;     //INITIALISE FIRST FLAG
	$breakflag = 0;
	$date_1 = explode(" ",$startdate);
	$date_2 = explode(" ",$enddate);

	$datefrom = $date_1[0];
	$dateto = $date_2[0];
	$timefrom = $date_1[1];
	$timeto = $date_2[1];

	global $daily_dist_tmp;
	global $daily_halt_tmp;	

	get_All_Dates($datefrom, $dateto, &$userdates);
	//date_default_timezone_set("Asia/Calcutta");
	$current_datetime = date("Y-m-d H:i:s");
	$current_date = date("Y-m-d");
	//print "<br>CurrentDate=".$current_date;
	$date_size = sizeof($userdates);
	//$fh = fopen($xmltowrite, 'a') or die("can't open file 6"); //append
	//$j = 0;
	$total_dist = 0; 									  
	global $timetmp1;
	global $breakflag; 
	global $user_interval;   
	//echo "\nDate size=".$date_size;  
	$xml_sorted = "/mnt/volume4";

	for($i=0;$i<=($date_size-1);$i++)
	{
		//echo "\nIn Date Loop";
		//echo "<br>time=".$timetmp1;
		$timetmp2 = date("Y-m-d H:i:s");	
		$timetmp2 = strtotime($timetmp2);    
		$difftmp = ($timetmp2 - $timetmp1);
		//echo "<br>diff=".$difftmp;
		/*if($difftmp >= 200)
		{
			$breakflag=1;
			//echo "<center><font color=red>Data too large please select less duration/days</font></center>";
			break;
		}*/  
		$daily_dist = 0; 
		$daily_halt = 0;       
		$xml_current = "/mnt/volume3/current_data/xml_data/".$userdates[$i]."/".$vehicle_serial.".xml";	    		
		//echo "<br>xml_current=".$xml_current;    
		if (file_exists($xml_current))      
		{		    		
			//echo "in else";
			$xml_file = $xml_current;
			$CurrentFile = 1;
		}		
		else
		{
			$xml_file = "/mnt/volume4/".$userdates[$i]."/".$vehicle_serial.".xml";
			$CurrentFile = 0;
		}
		
		//echo "<br>xml_file =".$xml_file;	    	
		if (file_exists($xml_file)) 
		{			
			//echo "\nfile_exists xml_file";
			$t=time();
			$xml_original_tmp = "../../../../xml_tmp/original_xml/tmp_".$vehicle_serial."_".$t."_".$i.".xml";
			//echo "<br>xml_file=".$xml_file." <br>tmpxml=".$xml_original_tmp."<br>";

			if($CurrentFile == 0)
			{
				//echo "<br>ONE<br>";
				copy($xml_file,$xml_original_tmp);
			}
			else
			{
				//echo "<br>TWO<br>";
				$xml_unsorted = "../../../../xml_tmp/unsorted_xml/tmp_".$vehicle_serial."_".$t."_".$i."_unsorted.xml";
				//echo  "<br>".$xml_file." <br>".$xml_unsorted."<br><br>";

				copy($xml_file,$xml_unsorted);        // MAKE UNSORTED TMP FILE
				SortFile($xml_unsorted, $xml_original_tmp, $userdates[$i]);    // SORT FILE
				unlink($xml_unsorted);                // DELETE UNSORTED TMP FILE
			}      
			$total_lines = count(file($xml_original_tmp));
			//echo "<br>Total lines orig=".$total_lines;      
			$xml = @fopen($xml_original_tmp, "r") or $fexist = 0;       
			$logcnt=0;
			$DataComplete=false;                  
			$vehicleserial_tmp=null;
			$format =2;      
			if (file_exists($xml_original_tmp)) 
			{      
				//echo "\nFileExists";
				//$daily_dist =0;
				//  $firstdata_flag =0;
				//SWITCH MASTER VARIABLES
				set_master_variable($userdates[$i]);

				while(!feof($xml))          // WHILE LINE != NULL
				{
					$DataValid = 0;
					//echo fgets($file). "<br />";
					$line = fgets($xml);        // STRING SHOULD BE IN SINGLE QUOTE			

					if(strlen($line)>20)
					{
						$linetmp =  $line;
					}
  				
					$linetolog =  $logcnt." ".$line;
					$logcnt++;
					//fwrite($xmllog, $linetolog);
					  
					if(strpos($line,''.$vc.'="1"'))     // RETURN FALSE IF NOT FOUND
					{
						$format = 1;
						$fix_tmp = 1;
					}                
					else if(strpos($line,''.$vc.'="0"'))
					{
						$format = 1;
						$fix_tmp = 0;
					}  				
					if( (preg_match('/'.$vd.'="\d+.\d+[a-zA-Z0-9]\"/', $line, $lat_match)) &&  (preg_match('/'.$ve.'="\d+.\d+[a-zA-Z0-9]\"/', $line, $lng_match)) )
					{ 
						$lat_value = explode('=',$lat_match[0]);
						$lng_value = explode('=',$lng_match[0]);
						//echo " lat_value=".$lat_value[1];         
						if( (strlen($lat_value[1])>5) && ($lat_value[1]!="-") && (strlen($lng_value[1])>5) && ($lng_value[1]!="-") )
						{
							$DataValid = 1;
						}
					}          
					//if( (substr($line, 0,1) == '<') && (substr( (strlen($line)-1), 0,1) == '>') && ($fix_tmp==1) && ($f>0) && ($f<$total_lines-1) )        
					if( ($line[0] == '<') && ($line[strlen($line)-2] == '>') && ($DataValid == 1) )   // FIX_TMP =1 COMES IN BOTH CASE     
					{
						//preg_match('/\d+-\d+-\d+ \d+:\d+:\d+/', $line, $str3tmp);    // EXTRACT DATE FROM LINE
						//echo "<br>str3tmp[0]=".$str3tmp[0];
						$status = preg_match('/'.$vh.'="[^"]+/', $line, $datetime_tmp);
						$datetime_tmp1 = explode("=",$datetime_tmp[0]);
						$datetime = preg_replace('/"/', '', $datetime_tmp1[1]);	
						$xml_date = $datetime;
					}				
					//echo "Final0=".$xml_date." datavalid=".$DataValid;

					if($xml_date!=null)
					{				    					
						if( ($xml_date >= $startdate && $xml_date <= $enddate) && ($xml_date!="-") && ($DataValid==1) )
						{							           	
							//echo "<br>One";             
							/*$status = preg_match('/vehicleserial="[^" ]+/', $line, $vehicleserial_tmp);
							//echo "Status=".$status.'<BR>';
							//echo "test1".'<BR>';
							if($status==0)
							{
								continue;
							}*/
              
							$status = preg_match('/'.$vd.'="[^" ]+/', $line, $lat_tmp);
							if($status==0)
							{
								continue;               
							}
							//echo "test6".'<BR>';
							$status = preg_match('/'.$ve.'="[^" ]+/', $line, $lng_tmp);
							if($status==0)
							{
								continue;
							}                           
							$lat_tmp1 = explode("=",$lat_tmp[0]);
							$lat = preg_replace('/"/', '', $lat_tmp1[1]);

							$lng_tmp1 = explode("=",$lng_tmp[0]);
							$lng = preg_replace('/"/', '', $lng_tmp1[1]);                                                           
							
							$vserial = $vehicle_serial;
							//echo "<br>first=".$firstdata_flag;                                        
							if($firstdata_flag==0)
							{
								//echo "<br>FirstData";
								$firstdata_flag = 1;
								$halt_flag = 0;
								$lat1_dist = $lat;
								$lng1_dist = $lng;
								$lat1_halt = $lat;
								$lng1_halt = $lng;
								$time1_halt = $datetime;
								$interval = $user_interval*60;		//30 mins interval
								$last_time1 = $datetime;                                                        													                 	
							}           	
							//echo "<br>k2=".$k2."<br>";              	
							else
							{                           
								//********* HALT LOGIC BEGINS
								$lat2_halt = $lat;
								$lng2_halt = $lng;                
								$time2_halt = $datetime;                 
								calculate_distance($lat1_halt, $lat2_halt, $lng1_halt, $lng2_halt, &$distance_halt);              	
								//if( ($distance > 0.200) || ($f== $total_lines-2) )          			
								//echo "\nlat1_halt=".$lat1_halt.", lat2_halt=".$lat2_halt.", lng1_halt=".$lng1_halt.", lng2_halt=".$lng2_halt.", distance_halt=".$distance_halt;

								//if( ($distance_halt > 0.0100) || ($f== $total_lines-2) )
								if($distance > 0.150)
								{
									//echo "\nIn distance";
									//echo "<br>In dist ".$distance." lat_ref ".$lat_ref." lng_ref ".$lng_ref." lat_cr ".$lat_cr." lng_cr ".$lng_cr."<br>";
									if ($halt_flag == 1)
									{				
										//echo "\nIn Halt1";
										$arrivale_time = $time1_halt;
										$starttime = strtotime($time1_halt);

										//$stoptime = strtotime($datetime_cr);  
										$stoptime = strtotime($time2_halt);
										$depature_time = $time2_halt;
										//echo "<br>".$starttime." ,".$stoptime;

										$halt_dur =  ($stoptime - $starttime);

										//echo "\nHalt Dur=".$halt_dur." ,interval=".$interval." ,time1_halt=".$time1_halt." ,time2_halt=".$time2_halt;

										//if( ($halt_dur >= $interval) || ($f== $total_lines-2))
										if(($halt_dur >= $interval))
										{
											//echo "<br>In Halt else";
											$daily_halt = $daily_halt + $halt_dur; 
											/*$total_halt_vehicle = "\n<marker imei=\"".$vserial."\" vname=\"".$vname."\" lat=\"".$lat_ref."\" lng=\"".$lng_ref."\" arr_time=\"".$arrivale_time."\" dep_time=\"".$depature_time."\" duration=\"".$halt_dur."\"/>";						          						
											//echo "<br>total halt vehicle=".$total_halt_vehicle;
											$linetowrite = $total_halt_vehicle; // for distance       // ADD DISTANCE
											fwrite($fh, $linetowrite); */ 

											//$date_secs1 = strtotime($datetime_cr);
											//$date_secs1 = (double)($date_secs1 + $interval);                                                   
										}		// IF TOTAL MIN										
									}   //IF HALT FLAG              			
									$lat1_halt = $lat2_halt;
									$lng1_halt = $lng2_halt;
									$time1_halt = $time2_halt;            				
									$halt_flag = 0;
								}
								else if(((strtotime($time2_halt)-strtotime($time1_halt))>60) && ($halt_flag != 1))
								{            			
									//echo "<br>normal flag set";
									$halt_flag = 1;
								}					                                              							  							
								//********* HALT LOGIC CLOSED
								//********* DISTANCE LOGIC BEGINS
								$time2 = $datetime;											
								$date_secs2 = strtotime($time2);	

								/*$vehicleserial_tmp1 = explode("=",$vehicleserial_tmp[0]);
								$vserial = preg_replace('/"/', '', $vehicleserial_tmp1[1]);*/

								$lat2_dist = $lat;
								$lng2_dist = $lng;                 
								calculate_distance($lat1_dist, $lat2_dist, $lng1_dist, $lng2_dist, &$distance);
								//echo "<br>lat1=".$lat1." ,lat2=".$lat2." ,lng1=".$lng1." ,lng2=".$lng2." ,dist=".$distance;
  								$tmp_time_diff1 = (strtotime($datetime) - strtotime($last_time1)) / 3600;
								if($tmp_time_diff1>0)
								{
									$tmp_speed = $distance / $tmp_time_diff1;
									$last_time1 = $datetime;
								}
								$tmp_time_diff = (strtotime($datetime) - strtotime($last_time)) / 3600;                                
								//if($tmp_speed <3000 && $distance>0.1)
								if($tmp_speed<500 && $distance>0.1 && $tmp_time_diff>0)
								{		              
									//echo "\nIndistance";
									$daily_dist= (float) ($daily_dist + $distance);	
									$daily_dist = round($daily_dist,2);							                          

									///////////////////////////////////////////////////////////																							
									$lat1_dist = $lat2_dist;
									$lng1_dist = $lng2_dist;
									$last_time = $datetime;			
								}	
						                //**** DISTANCE LOGIC CLOSED						                               
							}
						} // $xml_date_current >= $startdate closed
					}   // if xml_date!null closed
  			 //$j++;
				}   // while closed
			} // if original_tmp closed 
			//WRITE DAILY DISTANCE DATA
			/*$daily_distance_data = "\n<marker imei=\"".$vserial."\" vname=\"".$vname."\" date=\"".$userdates[$i]."\" daily_dist=\"".$daily_dist."\"/>";						          						
			//echo "<br><br>".$daily_distance_data;
			$linetowrite = $daily_distance_data; // for distance       // ADD DISTANCE
			fwrite($fh, $linetowrite); */ 		
              
			$daily_dist_tmp = $daily_dist;
			$daily_halt_tmp = $daily_halt;

			fclose($xml);            
			unlink($xml_original_tmp);
		} // if (file_exists closed
	}  // for closed 
	if ($halt_flag == 1)
	{				
		$daily_halt = $daily_halt + $halt_dur;                       										
	}   //IF HALT FLAG

}


////////////////////////////
/// WRITE TO FILE

//date_default_timezone_set("Asia/Calcutta");
$datetime_sent = date("Y-m-d H:i:s");
  
/*$query2 = "SELECT DISTINCT escalation.person_name,escalation.person_email,".
          "escalation.escalation_id,alert.alert_id FROM ".
          "escalation,alert_assignment,alert WHERE ".
          "escalation.escalation_id = alert_assignment.escalation_id AND ".            
          "alert.alert_name='visit_report' AND ".
          "alert.alert_id = alert_assignment.alert_id AND ".                                        
          "alert_assignment.mail_status = 1 AND ".
          "alert_assignment.status=1 AND ".          
          "escalation.status=1 AND ".
          "alert.status=1";
echo "\n".$query2."\n";
$result2 = mysql_query($query2,$DbConnection); */

$k=0;
/*while($row2 = mysql_fetch_object($result2))
{
    $alert_id = $row2->alert_id; 
    $escalation_id = $row2->escalation_id; 
    $person_name = $row2->person_name;
    $person_email = $row2->person_email;    
    
    //*** GET TOTAL NUMBER OF VEHICLES IN THIS ESCALATION
    $query3 = "SELECT DISTINCT vehicle_assignment.device_imei_no FROM vehicle_assignment,alert_assignment WHERE ".              
              "vehicle_assignment.vehicle_id = alert_assignment.vehicle_id AND ".
              "alert_assignment.alert_id='$alert_id' AND alert_assignment.escalation_id='$escalation_id' AND vehicle_assignment.status=1";
    echo "\nQuery3=".$query3;
    $result3 = mysql_query($query3,$DbConnection);
*/
         
    /////********* CREATE EXCEL FILE *******************///////
	$inc_serial = rand();
    $filename_title = 'MONTHLY_WORKHARDT_VTS_REPORT_'.$previous_month.'_'.$previous_year.'_'.$inc_serial;
    echo "\nfilename=".$filename_title;
    $file_path = "/var/www/html/vts/beta/src/php/download/".$filename_title;
    //$file_path = "/var/www/html/vts/test/src/php/download/MONTHLY_VTS_REPORT";
    //$file_path = "/var/www/html/vts/test/src/php/download/test";
    //$file_path = 'test';
    //echo "\nfile_path=".$file_path;
    $fname = tempnam_sfx($file_path, ".xls");
    
    //$path ="/var/www/html/vts/test/src/php/download/test";
    //$fname = tempnam_sfx($path, ".xls");

    ////********** CREATE EXCEL WORKBOOK  ****************//////
    //echo "\nfname=".$fname;  
    $workbook =& new writeexcel_workbook($fname);                     //******* ADD WORKBOOK
    //$worksheet =& $workbook->addworksheet('Report1');
    //$worksheet->write('A1', "Worksheet1-Text1");
    //$workbook->close();
    //echo "\nworkbook created";
    
    $border1 =& $workbook->addformat();                               //******* DEFINE BORDER STYLE
    $border1->set_color('white');
    $border1->set_bold();
    $border1->set_size(9);
    $border1->set_pattern(0x1);
    $border1->set_fg_color('green');
    $border1->set_border_color('yellow');
    //$border1->set_top(5);
    //$border1->set_bottom(5);
    //$border1->set_left(5);
    //$border1->set_align('center');
    //$border1->set_align('vcenter');
    //$border1->set_merge(); # This is the key feature
    
    //echo "\nafter border1";
    
    $text_format =& $workbook->addformat(array(                      //******* DEFINE TEXT STYLE
                    bold    => 1,
                    //italic  => 1,                       
                    color   => 'blue',
                    size    => 10,
                    //font    => 'Comic Sans MS'
                ));
                                                                                  
    $blank_format = & $workbook->addformat();                       //******* DEFINE BLANK STYLE
    $blank_format->set_color('white');
    $blank_format->set_bold();
    $blank_format->set_size(12);
    $blank_format->set_merge();
    /////**********************
    //echo "\nafter blank format";        
   
    $imei_msg = "";
    $vids ="";
    //while($row3= mysql_fetch_object($result3))
    //{
      //$vserial_db = $row3->device_imei_no;
      //echo "\nvserial_db=".$vserial_db." ,vserial_global size=".sizeof($vserial_global);
       
      for($j=0;$j<sizeof($vserial_global);$j++)
      {        

	echo "\nSERIAL=".$j." ,TOTAL=".sizeof($verserial_global)."\n";
        //if($vserial_db == $vserial_global[$j])
        //{
          echo "\nVehicle Matched";
          include('create_excel_sheet_worckhardt.php');
          
          if($imei_msg=="")
          {
            $imei_msg = $imei_msg.$vserial_global[$j];
            $vids = $vids.$vid_global[$j];
          }
          else
          {
            $imei_msg = $imei_msg.",".$vserial_global[$j];
            $vids = $vids.",".$vid_global[$j];
          }
          //break;
       // }
        //echo "\nvehicle did not match";
      }       
    //}  //inner while closed
         
    echo "\nimei_msg=".$imei_msg." ,vids=".$vids;
    $workbook->close(); //CLOSE WORKBOOK
    echo "\nWORKBOOK CLOSED";
    /////////////////////////////////////////////////////        
    //echo "<br>visit_area_id=".$visit_area_id_tmp." ,vehicle_id=".$vehicle_id_tmp             
    
    //$k=0;
    $vehicle_id_2 ="";
    $email_message = ""; 
  	$fileatt_final="";
  	$fileatt_type_final="";
  	$fileatt_name_final="";
                     
    $email_from_d="support@iembsys.co.in";
  	$email_subject_d = "MONTHLY_WORKHARDT_VTS_REPORT_(".$previous_month."/".$previous_year.")";
  	$email_message_d = "MONTHLY_WORKHARDT_VTS_REPORT_(".$previous_month."/".$previous_year.") for imeis\n\n-".$imei_msg;
  	$email_to_d = $person_email;  //"rizwan@iembsys.com";
  	
    $fileatt_d = $file_path.".xls";
    $fileatt_type_d = "xls";
    $fileatt_name_d = 'MONTHLY_WORKHARDT_VTS_REPORT_('.$previous_month.'/'.$previous_year.')S_'.$k;	
    
    echo "\nemail_from=".$email_from_d;
    echo ", email_subject=".$email_subject_d;
    echo ", email_to=".$email_to_d;
    echo ", email_message=".$email_message_d."\n";           
    
    //SAVE FINAL MAIL CONTENT TO MAIL INFO TABLE	
  	$email_from="support@iembsys.co.in";
    
    echo "\nalert_id=".$alert_id;
    echo ", vehicle_id=".$vids;
    echo ", person_name=".$person_name;
    
    /*$query4 = "INSERT INTO mail_info_visit_report(alert_id,vehicle_id,escalation_id,person_name,".
              "fileatt,fileatt_type,fileatt_name,email_from,email_subject,email_message,email_to,datetime_sent,status) VALUES(".
              "'$alert_id','$vids','$escalation_id','$person_name','$fileatt_d','$fileatt_type_d','$fileatt_name_d',".
              "'$email_from_d','$email_subject_d','$email_message_d','$email_to_d','$datetime_sent',1)";
              
    echo "\nq4=".$query4;
    $result4 = mysql_query($query4,$DbConnection);    
    */
    $k++; //INCREMENT ESCALATION
         
//}  // ESCALATION WHILE CLOSED               

//***CLOSED-WRITE MONTHLY WOCKHARDT REPORT 


	########### SEND MAIL ##############//
	//$to = 'rizwan@iembsys.com';
	$to = 'jyoti.jaiswal@iembsys.com';
	$subject = $email_subject_d;
	$message = $email_message_d; 
	$random_hash = md5(date('r', time()));  
	$headers = "From: support@iembsys.co.in\r\n";
	//$headers .= "Cc: rizwan@iembsys.com";  
	$headers .= "Cc: rizwan@iembsys.com";
	//$headers .= "Cc: rizwan@iembsys.com,jyoti.jaiswal@iembsys.com";
	$headers .= "\r\nContent-Type: multipart/mixed; boundary=\"PHP-mixed-".$random_hash."\""; 
	$filename_title = $fileatt_name_d.".xls";
	$file_path = $file_path.".xls";

	//echo "\nFILE PATH=".$file_path;  
	include_once("send_mail_api.php");
	//################################//

?>
					
