<?php

	/***
	* Prints Cassandra query results in HTML 
	*
	* @param array		Results of CQL
	*
	* @return		TRUE 
	*
	*/
	function printHTML($st_results)
	{
	
		echo "\n";
		echo 'Printing Top 10 rows:'."\n";
		
		echo '<table style="width:100%">';
		echo '<tr>';
		echo '<td>imei</td>';
		echo '<td>DateTime</td>';
		echo '<td>Data</td>';
		echo'</tr>';
		
		foreach ($st_results as $row){
			echo '<tr>';
			foreach($row as $key=>$value){
					echo '<td>';
					echo $value;
					echo '</td>';
			}
			echo '</tr>';
		}
		
		echo'</table>';
	}


	/***
	* Returns last seen data before given datetime from full data log
	* 
	* @param object $o_cassandra	Cassandra object 
	* @param string $imei		IMEI
	* @param string $datetime	YYYY-MM-DD HH:MM:SS
	* 
	* @return array 	Results of the query 
	*/
	function getLastSeenDateTime($o_cassandra,$imei,$datetime)
	{
		$yy = substr($datetime,0,4);
	
		$date1 = substr($datetime,0,10);
		$date2 = substr(str_replace($yy,$yy-1,$datetime),0,10);

		$interval = new DateInterval('P1D');
		$start = new DateTime($date1);
		$end = new DateTime($date2);
		$start->add($interval);

		$st_results = array(); // initialize with empty array
 
		for ($date = $start; $date->sub($interval); $date >= $end)
		{
			$strDate = $date->format('Y-m-d');
			$s_cql = "SELECT * FROM log1 
				  WHERE 
				  imei = '$imei'
				  AND
				  date = '$strDate'
				  AND	
				  dtime < '$datetime'
				  LIMIT 1
				  ;";
			$st_results = $o_cassandra->query($s_cql);
			if (!empty($st_results))
				break;
		}
		return $st_results;

	}


	/***
	* Returns last seen data from last data table lastlog
	* 
	* @param object $o_cassandra	Cassandra object 
	* @param string $imei	IMEI
	* @param string $date	YYYY-MM-DD
	* 
	* @return array 	Results of the query 
	*/
	function getLastSeen($o_cassandra,$imei)
	{
		$s_cql = "SELECT * FROM lastlog 
			  WHERE 
			  imei = '$imei'
			  ;";
	
		$st_results = $o_cassandra->query($s_cql);
		return $st_results;
	}


	/***
	* Parses and Converts array returned by CQL to object
	*
	* @param array		Results of CQL
	* @param array		Param filter	
	* @param boolean	Full or Last Seen 
	*
	* @return object	Object with names of entities
	*
	* return json_decode(json_encode($st_results),FALSE);
	*/
	function gpsParser($st_results,$params,$datatype)
	{
		$st_obj = new stdClass();			
		$full_params = array('a','b','c','d','e','f','i','j','k','l','m','n','o','p','q','r','ci','ax','ay','az','mx','my','mz','bx','by','bz');
		$last_params = array('a','b','c','d','e','f','h','i','j','k','l','m','n','o','p','q','r','s','t','u','ci','ax','ay','az','mx','my','mz','bx','by','bz');
		$gps_params = ($datatype)?$full_params:$last_params;

		$num = 0;
		foreach ($st_results as $row)
		{
			$st_obj->$num = new stdClass;
			$st_obj->$num->g = date('Y-m-d@H:i:s',$row['stime']/1000-19800);	// device time is stored as row key as timestamp in milisecond
			if ($datatype) $st_obj->$num->h = date('Y-m-d@H:i:s',$row['dtime']/1000-19800);	// device time is stored as row key as timestamp in milisecond

			$i = 0;
			foreach (str_getcsv($row['data'], ";") as $gps_val)
			{
				if (in_array($gps_params[$i], $params))
				{
					$st_obj->$num->$gps_params[$i] = $gps_val;
				}
				$i++;
			}
			$num++;
		}
		
		return $st_obj;
	}
	

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


	/***
	* Runs CQL query on Cassandra datastore
	* 
	* @param object $o_cassandra	Cassandra object 
	* @param string $imei		IMEI
	* @param string $datetime1	YYYY-MM-DD HH:MM:SS
	* @param string $datetime2	YYYY-MM-DD HH:MM:SS
	* 
	* @return array 	Results of the query 
	*/
	function getImeiDateTimes($o_cassandra, $imei, $datetime1, $datetime2, $deviceTime)
	{

		$table = ($deviceTime)?'log1':'log2';
		$qtime = ($deviceTime)?'dtime':'stime';

		$dateList = getDateList($datetime1,$datetime2);
		$s_cql2 = "SELECT * FROM $table
			WHERE
			imei = '$imei'
			AND
			date IN $dateList
			AND
			$qtime >= '$datetime1' 
			AND
			$qtime <= '$datetime2'
			;";
		$st_results = $o_cassandra->query($s_cql2);

		return $st_results; 
			

	}


?>
