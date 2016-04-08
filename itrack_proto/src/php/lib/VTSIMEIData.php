<?php

include_once('UTIL.php');
include_once('BUG.php');
include_once('VTSXMLRead.php');

class VTSIMEIData
{
	public static function getSpeedData($imei, $startDateTime, $endDateTime)
	{
		$fields = 'speed';
		$filedsMin = '0';
		$fieldsMax = '300';

		$fieldsDataAll = VTSXMLRead::getVTSFieldsData($imei, $startDateTime, $endDateTime, $fields, $filedsMin, $fieldsMax);
		return $fieldsDataAll[$fields];
	}

	public static function getDeviceDistanceData($imei, $startDateTime, $endDateTime)
	{
		$fields = 'distance';
		$filedsMin = '-';
		$fieldsMax = '-';

		$fieldsDataAll = VTSXMLRead::getVTSFieldsData($imei, $startDateTime, $endDateTime, $fields, $filedsMin, $fieldsMax);
		return $fieldsDataAll[$fields];
	}

	public static function getIOData($imei, $datetime_start, $datetime_end, $io)
	{
		$fields = 'io' . $io;
		$filedsMin = '0';
		$fieldsMax = '4096';

		$fieldsDataAll = VTSXMLRead::getVTSFieldsData($imei, $startDateTime, $endDateTime, $fields, $filedsMin, $fieldsMax);
		return $fieldsDataAll[$fields];
	}

	public static function getVoltageData($imei, $datetime_start, $datetime_end)
	{
		$fields = 'sup_v';
		$filedsMin = '-';
		$fieldsMax = '-';

		$fieldsDataAll = VTSXMLRead::getVTSFieldsData($imei, $startDateTime, $endDateTime, $fields, $filedsMin, $fieldsMax);
		return $fieldsDataAll[$fields];
	}

	public static function getSatelliteData($imei, $datetime_start, $datetime_end)
	{
		$fields = 'no_of_sat';
		$filedsMin = '-';
		$fieldsMax = '-';

		$fieldsDataAll = VTSXMLRead::getVTSFieldsData($imei, $startDateTime, $endDateTime, $fields, $filedsMin, $fieldsMax);
		return $fieldsDataAll[$fields];
	}
}

?>
