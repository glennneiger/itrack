<?php
	//error_reporting(-1);
	//ini_set('display_errors', 'On');
	set_time_limit(3000);	
	date_default_timezone_set("Asia/Kolkata");
	include_once("main_vehicle_information_1.php");
	include_once('Hierarchy.php');
	$root=$_SESSION["root"];
	include_once('util_session_variable.php');
	include_once('xmlParameters.php');
	include_once("report_title.php");
	include_once('parameterizeData.php');
	include_once('data.php');
	include_once("sortXmlData.php");
	include_once("getXmlData.php");
	include_once("calculate_distance.php");
	
	$DEBUG =0;
	$month = $_POST['month'];
	$year = $_POST['year'];
	$daystmp = $_POST['days'];
        
	//echo "Month=".$month." ,Year=".$year.", Day=".$daystmp;
	/*$month = "01";
	$year = "2015";
	$daystmp = "1";*/

	$lastday=date('t',mktime(0,0,0,$month,1,$year)); 	//get last day OR echo cal_days_in_month(CAL_GREGORIAN, 06, 
	$device_str = $_POST['vehicleserial'];
	//$device_str = "359231030125239";        
	$vserial = explode(':',$device_str);
	$vsize=count($vserial);

	for($rti=0;$rti<sizeof($reportType);$rti++)
	{	
		if($reportType[$rti]=="speed")
		{
			$speed_flag=1;
		}		
	}	
	if($daystmp<=9)
	{
		$date = $year."-".$month."-0".$daystmp;
	}
	else
	{
		$date = $year."-".$month."-".$daystmp;
	}
	
	$datefrom=$date;
	$dateto=$date;
	
	$date1 = $date." 00:00:00";
	$date2 = $date." 23:59:59"; 
       // $date2 = $date." 11:59:59"; 
	
	$sortBy='h';
	$firstDataFlag=0;
	$endDateTS=strtotime($date2);
	$dataCnt=0;	
	$userInterval = "0";
	$requiredData="All";
	
	$parameterizeData=new parameterizeData();
	$ioFoundFlag=0;
	
	$parameterizeData->latitude="d";
	$parameterizeData->longitude="e";
	$parameterizeData->speed="f";
	
	$finalVNameArr=array();

	for($i=0;$i<$vsize;$i++)
	{
		$dataCnt=0;
//          $vehicle_info=get_vehicle_info($root,$vserial[$i]);
//          $vehicle_detail_local=explode(",",$vehicle_info);
//          $finalVNameArr[$i]=$vehicle_detail_local[0];
		$finalVNameArr[$i]= "TEST_Vehicle";
		//echo "vehcileName=".$finalVNameArr[$i]." vSerial=".$vehicle_detail_local[0]."<br>";
		//echo "<br>Before-getLastSortedDate";
		$LastSortedDate = getLastSortedDate($vserial[$i],$datefrom,$dateto);
		//echo "<br>After-LastSortedDate=".$LastSortedDate;
		$SortedDataObject=new data();
		$UnSortedDataObject=new data();

		if(($LastSortedDate+24*60*60)>=$endDateTS) //All sorted data
		{	
				//echo "in if1";
				$type="sorted";
				readFileXml($vserial[$i],$date1,$date2,$datefrom,$dateto,$userInterval,$requiredData,$sortBy,$type,$parameterizeData,$firstDataFlag,$SortedDataObject);
		}
		else if($LastSortedDate==null) //All Unsorted data
		{
				//echo "in if2";
				$type="unSorted";
				readFileXml($vserial[$i],$date1,$date2,$datefrom,$dateto,$userInterval,$requiredData,$sortBy,$type,$parameterizeData,$firstDataFlag,$UnSortedDataObject);
		}
		else //Partially Sorted data
		{
				$LastSDate=date("Y-m-d",$LastSortedDate+24*60*60);
				//echo "in else";
				$type="sorted";					
				readFileXml($vserial[$i],$date1,$date2,$datefrom,$LastSDate,$userInterval,$requiredData,$sortBy,$type,$parameterizeData,$firstDataFlag,$SortedDataObject);

				$type="unSorted";
				readFileXml($vserial[$i],$date1,$date2,$LastSDate,$dateto,$userInterval,$requiredData,$sortBy,$type,$parameterizeData,$firstDataFlag,$UnSortedDataObject);
		}

		//echo "udt1=".$UnSortedDataObject->deviceDatetime[0]."<br>";
		//echo "udt2=".$UnSortedDataObject->deviceDatetime[1]."<br>";	
		//echo "udt1=".$UnSortedDataObject->speedData[0]."<br>";
		//echo "udt2=".$UnSortedDataObject->speedData[1]."<br>";
		//echo "<br><br>";

		if(count($SortedDataObject->deviceDatetime)>0)
		{
				$prevSortedSize=sizeof($SortedDataObject->deviceDatetime);
				for($obi=0;$obi<$prevSortedSize;$obi++)
				{			
					$finalDateTimeArr[$i][]=$SortedDataObject->deviceDatetime[$obi];
					$finalLatitudeArr[$i][]=$SortedDataObject->latitudeData[$obi];
					$finalLongitudeArr[$i][]=$SortedDataObject->longitudeData[$obi];
					$finalSpeedArr[$i][]=$SortedDataObject->speedData[$obi];				
						///$dataCnt++;
				}
		}
		//echo "<br>UnsortedDataObj=".count($UnSortedDataObject->deviceDatetime);
		if(count($UnSortedDataObject->deviceDatetime)>0)
		{
				$sortObjTmp=sortData($UnSortedDataObject,$sortBy,$parameterizeData);
				//var_dump($sortObjTmp);
				$sortedSize=sizeof($sortObjTmp->deviceDatetime);
				for($obi=0;$obi<$sortedSize;$obi++)
				{				
						$finalDateTimeArr[$i][]=$sortObjTmp->deviceDatetime[$obi];	
						$finalLatitudeArr[$i][]=$sortObjTmp->latitudeData[$obi];
						$finalLongitudeArr[$i][]=$sortObjTmp->longitudeData[$obi];	
						$finalSpeedArr[$i][]=$sortObjTmp->speedData[$obi];
						//$dataCnt++;
				}  
		}
		
		//echo "<br>UnsortedDataObj=".count($UnSortedDataObject->deviceDatetime);
		/*if(count($UnSortedDataObject->deviceDatetime)>0)
		{
			//$sortObjTmp=sortData($UnSortedDataObject,$sortBy,$parameterizeData);
			var_dump($sortObjTmp);
			$dataSize=sizeof($UnSortedDataObject->deviceDatetime);
			for($obi=0;$obi<$dataSize;$obi++)
			{				
				$finalDateTimeArr[$i][]=$UnSortedDataObject->deviceDatetime[$obi];	
				$finalLatitudeArr[$i][]=$UnSortedDataObject->latitudeData[$obi];
				$finalLongitudeArr[$i][]=$UnSortedDataObject->longitudeData[$obi];	
				$finalSpeedArr[$i][]=$UnSortedDataObject->speedData[$obi];
				//$dataCnt++;
			}  
		}*/
		//echo "<br>After Storage1";
		$SortedDataObject=null;			
		$sortObjTmp=null;
		$UnsortedDataObject =null;

    }
    //echo "<br>After Storage2";
    $parameterizeData=null;	
    $reportTitle="Daily Distance ";
    $displayFormat="Distance (km)";
    $m1=date('M',mktime(0,0,0,$month,1));	

    $title="Daily Distance Report :  (".$daystmp."-".$m1."-".$year."  )";
   // print_r($finalVNameArr);
   //echo "sizeFinaVname=".count($finalVNameArr);
