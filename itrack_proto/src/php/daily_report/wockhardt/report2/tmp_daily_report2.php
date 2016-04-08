<?php
//$back_dir1 = "../../../";
//$back_dir1 = "../";
set_time_limit(3000);
$abspath = "/var/www/html/vts/beta/src/php/";

require_once $abspath."excel_lib/class.writeexcel_workbook.inc.php";
require_once $abspath."excel_lib/class.writeexcel_worksheet.inc.php";
//include_once('util_session_variable.php');
//include_once('util_php_mysql_connectivity.php');
include_once($abspath."get_all_dates_between.php");
include_once($abspath."sort_xml.php");
include_once($abspath."calculate_distance.php");
include_once("get_daily_data.php");
include_once($abspath."util.hr_min_sec.php");

$DBASE = "iespl_vts_beta";
$USER = "root";
$PASSWD = "mysql";
$HOST = "localhost";
$DbConnection = mysql_connect($HOST,$USER,$PASSWD) or die("Could Not Connect to Server");
mysql_select_db ($DBASE, $DbConnection) or die("could not find DB");
//echo "\nDB=".$DbConnection;	


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

//################ SET VARIABLE ##############/

$current_month=date('m');
$current_year=date('Y');

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

//COMMENT THIS BLOCK -TEMPORARY CODE FOR TESTING
$previous_month = $current_month;
$previous_year = $current_year;

$previous_month = "08";
///////////////////////////////////////////////


$total_days=date('t',mktime(0,0,0,$previous_month,1,$previous_year));		// TOTAL DAYS IN PREVIOUS MONTH

echo "\nPREV_MONTH1=".$previous_month." TOTAL DAYS=".$total_days;

$prev_day = date('d', strtotime(' -1 day')); 

$prev_day = "31";

switch($previous_month) 
{
  case 1 :
      $stringmonth = "January";
      break;
  case 2 :
      $stringmonth = "February";
      break;
  case 3 :
      $stringmonth = "March";
      break;
  case 4 :
      $stringmonth = "April";
      break;
  case 5 :
      $stringmonth = "May";
      break;
  case 6 :
      $stringmonth = "June";
      break;
  case 7 :
      $stringmonth = "July";
      break;
  case 8 :
      $stringmonth = "August";
      break;
  case 9 :
      $stringmonth = "September";
      break;
  case 10 :
      $stringmonth = "October";
      break;
  case 11 :
      $stringmonth = "November";
      break;
  case 12 :
      $stringmonth = "December";
      break;
}         
/////********* CREATE EXCEL FILE *******************///////
$filename_title = 'DAILY_WOCKHARDT_VTS_REPORT2';  
//$file_path = "/var/www/html/vts/beta/src/php/download/".$filename_title;
//ONLINE
$file_path = "/var/www/html/vts/beta/src/php/daily_report/wockhardt/report2/excel_data/".$filename_title;

//OFFLINE
//$file_path = "D:\\SERVER_GO4HOSTING/ITRACKSOLUTION.CO.IN/MAIL_SERVICE/WOCKHARDT/REPORT_MONTHLY/REPORT2/excel_data/".$filename_title;

$fname = tempnam_sfx($file_path, ".xls");    

////********** CREATE EXCEL WORKBOOK  ****************//////
//echo "\nfname=".$fname;  
$workbook =& new writeexcel_workbook($fname);                     //******* ADD WORKBOOK
$worksheet =& $workbook->addworksheet("WORKSHEET1");
//$worksheet->set_row(0, 25); 

# Create a border format
$border1 =& $workbook->addformat();
$border1->set_bold();
$border1->set_size(8);

$border2 =& $workbook->addformat();
$border2->set_size(8);
/*$border1->set_color('white');
$border1->set_bold();
$border1->set_size(10);
$border1->set_pattern(0x1);
//$border1->set_fg_color('black');
//$border1->set_border_color('black');
$border1->set_top(6);
$border1->set_bottom(6);
$border1->set_left(6);
$border1->set_align('center');
$border1->set_align('vcenter');
$border1->set_merge(); # This is the key feature */

/////********************** 
 
//$report_title = "WOCKHARDT TEST";
//$worksheet->write ($r, 0, $report_title, $border1);

//$date = '2012-07-25 12:21:00';
//echo "\nTIME SHIFT=".date('h:i A', strtotime($date));
$border1->set_align('center');
$border1->set_align('vcenter');
$border1->set_align('vjustify');
$border1->set_text_wrap();

