<?php


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
	$end->add($interval);	

	$period = new DatePeriod($start, $interval, $end);
	$dateList = "(";
	foreach ($period as $date)
	{
		$dateList .= "'".$date->format('Y-m-d')."',";
	}
	$dateList = substr($dateList,0,-1) . ")";
	
	return $dateList;

}



?>