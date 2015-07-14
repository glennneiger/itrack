<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
include_once('class_pop_road.php');
$road_pop=new class_pop_road();

$flag_by_name=0;
$flag_by_lat_lng=0;
$flag_by_lat_lng_radius=0;
$flag_by_code=0;
$flag_by_lat_lng_array=0;
$flag_by_lat_lng_eith_landmark_array=1;
//===pop by road name using gisgraphy project useful to access via js=====//
if($flag_by_name==1)
{
    $road_name='Gooba Garden Road';
    $result_data_array=$road_pop->get_road_by_name($road_name);
    print_r($result_data_array); //ex: array('code'=>$feature_id,'location'=>$location_name,'lat'=>$lat,'lng'=>$lng);
}

//===pop by road lat lng using gisgraphy project useful to access via js=====//
if($flag_by_lat_lng==1)
{
   $lat="26.503187391875";
    $lng="80.247294902802";
    $result_data_array=$road_pop->get_road_by_latlng($lat,$lng);
    print_r($result_data_array);// ex: array('code'=>$g_id,'location'=>$name,'lat'=>$lat,'lng'=>$lng,'distance'=>$distance)
}

//===pop by road lat lng within radius using gisgraphy project useful to access via js=====//
if($flag_by_lat_lng_radius==1)
{
    $lat="26.503187391875";
    $lng="80.247294902802";
    $radius='100';//in meter
    $result_data_array=$road_pop->get_near_road_by_latlng($lat,$lng,$radius);
    print_r($result_data_array);// ex: array('code'=>$g_id,'location'=>$name,'lat'=>$lat,'lng'=>$lng,'distance'=>$distance)
}

//===pop road by road id/code using database postgres
if($flag_by_code==1)
{
    $code="100220619";    
    $result_data_array=$road_pop->get_road_by_code($code);
    print_r($result_data_array);// ex: array('code'=>$code,'location'=>$name,'lat'=>$lat_tmp[0],'lng'=>$lng_tmp[1]);
}

//===pop road by lat lng with radius array using database postgres
if($flag_by_lat_lng_array==1)
{
    $lat_lng_array=array();
    $lat_lng_array[]=array('lat'=>'26.506187391875','lng'=>'80.247294902802');
    $radius="500";//in meter
    $result_data_array=$road_pop->get_road_by_latlng_array($lat_lng_array,$radius);
    print_r($result_data_array); // ex: array('lat'=>$lat,'lng'=>$lng,'road_name'=>$road_name,'road_code'=>$road_code,'distance'=>$get_radius,'landmark'=>'-');
}

//===pop road with landmark by lat lng array with radius through db postgres
if($flag_by_lat_lng_eith_landmark_array==1)
{
    $lat_lng_array=array();
    $lat_lng_array[]=array('lat'=>'26.506187391875','lng'=>'80.247294902802');
    $lat_lng_array[]=array('lat'=>'26.5108337402344','lng'=>'80.2469100952148');
    $radius="500";//in meter
    $result_data_array=$road_pop->get_road_location_by_latlng_array($lat_lng_array,$radius);
    print_r($result_data_array); // ex: array('lat'=>$lat,'lng'=>$lng,'road_name'=>$road_name,'road_code'=>$road_code,'distance'=>$get_radius,'landmark'=>'-');
}
?>