$border2->set_align('center');
$border2->set_align('vcenter');
$border2->set_align('vjustify');
$border2->set_text_wrap();

$r=0;
$c=0;

$worksheet->write($r,$c, "Sr.No", $border1);
$worksheet->set_column($c, $c, 6);
$c++;
$worksheet->write($r,$c, "Location", $border1);
$worksheet->set_column($c, $c, 15);
$c++;
$worksheet->write($r,$c, "Van No", $border1);
$worksheet->set_column($c, $c, 25);
$c++;
$worksheet->write($r,$c, "Date", $border1);
$worksheet->set_column($c, $c, 12);
$c++;
$worksheet->write($r,$c, "Time Deviation", $border1);
$worksheet->set_column($c, $c, 35);
$c++;      
$worksheet->write($r,$c, "Route Deviation", $border1);
$worksheet->set_column($c, $c, 16);
$c++;
$worksheet->write($r,$c, "Halt Time Deviation", $border1);
$worksheet->set_column($c, $c, 16);
$c++;
$worksheet->write($r,$c, "Non POI Halt Report", $border1);
$worksheet->set_column($c, $c, 20);
$c++;    	    


$prev_month_str = $previous_year."-".$previous_month;

$prev_date_tmp1 = $previous_year."-".$previous_month."-".$prev_day." 00:00:00";
$prev_date_tmp2 = $previous_year."-".$previous_month."-".$prev_day." 23:59:59";

//echo "\nprev_month_str=".$prev_month_str;

$query1 = "SELECT account_id FROM account WHERE user_id='wockhardt'";
$result1 = mysql_query($query1,$DbConnection);
$row1 = mysql_fetch_object($result1);
$account_id = $row1->account_id;

$query2 = "SELECT vehicle_id FROM vehicle_grouping WHERE account_id='$account_id'";
$result2 = mysql_query($query2,$DbConnection);
$vehicle_id_str ="";
while($row2 = mysql_fetch_object($result2))
{
  $vehicle_id_str = $vehicle_id_str.$row2->vehicle_id.",";
}
$vehicle_id_str = substr($vehicle_id_str, 0, -1);

$query = "SELECT vehicle_assignment.device_imei_no, schedule_assignment.vehicle_id, schedule_assignment.min_operation_time, ".
        "schedule_assignment.max_operation_time, schedule_assignment.date_from, schedule_assignment.date_to,".
        "schedule_assignment.by_day, schedule_assignment.day, schedule_assignment.location_id FROM ".
        "schedule_assignment, vehicle_assignment WHERE vehicle_assignment.vehicle_id = schedule_assignment.vehicle_id AND ".
        "vehicle_assignment.vehicle_id IN($vehicle_id_str) AND ".
        "schedule_assignment.date_from <='$prev_date_tmp1' AND schedule_assignment.date_to >= '$prev_date_tmp2' AND ".
        "schedule_assignment.status=1";
echo "\nQUERY_MAIN=".$query."\n";//echo "\n".$query;
//echo "\nDBCon=".$DbConnection;
$result = mysql_query($query,$DbConnection);

while($row = mysql_fetch_object($result))
{
  $vid_tmp = $row->vehicle_id;
  $imei[] = $row->device_imei_no;
  $date_from[] = $row->date_from;
  $date_to[] = $row->date_to;
  $min_operation_time[] = $row->min_operation_time;
  $max_operation_time[] = $row->max_operation_time;
  $min_halt_time[] = $row->min_halt_time;
  $max_halt_time[] = $row->max_halt_time;
  $nonpoi_halt_time[] = $row->nonpoi_halt_time;   
  $by_day[] = $row->by_day;
  $day[] = $row->day;
  $location_id_tmp = $row->location_id;
  
  $query1 = "SELECT vehicle_name FROM vehicle WHERE vehicle_id='$vid_tmp' AND status=1";
  //echo "\n".$query1."\n";
  
  $result1 = mysql_query($query1,$DbConnection);
  if($row1 = mysql_fetch_object($result1))
  {
    $vname_tmp = $row1->vehicle_name;
    //echo "\nvnamex=".$vname_tmp;   
    $vname[] = $vname_tmp;
  } 
      
  $query2 = "SELECT location_id,location_name,geo_point FROM schedule_location WHERE location_id IN($location_id_tmp) AND status=1";
  //echo "\nQQQQQQQQQQ2=".$query2;
  $result2 = mysql_query($query2,$DbConnection);
  while($row2 = mysql_fetch_object($result2))
  {
    $geo_id[] = $row2->location_id;
    $geo_name[] = $row2->location_name;
    $geo_point[] = $row2->geo_point; 
  }   
}