if(count($finalVNameArr)>0)
{
    $csv_string = "";
    echo'<form  name="text_data_report" method="post" target="_blank">';
 //   report_title($reportTitle,$date1,$date2);
        echo '<center>
                <div style="overflow: auto;height: 300px; width: 620px;">';
                        //$reportSize=sizeof($finalVNameArr);
                        //echo "vsize=".$vsize."<br>";
                        $pdfi=0;
                        //echo "<br>Vsize=".$vsize;
                        for($i=0;$i<$vsize;$i++)
                        {
                                if($i==0)
                                {
                                        $sno=1;
                                        $csvtitle1=$reportTitle." Report :- ".$finalVNameArr[$i]." 
                                                                (".$vserial[$i]." )  DateTime :".$date1." - ".$date2." )";
                                        echo"<input TYPE=\"hidden\" VALUE=\"$title\" NAME=\"title[$x]\">";
                                        $csv_string = $csv_string.$title."\n";
                                        $csv_string = $csv_string."SNo,Vehicle,Date,".$displayFormat.",Average Speed (km/hr),Max Speed (km/hr)\n";
                                        echo'<br>
                                        <table align="center">
                                                <tr>
                                                        <td class="text" align="center">
                                                                <b>'.$title.'</b> 
                                                                <div style="height:8px;">
                                                                </div>
                                                        </td>
                                                </tr>
                                        </table>
                                        <table border=1 width="95%" rules=all bordercolor="#e5ecf5" align="center" cellspacing=0 cellpadding=3>	
                                                <tr>
                                                        <td class="text" align="left">
                                                                <b>SNo</b>
                                                        </td>
                                                        <td class="text" align="left">
                                                                <b>Vehicle</b>
                                                        </td>	
                                                        <td class="text" align="left">
                                                                <b>Date</b>
                                                        </td>
                                                        <td class="text" align="left">
                                                                <b>'.$displayFormat.'</b>
                                                        </td>';
                                                        if($speed_flag==1)
                                                        {
                                                echo'<td class="text" align="left">
                                                                <b>Average Speed (km/hr)</b>
                                                        </td>
                                                        <td class="text" align="left">
                                                                <b>Max Speed (km/hr)</b>
                                                        </td>';
                                                        }									
                                        echo'</tr>';  
                                }						

                                $distance=0.0;
                                $distance1=0.0;
                                $travel_dist = 0.0;
                                $total_travel_dist = 0.0;

                                $avg_speed = null;
                                $max_speed = null;

                                $run_start_flag = 0;
                                $run_stop_flag = 0;	

                                $innerSize=0;
                                $innerSize=sizeof($finalDateTimeArr[$i]);						

                                $speed_arr=null;
                                $speed_str=null;
                                $daily_dist = 0.0;
                                $total_runtime=0;					
                                $tmp_speed=0;
                                $tmp_time_diff=0;
                                $tmp_time_diff1=0;
                                $runtime_stop=0;
                                $runtime_start=0;
                                $runtime=0;
                                $last_time=null;

                                for($j=0;$j<$innerSize;$j++)
                                {
                                        $lat = $finalLatitudeArr[$i][$j];						
                                        $lng = $finalLongitudeArr[$i][$j];							
                                        $speed = $finalSpeedArr[$i][$j];
                                        $datetime=$finalDateTimeArr[$i][$j];
                                        //echo "<br>Lat=".$lat." ,lng=".$lng." ,speed=".$speed." ,datetime=".$datetime;

                                        if($j==0)
                                        {																			
                                                $lat1 = $lat;
                                                $lng1 = $lng;
                                                $last_time1 = $datetime; 
                                                $latlast = $lat;
                                                $lnglast =  $lng;				
                                                if($speed_flag==1)
                                                {	
                                                        $speed_str = $speed;
                                                        if($speed_str > 200)
                                                        {
                                                                $speed_str =0;   
                                                        }
                                                        $speed_tmp = "";
                                                        for ($x = 0, $y = strlen($speed_str); $x < $y; $x++) 
                                                        {
                                                                if($speed_str[$x]>='0' && $speed_str[$x]<='9')
                                                                {
                                                                        $speed_tmp = $speed_tmp.$speed_str[$x];
                                                                }      
                                                                else
                                                                {
                                                                        $speed_tmp = $speed_tmp.".";
                                                                }  
                                                        }
                                                        $speed = $speed_tmp;  
                                                        $speed = round($speed,2);  
                                                        $speed_arr[] = $speed;
                                                } 	
                                        }
                                        else
                                        {
                                                //########## DISTANCE DATA																         
                                                $lat2 = $lat;
                                                $lng2 = $lng;
                                                calculate_distance($lat1, $lat2, $lng1, $lng2, $distance);
                                                if($distance>2000)
                                                {
                                                        $distance=0;
                                                        $lat1 = $lat2;
                                                        $lng1 = $lng2;
                                                }
                                                //echo "<br>lat1=".$lat1." ,lat2=".$lat2." ,lng1=".$lng1." ,lng2=".$lng2." ,dist=".$distance;
                                                $tmp_time_diff1 = ((double) (strtotime($datetime) - strtotime($last_time1))) / 3600;
                                                calculate_distance($latlast, $lat2, $lnglast, $lng2, $distance1);

                                                if($tmp_time_diff1>0)
                                                {
                                                        $tmp_speed = ((double) ($distance1)) / $tmp_time_diff1;
                                                        $last_time1 = $datetime;
                                                        $latlast = $lat2;
                                                        $lnglast =  $lng2;        					
                                                }
                                                $tmp_time_diff =((double)( strtotime($datetime) - strtotime($last_time))) / 3600;
                                                //if($tmp_speed <3000 && $distance>0.1)
                                                //echo "\nTmpSpeed=".$tmp_speed." ,distance=".$distance." ,tmp_time_diff=".$tmp_time_diff;
                                                if($tmp_speed<500 && $distance>0.1 && $tmp_time_diff>0)
                                                {		              
                                                        $daily_dist= $daily_dist + $distance;	
                                                        if($i==1)
                                                        {
                                                                //echo "dailydist=".$daily_dist."<br>";
                                                        }
                                                        if($speed>1)
                                                        {
                                                                $travel_dist += $distance;
                                                        }							 																				
                                                        $lat1 = $lat2;
                                                        $lng1 = $lng2;
                                                        $last_time = $datetime;															  
                                                }
                                                //###### DISTANCE DATA CLOSED
                                                //######## SPEED DATA OPEN
                                                ///////// FIXING SPEED PROBLEM ///////////  
                                                if($speed_flag==1)
                                                {
                                                        $speed_str = $speed;
                                                        if($speed_str > 200)
                                                        {
                                                                $speed_str =0;
                                                        }									  
                                                        $speed_tmp = "";
                                                        for ($x = 0, $y = strlen($speed_str); $x < $y; $x++) 
                                                        {
                                                                if($speed_str[$x]>='0' && $speed_str[$x]<='9')
                                                                {
                                                                        $speed_tmp = $speed_tmp.$speed_str[$x];
                                                                }      
                                                                else
                                                                {
                                                                        $speed_tmp = $speed_tmp.".";
                                                                }  
                                                        }
                                                        $speed = $speed_tmp;  
                                                        $speed = round($speed,2);  							
                                                        $speed_arr[] = $speed;
                                                        $max_speed = max($speed_arr);
                                                        $max_speed = round($max_speed,2);
                                                        //echo "<br>Distance=".$distance;
                                                        if(($speed>1) && (!$run_start_flag) && (!$run_stop_flag) && ($distance > 0.1))
                                                        {
                                                                //echo "<br>RunStart";
                                                                $run_start_flag = 1;
                                                                $runtime_start = $datetime;
                                                        }
                                                        else if(($speed<1) &&($run_start_flag && !$run_stop_flag) && (($distance<0.1) || (($i==($date_size-1)) && ($f==$total_lines-10))))
                                                        {
                                                                //echo "<br>IN StopFlag";
                                                                $run_stop_flag = 1;
                                                                $runtime_stop = $datetime;
                                                        }
                                                        else if($run_start_flag && $run_stop_flag)
                                                        {								
                                                                $runtime = strtotime($runtime_stop) - strtotime($runtime_start);
                                                                //echo "<br>Runtime=".$runtime;
                                                                $total_runtime = $total_runtime + $runtime;
                                                                //echo "<br>Total_runtime=".$total_runtime;
                                                                $run_start_flag = 0;
                                                                $run_stop_flag = 0;
                                                        }
                                                }								
                                        }						
                                }


                                if($speed_flag==1)
                                {
                                        $avg_speed = ($travel_dist / $total_runtime)*3600; 
                                        $avg_speed = round($avg_speed,2);

                                        if( ($avg_speed > $max_speed) && ($max_speed > 2.0) )
                                        {
                                                $avg_speed = $max_speed - 2;
                                        }              
                                        else if( ($avg_speed > $max_speed) && ($max_speed > 0.2) && ($max_speed <= 2.0) )
                                        {								
                                                $avg_speed = $max_speed - 0.2;
                                        }							              							

                                        //echo "<br>AVG_SPEED=".$avg_speed;
                                        if($avg_speed<150)
                                        {
                                                $dayAvgSpeed = $avg_speed;
                                        }							
                                        $dayMaxSpeed = max($speed_arr);
                                        $dayAvgSpeed = round($dayAvgSpeed,2);
                                        $dayMaxSpeed = round($dayMaxSpeed,2);
                                }
                                else
                                {
                                        $dayAvgSpeed="-";
                                        $dayMaxSpeed="-";
                                }
                                $daily_dist=round($daily_dist,1);
                                //$dayDistance = round($dayDistance,1);
                                $dateOnly=explode(" ",$finalDateTimeArr[$i][$j-1]);
                                if($dateOnly[0]!="")
                                {
                                echo'<tr>
                                                <td class="text" align="left">
                                                        <b>'.$sno.'</b>
                                                </td>';
                                        echo"<input TYPE=\"hidden\" VALUE=\"$sno\" NAME=\"temp[$pdfi][$i][SNo]\">";
                                                $csv_string = $csv_string.$sno;		

                                        echo'<td class="text" align="left">
                                                        <b>'.$finalVNameArr[$i].'</b>
                                                </td>';	
                                        echo"<input TYPE=\"hidden\" VALUE=\"$finalVNameArr[$i]\" NAME=\"temp[$pdfi][$i][Vehicle Name]\">";
                                                $csv_string = $csv_string.','.$finalVNameArr[$i];			

                                        //$dateOnly=explode(" ",$finalDateTimeArr[$i][$j-1]);

                                        echo'<td class="text" align="left">
                                                        <b>'.$dateOnly[0].'</b>
                                                </td>';
                                        echo"<input TYPE=\"hidden\" VALUE=\"$dateOnly[0]\" NAME=\"temp[$pdfi][$i][Date]\">";
                                                $csv_string = $csv_string.','.$dateOnly[0];
                                        //echo '<td class="text" align="left"><b>'.$imei[$i].'</b></td>';
                                        echo '<td class="text" align="left">
                                                                <b>'.$daily_dist.'</b>
                                                </td>';
                                                echo"<input TYPE=\"hidden\" VALUE=\"$daily_dist\" NAME=\"temp[$pdfi][$i][$displayFormat]\">";
                                                $csv_string = $csv_string.','.$daily_dist;
                                        if($speed_flag==1)
                                        {
                                        echo'<td class="text" align="left">
                                                        <b>'.$dayAvgSpeed.'</b>
                                                </td>';
                                        echo"<input TYPE=\"hidden\" VALUE=\"$dayAvgSpeed\" NAME=\"temp[$pdfi][$i][Average Speed (km/hr)]\">";
                                                $csv_string = $csv_string.','.$dayAvgSpeed;
                                        echo'<td class="text" align="left">
                                                        <b>'.$dayMaxSpeed.'</b>
                                                </td> ';
                                                echo"<input TYPE=\"hidden\" VALUE=\"$dayMaxSpeed\" NAME=\"temp[$pdfi][$i][Max Speed (km/hr)]\">";
                                                $csv_string = $csv_string.','.$dayMaxSpeed;
                                        }
                                echo'</tr>';
                                $csv_string=$csv_string."\n";
                                $sno++;
                                }
                }
				
  echo "</table>
		</div>
  </center>"; 
	echo'<input TYPE="hidden" VALUE="full data" NAME="csv_type">';
	echo'<input TYPE="hidden" VALUE="'.$csv_string.'" NAME="csv_string">';                 
	echo'<br><center>
	<input type="button" onclick="javascript:report_pdf_csv(\'src/php/report_getpdf_type4.php?size='.$vsize.'\');" value="Get PDF" class="noprint">&nbsp;
	<input type="button" onclick="javascript:report_pdf_csv(\'src/php/report_csv.php\');" value="Get CSV" class="noprint">&nbsp;
	<!--<input type="button" value="Print it" onclick="window.print()" class="noprint">&nbsp;-->
	</form>';						
}
else
{
	print"<center><FONT color=\"Red\" size=2><strong>No ".$reportTitle." Record Found</strong></font></center>";
}


echo'<center>
		<a href="javascript:showReportPrevPage(\'report_daily_distance.htm\',\''.$selected_account_id.'\',\''.$selected_options_value.'\',\''.$s_vehicle_display_option.'\');" class="back_css">
			&nbsp;<b>Back</b>
		</a>
	</center>';	
?>							 					