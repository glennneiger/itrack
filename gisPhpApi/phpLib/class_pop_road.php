<?php
class class_pop_road{
	//===========First function called using name=======//
	function get_road_by_name($name_loc){
		
		$road_info=array();
		//$name_loc="Gooba Garden Road";
		$name_loc = preg_replace('/\s+/', '%20', $name_loc);
		//echo $name_loc;
		$url="http://52.74.144.159:8080/fulltext/fulltextsearch?q=$name_loc&placetype=street&country=IN&format=JSON&from=1&to=1";
		$json = file_get_contents($url);
		$data = json_decode($json, TRUE);
		//print_r($data);

		$feature_id=$data['response']['docs'][0]['feature_id'];
		$name=$data['response']['docs'][0]['name'];
		$lat=$data['response']['docs'][0]['lat'];
		$lng=$data['response']['docs'][0]['lng'];
		$isIn=$data['response']['docs'][0]['is_in'];
		//$oneway=$data['response']['docs'][0]['one_way'];
		//$placetype=$data['response']['docs'][0]['placetype']; //Street
		//$streettype=$data['response']['docs'][0]['street_type']; //ROAD
		//$length=$data['response']['docs'][0]['length'];
		
		//echo "ROAD INFO, feature_id=".$feature_id.":Name=".$name.":Lat=".$lat.":Lng=".$lng.":Length(meter)=".$length;
		
		$location_name=$name.",".$isIn;		
		$road_info[]=array('code'=>$feature_id,'location'=>$location_name,'lat'=>$lat,'lng'=>$lng);
		
		return $road_info;
	}
	function  get_road_by_latlng($lat,$lng)
	{
		
		$road_info=array();
		$url="http://52.74.144.159:8080/street/search?lat=$lat&lng=$lng&format=JSON&from=1&to=1";
		$json = file_get_contents($url);
		$data = json_decode($json, TRUE);
		//print_r($data);

		$g_id=$data['result'][0]['gid'];
		$name=$data['result'][0]['name'];
		$lat=$data['result'][0]['lat'];
		$lng=$data['result'][0]['lng'];
		$distance=$data['result'][0]['distance'];
		//$length=$data['result'][0]['length'];
		//$oneway=$data['result'][0]['oneWay'];
		//$streettype=$data['result'][0]['streetType']; //ROAD
		//echo "ROAD INFO, FeatureID=".$g_id.":Name=".$name.":Lat=".$lat.":Lng=".$lng.":isIn=".$isIn.":length=".$length;
		$location_name=$name.",".$isIn;		
		$road_info[]=array('code'=>$g_id,'location'=>$name,'lat'=>$lat,'lng'=>$lng,'distance'=>$distance);
		return $road_info;
	}
	function  get_near_road_by_latlng($lat,$lng,$radius)
	{
		//============This will return from pOSTGRES sql=============================
		$road_info=array();
		$url="http://52.74.144.159:8080/street/streetsearch?format=JSON&lat=$lat&lng=$lng&radius=$radius&from=1&to=1";
		$json = file_get_contents($url);
		$data = json_decode($json, TRUE);
		//print_r($data);

		$g_id=$data['result'][0]['gid'];
		$name=$data['result'][0]['name'];
		$lat=$data['result'][0]['lat'];
		$lng=$data['result'][0]['lng'];
		$isIn=$data['result'][0]['isIn'];
		$distance=$data['result'][0]['distance'];
		//$length=$data['result'][0]['length'];
		//$oneway=$data['result'][0]['oneWay'];
		//echo "Road NEAR INFO, FeatureID=".$g_id.":Name=".$name.":Lat=".$lat.":Lng=".$lng.":isIn=".$isIn.":length=".$length;
		$location_name=$name.",".$isIn;	
		$road_info[]=array('code'=>$g_id,'location'=>$name,'lat'=>$lat,'lng'=>$lng,'distance'=>$distance);
		return $road_info;
	}
	
	function get_road_by_code($code)
	{
		//============This will return from pOSTGRES sql=============================
		include('gis_connection.php');
		if(!$db_connection){
		  echo "Error : Unable to open database\n";
		} else {
		 // echo "Opened database successfully\n";
		}
		$road_info=array();
		//$code="100220619";
		$query="SELECT name,astext(location) as lnglat,isIn FROM openstreetmap where gid=$code ";
		//echo $query;
		$result1 = pg_query($db_connection, $query);		
		$row1=pg_fetch_object($result1);
		$name=$row1->name;
		$lnglat=explode(" ",$row1->lnglat);
		$lng_tmp=explode("(",$lnglat[0]);
		$lat_tmp=explode(")",$lnglat[1]);
		$isIn=$row1->isIn;
		$location_name=$name.",".$isIn;
		//return "Name=".$name.":Lng=".$lng_tmp[1].":Lat=".$lat_tmp[0];
		$road_info[]=array('code'=>$code,'location'=>$name,'lat'=>$lat_tmp[0],'lng'=>$lng_tmp[1]);
		return $road_info;
	}
        