$r++;     //LOOP THROUGH ROWS

$serial = 1;

//for($x=0;$x<3;$x++) 
for($x=0;$x<sizeof($imei);$x++)                            //IMEI LOOP
{            
  $tmpsno = $x+1;
  echo "\nSERIAL=".$tmpsno." ,LEN=".sizeof($imei)."\n";
  /*$serial = $x+1;
  $c=0;
  $worksheet->write($r,$c, $serial, $border2);               //SERIAL
  //$worksheet->set_column($c, $c, 6);
  $c++;
  $worksheet->write($r,$c, $vname[$x], $border2);            //VEHICLE NAME
  //$worksheet->set_column($c, $c, 15);
  $c++; */ 
      
  //for($i=1;$i<=$total_days;$i++)                            //DATE LOOP
  for($i=$prev_day;$i<=$prev_day;$i++) 
  {
    echo "\n imei=".$imei[$x], ",DAY=".$i;
    /*if($i<=9)
      $date_tmp =  $previous_year."-".$previous_month."-0".$i;
    else
      $date_tmp =  $previous_year."-".$previous_month."-".$i;*/
 
    $date_tmp =  $previous_year."-".$previous_month."-".$i;
       
    $date1 = $date_tmp." 00:00:00"; 
    $date2 = $date_tmp." 23:59:59";        
         
    $op_date1 = $date_tmp." ".$min_operation_time[$x];
    $op_date2 = $date_tmp." ".$max_operation_time[$x];
    //echo "\nONE::opdate1=".$op_date1." ,op_date2=".$op_date2;
    
    $op_date1 = date('Y-m-d H:i:s',strtotime($op_date1));      //FORMAT WITH LEADING ZEROS
    $op_date2 = date('Y-m-d H:i:s',strtotime($op_date2));
    echo "\nTWO::opdate1=".$op_date1." ,op_date2=".$op_date2;
    
    $c =0;    
    $c=0;
    $worksheet->write($r,$c, $serial, $border2);               //SERIAL
    //$worksheet->set_column($c, $c, 6);
    $c++;
    $worksheet->write($r,$c, "", $border2);                    //VEHICLE NAME
    //$worksheet->set_column($c, $c, 15);
    $c++; 
    $worksheet->write($r,$c, $vname[$x], $border2);            //VEHICLE NAME
    //$worksheet->set_column($c, $c, 15);
    $c++;  
    $worksheet->write($r,$c, $date_tmp, $border2);            //VEHICLE NAME
    //$worksheet->set_column($c, $c, 15);
    $c++;            

    
    if($by_day[$x] ==1)
    {    
      //echo "\nDAY[x]=".$day[$x];      
      //if( ($date_tmp >= $date_from[$x]) && ($date_tmp <= $date_to[$x]) )
      //{
        $day_db = explode(",",$day[$x]);     
              
        get_All_Dates($date_from[$x], $date_to[$x], &$userdates);   
        $date_size = sizeof($userdates);
      
        for($y=0;$y<=($date_size-1);$y++)                       //CHECKS EVERYTIME FOR ONE RECORD AND BREAKS- OUTER LOOP
      	{           
          $wflag = 0;
          //echo "\nUserDates[y]=".$userdates[$y];
          $timestmp = strtotime($userdates[$y]);
          $weekday = date("w",$timestmp);     //0 =SUN, 6=SAT
          //echo "\nWEEKDAY=".$weekday;
        
          for($z=0;$z<sizeof($day_db);$z++)                     //CHECKS EVERYTIME FOR ONE RECORD AND BREAKS- INNER LOOP
          {
            //echo "\nWEEKDAY=".$weekday." ,day_db=".$day_db[$z];
            if($weekday == $day_db[$z])
            {                            
              //echo "\nDAY SPECIFIED";
              add_data_to_field($imei[$x], $vname[$x], $date1, $date2, $op_date1, $op_date2, $min_halt_time, $max_halt_time, $nonpoi_halt_time[$x], $geo_point, $border1, $border2, $worksheet);          
              $wflag = 1;
              break;
            }
          }       	 
          if($wflag)
          {
            break;
          }
        } //INNER FOR CLOSED
        
        if(!$wflag)       //CALL TO RETURN DUMMY RECORDS IN EXCEL FIELD
        {
          add_data_to_field($imei[$x], $vname[$x], $date1, $date2, $op_date1, $op_date2, $min_halt_time, $max_halt_time, $nonpoi_halt_time[$x], $geo_point, $border1, $border2, $worksheet);
        }
     // } //INNER IF CLOSED
    }  //OUTER IF CLOSED
    else
    {
      //echo "\nDAY -NOT SPECIFIED";             
      add_data_to_field($imei[$x], $vname[$x], $date1, $date2, $op_date1, $op_date2, $min_halt_time, $max_halt_time, $nonpoi_halt_time[$x], $geo_point, $border1, $border2, $worksheet);   
    }
    $r++;
    $serial++;    
  } // DAY LOOP CLOSED
}
                  
