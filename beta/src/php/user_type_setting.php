<?php
  for($i=0;$i<$size_utype_session;$i++)
  {
    $fleet_user_type=$user_type_name_session[$i] == "fleet"?1:0;
    $courier_user_type=$user_type_name_session[$i] == "courier"?1:0;
    $school_user_type=$user_type_name_session[$i] == "school"?1:0;
    $pos_user_type=$user_type_name_session[$i] == "pos"?1:0;
    $mining_user_type=$user_type_name_session[$i] == "mining"?1:0;
    $person_user_type=$user_type_name_session[$i] == "person"?1:0;
  }
  
 if(@$person_user_type!=1 || ((@$fleet_user_type==1 || @$mining_user_type==1 || @$courier_user_type==1 || @$school_user_type ==1 || @$pos_user_type==1)))
  {
	$report_type="Vehicle";
  }
  else
  {
	$report_type="Person";
  }
?>
