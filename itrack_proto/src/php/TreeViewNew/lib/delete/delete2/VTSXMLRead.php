<?php

include_once('src/php/lib/UTIL.php');
include_once('src/php/lib/BUG.php');

class VTSXMLRead
{
	// public static $xmlRoot = "../xml";
	public static $xmlRoot = "/var/www/html/vts/xml";
	public static $xmlDirs = array("xml_current", "xml_past");

	public static function getVTSFieldsData($imei, $datetimeStart, $datetimeEnd, $fields, $fieldsMin = '-', $fieldsMax = '-', $isAllData = 0, $datetimeRef = 'datetime')
	{
		$xmlRoot = self::$xmlRoot;
		$xmlDirs = self::$xmlDirs;

		// BUG::debug("IMEI         : " . $imei);
		// BUG::debug("Time Start   : " . $datetimeStart);
		// BUG::debug("Time End     : " . $datetimeEnd);
		// BUG::debug("Fields       : " . $fields);
		// BUG::debug("Fields Min   : " . $fieldsMin);
		// BUG::debug("Fields Max   : " . $fieldsMax);
		// BUG::debug("is All Data  : " . $isAllData);
		// BUG::debug("Datetime Ref : " . $datetimeRef);

		$fieldDataNull = array();

		if(strlen($imei)==0 || strlen($datetimeStart)==0 || strlen($datetimeEnd)==0 || strlen($fields)==0 || strlen($fieldsMin)==0 || strlen($fieldsMax)==0 || strlen($isAllData)==0 || strlen($datetimeRef)==0) { return $fieldDataNull; }

		$fieldsArray    = explode(":", $fields);
		$fieldsCount    = sizeof($fieldsArray);

		if($fieldsMin=="-")
		{
			for($fieldIndex = 0 ; $fieldIndex < $fieldsCount ; $fieldIndex++)
			{
				$fieldsMinArray[] = "-";
			}
		}
		else
		{
			$fieldsMinArray = explode(":", $fieldsMin);
		}

		if($fieldsMax=="-")
		{
			for($fieldIndex = 0 ; $fieldIndex < $fieldsCount ; $fieldIndex++)
			{
				$fieldsMaxArray[] = "-";
			}
		}
		else
		{
			$fieldsMaxArray = explode(":", $fieldsMax);
		}
		// BUG::debug("Total Fields : " . sizeof($fieldsArray) . " / " . sizeof($fieldsMinArray) . " / " . sizeof($fieldsMaxArray));
		// BUG::debugArray("Fields Array", $fieldsArray);
		// BUG::debugArray("Fields Min Array", $fieldsMinArray);
		// BUG::debugArray("Fields Max Array", $fieldsMaxArray);

		if((sizeof($fieldsArray)!=sizeof($fieldsMinArray)) || (sizeof($fieldsArray)!=sizeof($fieldsMaxArray))) { return $fieldDataNull; }

		$datetimeStartTS = strtotime($datetimeStart);
		$datetimeEndTS = strtotime($datetimeEnd);

		if($datetimeEndTS<$datetimeStartTS) { return $fieldDataNull; }

		$datetimeEnd1 = date('Y-m-d H:i:s', strtotime($datetimeEnd)+(1*24*60*60));
		$dateList = UTIL::getAllDates(substr($datetimeStart,0,10), substr($datetimeEnd1,0,10));
		// BUG::debug("Total Dates : " . sizeof($dateList));

		if(sizeof($dateList)<=0) { return $fieldDataNull; }

		foreach($dateList as $i => $date)
		{
			foreach($xmlDirs as $xmlDir)
			{
				$file = $xmlRoot . "/" . $xmlDir . "/" . $date . "/" . $imei . ".xml";
				if(file_exists($file))
				{
					// BUG::debug("Reading " . $file . " ...");
					// BUG::debugNoNL($date . " ");
					$xml = @fopen($file, "r");
					if($xml)
					{
						while(!feof($xml))
						{
							$line = fgets($xml, 4096);
							if(strpos($line,"marker"))
							{
								$datetime = UTIL::getXMLData('/'.$datetimeRef.'="[^"]+"/', $line);
								$datetimeTS = strtotime($datetime);
								if(($datetimeTS>=$datetimeStartTS) && ($datetimeTS<=$datetimeEndTS))
								{
									$isDataValid = 1;
									for($fieldIndex = 0 ; $fieldIndex < $fieldsCount ; $fieldIndex++)
									{
										$field = $fieldsArray[$fieldIndex];
										$fieldMin = $fieldsMinArray[$fieldIndex];
										$fieldMax = $fieldsMaxArray[$fieldIndex];
										if($field=="latlng")
										{
											$lat = UTIL::getXMLData('/lat="\d+\.\d+[NS]\"/', $line);
											$lng = UTIL::getXMLData('/lng="\d+\.\d+[EW]\"/', $line);
											if(strlen($lat)>2 && strlen($lng)>2)
											{
												$fieldValue = $lat.":".$lng;
											}
											else
											{
												$fieldValue = "";
											}
										}
										else
										{
											$fieldValue = UTIL::getXMLData('/'.$field.'="[^"]+"/', $line);
										}
										if($fieldMin != '-' && $fieldValue < $fieldMin) { $isDataValid = 0; }
										if($fieldMax != '-' && $fieldValue > $fieldMax) { $isDataValid = 0; }
										if(strlen($fieldValue)==0)                      { $isDataValid = $isDataValid * $isAllData; }
										$fieldsValue[$field] = $fieldValue;
									}
									// BUG::debug("is Data Valide : " . $isDataValid);
									// BUG::debugArray("Fields Value", $fieldsValue);
									if($isDataValid == 1)
									{
										for($fieldIndex = 0 ; $fieldIndex < $fieldsCount ; $fieldIndex++)
										{
											$field = $fieldsArray[$fieldIndex];
											$fieldsData[$field][$datetime] = $fieldsValue[$field];
										}
									}
								}
							}
						}
					}
					fclose($xml);
				}
			}
		}
		// BUG::debugArray("Fields Data", $fieldsData);
		// BUG::debug("Fields : " . sizeof($fieldsData));
		// BUG::debug("Field Data : " . sizeof($fieldsData[$fieldsArray[0]]));

		if(sizeof($fieldsData[$fieldsArray[0]])==0) { return $fieldDataNull; }

		// BUG::debug("Sorting Field Data");
		$fieldsDataSort = UTIL::sortDateTimeArray($fieldsData);
		return $fieldsDataSort;
	}



