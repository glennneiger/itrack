<?php
	include_once('util_session_variable.php');
	include_once('util_php_mysql_connectivity.php');
	include_once("calculate_distance.php");
	
	$lt1=$_GET['lm_lat'];
	$lng1=$_GET['lm_lng'];

   // echo "lat=".$lt1."lng=".$lng1."<br>";
    $query = "SELECT * FROM landmark WHERE account_id='$account_id' AND status=1";
   // echo $query;
    $result = mysql_query($query,$DbConnection);
    
    $placename1 ="";
    $i=0;
    while($row=mysql_fetch_object($result))
    {
    	$landmark_name=$row->landmark_name;
    	$coord = $row->landmark_coord;
    	
    	$coord1 = explode(',',$coord);
    	$lat2= $coord1[0];
    	$lng2= $coord1[1];
  
  		//echo "lt1=".$lt1."lng2=".$lng2."lat2=".$lat2."lng2=".$lng2;
  		calculate_distance($lt1,$lat2,$lng1,$lng2,&$distance);
  		$distance1=round($distance,2);
  		
  		//echo "<br>dist=".$distance1." ,".$landmark;
      if($i==0)
  		{
  		  $lowest_dist = $distance1;
  		  $placename1 = $landmark_name;
      }
      else
      {
        if($distance1 < $lowest_dist)
        {
          $lowest_dist = $distance1;
          $placename1 = $landmark_name;
        } 
      }
  	
      $i++;	
  		//echo "<br>L1:".$distance1." #".$lowest_dist." #".$placename1;
      //echo "dist=".$distance;
  		//echo "lat1=".$lat1."long1=".$lon1."distance=".$distance."lat2=".$lat2."long2=".$lon2;;
  	}
    
  	//echo "<br>L1:".$distance1." #".$lowest_dist." #".$placename1;
    //if(($lowest_dist <=10) && ($placename1!=""))
	$landmark="";
    if(($lowest_dist <=1) && ($placename1!=""))
  	{
  		$landmark = $lowest_dist." km from ".$placename1;	 
  		//echo "<br>place=".$landmark;
  	} 
	echo $landmark;
  
	
?>
