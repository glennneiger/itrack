<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
include_once('class_push_road.php');
//=======Setting into road array=============//
$data_road_array=array();
$countrycode="IN";
$isin="Kanpur, UP";
$lat="26.50318739187539";
$lng="80.24729490280151";
$lnglatseries="80.24960160255432 26.507440705757638,80.24931192398071 26.507167077436733,80.24919390678406  26.506960655640555, 80.24870038032532 26.506432598194902,80.24861991405487 26.506296382982715,80.24858236312866 26.505931541442614, 80.24843484163284 26.505456285593556,80.24816393852234 26.504457761849785,80.24800837039948 26.50403290656754, 80.24755775928497 26.503483232713055,80.24594843387604 26.501660769036295"; //comma sep
$location="Gooba Garden Road";
$streettype='ROAD';
$length="450.0"; //meter in double
$oneway_t_f='f'; //true or false
$data_road_array[]=array('lat'=>$lat,'lng'=>$lng,'lnglatseries'=>$lnglatseries,'location'=>$location,'streettype'=>$streettype,'length'=>$length,'oneway_t_f'=>$oneway_t_f,'countrycode'=>$countrycode,'isin'=>$isin);
//==calling class==//
$set_road= new class_push_road();
$result_set_road=$set_road->set_road($data_road_array);
echo $result_set_road;
?>