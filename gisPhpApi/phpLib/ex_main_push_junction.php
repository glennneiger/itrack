<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 
 */
include_once('class_push_junction.php');
//=======Setting into junction array=============//
$countrycode="IN";
$source="PERSONAL";
$lat="26.479975014372428";
$lng="80.30555248260498";
$location="Gol Churaha, Kanpur";
$feature_class="R";
$feature_code="RDJCT"; //junction
$placetype='ROAD';
$countryname="India";
$modificationdate=$date;

$data_junction_array[]=array('lat'=>$lat,'lng'=>$lng,'location'=>$location,'feature_class'=>$feature_class,'feature_code'=>$feature_code,'placetype'=>$placetype,'countrycode'=>$countrycode,'source'=>$source,'countryname'=>$countryname);
//==calling class==//
$set_junction= new class_push_junction();
$result_set_junction=$set_junction->set_junction($data_junction_array);
echo $result_set_junction;
?>