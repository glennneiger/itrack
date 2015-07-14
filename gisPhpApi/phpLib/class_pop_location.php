<?php
class class_pop_location{
	
	function get_location_by_name($name_loc){		
		$location_info=array();
		
		$name_loc = preg_replace('/\s+/', '%20', $name_loc);
		//echo $name_loc;
		//$url="http://52.74.144.159:8080/fulltext/fulltextsearch?q=$name_loc&placetype=road&format=JSON&__checkbox_indent=true&from=1&to=1";
                $url="http://52.74.144.159:8080/fulltext/fulltextsearch?q=$name_loc&format=JSON&__checkbox_indent=true&from=1&to=1";
		$json = file_get_contents($url);
		$data = json_decode($json, TRUE);
		//print_r($data);

		$feature_id=$data['response']['docs'][0]['feature_id'];
		$name=$data['response']['docs'][0]['name'];
		$lat=$data['response']['docs'][0]['lat'];
		$lng=$data['response']['docs'][0]['lng'];
		//$feature_code=$data['response']['docs'][0]['feature_code'];
		//$placetype=$data['response']['docs'][0]['placetype'];//ROAD
		//echo "CHURAHA INFO, FeatureID=".$feature_id.":Name=".$name.":Lat=".$lat.":Lng=".$lng.":FeatureCode=".$feature_code;
		$location_name=$name;		
		$location_info[]=array('code'=>$feature_id,'location'=>$location_name,'lat'=>$lat,'lng'=>$lng);
		
		return $location_info;
	}
	function  get_location_by_latlng($lat,$lng)
	{
		$location_info=array();
		$url="http://52.74.144.159:8080/geoloc/geolocsearch?lat=$lat&lng=$lng&placetype=gisfeature&format=JSON&distance=true&from=1&to=1";
		      //http://52.74.144.159:8080/geoloc/geolocsearch?lat=26.501777648925&lng=80.251747131347&radius=&placetype=gisfeature&format=XML&distance=true&__checkbox_indent=true&from=1&to=10
                //echo $url;
                $json = file_get_contents($url);
		$data = json_decode($json, TRUE);
		//print_r($data);
		
		$feature_id=$data['result'][0]['featureId'];
		$name=$data['result'][0]['name'];
		$lat=$data['result'][0]['lat'];
		$lng=$data['result'][0]['lng'];
		$feature_code=$data['result'][0]['featureCode'];
		$distance=$data['result'][0]['distance'];
		//$placetype=$data['result'][0]['placetype'];//ROAD
		//echo "CHURAHA INFO, FeatureID=".$feature_id.":Name=".$name.":Lat=".$lat.":Lng=".$lng.":FeatureCode=".$feature_code;
		$location_name=$name;		
		
		$location_info[]=array('code'=>$feature_id,'location'=>$name,'lat'=>$lat,'lng'=>$lng,'distance'=>$distance);
		return $location_info;
	}
	
	function  get_location_near_by_latlng($lat,$lng,$radius)
	{
		//============This will return from pOSTGRES sql=============================
		$junction_info=array();
		$url="http://52.74.144.159:8080/geoloc/geolocsearch?lat=$lat&lng=$lng&placetype=gisfeature&format=JSON&distance=true&radius=$radius&from=1&to=1";
		$json = file_get_contents($url);
		$data = json_decode($json, TRUE);
		//print_r($data);
		
		$feature_id=$data['result'][0]['featureId'];
		$name=$data['result'][0]['name'];
		$lat=$data['result'][0]['lat'];
		$lng=$data['result'][0]['lng'];
		$feature_code=$data['result'][0]['featureCode'];
		$distance=$data['result'][0]['distance'];
		//$placetype=$data['result'][0]['placetype'];//ROAD
		//echo "CHURAHA INFO, FeatureID=".$feature_id.":Name=".$name.":Lat=".$lat.":Lng=".$lng.":FeatureCode=".$feature_code;
		$location_name=$name;		
		
		$junction_info[]=array('code'=>$feature_id,'location'=>$name,'lat'=>$lat,'lng'=>$lng,'distance'=>$distance);
		return $junction_info;
	}
	
	function get_location_by_code($code)
	{
		//============This will return from pOSTGRES sql=============================
		include('gis_connection.php');
		if(!$db_connection){
		  echo "Error : Unable to open database\n";
		} else {
		  //echo "Opened database successfully\n";
		}
		$location_info=array();
		//$code="100220619";
		$query="SELECT name,astext(location) as lnglat,featurecode FROM gisfeature where featureid=$code ";
		//echo $query;
		$result1 = pg_query($db_connection, $query);		
		$row1=pg_fetch_object($result1);
		$name=$row1->name;
		$lnglat=explode(" ",$row1->lnglat);
		$lng_tmp=explode("(",$lnglat[0]);
		$lat_tmp=explode(")",$lnglat[1]);
		//$featurecode=$row1->featurecode;
		$location_name=$name;
		//return "Name=".$name.":Lng=".$lng_tmp[1].":Lat=".$lat_tmp[0];
		$location_info[]=array('code'=>$code,'location'=>$name,'lat'=>$lat_tmp[0],'lng'=>$lng_tmp[1]);
		return $location_info;
	}
	
	
        function get_location_by_latlng_array($lat_lng_array,$radius)
	{
		//============This will return from pOSTGRES sql=============================
		include('gis_connection.php');
		if(!$db_connection){
		  echo "Error : Unable to open database\n";
		} else {
		  //echo "Opened database successfully\n";
		}
		$location_info=array();
		foreach($lat_lng_array as $lat_lng)
                {
                    $lat=$lat_lng['lat'];
                    $lng=$lat_lng['lng'];							
                    $lat_minus= $lat-1;
                    $lat_plus= $lat+1;
                    $lng_minus= $lng-1;
                    $lng_plus= $lng+1;
                    
                    $query="SELECT  name,featureid,CAST (st_distance_sphere(location, st_setsrid(st_makepoint($lng,$lat),4326)) AS INT) AS d  FROM gisfeature WHERE location && 'BOX3D($lng_minus $lat_minus,$lng_plus $lat_plus)'::box3d  and name!='' ORDER BY location <-> st_setsrid(st_makepoint($lng ,$lat), 4326) LIMIT 1";
                    //echo $query;
                    $result1 = pg_query($db_connection, $query);		
                    $row1=pg_fetch_object($result1);
                    $loc_name=$row1->name;
                    $loc_code=$row1->featureid;
                    $get_radius=$row1->d;
                    if($get_radius <= $radius)
                    {
                        $location_info[]=array('lat'=>$lat,'lng'=>$lng,'loc_name'=>$loc_name,'loc_code'=>$loc_code,'get_radius'=>$get_radius,'landmark'=>'-');
                    }
                    else
                    {
                        if($get_radius < 5000)
                        {
                            $location_info[]=array('lat'=>$lat,'lng'=>$lng,'loc_name'=>$loc_name,'loc_code'=>$loc_code,'get_radius'=>$get_radius,'landmark'=>'-');
                        }
                        else
                        {
                            $location_info[]=array('lat'=>$lat,'lng'=>$lng,'loc_name'=>'-','loc_code'=>'-','get_radius'=>'-','landmark'=>'-');
                        }
                    }
                }               
               
		return $location_info;
	}
	
}
?>