	public static function getVTSFieldData($imei, $datetimeStart, $datetimeEnd, $field, $fieldMin = '-', $fieldMax = '-')
	{
		$xmlRoot = self::$xmlRoot;
		$xmlDirs = self::$xmlDirs;

		// BUG::debug("IMEI        : " . $imei);
		// BUG::debug("Time Start  : " . $datetimeStart);
		// BUG::debug("Time End    : " . $datetimeEnd);
		// BUG::debug("Field       : " . $field);
		// BUG::debug("Field Min   : " . $fieldMin);
		// BUG::debug("Field Max   : " . $fieldMax);

		$fieldDataNull['datetime'] = array();
		$fieldDataNull['datetimeTS'] = array();

		if($imei=="" || $datetimeStart=="" || $datetimeEnd=="" || $field=="" || $fieldMin=="" || $fieldMax=="") { return $fieldDataNull; }

		$datetimeStartTS = strtotime($datetimeStart);
		$datetimeEndTS = strtotime($datetimeEnd);

		if($datetimeEndTS<$datetimeStartTS) { return $fieldDataNull; }

		$datetimeEnd1 = date('Y-m-d H:i:s', strtotime($datetimeEnd)+(1*24*60*60));
		$dateList = UTIL::getAllDates(substr($datetimeStart,0,10), substr($datetimeEnd1,0,10));
		// BUG::debug("Total Dates : " . sizeof($dateList));

		if(sizeof($dateList)<=0) { return $fieldDataNull; }

		foreach($dateList as $i => $date)
		{
			foreach($xmlDirs as $xmlDir)
			{
				$file = $xmlRoot . "/" . $xmlDir . "/" . $date . "/" . $imei . ".xml";
				if(file_exists($file))
				{
					// BUG::debug("Reading " . $file . " ...");
					// BUG::debugNoNL($date . " ");
					$xml = @fopen($file, "r");
					if($xml)
					{
						while(!feof($xml))
						{
							$line = fgets($xml, 4096);
							if(strpos($line,"marker"))
							{
								$datetime = UTIL::getXMLData('/datetime="[^"]+"/', $line);
								$datetimeTS = strtotime($datetime);
								if(($datetimeTS>=$datetimeStartTS) && ($datetimeTS<=$datetimeEndTS))
								{
									if($field=="latlng")
									{
										$lat = UTIL::getXMLData('/lat="\d+\.\d+[NS]\"/', $line);
										$lng = UTIL::getXMLData('/lng="\d+\.\d+[EW]\"/', $line);
										if(strlen($lat)>2 && strlen($lng)>2)
										{
											$fieldValue = $lat.":".$lng;
										}
										else
										{
											$fieldValue = "";
										}
									}
									else
									{
										$fieldValue = UTIL::getXMLData('/'.$field.'="[^"]+"/', $line);
									}
									if(strlen($fieldValue)>0)
									{
										if($fieldMin == '-' || $fieldValue >= $fieldMin)
										{
											if($fieldMax == '-' || $fieldValue <= $fieldMax)
											{
												$fieldData[$datetime] = $fieldValue;
											}
										}
									}
								}
							}
						}
					}
					fclose($xml);
				}
			}
		}
		// BUG::debug("Field Data  : " . sizeof($fieldData));

		if(sizeof($fieldData)<=0) { return $fieldDataNull; }

		// BUG::debug("Sorting Field Data");
		$fieldDataSort = UTIL::sortDateTime($fieldData);
		return $fieldDataSort;
	}
}


?>
