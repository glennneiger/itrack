<?php
$s_server_host     = '52.6.155.96'; // latest ip 
//$s_server_host     = '54.152.30.69'; // us-east ic1 (EIP)
#$s_server_host     = '52.74.33.255'; //ap-southeast itrackcass0
#$s_server_host     = '127.0.0.1';    // Localhost
$i_server_port     = 9042; 
$s_server_username = 'iccassandra';  // We don't have username
$s_server_password = '3ba952a2afad76cce2111cd0306495e7';  // We don't have password
$s_server_keyspace = 'gps';  

$TZ='0530';	// Asia/Kolkata


/***
* Returns the list of dates for different days 
*
* @param string $datetime1 YYYY-MM-DD HH:MM:SS
* @param string $datetime2 YYYY-MM-DD HH:MM:SS
* 
* @return string	date list	
*
*/
function getDateList($datetime1,$datetime2)
{	
	$date1 = substr($datetime1,0,10);
	$date2 = substr($datetime2,0,10);

	$interval = new DateInterval('P1D');
	$start = new DateTime($date1);
	$end = new DateTime($date2);
	//$end->add($interval);	

	//$period = new DatePeriod($start, $interval, $end);
	$date = $end;
	$dateList = "(";
	while ($date >= $start)
	{
		$dateList .= "'".$date->format('Y-m-d')."',";
		$date = $date->sub($interval);
	}

	/*foreach ($period as $date)
	{
		$dateList .= "'".$date->format('Y-m-d')."',";
	}*/
	$dateList = substr($dateList,0,-1) . ")";
	
	return $dateList;
}

/***
* Returns Array of dates for different days 
*
* @param string $datetime1 YYYY-MM-DD HH:MM:SS
* @param string $datetime2 YYYY-MM-DD HH:MM:SS
* 
* @return string	date list	
*
*/
function getDateArray($datetime1,$datetime2)
{
	$date1 = substr($datetime1,0,10);
	$date2 = substr($datetime2,0,10);

	$interval = new DateInterval('P1D');
	$start = new DateTime($date1);
	$end = new DateTime($date2);
	//$end->add($interval);	

	$date = $end;
	$dateArray = Array();	
	$i = 0;
	while ($date >= $start)
	{
		$dateArray[$i++] = $date->format('Y-m-d');
		$date = $date->sub($interval);
	}

	return $dateArray;

}

?>
