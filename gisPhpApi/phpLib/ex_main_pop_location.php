<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
include_once('class_pop_location.php');
$location_pop=new class_pop_location();

$flag_by_name=0;
$flag_by_lat_lng=0;
$flag_by_lat_lng_radius=0;
$flag_by_code=0;
$flag_by_lat_lng_array=1;

//===pop by road name using gisgraphy project useful to access via js=====//
if($flag_by_name==1)
{
    $road_name='IIT';
    $result_data_array=$location_pop->get_location_by_name($road_name);
    print_r($result_data_array); //ex: array('code'=>$feature_id,'location'=>$location_name,'lat'=>$lat,'lng'=>$lng);
}

//===pop by road lat lng using gisgraphy project useful to access via js=====//
if($flag_by_lat_lng==1)
{
   $lat="26.4948291778564";
   $lng="80.2815704345703";   

    $result_data_array=$location_pop->get_location_by_latlng($lat,$lng);
    print_r($result_data_array);// ex: array('code'=>$g_id,'location'=>$name,'lat'=>$lat,'lng'=>$lng,'distance'=>$distance)
}

//===pop by road lat lng within radius using gisgraphy project useful to access via js=====//
if($flag_by_lat_lng_radius==1)
{
    $lat="26.4948291778564";
   $lng="80.2815704345703";   

    $radius='500';//in meter
    $result_data_array=$location_pop->get_location_near_by_latlng($lat,$lng,$radius);
    print_r($result_data_array);// ex: array('code'=>$g_id,'location'=>$name,'lat'=>$lat,'lng'=>$lng,'distance'=>$distance)
}

//===pop road by road id/code using database postgres
if($flag_by_code==1)
{
    $code="20000002";    
    $result_data_array=$location_pop->get_location_by_code($code);
    print_r($result_data_array);// ex: array('code'=>$code,'location'=>$name,'lat'=>$lat_tmp[0],'lng'=>$lng_tmp[1]);
}

//===pop road by lat lng with radius array using database postgres
if($flag_by_lat_lng_array==1)
{
    $lat_lng_array=array();
    $lat_lng_array[]=array('lat'=>'26.4948291778564','lng'=>'80.2815704345703');
    $radius="500";//in meter
    $result_data_array=$location_pop->get_location_by_latlng_array($lat_lng_array,$radius);
    print_r($result_data_array); // ex: array('lat'=>$lat,'lng'=>$lng,'junction_name'=>$junction_name,'junction_code'=>$junction_code,'distance'=>$get_radius,'landmark'=>'-');
}


?>