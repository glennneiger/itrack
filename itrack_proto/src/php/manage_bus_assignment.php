<?php
	include_once('Hierarchy.php');
	include_once('util_session_variable.php');
	include_once('util_php_mysql_connectivity.php');
	//include_once('manage_hierarchy_header1.php');
	
	$root=$_SESSION['root'];
	$common_id1=$_POST['common_id'];
	
	$group_id="";
	 $query="SELECT group_id from account where account_id='$common_id1' AND status='1'";
		//echo "query=".$query."<br>";
		$result=mysql_query($query,$DbConnection);
		$row_result=mysql_num_rows($result);		
		if($row_result!=null)
		{
		$row=mysql_fetch_object($result);
		$group_id=$row->group_id;
		echo $group_id;
		}
	
	echo'<input type="hidden" id="account_id_hidden" value='.$common_id1.'>';
	echo"<br>			
			<form name='manage1' method='post'>
				<center> 				
				<br>                                    
  ";      
   echo'
   	   <fieldset class=\'manage_fieldset\'>
				<legend><strong>Bus Assignment</strong></legend>
  				<table border=0 cellspacing=0 cellpadding=0 class=\'module_left_menu\' align="center">
  				 ';
          echo'
          <tr>
            <td><b>Shift &nbsp;&nbsp;</b></td>
            <td><b> : &nbsp;&nbsp;</b></td>
    			   <td>
              <select name="shift_id" id="shift_id" >
              <option value="select">Select</option>';
            $query="SELECT shift_id,shift_name FROM shift WHERE group_id='$group_id' AND status='1'";
    				
            				$result=mysql_query($query,$DbConnection);
    								$row_result=mysql_num_rows($result);								
    								if($row_result!=null)
          					{
          						while($row=mysql_fetch_object($result))
          						{							
    									$shift_id =$row->shift_id;
    									$shift_name =$row->shift_name;
      								echo'<option value="'.$shift_id.'">'.$shift_name.'</option>';
                      }
    								}
    								
    								echo'
								    </select>
								 </td>
                 </tr>
                 ';
                 
          // for busroute       
            echo'
          <tr>
            <td><b>Route &nbsp;&nbsp;</b></td>
            <td><b> : &nbsp;&nbsp;</b></td>
    			   <td>
              <select name="busroute_id" id="busroute_id" >
              <option value="select">Select</option>';
            $query="SELECT busroute_id,busroute_name FROM busroute WHERE group_id='$group_id' AND status='1'";
    				
            				$result=mysql_query($query,$DbConnection);
    								$row_result=mysql_num_rows($result);								
    								if($row_result!=null)
          					{
          						while($row=mysql_fetch_object($result))
          						{							
    									$busroute_id =$row->busroute_id;
    									$busroute_name =$row->busroute_name;
      								echo'<option value="'.$busroute_id.'">'.$busroute_name.'</option>';
                      }
    								}
    								
    								echo'
								    </select>
								 </td>
                 </tr>
                 ';
          // for buses
          
          echo'
          <tr>
            <td><b>Bus &nbsp;&nbsp;</b></td>
            <td><b> : &nbsp;&nbsp;</b></td>
			<td>';
			 //get_user_vehicle($root,$common_id1);
			echo' 
			<select name="bus_id" id="bus_id" >
			<option value="select">Select</option>';              
                    get_user_vehicle($root,$common_id1);		
					echo'
			</select>
			 </td>
		 </tr>
                 ';
         echo'  
         <tr>
            <td><b>Driver &nbsp;&nbsp;</b></td>
            <td><b> : &nbsp;&nbsp;</b></td>
    			   <td>
              <select name="driver_id" id="driver_id" >
              <option value="select">Select</option>';
            $query="SELECT driverid,drivername,dlnumber FROM bus_driver WHERE group_id='$group_id' AND status='1'";
    				
            				$result=mysql_query($query,$DbConnection);
    								$row_result=mysql_num_rows($result);								
    								if($row_result!=null)
          					{
          						while($row1=mysql_fetch_object($result))
                			{
                			  $driverid=$row1->driverid;
                        $drivername=$row1->drivername; 
                         $dlnumber=$row1->dlnumber;              								 
                			  echo '<option value='.$driverid.'>'.$drivername.'['.$dlnumber.']</option>';
                			}
    								}
    								
    								echo'
								    </select>
								 </td>
                 </tr>
                 ';
          // for buses
          
          echo' 
		</table>
	 </fieldset>   
			';
     // echo $query;	
		  
      
		 echo'	 
				<br>
					<input type="button" id="enter_button" name="enter_button" Onclick="javascript:return action_manage_bus(\'assign\')" value="Assign">&nbsp;<input type="reset" value="Cancel">
				<br><a href="javascript:show_option(\'manage\',\'bus\');" class="back_css">&nbsp;<b>Back</b></a>
				</center>
			</form>';	



function common_function_for_vehicle($vehicle_imei,$vehicle_id,$vehicle_name,$option_name)
{	
	//date_default_timezone_set('Asia/Calcutta');
	$current_date = date('Y-m-d');

	$xml_file = "../../../xml_vts/xml_data/".$current_date."/".$vehicle_imei.".xml";
	// echo "xml_file=". $xml_file;	
	//echo $vehicleSerial[$i];
	if(file_exists($xml_file))
	{								
		echo'<option VALUE="'.$vehicle_id.'">'.$vehicle_name.' * </option>';
	}
	else
	{							
		echo'<option VALUE="'.$vehicle_id.'">'.$vehicle_name.'</option>';
	}

}


function get_user_vehicle($AccountNode,$account_id_local)
{
	global $vehicleid;
	global $vehicle_cnt;
	global $td_cnt;
	global $DbConnection;
	//echo"account_id_local : ".$account_id_local;
	//echo"AccountNode : ".$AccountNode->data->AccountID;
	
	if($AccountNode->data->AccountID==$account_id_local)
	{
		//$td_cnt =0;
		for($j=0;$j<$AccountNode->data->VehicleCnt;$j++)
		{			    
			$vehicle_id = $AccountNode->data->VehicleID[$j];
			$vehicle_name = $AccountNode->data->VehicleName[$j];
			$vehicle_imei = $AccountNode->data->DeviceIMEINo[$j];
			$vehicle_tag = $AccountNode->data->VehicleTag[$j];
			if($vehicle_id!=null)
			{
				for($i=0;$i<$vehicle_cnt;$i++)
				{
					if($vehicleid[$i]==$vehicle_id)
					{
						break;
					}
				}			
				if($i>=$vehicle_cnt)
				{
					$vehicleid[$vehicle_cnt]=$vehicle_id;
					$vehicle_cnt++;
					//$td_cnt++;
					
					//if($vehicle_tag=="bus")
					//{							
						common_function_for_vehicle($vehicle_imei,$vehicle_id,$vehicle_name,$AccountNode->data->AccountGroupName);
					//}
					/*if($td_cnt==3)
					{
						$td_cnt=0;
					}*/
				}
			}
		}
	}
	$ChildCount=$AccountNode->ChildCnt;
	for($i=0;$i<$ChildCount;$i++)
	{ 
		get_user_vehicle($AccountNode->child[$i],$account_id_local);
	}
}

			
?>  
