<?php
	set_time_limit(2000);
	include_once('main_vehicle_information_1.php');
	include_once('Hierarchy.php');
	include_once('util_session_variable.php');
	include_once('util_php_mysql_connectivity.php');
	include_once('user_type_setting.php');	
	include_once('calculate_distance.php');	
	include_once('common_xml_element.php');

	$xmltowrite = $_REQUEST['xml_file']; 	
	$mode = $_REQUEST['mode'];
	$vserial1 = $_REQUEST['vserial'];
	$startdate = $_REQUEST['startdate'];
	$enddate = $_REQUEST['enddate']; 
	$time_interval1 = $_REQUEST['time_interval']; 
	
	//echo "Crd=".$crd_data."mode=".$mode."<br>";
	//echo "xmltowrite=".$vserial1."<br>";
	//echo "xmltowrite=".$xmltowrite."<br>";
		
	$vserial = explode(',',$vserial1) ;   
	if($report_type	== "Vehicle")
	{
		include_once("sort_xml.php");
	}
	else
	{
		include_once("sort_xml_person.php");
	}
	//include_once("sort_xml.php");
	$minlat = 180; 
	$maxlat = -180;
	$minlong = 180;
	$maxlong = -180;
	$maxPoints = 1000;
	$file_exist = 0;	
	$tmptimeinterval = strtotime($enddate) - strtotime($startdate);
	
	if($time_interval1=="auto")
	{
		$timeinterval =   ($tmptimeinterval/$maxPoints);
		$distanceinterval = 0.1; 
	}
	else
	{
		if($tmptimeinterval>86400)
		{
			$timeinterval =   $time_interval1;		
			$distanceinterval = 0.3;
		}
		else
		{
			$timeinterval =   $time_interval1;
			$distanceinterval = 0.02;
		}
	} 
	if($mode==1)
	{
		$fh = fopen($xmltowrite, 'w') or die("can't open file 1"); // new
		fwrite($fh, "<t1>");  
		fclose($fh);
		$vname_str ="";
		$vnumber_str ="";		
		for($i=0;$i<sizeof($vserial);$i++)
		{  	
			$vehicle_info=get_vehicle_info($root,$vserial[$i]);
			$vehicle_detail_local=explode(",",$vehicle_info);
			$vname_str = $vname_str.$vehicle_detail_local[0].":";
			$vnumber_str = $vnumber_str.$vehicle_detail_local[2].":";
			//echo "vname=".$vehicle_detail_local[1].'vnumber='.$vehicle_detail_local[2].'<br>';
		//	getLastPosition($vserial[$i],$vehicle_detail_local[0],$vehicle_detail_local[1],$vehicle_detail_local[2],$startdate,$enddate,$xmltowrite);
			
			if($report_type=="Vehicle")
			{
				getLastPosition($vserial[$i],$vehicle_detail_local[0],$vehicle_detail_local[1],$vehicle_detail_local[2],$startdate,$enddate,$xmltowrite);
			}
			else
			{
				getLastPositionPerson($vserial[$i],$vehicle_detail_local[0],$vehicle_detail_local[1],$vehicle_detail_local[2],$startdate,$enddate,$xmltowrite);
			}
			//getCurrentRecard($vserial[$i],$vehicle_detail_local[0],$vehicle_detail_local[1],$vehicle_detail_local[2],$startdate,$enddate,$xmltowrite);
		}
		$vname1=substr($vname_str,0,-1); /////////for last position text report
		$vnumber1=substr($vnumber_str,0,-1); /////////for last position text report
		$fh = fopen($xmltowrite, 'a') or die("can't open file 2"); //append
		fwrite($fh, "\n<a1 datetime=\"unknown\"/>");
		fwrite($fh, "\n</t1>");  
		fclose($fh);
	}
	else if($mode==2)
	{
		$fh = fopen($xmltowrite, 'w') or die("can't open file 3"); // new
		fwrite($fh, "<t1>");  
		fclose($fh);
		$vehicle_info=get_vehicle_info($root,$vserial[0]);
		$vehicle_detail_local=explode(",",$vehicle_info);	
		if($report_type=="Vehicle")
		{
			getTrack($vserial[0], $vehicle_detail_local[0], $vehicle_detail_local[2], $startdate,$enddate,$xmltowrite,$timeinterval,$distanceinterval);
		}
		else
		{
			getTrackPerson($vserial[0], $vehicle_detail_local[0], $vehicle_detail_local[2], $startdate,$enddate,$xmltowrite,$timeinterval,$distanceinterval);
		}		
		$fh = fopen($xmltowrite, 'a') or die("can't open file 4"); //append
		fwrite($fh, "\n<a1 datetime=\"unknown\"/>");
		fwrite($fh, "\n</t1>");  
		fclose($fh);
	}
	
	if($crd_data==1)
	{
		$fh = fopen($xmltowrite, 'w') or die("can't open file 1"); // new
		fwrite($fh, "<t1>");  
		fclose($fh);
		$vserial_arr = explode(',',$vserial);
		$vname1 ="";

		for($i=0;$i<sizeof($vserial_arr);$i++)
		{
			$tmp = explode('#',$vserial_arr[$i]);
			$imei = $tmp[0];
			$last_time = $tmp[1];
			$vehicle_info=get_vehicle_info($root,$imei);
			$vehicle_detail_local=explode(",",$vehicle_info);	
			get_vehicle_last_data($current_date, $imei, $last_time, $vehicle_detail_local[0], $xmltowrite);
		}

		$fh = fopen($xmltowrite, 'a') or die("can't open file 2"); //append
		fwrite($fh, "\n<a1 datetime=\"unknown\"/>");       
		fwrite($fh, "\n</t1>");  
		fclose($fh);
  
	}
	
	
	function get_vehicle_last_data($current_date, $imei, $last_time, $vname, $pathtowrite)
	{
		//date_default_timezone_set('Asia/Calcutta');
		$current_time = date('Y-m-d H:i:s');
		global $d;
		$d++;
		$xml_file = "../../../xml_vts/xml_last/".$imei.".xml";
		$file = file_get_contents($xml_file);
		if(!strpos($file, "</t1>")) 
		{
			usleep(1000);
		}		
  
		$t=time();
		$rno = rand();			
		$xml_original_tmp = "../../../xml_tmp/original_xml/tmp_".$imei."_".$t."_".$rno.".xml";		
		copy($xml_file,$xml_original_tmp); 
	    
		if (file_exists($xml_original_tmp))
		{
			//echo "<br>exist2";
			$fexist =1;
			$fp = fopen($xml_original_tmp, "r") or $fexist = 0;   
			$total_lines =0;
			$total_lines = count(file($xml_original_tmp));
			//echo "<br>total_lines=".$total_lines;
			$c =0;
			while(!feof($fp)) 
			{
				$line = fgets($fp);
				$c++;		
				if(strlen($line)>15)
				{
					if($userdates[$i]<$old_xml_date)  /// for sorted xml
					{
						old_xml_variables();						
					}
					else
					{
						new_xml_variables();
					}
					if ( (preg_match('/'.$d.'="\d+.\d+[a-zA-Z0-9]\"/', $line, $lat_match)) && (preg_match('/'.$e.'="\d+.\d+[a-zA-Z0-9]\"/', $line, $lng_match)) )
					{
						$status = preg_match('/'.$h.'="[^"]+/', $line, $datetime_tmp);
						$datetime_tmp1 = explode("=",$datetime_tmp[0]);
						$datetime = preg_replace('/"/', '', $datetime_tmp1[1]);
						$xml_date = $datetime;

						$status = preg_match('/'.$f.'="[^"]+/', $line, $speed_tmp);
						$speed_tmp1 = explode("=",$speed_tmp[0]);
						$speed = preg_replace('/"/', '', $speed_tmp1[1]);

						$status = preg_match('/'.$d.'="[^"]+/', $line, $lat_tmp);
						$lat_tmp1 = explode("=",$lat_tmp[0]);
						$lat = preg_replace('/"/', '', $lat_tmp1[1]);

						$status = preg_match('/'.$e.'="[^"]+/', $line, $lng_tmp);
						$lng_tmp1 = explode("=",$lng_tmp[0]);
						$lng = preg_replace('/"/', '', $lng_tmp1[1]);

						$status = preg_match('/'.$s.'="[^"]+/', $line, $day_max_speed_tmp);
						$day_max_speed_tmp1 = explode("=",$day_max_speed_tmp[0]);
						$day_max_speed = preg_replace('/"/', '', $day_max_speed_tmp1[1]);

						$status = preg_match('/'.$t.'="[^"]+/', $line, $day_max_speed_time_tmp);
						$day_max_speed_time_tmp1 = explode("=",$day_max_speed_time_tmp[0]);
						$day_max_speed_time = preg_replace('/"/', '', $day_max_speed_time_tmp1[1]);

						$status = preg_match('/'.$u.'="[^"]+/', $line, $last_halt_time_tmp);
						$last_halt_time_tmp1 = explode("=",$last_halt_time_tmp[0]);
						$last_halt_time = preg_replace('/"/', '', $last_halt_time_tmp1[1]);                                                                 
								  

						$xml_date_sec = strtotime($xml_date);   
						$current_time_sec = strtotime($current_time);

						//////////////////////////////////////////
						$diff = ($current_time_sec - $xml_date_sec);   // IN SECONDS
							  
						//if( ($diff < 120) && ($lat!="" || $lng!="") && ($speed>=5) )         //< 2 min
						//if( ($diff < 180) && ($lat!="" || $lng!="") )
						//if($speed>=10 && $diff <=180)
						if($speed>=5 && $diff <=600)
						{
							$status = "Running";
							//echo "<br>Running";
						}               
						/*else if((($diff < 120) || ($diff >180 && $diff <1200)) && ($speed<10))      //>2 and <20 min
						{
							$status = "Idle";
							//echo "<br>Idle";
						}
						//else if(($diff >1200) && ($speed <10))               //>20 min
						else if($diff >1200)        //>20 min
						{
						$status = "Stopped";
						//echo "<br>Stopped";
						} */
						else
						{
							$status = "Stopped";
						}                           
				
						$line = substr($line, 0, -3);
						$line2 = "\n".$line.' s="'.$vehicle_serial.'" t="'.$vname.'" aa="'.$status.'"/>';                          									
					}																			
				}			
			}		
			//echo "<br>pathtowrite1:".$pathtowrite."<br>";			
			$len = strlen($line2);
			if($len>0)
			{
				//echo "<br>pathtowrite2:".$pathtowrite."<br>";				
				$fh = fopen($pathtowrite, 'a') or die("can't open file pathtowrite"); //append
				//$fh = fopen($pathtowrite, 'w') or die("can't open file 1");
				fwrite($fh, $line2);  
				fclose($fh);
				fclose($fp);
				unlink($xml_original_tmp);
				//break;
			}
			else
			{
				fclose($fp);
				unlink($xml_original_tmp);
			}							
		}	
	}

	function get_All_Dates($fromDate, $toDate, &$userdates)
	{
		$dateMonthYearArr = array();
		$fromDateTS = strtotime($fromDate);
		$toDateTS = strtotime($toDate);

		for ($currentDateTS = $fromDateTS; $currentDateTS <= $toDateTS; $currentDateTS += (60 * 60 * 24)) 
		{
			// use date() and $currentDateTS to format the dates in between
			$currentDateStr = date("Y-m-d",$currentDateTS);
			$dateMonthYearArr[] = $currentDateStr;
			//print $currentDateStr.�<br />�;
		}
		$userdates = $dateMonthYearArr;
	}

	function get_maxspd_halt($imei)
	{
		global $va,$vb,$vc,$vd,$ve,$vf,$vg,$vh,$vi,$vj,$vk,$vl,$vm,$vn,$vo,$vp,$vq,$vr,$vs,$vt,$vu,$vv,$vw,$vx,$vy,$vz,$vaa,$vab;
		global $old_xml_date;
		new_xml_variables();		
		$xml_file = "../../../xml_vts/xml_last/".$imei.".xml";
		//echo "xml_file=".$xml_file."<br>";
		//echo "a=".$vs." b=".$vt."<br>";
		$file = file_get_contents($xml_file);
		if(!strpos($file, "</t1>")) 
		{
			usleep(1000);
		}		
		 
		$t=time();
		$rno = rand();			
		$xml_original_tmp = "../../../xml_tmp/original_xml/tmp_".$imei."_".$t."_".$rno.".xml";		
		copy($xml_file,$xml_original_tmp); 

		if(file_exists($xml_original_tmp))
		{
			//echo "<br>exist2";
			$fexist =1;
			$fp = fopen($xml_original_tmp, "r") or $fexist = 0;   
			$total_lines =0;
			$total_lines = count(file($xml_original_tmp));
			//echo "<br>total_lines=".$total_lines;
			$c =0;
			while(!feof($fp)) 
			{
				$line = fgets($fp);
				$c++;				

				if(strlen($line)>15)
				{					
					if((preg_match('/'.$vd.'="\d+.\d+[a-zA-Z0-9]\"/', $line, $lat_match)) && (preg_match('/'.$ve.'="\d+.\d+[a-zA-Z0-9]\"/', $line, $lng_match)) )
					{
						//echo "in if";
						/*$status = preg_match('/datetime="[^"]+/', $line, $datetime_tmp);
						$datetime_tmp1 = explode("=",$datetime_tmp[0]);
						$datetime = preg_replace('/"/', '', $datetime_tmp1[1]);
						$xml_date = $datetime; */          

						$status = preg_match('/'.$vs.'="[^"]+/', $line, $day_max_speed_tmp);
						//print_r($last_halt_time_tmp);
						$day_max_speed_tmp1 = explode("=",$day_max_speed_tmp[0]);
						$day_max_speed = preg_replace('/"/', '', $day_max_speed_tmp1[1]);

						$status = preg_match('/'.$vt.'="[^"]+/', $line, $day_max_speed_time_tmp);
						$day_max_speed_time_tmp1 = explode("=",$day_max_speed_time_tmp[0]);
						$day_max_speed_time = preg_replace('/"/', '', $day_max_speed_time_tmp1[1]);

						$status = preg_match('/'.$vu.'="[^"]+/', $line, $last_halt_time_tmp);
						//echo "ddd=".$last_halt_time_tmp[0]."<br>";
						//print_r($last_halt_time_tmp);
						$last_halt_time_tmp1 = explode("=",$last_halt_time_tmp[0]);
						$last_halt_time = preg_replace('/"/', '', $last_halt_time_tmp1[1]);
						//echo "last_halt_time=".$last_halt_time."<br>";
					}																			
				}			
			}

			fclose($fp);
			unlink($xml_original_tmp);

			if($day_max_speed > 200)
			{
				$day_max_speed = "0";
			}

			$day_max_speed = round($day_max_speed,2);
			$day_max_speed = $day_max_speed." km/hr";
			$data_string = $day_max_speed.",".$day_max_speed_time.",".$last_halt_time;		
		}
		return $data_string;  
	}

	function getLastPosition($vehicle_serial,$vname,$vtype,$vehicle_number,$startdate,$enddate,$xmltowrite)
	{
		//echo "in function<br>";
		global $va,$vb,$vc,$vd,$ve,$vf,$vg,$vh,$vi,$vj,$vk,$vl,$vm,$vn,$vo,$vp,$vq,$vr,$vs,$vt,$vu,$vv,$vw,$vx,$vy,$vz,$vaa,$vab;
	global $old_xml_date;		
		//echo "xml_Date=".$old_xml_date."<br>";	
		//echo "<br>".$vehicle_serial.", ,".$vname." ,".$startdate.", ".$enddate.", ".$xmltowrite."<br>";	
		$fix_tmp = 1;
		$xml_date_latest="1900-00-00 00:00:00";
		$linetowrite="";
		$dataValid = 0;
		$date_1 = explode(" ",$startdate);
		$date_2 = explode(" ",$enddate);

		$datefrom = $date_1[0];
		$dateto = $date_2[0];
		$timefrom = $date_1[1];
		$timeto = $date_2[1];

		get_All_Dates($datefrom, $dateto, &$userdates);	
		//echo "3:datefrom=".$datefrom.' '."dateto=".$dateto.' '."userdates=".$userdates[0].'<BR>';
		date_default_timezone_set("Asia/Calcutta");
		$current_datetime = date("Y-m-d H:i:s");
		$current_date = date("Y-m-d");
		//print "<br>CurrentDate=".$current_date;
		$date_size = sizeof($userdates);
		//echo "4:date_size=".$date_size.'<BR>';
		
	//echo "serial=".$vehicle_serial."<br>";

		$last_location_string = get_maxspd_halt($vehicle_serial);
		//echo "vc=".$vc."<br>";
		$data = explode(',',$last_location_string);
		
		$day_max_speed = $data[0];
		$day_max_speed_time = $data[1];
		$last_halt_time = $data[2];
		
		
		for($i=($date_size-1);$i>=0;$i--)
		{		
			//if($userdates[$i] == $current_date)
			//{	
			$xml_current = "../../../xml_vts/xml_data/".$userdates[$i]."/".$vehicle_serial.".xml";
			//echo "xml_current=".$xml_current."<br>";
			if (file_exists($xml_current))      
			{ 
				$xml_file = $xml_current;						
			}		
			else
			{
				$xml_file = "../../../sorted_xml_data/".$userdates[$i]."/".$vehicle_serial.".xml";	
			}	
			
			//echo "xml_current=".$xml_file."<br>";
			if (file_exists($xml_file)) 
			{
				$t=time();			
				$xml_original_tmp = "../../../xml_tmp/original_xml/tmp_".$vehicle_serial."_".$t."_".$i.".xml";	
				//echo "xmo_file=".$xml_original_tmp." xml_file=".$xml_file."<br>";
				copy($xml_file,$xml_original_tmp); 
				$fexist =1;
				$xml = fopen($xml_original_tmp, "r") or $fexist = 0;               
				$format = 2;  
				//echo "in if<br>";
				if (file_exists($xml_original_tmp)) 
				{
					set_master_variable($userdates[$i]);
					//echo "exists<br>";
					while(!feof($xml))          // WHILE LINE != NULL
					{								
						$DataValid = 0;
						$line = fgets($xml);        // STRING SHOULD BE IN SINGLE QUOTE
						//echo"<textarea>".$line."</textarea>";
						//echo $line;
					//echo "date1=".$userdates[$i]."<br>";
					
						
						//echo "vc=".$vc."<br>";
						
						if(strpos($line,''.$vc.'="1"'))     // RETURN FALSE IF NOT FOUND
						{
							$fix_tmp = 1;
						}
						else if(strpos($line,''.$vc.'="0"'))
						{
							$fix_tmp = 0;
						}					
						if((preg_match('/'.$vd.'="\d+.\d+[a-zA-Z0-9]\"/', $line, $lat_match) ) && (preg_match('/'.$ve.'="\d+.\d+[a-zA-Z0-9]\"/', $line, $lng_match)))
						{ 
							//echo "in lat<br>";
							$lat_value = explode('=',$lat_match[0]);
							$lng_value = explode('=',$lng_match[0]);
							if( (strlen($lat_value[1])>5) && ($lat_value[1]!="-") && (strlen($lng_value[1])>5) && ($lng_value[1]!="-") )
							{
								$DataValid = 1;
							}
						}
						/*echo "datavalid=".$DataValid;
						echo " line=".$line[0];
						echo " fix_tmp=".$fix_tmp;
						echo " length=".strlen($line);
						echo " line1=".$line[strlen($line)-3];*/
						if(($line[0] == '<') && ($line[strlen($line)-2] == '>') && ($fix_tmp==1) && ($DataValid == 1))
						{
							//echo "in lng<br>";
							$status = preg_match('/'.$vh.'="[^"]+/', $line, $datetime_tmp);
							$datetime_tmp1 = explode("=",$datetime_tmp[0]);
							$datetime = preg_replace('/"/', '', $datetime_tmp1[1]);
							$xml_date_current = $datetime;				
						}
						//echo "xml_date_1=".$xml_date_current."<br>";
						if($xml_date_current!=null)
						{
							if(($xml_date_current >= $startdate && $xml_date_current <= $enddate) && ($xml_date_current!="-"))
							{
								if(($xml_date_current>$xml_date_latest) && (($xml_date_current<=$enddate) && ($xml_date_current>=$startdate)))
								{
									$xml_date_latest = $xml_date_current;
									$line = substr($line, 0, -3);	
									//echo "in if 111";
									if($userdates[$i]<$old_xml_date)
									{
										//echo "in if 111";
										$line=str_replace("marker","x",$line);
										$line=str_replace("msgtype=","a=",$line);
										$line=str_replace("vehicleserial=","v=",$line);
										$line=str_replace("ver=","b=",$line);
										$line=str_replace("fix=","c=",$line);
										$line=str_replace("lat=","d=",$line);
										$line=str_replace("lng=","e=",$line);
										$line=str_replace("speed=","f=",$line);
										$line=str_replace("sts=","g=",$line);
										$line=str_replace("datetime=","h=",$line);
										$line=str_replace("io1=","i=",$line);
										$line=str_replace("io2=","j=",$line);
										$line=str_replace("io3=","k=",$line);
										$line=str_replace("io4=","l=",$line);
										$line=str_replace("io5=","m=",$line);
										$line=str_replace("io6=","n=",$line);
										$line=str_replace("io7=","o=",$line);
										$line=str_replace("io8=","p=",$line);
										$line=str_replace("sig_str=","q=",$line);
										$line=str_replace("sup_v=","r=",$line);
										$line=str_replace("day_max_speed=","s=",$line);
										$line=str_replace("day_max_speed_time=","t=",$line);
										$line=str_replace("last_halt_time=","u=",$line);
										$line=str_replace("cellname=","ab=",$line);
										$linetowrite = "\n".$line.' s="'.$day_max_speed.'" t="'.$day_max_speed_time.'" u="'.$last_halt_time.'" w="'.$vname.'" x="'.$vehicle_number.'" y="'.$vtype.'"/>';
									}
									else
									{
										$linetowrite = "\n".$line.' s="'.$day_max_speed.'" t="'.$day_max_speed_time.'" u="'.$last_halt_time.'" v="'.$vehicle_serial.'" w="'.$vname.'" x="'.$vehicle_number.'" y="'.$vtype.'"/>';
									}
									//$linetowrite = "\n".$line.' vname="'.$vname.'" vnumber="'.$vehicle_number.'"  vtype="'.$vtype.'"/>';
									//$linetowrite = "\n".$line.' s="'.$day_max_speed.'" t="'.$day_max_speed_time.'" u="'.$last_halt_time.'" v="'.$vehicle_serial.'" w="'.$vname.'" x="'.$vehicle_number.'" y="'.$vtype.'"/>';
										
									//$linetowrite = "\n".$line.' day_max_speed="'.$day_max_speed.'" day_max_speed_time="'.$day_max_speed_time.'" last_halt_time="'.$last_halt_time.'" vname="'.$vname.'" vehicle_number="'.$vehicle_number.'" vtype="'.$vtype.'"/>';
								}
							}
						}
					} // while closed
				}  // if original_tmp exist closed        
				if(strlen($linetowrite)!=0)
				{
					//echo "<br>".$xmltowrite."<br>";				
					$fh = fopen($xmltowrite, 'a') or die("can't open file 5"); //append
					fwrite($fh, $linetowrite);  
					fclose($fh);
					fclose($xml);
					unlink($xml_original_tmp);
					break;
				}
				fclose($xml);
				unlink($xml_original_tmp);
			} 
		} // Date closed
	}
	
	function getLastPositionPerson($vehicle_serial,$vname,$vtype,$vehicle_number,$startdate,$enddate,$xmltowrite)
	{
		//echo "in function<br>";
		global $va,$vb,$vc,$vd,$ve,$vg,$vh,$vv,$vw,$vx,$vy,$vz,$vaa,$vab;
	global $old_xml_date;		
		//echo "xml_Date=".$old_xml_date."<br>";	
		//echo "<br>".$vehicle_serial.", ,".$vname." ,".$startdate.", ".$enddate.", ".$xmltowrite."<br>";	
		$fix_tmp = 1;
		$xml_date_latest="1900-00-00 00:00:00";
		$linetowrite="";
		$dataValid = 0;
		$date_1 = explode(" ",$startdate);
		$date_2 = explode(" ",$enddate);

		$datefrom = $date_1[0];
		$dateto = $date_2[0];
		$timefrom = $date_1[1];
		$timeto = $date_2[1];

		get_All_Dates($datefrom, $dateto, &$userdates);	
		//echo "3:datefrom=".$datefrom.' '."dateto=".$dateto.' '."userdates=".$userdates[0].'<BR>';
		date_default_timezone_set("Asia/Calcutta");
		$current_datetime = date("Y-m-d H:i:s");
		$current_date = date("Y-m-d");
		//print "<br>CurrentDate=".$current_date;
		$date_size = sizeof($userdates);
		//echo "4:date_size=".$date_size.'<BR>';
		
	//echo "serial=".$vehicle_serial."<br>";

		$last_location_string = get_maxspd_halt($vehicle_serial);
		//echo "vc=".$vc."<br>";
		$data = explode(',',$last_location_string);
		
		$day_max_speed = $data[0];
		$day_max_speed_time = $data[1];
		$last_halt_time = $data[2];
		
		
		for($i=($date_size-1);$i>=0;$i--)
		{		
			//if($userdates[$i] == $current_date)
			//{	
			$xml_current = "../../../xml_vts/xml_data/".$userdates[$i]."/".$vehicle_serial.".xml";
			//echo "xml_current=".$xml_current."<br>";
			if (file_exists($xml_current))      
			{ 
				$xml_file = $xml_current;						
			}		
			else
			{
				$xml_file = "../../../sorted_xml_data/".$userdates[$i]."/".$vehicle_serial.".xml";	
			}	
			
			//echo "xml_current=".$xml_file."<br>";
			if (file_exists($xml_file)) 
			{
				$t=time();			
				$xml_original_tmp = "../../../xml_tmp/original_xml/tmp_".$vehicle_serial."_".$t."_".$i.".xml";	
				//echo "xmo_file=".$xml_original_tmp." xml_file=".$xml_file."<br>";
				copy($xml_file,$xml_original_tmp); 
				$fexist =1;
				$xml = fopen($xml_original_tmp, "r") or $fexist = 0;               
				$format = 2;  
				//echo "in if<br>";
				if (file_exists($xml_original_tmp)) 
				{
					set_master_variable($userdates[$i]);
					//echo "exists<br>";
					while(!feof($xml))          // WHILE LINE != NULL
					{								
						$DataValid = 0;
						$line = fgets($xml);        // STRING SHOULD BE IN SINGLE QUOTE
					//	echo"<textarea>".$line."</textarea>";
						//echo $line;
					//echo "date1=".$userdates[$i]."<br>";
					
						
						//echo "vc=".$vc."<br>";
						
						if(strpos($line,''.$vc.'="1"'))     // RETURN FALSE IF NOT FOUND
						{
							$fix_tmp = 1;
						}
						else if(strpos($line,''.$vc.'="0"'))
						{
							$fix_tmp = 0;
						}					
						if((preg_match('/'.$vd.'="\d+.\d+[a-zA-Z0-9]\"/', $line, $lat_match) ) && (preg_match('/'.$ve.'="\d+.\d+[a-zA-Z0-9]\"/', $line, $lng_match)))
						{ 
							//echo "in lat<br>";
							$lat_value = explode('=',$lat_match[0]);
							$lng_value = explode('=',$lng_match[0]);
							if( (strlen($lat_value[1])>5) && ($lat_value[1]!="-") && (strlen($lng_value[1])>5) && ($lng_value[1]!="-") )
							{
								$DataValid = 1;
							}
						}
						/*echo "datavalid=".$DataValid;
						echo " line=".$line[0];
						echo " fix_tmp=".$fix_tmp;
						echo " length=".strlen($line);
						echo " line1=".$line[strlen($line)-2];*/
						if(($line[0] == '<') && ($line[strlen($line)-2] == '>') && ($fix_tmp==1) && ($DataValid == 1))
						{
							//echo "in lng<br>";
							$status = preg_match('/'.$vh.'="[^"]+/', $line, $datetime_tmp);
							$datetime_tmp1 = explode("=",$datetime_tmp[0]);
							$datetime = preg_replace('/"/', '', $datetime_tmp1[1]);
							$xml_date_current = $datetime;				
						}
						//echo "xml_date_1=".$xml_date_current."<br>";
						if($xml_date_current!=null)
						{
							if(($xml_date_current >= $startdate && $xml_date_current <= $enddate) && ($xml_date_current!="-"))
							{
								if(($xml_date_current>$xml_date_latest) && (($xml_date_current<=$enddate) && ($xml_date_current>=$startdate)))
								{
									$xml_date_latest = $xml_date_current;
									$line = substr($line, 0, -3);	
									//echo "in if 111";
									if($userdates[$i]<$old_xml_date)
									{
										//echo "in if 111";
										$line=str_replace("marker","x",$line);
										$line=str_replace("msgtype=","a=",$line);
										$line=str_replace("vehicleserial=","v=",$line);
										$line=str_replace("ver=","b=",$line);
										$line=str_replace("fix=","c=",$line);
										$line=str_replace("lat=","d=",$line);
										$line=str_replace("lng=","e=",$line);									
										$line=str_replace("sts=","g=",$line);
										$line=str_replace("datetime=","h=",$line);									
										$line=str_replace("cellname=","ab=",$line);
										$linetowrite = "\n".$line.' w="'.$vname.'" x="'.$vehicle_number.'" y="'.$vtype.'"/>';
									}
									else
									{
										//echo "in else";
										
										$linetowrite = "\n".$line.' v="'.$vehicle_serial.'" w="'.$vname.'" x="'.$vehicle_number.'" y="'.$vtype.'"/>';
										//echo"<textarea>".$linetowrite."</textarea>";
									}
									//$linetowrite = "\n".$line.' vname="'.$vname.'" vnumber="'.$vehicle_number.'"  vtype="'.$vtype.'"/>';
									//$linetowrite = "\n".$line.' s="'.$day_max_speed.'" t="'.$day_max_speed_time.'" u="'.$last_halt_time.'" v="'.$vehicle_serial.'" w="'.$vname.'" x="'.$vehicle_number.'" y="'.$vtype.'"/>';
										
									//$linetowrite = "\n".$line.' day_max_speed="'.$day_max_speed.'" day_max_speed_time="'.$day_max_speed_time.'" last_halt_time="'.$last_halt_time.'" vname="'.$vname.'" vehicle_number="'.$vehicle_number.'" vtype="'.$vtype.'"/>';
								}
							}
						}
					} // while closed
				}  // if original_tmp exist closed        
				if(strlen($linetowrite)!=0)
				{
					//echo "<br>".$xmltowrite."<br>";				
					$fh = fopen($xmltowrite, 'a') or die("can't open file 5"); //append
					fwrite($fh, $linetowrite);  
					fclose($fh);
					fclose($xml);
					unlink($xml_original_tmp);
					break;
				}
				fclose($xml);
				unlink($xml_original_tmp);
			} 
		} // Date closed
	}

	function getCurrentRecord($vehicle_serial,$vname,$vtype,$vehicle_number,$startdate,$enddate,$xmltowrite)
	{
		//echo "<br>".$vehicle_serial.", ,".$vname." ,".$startdate.", ".$enddate.", ".$xmltowrite."<br>";	
		$fix_tmp = 1;
		$xml_date_latest="1900-00-00 00:00:00";
		$linetowrite="";
		$dataValid = 0;
		
		

		$last_location_string = get_maxspd_halt($vehicle_serial);
		$data = explode(',',$last_location_string);
		
		$day_max_speed = $data[0];
		$day_max_speed_time = $data[1];
		$last_halt_time = $data[2];
		$xml_file="../../../xml_vts/xml_last/".$vehicle_serial.".xml";
		
		if (file_exists($xml_file)) 
		{
			$t=time();
			$rno = rand();			
			$xml_original_tmp = "../../../xml_tmp/original_xml/tmp_".$vehicle_serial."_".$t."_".$rno.".xml";
			copy($xml_file,$xml_original_tmp); 
			$fexist =1;
			$xml = fopen($xml_original_tmp, "r") or $fexist = 0;               
			$format = 2;      
			if (file_exists($xml_original_tmp)) 
			{ 
				new_xml_variables();
				while(!feof($xml))          // WHILE LINE != NULL
				{								
					$DataValid = 0;
					$line = fgets($xml);        // STRING SHOULD BE IN SINGLE QUOTE
					
					if(strpos($line,''.$vc.'="1"'))     // RETURN FALSE IF NOT FOUND
					{
						$fix_tmp = 1;
					}
					else if(strpos($line,''.$vc.'="0"'))
					{
						$fix_tmp = 0;
					}  				
					if((preg_match('/'.$vd.'="\d+.\d+[a-zA-Z0-9]\"/', $line, $lat_match) ) && (preg_match('/'.$ve.'="\d+.\d+[a-zA-Z0-9]\"/', $line, $lng_match)))
					{ 
						$lat_value = explode('=',$lat_match[0]);
						$lng_value = explode('=',$lng_match[0]);
						if( (strlen($lat_value[1])>5) && ($lat_value[1]!="-") && (strlen($lng_value[1])>5) && ($lng_value[1]!="-") )
						{
							$DataValid = 1;
						}
					}				
					$line = substr($line, 0, -3);
					
					//$linetowrite = "\n".$line.' vname="'.$vname.'" vnumber="'.$vehicle_number.'"  vtype="'.$vtype.'"/>';
					$linetowrite = "\n".$line.' s="'.$vehicle_serial.'" t="'.$vname.'" u="'.$vehicle_number.'" v="'.$vtype.'" w="'.$day_max_speed.'" x="'.$day_max_speed_time.'" y="'.$last_halt_time.'"/>';
				} // while closed
			}  // if original_tmp exist closed        
			if(strlen($linetowrite)!=0)
			{
				//echo "<br>".$xmltowrite."<br>";				
				$fh = fopen($xmltowrite, 'a') or die("can't open file 5"); //append
				fwrite($fh, $linetowrite);  
				fclose($fh);
				fclose($xml);
				unlink($xml_original_tmp);
				break;
			}
			fclose($xml);
			unlink($xml_original_tmp);
		} 
	}

	function getTrack($vehicle_serial,$vname,$vehicle_number,$startdate,$enddate,$xmltowrite,$timeinterval,$distanceinterval)
	{
		//echo "in function<br>";		
		global $va,$vb,$vc,$vd,$ve,$vf,$vg,$vh,$vi,$vj,$vk,$vl,$vm,$vn,$vo,$vp,$vq,$vr,$vs,$vt,$vu,$vv,$vw,$vx,$vy,$vz,$vaa,$vab;
		global $old_xml_date;	
		//echo "In Track";
		
		$fix_tmp = 1;
		$xml_date_latest="1900-00-00 00:00:00";
		$CurrentLat = 0.0;
		$CurrentLong = 0.0;
		$LastLat = 0.0;
		$LastLong = 0.0;
		$firstData = 0;
		$distance =0.0;
		$linetowrite="";
		$date_1 = explode(" ",$startdate);
		$date_2 = explode(" ",$enddate);

		$datefrom = $date_1[0];
		$dateto = $date_2[0];
		$timefrom = $date_1[1];
		$timeto = $date_2[1];

		get_All_Dates($datefrom, $dateto, &$userdates);

		date_default_timezone_set("Asia/Calcutta");
		$current_datetime = date("Y-m-d H:i:s");
		$current_date = date("Y-m-d");
		//print "<br>CurrentDate=".$current_date;
		$date_size = sizeof($userdates);
		$fh = fopen($xmltowrite, 'a') or die("can't open file 6"); //append

		for($i=0;$i<=($date_size-1);$i++)
		{
			//if($userdates[$i] == $current_date)
			//{	
			$xml_current = "../../../xml_vts/xml_data/".$userdates[$i]."/".$vehicle_serial.".xml";	    		
			if (file_exists($xml_current))      
			{    		
				//echo "in else";
				$xml_file = $xml_current;
				$CurrentFile = 1;
			}		
			else
			{
				$xml_file = "../../../sorted_xml_data/".$userdates[$i]."/".$vehicle_serial.".xml";
				$CurrentFile = 0;
			}
			//echo "<br>xml_file=".$xml_file;			
			if (file_exists($xml_file)) 
			{	
				set_master_variable($userdates[$i]);
				$t=time();
				//$current_datetime1 = date("Y_m_d_H_i_s");      
				//$xml_original_tmp = "xml_tmp/original_xml/tmp_".$current_datetime1.".xml";
				$xml_original_tmp = "../../../xml_tmp/original_xml/tmp_".$t."_".$i.".xml";
				//$xml_log = "xml_tmp/filtered_xml/tmp_".$current_datetime1.".xml";
				//echo "<br>xml_file=".$xml_file." <br>tmpxml=".$xml_original_tmp."<br>";
				//copy($xml_file,$xml_original_tmp); 
											  
				if($CurrentFile == 0)
				{
					//echo "<br>ONE<br>";
					copy($xml_file,$xml_original_tmp);
				}
				else
				{
					//echo "<br>TWO<br>";
					//$xml_unsorted = "xml_tmp/unsorted_xml/tmp".$current_datetime1."_unsorted.xml";
					$xml_unsorted = "../../../xml_tmp/unsorted_xml/tmp_".$t."_".$i."_unsorted.xml";
					//echo  "<br>".$xml_file." <br>".$xml_unsorted;				
					copy($xml_file,$xml_unsorted);        // MAKE UNSORTED TMP FILE
					SortFile($xml_unsorted, $xml_original_tmp,$userdates[$i]);    // SORT FILE
					unlink($xml_unsorted);                // DELETE UNSORTED TMP FILE
				}      
				$f=0;  
				$xml = @fopen($xml_original_tmp, "r") or $fexist = 0;
				$total_lines = count(file($xml_original_tmp));  
				//$xmllog = fopen($xml_log, "w") or $fexist1 = 0;
				$logcnt=0;
				$DataComplete=false;
				
				if (file_exists($xml_original_tmp)) 
				{      
					while(!feof($xml))          // WHILE LINE != NULL
					{
						//echo fgets($file). "<br />";
						$DataValid = 0;
						$line = fgets($xml);        // STRING SHOULD BE IN SINGLE QUOTE
						//echo "0:line:".$line;

						if(strlen($line)>20)
						{
							// $linetmp =  $line;
						}
					
						$linetolog =  $logcnt." ".$line;
						$logcnt++;
						//fwrite($xmllog, $linetolog);
						
						//echo "vc:".$vc;
						if(strpos($line,''.$vc.'="1"'))     // RETURN FALSE IF NOT FOUND
						{
							$fix_tmp = 1;
						}                
						else if(strpos($line,''.$vc.'="0"'))
						{
							$fix_tmp = 0;
						}
						else
						{
							$fix_tmp = 2;
						}				
						if((preg_match('/'.$vd.'="\d+.\d+[a-zA-Z0-9]\"/', $line, $lat_match)) &&  (preg_match('/'.$ve.'="\d+.\d+[a-zA-Z0-9]\"/', $line, $lng_match)) )
						{ 
							$lat_value = explode('=',$lat_match[0]);
							$lng_value = explode('=',$lng_match[0]);
							//echo " lat_value=".$lat_value[1];         
							if( (strlen($lat_value[1])>5) && ($lat_value[1]!="-") && (strlen($lng_value[1])>5) && ($lng_value[1]!="-") )
							{
								$DataValid = 1;
							}         
						}
						/*echo "datavalie=".$DataValid;
						echo "line1=".$line[strlen($line)-2];
						echo "fix_tmp=".$fix_tmp;*/
						$linetmp = "";
						//if( (substr($line, 0,1) == '<') && (substr( (strlen($line)-1), 0,1) == '>') && ($fix_tmp==1) && ($f>0) && ($f<$total_lines-1) )        
						if(($line[0] == '<') && ($line[strlen($line)-2] == '>') && (($fix_tmp==1) || ($fix_tmp == 2))&& ($DataValid == 1) )        
						{
							//preg_match('/\d+-\d+-\d+ \d+:\d+:\d+/', $line, $str3tmp);    // EXTRACT DATE FROM LINE
							//echo "<br>str3tmp[0]=".$str3tmp[0];
							//$xml_date_current = $str3tmp[0];
							$linetmp =  $line;
							//echo "linetmp=".$linetmp;
							$status = preg_match('/'.$vh.'="[^"]+/', $line, $datetime_tmp);
							$datetime_tmp1 = explode("=",$datetime_tmp[0]);
							$datetime = preg_replace('/"/', '', $datetime_tmp1[1]);
							$xml_date_current = $datetime;										
						}  				
						//echo "Final0=".$xml_date_current." datavalid=".$DataValid;
			  
						if (($xml_date_current!=null)  && ($DataValid==1) && (($fix_tmp==1) || ($fix_tmp == 2)))
						{
							$linetolog = $xml_date_current.' '.$firstData."\n";
							//fwrite($xmllog, $linetolog);
							//echo "Final1";
							$CurrentLat = $lat_value[1] ;
							$CurrentLong = $lng_value[1];

							if(($xml_date_current >= $startdate && $xml_date_current <= $enddate) && ($xml_date_current!="-"))
							{
								//echo "Final2";
								if($firstData==1)
								{
									if($minlat>$CurrentLat)
									{
										$minlat = $CurrentLat;
									}
									if($maxlat<$CurrentLat)
									{
										$maxlat = $CurrentLat;
									}
					
									if($minlong>$CurrentLong)
									{
										$minlong = $CurrentLong;
									}
									if($maxlong<$CurrentLong)
									{
										$maxlong = $CurrentLong;
									}                
									$tmp1lat = round(substr($CurrentLat,1,(strlen($CurrentLat)-3)),4);
									$tmp2lat = round(substr($LastLat,1,(strlen($LastLat)-3)),4);
									$tmp1lng = round(substr($CurrentLong,1,(strlen($CurrentLong)-3)),4);
									$tmp2lng = round(substr($LastLong,1,(strlen($LastLong)-3)),4);  							
									//echo  "Coord: ".$tmp1lat.' '.$tmp2lat.' '.$tmp1lng.' '.$tmp2lng.'<BR>';             							
									calculate_distance($tmp1lat,$tmp2lat,$tmp1lng,$tmp2lng,&$distance);                
									$linetolog = $CurrentLat.','.$CurrentLong.','.$LastLat.','.$LastLong.','.$distance.','.$xml_date_current.','.$xml_date_last.','.(strtotime($xml_date_current)-strtotime($xml_date_last)).','.$timeinterval.','.$distanceinterval.','.$enddate.','.$startdate."\n";
									//fwrite($xmllog, $linetolog);
								}
								if((((((strtotime($xml_date_current)-strtotime($xml_date_last))>$timeinterval) && ($distance>=$distanceinterval)) || ($firstData==0)) && 
								(($xml_date_current<=$enddate) && ($xml_date_current>=$startdate))) || ($f==$total_lines-2) )
								{
									//echo "please wait..";
									$linetolog = "Data Written\n";
									//fwrite($xmllog, $linetolog);
									//echo "<br>FinalWrite";
									$xml_date_last = $xml_date_current;
									$LastLat =$CurrentLat;
									$LastLong =$CurrentLong;
									$line = substr($line, 0, -3);   // REMOVE LAST TWO /> CHARARCTER
									$finalDistance = $finalDistance + $distance;
									if($userdates[$i]<$old_xml_date)
									{
										//echo "in replace 1";
										$line=str_replace("marker","x",$line);
										$line=str_replace("msgtype=","a=",$line);
										$line=str_replace("vehicleserial=","v=",$line);
										$line=str_replace("ver=","b=",$line);
										$line=str_replace("fix=","c=",$line);
										$line=str_replace("lat=","d=",$line);
										$line=str_replace("lng=","e=",$line);
										$line=str_replace("speed=","f=",$line);
										$line=str_replace("sts=","g=",$line);
										$line=str_replace("datetime=","h=",$line);
										$line=str_replace("io1=","i=",$line);
										$line=str_replace("io2=","j=",$line);
										$line=str_replace("io3=","k=",$line);
										$line=str_replace("io4=","l=",$line);
										$line=str_replace("io5=","m=",$line);
										$line=str_replace("io6=","n=",$line);
										$line=str_replace("io7=","o=",$line);
										$line=str_replace("io8=","p=",$line);
										$line=str_replace("sig_str=","q=",$line);
										$line=str_replace("sup_v=","r=",$line);
										$line=str_replace("day_max_speed=","s=",$line);
										$line=str_replace("day_max_speed_time=","t=",$line);
										$line=str_replace("last_halt_time=","u=",$line);
										$line=str_replace("cellname=","ab=",$line);
										$linetowrite = "\n".$line.' w="'.$vname.'" x="'.$vehicle_number.'" z="'.round($finalDistance,2).'"/>'; // for distance       // ADD DISTANCE
									}
									else
									{
										$linetowrite = "\n".$line.' v="'.$vehicle_serial.'" w="'.$vname.'" x="'.$vehicle_number.'" z="'.round($finalDistance,2).'"/>'; // for distance       // ADD DISTANCE
									}
									//echo "<br>finalDistance=".$finalDistance;
									//$linetowrite = "\n".$line.' cumdist="'.$finalDistance.'"./>'; // for distance       // ADD DISTANCE
									//$linetowrite = "\n".$line.'/>';
									//echo "<textarea>".$linetowrite."</textarea>";
									//echo "lintowrite=".$linetowrite;
									$firstData = 1;  
									//$fh = fopen($xmltowrite, 'a') or die("can't open file 4"); //append
									fwrite($fh, $linetowrite);  
								}
							}
							else if(($xml_date_current > $enddate) && ($xml_date_current!="-") && ($DataValid==1) )
							{
								//echo "in first";
								$linetolog = "Data Written1\n";
								//fwrite($xmllog, $linetolog);
								$line = substr($line, 0, -3);   // REMOVE LAST TWO /> CHARARCTER
								//$linetowrite = "\n".$line.' distance="'.round($finalDistance,2).'"/>'; // for distance       // ADD DISTANCE
								// $linetowrite = "\n".$line.'/>'; // for distance       // ADD DISTANCE
								if($userdates[$i]<$old_xml_date)
								{
									$line=str_replace("marker","x",$line);
									$line=str_replace("msgtype=","a=",$line);
									$line=str_replace("vehicleserial=","v=",$line);
									$line=str_replace("ver=","b=",$line);
									$line=str_replace("fix=","c=",$line);
									$line=str_replace("lat=","d=",$line);
									$line=str_replace("lng=","e=",$line);
									$line=str_replace("speed=","f=",$line);
									$line=str_replace("sts=","g=",$line);
									$line=str_replace("datetime=","h=",$line);
									$line=str_replace("io1=","i=",$line);
									$line=str_replace("io2=","j=",$line);
									$line=str_replace("io3=","k=",$line);
									$line=str_replace("io4=","l=",$line);
									$line=str_replace("io5=","m=",$line);
									$line=str_replace("io6=","n=",$line);
									$line=str_replace("io7=","o=",$line);
									$line=str_replace("io8=","p=",$line);
									$line=str_replace("sig_str=","q=",$line);
									$line=str_replace("sup_v=","r=",$line);
									$line=str_replace("day_max_speed=","s=",$line);
									$line=str_replace("day_max_speed_time=","t=",$line);
									$line=str_replace("last_halt_time=","u=",$line);
									$linetowrite = "\n".$line.' w="'.$vname.'" x="'.$vehicle_number.'" z="'.round($finalDistance,2).'"/>'; // for distance       // ADD DISTANCE
								}
								else
								{
									$linetowrite = "\n".$line.' v="'.$vehicle_serial.'" w="'.$vname.'" x="'.$vehicle_number.'" z="'.round($finalDistance,2).'"/>'; // for distance       // ADD DISTANCE
								}//echo "lintowrite=".$linetowrite;
								fwrite($fh, $linetowrite);
								$DataComplete=true;
								break;
							}
						}
						$f++;
					}   // while closed
				} // if original_tmp exist closed 
		  
				if($DataComplete==false)
				{
					//echo "in false";
					if( (preg_match('/'.$vd.'="\d+.\d+[a-zA-Z0-9]\"/', $linetmp, $lat_match)) &&  (preg_match('/'.$ve.'="\d+.\d+[a-zA-Z0-9]\"/', $linetmp, $lng_match)) )
					{ 
						$lat_value = explode('=',$lat_match[0]);
						$lng_value = explode('=',$lng_match[0]);
						//echo " lat_value=".$lat_value[1];         
						if( (strlen($lat_value[1])>5) && ($lat_value[1]!="-") && (strlen($lng_value[1])>5) && ($lng_value[1]!="-") )
						{
							$DataValid = 1;
						} 
						else
						{
							$DataValid = 0;
						}
					}
					else
					{
						$DataValid = 0;
					}		
					if($DataValid == 1)
					{
						$linetolog = "Data Written2\n";
						//fwrite($xmllog, $linetolog);
						//echo "linetmp=".$linetmp;
						$line = substr($linetmp, 0, -3);   // REMOVE LAST TWO /> CHARARCTER
						//$linetowrite = "\n".$line.' distance="'.round($finalDistance,2).'"/>'; // for distance       // ADD DISTANCE
						//$linetowrite = "\n".$line.'/>'; // for distance       // ADD DISTANCE
						if($userdates[$i]<$old_xml_date)
						{
							//echo "1in2";
							$line=str_replace("marker","x",$line);
							$line=str_replace("msgtype=","a=",$line);
							$line=str_replace("vehicleserial=","v=",$line);
							$line=str_replace("ver=","b=",$line);
							$line=str_replace("fix=","c=",$line);
							$line=str_replace("lat=","d=",$line);
							$line=str_replace("lng=","e=",$line);
							$line=str_replace("speed=","f=",$line);
							$line=str_replace("sts=","g=",$line);
							$line=str_replace("datetime=","h=",$line);
							$line=str_replace("io1=","i=",$line);
							$line=str_replace("io2=","j=",$line);
							$line=str_replace("io3=","k=",$line);
							$line=str_replace("io4=","l=",$line);
							$line=str_replace("io5=","m=",$line);
							$line=str_replace("io6=","n=",$line);
							$line=str_replace("io7=","o=",$line);
							$line=str_replace("io8=","p=",$line);
							$line=str_replace("sig_str=","q=",$line);
							$line=str_replace("sup_v=","r=",$line);
							$line=str_replace("day_max_speed=","s=",$line);
							$line=str_replace("day_max_speed_time=","t=",$line);
							$line=str_replace("last_halt_time=","u=",$line);
							$line=str_replace("cellname=","ab=",$line);
							$linetowrite = "\n".$line.' w="'.$vname.'" x="'.$vehicle_number.'" z="'.round($finalDistance,2).'"/>'; // for distance       // ADD DISTANCE
						}
						else
						{
							$linetowrite = "\n".$line.' v="'.$vehicle_serial.'" w="'.$vname.'" x="'.$vehicle_number.'" z="'.round($finalDistance,2).'"/>'; // for distance       // ADD DISTANCE
						}//echo "lintowrite=".$linetowrite;
						fwrite($fh, $linetowrite);
					}
				}         
				fclose($xml);            
				unlink($xml_original_tmp);
			} // if (file_exists closed
		}  // for closed 	
		//echo "Test1";
		fclose($fh);
	//fclose($xmllog);
	}
	
	function getTrackPerson($vehicle_serial,$vname,$vehicle_number,$startdate,$enddate,$xmltowrite,$timeinterval,$distanceinterval)
	{
		//echo "in function<br>";		
		global $va,$vb,$vc,$vd,$ve,$vg,$vh,$vv,$vw,$vx,$vy,$vz,$vaa,$vab;
		global $old_xml_date;	
		//echo "In Track";
		
		$fix_tmp = 1;
		$xml_date_latest="1900-00-00 00:00:00";
		$CurrentLat = 0.0;
		$CurrentLong = 0.0;
		$LastLat = 0.0;
		$LastLong = 0.0;
		$firstData = 0;
		$distance =0.0;
		$linetowrite="";
		$date_1 = explode(" ",$startdate);
		$date_2 = explode(" ",$enddate);

		$datefrom = $date_1[0];
		$dateto = $date_2[0];
		$timefrom = $date_1[1];
		$timeto = $date_2[1];

		get_All_Dates($datefrom, $dateto, &$userdates);

		date_default_timezone_set("Asia/Calcutta");
		$current_datetime = date("Y-m-d H:i:s");
		$current_date = date("Y-m-d");
		//print "<br>CurrentDate=".$current_date;
		$date_size = sizeof($userdates);
		$fh = fopen($xmltowrite, 'a') or die("can't open file 6"); //append

		for($i=0;$i<=($date_size-1);$i++)
		{
			//if($userdates[$i] == $current_date)
			//{	
			$xml_current = "../../../xml_vts/xml_data/".$userdates[$i]."/".$vehicle_serial.".xml";	    		
			if (file_exists($xml_current))      
			{    		
				//echo "in else";
				$xml_file = $xml_current;
				$CurrentFile = 1;
			}		
			else
			{
				$xml_file = "../../../sorted_xml_data/".$userdates[$i]."/".$vehicle_serial.".xml";
				$CurrentFile = 0;
			}
			//echo "<br>xml_file=".$xml_file;			
			if (file_exists($xml_file)) 
			{
				set_master_variable($userdates[$i]);
				$t=time();
				//$current_datetime1 = date("Y_m_d_H_i_s");      
				//$xml_original_tmp = "xml_tmp/original_xml/tmp_".$current_datetime1.".xml";
				$xml_original_tmp = "../../../xml_tmp/original_xml/tmp_".$t."_".$i.".xml";
				//$xml_log = "xml_tmp/filtered_xml/tmp_".$current_datetime1.".xml";
				//echo "<br>xml_file=".$xml_file." <br>tmpxml=".$xml_original_tmp."<br>";
				//copy($xml_file,$xml_original_tmp); 
											  
				if($CurrentFile == 0)
				{
					//echo "<br>ONE<br>";
					copy($xml_file,$xml_original_tmp);
				}
				else
				{
					//echo "<br>TWO<br>";
					//$xml_unsorted = "xml_tmp/unsorted_xml/tmp".$current_datetime1."_unsorted.xml";
					$xml_unsorted = "../../../xml_tmp/unsorted_xml/tmp_".$t."_".$i."_unsorted.xml";
					//echo  "<br>".$xml_file." <br>".$xml_unsorted;				
					copy($xml_file,$xml_unsorted);        // MAKE UNSORTED TMP FILE
					SortFile($xml_unsorted, $xml_original_tmp,$userdates[$i]);    // SORT FILE
					unlink($xml_unsorted);                // DELETE UNSORTED TMP FILE
				}      
				$f=0;  
				$xml = @fopen($xml_original_tmp, "r") or $fexist = 0;
				$total_lines = count(file($xml_original_tmp)); 
				//echo "total_line=".$total_lines."<br>";
				//$xmllog = fopen($xml_log, "w") or $fexist1 = 0;
				$logcnt=0;
				$DataComplete=false;
				
				if (file_exists($xml_original_tmp)) 
				{
					
					while(!feof($xml))          // WHILE LINE != NULL
					{
						//echo fgets($file). "<br />";
						$DataValid = 0;
						$line = fgets($xml);        // STRING SHOULD BE IN SINGLE QUOTE
						//echo "0:line:".$line;
						/*echo "vc=".$vc."<br>";
						echo '<textarea>'.$line.'</textarea>';*/
						if(strlen($line)>20)
						{
							// $linetmp =  $line;
						}
					
						$linetolog =  $logcnt." ".$line;
						$logcnt++;
						//fwrite($xmllog, $linetolog);
						
						
						if(strpos($line,''.$vc.'="1"'))     // RETURN FALSE IF NOT FOUND
						{
							$fix_tmp = 1;
						}                
						else if(strpos($line,''.$vc.'="0"'))
						{
							$fix_tmp = 0;
						}
						else
						{
							$fix_tmp = 2;
						}				
						if((preg_match('/'.$vd.'="\d+.\d+[a-zA-Z0-9]\"/', $line, $lat_match)) &&  (preg_match('/'.$ve.'="\d+.\d+[a-zA-Z0-9]\"/', $line, $lng_match)) )
						{ 
							$lat_value = explode('=',$lat_match[0]);
							$lng_value = explode('=',$lng_match[0]);
							//echo " lat_value=".$lat_value[1];         
							if( (strlen($lat_value[1])>5) && ($lat_value[1]!="-") && (strlen($lng_value[1])>5) && ($lng_value[1]!="-") )
							{
								$DataValid = 1;
							}         
						}
						/*echo "datavalie=".$DataValid;
						echo "line1=".$line[strlen($line)-2];
						echo "fix_tmp=".$fix_tmp;*/
						$linetmp = "";
						//if( (substr($line, 0,1) == '<') && (substr( (strlen($line)-1), 0,1) == '>') && ($fix_tmp==1) && ($f>0) && ($f<$total_lines-1) )        
						if(($line[0] == '<') && ($line[strlen($line)-2] == '>') && (($fix_tmp==1) || ($fix_tmp == 2))&& ($DataValid == 1) )        
						{
							//preg_match('/\d+-\d+-\d+ \d+:\d+:\d+/', $line, $str3tmp);    // EXTRACT DATE FROM LINE
							//echo "<br>str3tmp[0]=".$str3tmp[0];
							//$xml_date_current = $str3tmp[0];
							$linetmp =  $line;
							//echo "linetmp=".$linetmp;
							$status = preg_match('/'.$vh.'="[^"]+/', $line, $datetime_tmp);
							$datetime_tmp1 = explode("=",$datetime_tmp[0]);
							$datetime = preg_replace('/"/', '', $datetime_tmp1[1]);
							$xml_date_current = $datetime;										
						}  				
						//echo "Final0=".$xml_date_current." datavalid=".$DataValid;
			  
						if (($xml_date_current!=null)  && ($DataValid==1) && (($fix_tmp==1) || ($fix_tmp == 2)))
						{
							$linetolog = $xml_date_current.' '.$firstData."\n";
							//fwrite($xmllog, $linetolog);
							//echo "Final1";
							$CurrentLat = $lat_value[1] ;
							$CurrentLong = $lng_value[1];

							if(($xml_date_current >= $startdate && $xml_date_current <= $enddate) && ($xml_date_current!="-"))
							{
								//echo "Final2";
								if($firstData==1)
								{
									if($minlat>$CurrentLat)
									{
										$minlat = $CurrentLat;
									}
									if($maxlat<$CurrentLat)
									{
										$maxlat = $CurrentLat;
									}
					
									if($minlong>$CurrentLong)
									{
										$minlong = $CurrentLong;
									}
									if($maxlong<$CurrentLong)
									{
										$maxlong = $CurrentLong;
									}                
									$tmp1lat = round(substr($CurrentLat,1,(strlen($CurrentLat)-3)),4);
									$tmp2lat = round(substr($LastLat,1,(strlen($LastLat)-3)),4);
									$tmp1lng = round(substr($CurrentLong,1,(strlen($CurrentLong)-3)),4);
									$tmp2lng = round(substr($LastLong,1,(strlen($LastLong)-3)),4);  							
									//echo  "Coord: ".$tmp1lat.' '.$tmp2lat.' '.$tmp1lng.' '.$tmp2lng.'<BR>';             							
									calculate_distance($tmp1lat,$tmp2lat,$tmp1lng,$tmp2lng,&$distance);                
									$linetolog = $CurrentLat.','.$CurrentLong.','.$LastLat.','.$LastLong.','.$distance.','.$xml_date_current.','.$xml_date_last.','.(strtotime($xml_date_current)-strtotime($xml_date_last)).','.$timeinterval.','.$distanceinterval.','.$enddate.','.$startdate."\n";
									//fwrite($xmllog, $linetolog);
								}
								if((((((strtotime($xml_date_current)-strtotime($xml_date_last))>$timeinterval) && ($distance>=$distanceinterval)) || ($firstData==0)) && 
								(($xml_date_current<=$enddate) && ($xml_date_current>=$startdate))) || ($f==$total_lines-2) )
								{
									//echo "please wait..";
									$linetolog = "Data Written\n";
									//fwrite($xmllog, $linetolog);
									//echo "<br>FinalWrite";
									$xml_date_last = $xml_date_current;
									$LastLat =$CurrentLat;
									$LastLong =$CurrentLong;
									$line = substr($line, 0, -3);   // REMOVE LAST TWO /> CHARARCTER
									$finalDistance = $finalDistance + $distance;
									if($userdates[$i]<$old_xml_date)
									{
									//	echo "in if3";
										//echo "in replace 1";
										$line=str_replace("marker","x",$line);
										$line=str_replace("msgtype=","a=",$line);
										$line=str_replace("vehicleserial=","v=",$line);
										$line=str_replace("ver=","b=",$line);
										$line=str_replace("fix=","c=",$line);
										$line=str_replace("lat=","d=",$line);
										$line=str_replace("lng=","e=",$line);										
										$line=str_replace("sts=","g=",$line);
										$line=str_replace("datetime=","h=",$line);
										$line=str_replace("cellname=","ab=",$line);
										$linetowrite = "\n".$line.' w="'.$vname.'" x="'.$vehicle_number.'" z="'.round($finalDistance,2).'"/>'; // for distance       // ADD DISTANCE
									}
									else
									{
										//echo "in else3";
									
										$linetowrite = "\n".$line.' v="'.$vehicle_serial.'" w="'.$vname.'" x="'.$vehicle_number.'" z="'.round($finalDistance,2).'"/>'; // for distance       // ADD DISTANCE
										//echo '<textarea>'.$linetowrite.'</textarea>';
									}
									//echo "<br>finalDistance=".$finalDistance;
									//$linetowrite = "\n".$line.' cumdist="'.$finalDistance.'"./>'; // for distance       // ADD DISTANCE
									//$linetowrite = "\n".$line.'/>';
									//echo "<textarea>".$linetowrite."</textarea>";
									//echo "lintowrite=".$linetowrite;
									$firstData = 1;  
									//$fh = fopen($xmltowrite, 'a') or die("can't open file 4"); //append
									fwrite($fh, $linetowrite);  
								}
							}
							else if(($xml_date_current > $enddate) && ($xml_date_current!="-") && ($DataValid==1) )
							{
								//echo "in first";
								$linetolog = "Data Written1\n";
								//fwrite($xmllog, $linetolog);
								$line = substr($line, 0, -3);   // REMOVE LAST TWO /> CHARARCTER
								//$linetowrite = "\n".$line.' distance="'.round($finalDistance,2).'"/>'; // for distance       // ADD DISTANCE
								// $linetowrite = "\n".$line.'/>'; // for distance       // ADD DISTANCE
								if($userdates[$i]<$old_xml_date)
								{
									//echo "in if2";
									$line=str_replace("marker","x",$line);
									$line=str_replace("msgtype=","a=",$line);
									$line=str_replace("vehicleserial=","v=",$line);
									$line=str_replace("ver=","b=",$line);
									$line=str_replace("fix=","c=",$line);
									$line=str_replace("lat=","d=",$line);
									$line=str_replace("lng=","e=",$line);									
									$line=str_replace("sts=","g=",$line);
									$line=str_replace("datetime=","h=",$line);									
									$line=str_replace("cellname=","ab=",$line);
									$linetowrite = "\n".$line.' w="'.$vname.'" x="'.$vehicle_number.'" z="'.round($finalDistance,2).'"/>'; // for distance       // ADD DISTANCE
								}
								else
								{
									//echo "in else2";
									$linetowrite = "\n".$line.' v="'.$vehicle_serial.'" w="'.$vname.'" x="'.$vehicle_number.'" z="'.round($finalDistance,2).'"/>'; // for distance       // ADD DISTANCE
									//echo '<textarea>'.$linetowrite.'</textarea>';
								}//echo "lintowrite=".$linetowrite;
								fwrite($fh, $linetowrite);
								$DataComplete=true;
								break;
							}
						}
						$f++;
					}   // while closed
				} // if original_tmp exist closed 
		  
				if($DataComplete==false)
				{
					//echo "in false";
					if( (preg_match('/'.$vd.'="\d+.\d+[a-zA-Z0-9]\"/', $linetmp, $lat_match)) &&  (preg_match('/'.$ve.'="\d+.\d+[a-zA-Z0-9]\"/', $linetmp, $lng_match)) )
					{ 
						$lat_value = explode('=',$lat_match[0]);
						$lng_value = explode('=',$lng_match[0]);
						//echo " lat_value=".$lat_value[1];         
						if( (strlen($lat_value[1])>5) && ($lat_value[1]!="-") && (strlen($lng_value[1])>5) && ($lng_value[1]!="-") )
						{
							$DataValid = 1;
						} 
						else
						{
							$DataValid = 0;
						}
					}
					else
					{
						$DataValid = 0;
					}		
					if($DataValid == 1)
					{
						$linetolog = "Data Written2\n";
						//fwrite($xmllog, $linetolog);
						//echo "linetmp=".$linetmp;
						$line = substr($linetmp, 0, -3);   // REMOVE LAST TWO /> CHARARCTER
						//$linetowrite = "\n".$line.' distance="'.round($finalDistance,2).'"/>'; // for distance       // ADD DISTANCE
						//$linetowrite = "\n".$line.'/>'; // for distance       // ADD DISTANCE
						if($userdates[$i]<$old_xml_date)
						{
							//echo "in if1";
							$line=str_replace("marker","x",$line);
							$line=str_replace("msgtype=","a=",$line);
							$line=str_replace("vehicleserial=","v=",$line);
							$line=str_replace("ver=","b=",$line);
							$line=str_replace("fix=","c=",$line);
							$line=str_replace("lat=","d=",$line);
							$line=str_replace("lng=","e=",$line);									
							$line=str_replace("sts=","g=",$line);
							$line=str_replace("datetime=","h=",$line);									
							$line=str_replace("cellname=","ab=",$line);
							$linetowrite = "\n".$line.' w="'.$vname.'" x="'.$vehicle_number.'" z="'.round($finalDistance,2).'"/>'; // for distance       // ADD DISTANCE
						}
						else
						{
							//echo "in else1";
							$linetowrite = "\n".$line.' v="'.$vehicle_serial.'" w="'.$vname.'" x="'.$vehicle_number.'" z="'.round($finalDistance,2).'"/>'; // for distance       // ADD DISTANCE
							//echo '<textarea>'.$linetowrite.'</textarea>';
						}//echo "lintowrite=".$linetowrite;
						fwrite($fh, $linetowrite);
					}
				}         
				fclose($xml);            
				unlink($xml_original_tmp);
			} // if (file_exists closed
		}  // for closed 	
		//echo "Test1";
		fclose($fh);
	//fclose($xmllog);
	}
  
/////////////////////////////////////////	
/*
function calculate_distance($lat1, $lat2, $lon1, $lon2, &$distance) 
{	
	$lat1 = deg2rad($lat1);
	$lon1 = deg2rad($lon1);

	$lat2 = deg2rad($lat2);
	$lon2 = deg2rad($lon2);
	
	$delta_lat = $lat2 - $lat1;
	$delta_lon = $lon2 - $lon1;
	
	$temp = pow(sin($delta_lat/2.0),2) + cos($lat1) * cos($lat2) * pow(sin($delta_lon/2.0),2);
	$distance = 3956 * 2 * atan2(sqrt($temp),sqrt(1-$temp));
	
	$distance = $distance*1.609344;	
} */

?>