        function get_road_by_latlng_array($lat_lng_array,$radius)
	{
		//============This will return from pOSTGRES sql=============================
		include('gis_connection.php');
		if(!$db_connection){
		  echo "Error : Unable to open database\n";
		} else {
		 // echo "Opened database successfully\n";
		}
		$road_info=array();
		foreach($lat_lng_array as $lat_lng)
                {
                    $lat=$lat_lng['lat'];
                    $lng=$lat_lng['lng'];							
                    $lat_minus= $lat-1;
                    $lat_plus= $lat+1;
                    $lng_minus= $lng-1;
                    $lng_plus= $lng+1;
                    
                    $query="SELECT id, name, astext(location) as lnglat,gid, isin,CAST (st_distance_sphere(shape, st_setsrid(st_makepoint($lng,$lat),4326)) AS INT) AS d FROM openstreetmap WHERE shape && 'BOX3D($lng_minus $lat_minus,$lng_plus $lat_plus)'::box3d  and name!='' ORDER BY shape <-> st_setsrid(st_makepoint($lng ,$lat), 4326)  LIMIT 1";
                    //echo $query;
                    $result1 = pg_query($db_connection, $query);		
                    $row1=pg_fetch_object($result1);
                    $road_name=$row1->name;
                    $road_code=$row1->gid;
                    $get_radius=$row1->d;
                    //echo $get_radius."\n";
                    if($get_radius <= $radius)
                    {
                        $road_info[]=array('lat'=>$lat,'lng'=>$lng,'road_name'=>$road_name,'road_code'=>$road_code,'distance'=>$get_radius,'landmark'=>'-');
                    }
                    else
                    {
                        if($get_radius < 5000)
                        {
                            $road_info[]=array('lat'=>$lat,'lng'=>$lng,'road_name'=>$road_name,'road_code'=>$road_code,'distance'=>$get_radius,'landmark'=>'-');
                        }
                        else
                        {
                            $road_info[]=array('lat'=>$lat,'lng'=>$lng,'road_name'=>'-','road_code'=>'-','distance'=>'-','landmark'=>'-');
                        }
                    }
                }               
               
		return $road_info;
	}
        function get_road_location_by_latlng_array($lat_lng_array,$radius)
	{
		//============This will return from pOSTGRES sql=============================
		include('gis_connection.php');
		if(!$db_connection){
		  echo "Error : Unable to open database\n";
		} else {
		 // echo "Opened database successfully\n";
		}
		$road_info=array();
		foreach($lat_lng_array as $lat_lng)
                {
                    $lat=$lat_lng['lat'];
                    $lng=$lat_lng['lng'];							
                    $lat_minus= $lat-1;
                    $lat_plus= $lat+1;
                    $lng_minus= $lng-1;
                    $lng_plus= $lng+1;
                    
                    $query="SELECT id, name, astext(location) as lnglat,gid, isin,CAST (st_distance_sphere(shape, st_setsrid(st_makepoint($lng,$lat),4326)) AS INT) AS d FROM openstreetmap WHERE shape && 'BOX3D($lng_minus $lat_minus,$lng_plus $lat_plus)'::box3d  and name!='' ORDER BY shape <-> st_setsrid(st_makepoint($lng ,$lat), 4326)  LIMIT 1";
                    $result1 = pg_query($db_connection, $query);		
                    $row1=pg_fetch_object($result1);
                    $road_name=$row1->name;
                    $road_code=$row1->gid;
                    $get_radius=$row1->d;
                    
                    $landmark="";
                    $query_loc="SELECT  name,CAST (st_distance_sphere(location, st_setsrid(st_makepoint($lng,$lat),4326)) AS INT) AS d_loc  FROM gisfeature WHERE location && 'BOX3D($lng_minus $lat_minus,$lng_plus $lat_plus)'::box3d  and name!='' ORDER BY location <-> st_setsrid(st_makepoint($lng ,$lat), 4326) LIMIT 1";
                    //echo $query_loc;
                    $result_loc = pg_query($db_connection, $query_loc);	
                    $num_rows_loc=pg_num_rows($result_loc);
                    if($num_rows_loc>0)
                    {
                        $row_loc=pg_fetch_object($result_loc);
                        //echo $row_loc->d_loc;
                        if($row_loc->d_loc <=200)
                        {
                            
                            $landmark=$row_loc->name;
                        }
                    }
                    if($get_radius <= $radius)
                    {
                        $road_info[]=array('lat'=>$lat,'lng'=>$lng,'road_name'=>$road_name,'road_code'=>$road_code,'get_radius'=>$get_radius,'landmark'=>$landmark);
                    }
                    else
                    {
                        if($get_radius < 5000)
                        {
                            $road_info[]=array('lat'=>$lat,'lng'=>$lng,'road_name'=>$road_name,'road_code'=>$road_code,'get_radius'=>$get_radius,'landmark'=>$landmark);
                        }
                        else
                        {
                            $road_info[]=array('lat'=>$lat,'lng'=>$lng,'road_name'=>'-','road_code'=>'-','get_radius'=>'-','landmark'=>'-');
                        }
                    }
                }               
               
		return $road_info;
	}
}
?>