$workbook->close(); //CLOSE WORKBOOK
echo "\nWORKBOOK CLOSED";



function add_data_to_field($imei, $vname, $date1, $date2, $op_date1, $op_date2, $min_halt_time, $max_halt_time, $nonpoi_halt_time, $geo_point, $border1, $border2, $worksheet)
{      
    global $worksheet;
    global $r;
    global $c;
    
    $total_dist = 0;
    $ophr_dist = 0;
    $non_ophr_dist = 0;
    $total_nof_halt = 0;
    $total_halt_time = 0;
    $avg_halt_time = 0; 
        
    global $total_monthly_dist;
    global $total_monthly_ophr_dist;
    global $total_monthly_non_ophr_dist;
    global $total_monthly_avg_dist;
    global $total_monthly_avg_ophr_dist;
    global $total_monthly_avg_non_ophr_dist;
    global $total_monthly_halt;
    global $total_monthly_avg_halt_time;    

    //####### CALL COMBINED FUNCTION ########//
    $daily_data = get_daily_data($imei, $vname, $date1, $date2, $op_date1, $op_date2, $min_halt_time, $max_halt_time, $nonpoi_halt_time, $geo_point);
    //#######################################//    
    echo "\nDailyData=".$daily_data;
    $tmpdata = explode("#", $daily_data);
    //$daily_data = $time_deviation1."#".$time_deviation2."#".$halttime_deviation_counter."#".$route_deviation_counter."#".$non_poi_string_final;    
    
    $time_deviation1 = $tmpdata[0]; 
    $time_deviation2 = $tmpdata[1];
    $route_deviation_counter = $tmpdata[2];
    $halttime_deviation_counter = $tmpdata[3];    
    $non_poi_string_final = $tmpdata[4];
    
    echo "\nNON POI=".$non_poi_string_final;
    //echo "\nA1=".$time_deviation1." ,A2=".$time_deviation2." ,A3=".$route_deviation_counter." ,A4=".$halttime_deviation_counter." ,A5=".$non_poi_string_final;
    
    $time_deviation = $time_deviation1.", ".$time_deviation2;       
    //echo "\nDate=".$date1." ,R1=".$r." ,C1=".$c;        
    $worksheet->write($r,$c, round($time_deviation,2), $border2);                 //TIME DEVIATION
    //$worksheet->set_column($c, $c, 8);
    $c++;    
    $worksheet->write($r,$c, round($route_deviation_counter,2), $border2);        //ROUTE DEVIATION
    //$worksheet->set_column($c, $c, 13);
    $c++;
    $worksheet->write($r,$c, round($halttime_deviation_counter,2), $border2);     //HALT TIME DEVIATION
    //$worksheet->set_column($c, $c, 8);
    $c++;
    $worksheet->write($r,$c, $non_poi_string_final, $border2);       //NON POI HALT
    //$worksheet->set_column($c, $c, 13);            
}


  //########### SEND MAIL ##############//
  //$to = 'rizwan@iembsys.com';
  $to = 'sanchan@wockhardtfoundation.org';   
  $subject = $filename_title.$previous_date;
  $message = $filename_title.$previous_date; 
  $random_hash = md5(date('r', time()));  
  $headers = "From: support@iembsys.co.in\r\n";
  //$headers .= "Reply-To: taseen@iembsys.com\r\n"; 
  $headers .= "Cc: rizwan@iembsys.com,jyoti.jaiswal@iembsys.com";
  //$headers .= "Cc: tanveerdad@gmail.com,rizwan@iembsys.com,jyoti.jaiswal@iembsys.com";
  $headers .= "\r\nContent-Type: multipart/mixed; boundary=\"PHP-mixed-".$random_hash."\""; 
  $filename_title = $filename_title.".xls";
  $file_path = $file_path.".xls";
  
  echo "\nFILE PATH=".$file_path;
  
  include_once("send_mail_api.php");
  //####################################//
  
  unlink($file_path); 

    
?>
