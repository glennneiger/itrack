<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
include_once('class_push_location.php');
//=======Setting into junction array=============//
$countrycode="IN";
$source="PERSONAL";
$lat="26.479975014372428";
$lng="80.30555248260498";
$location="Gol Churaha, Kanpur";
$feature_class="R";
$feature_code="ST"; //street
$placetype='STREET';
$countryname="India";
$modificationdate=$date;

$data_location_array[]=array('lat'=>$lat,'lng'=>$lng,'location'=>$location,'feature_class'=>$feature_class,'feature_code'=>$feature_code,'placetype'=>$placetype,'countrycode'=>$countrycode,'source'=>$source,'countryname'=>$countryname);
//==calling class==//
$set_location= new class_push_location();
$result_set_location=$set_location->set_location($data_location_array);
echo $result_set_location;
?>