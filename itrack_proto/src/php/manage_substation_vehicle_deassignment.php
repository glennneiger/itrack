﻿<?php
include_once('Hierarchy.php');
include_once('util_session_variable.php');
include_once('util_php_mysql_connectivity.php');
include("user_type_setting.php");

$root=$_SESSION['root'];  
$js_function_name="manage_select_by_entity";	
//echo "deassign_substation_vehicle##"; 
echo "deassign##"; 
$DEBUG=0;
//$common_id1 = $account_id;
	for($k=0;$k<$size_feature_session;$k++)
	{
		if($feature_name_session[$k] == "substation")
		{
			$flag_substation = 1;
		}
		if($feature_name_session[$k] == "raw_milk")
		{
			$flag_raw_milk = 1;
		}	
		if($feature_name_session[$k] == "hindalco_invoice")
		{
			$flag_hindalco_invoice = 1;
		}	
	}		
	
$common_id1 = $_POST['local_account_id'];

	$result=mysql_query($query,$DbConnection);
	$data= getDetailAllVehicleVG($common_id1,$DbConnection);
	$v_s=0;
	foreach($data as $dt)
	{
		$v_s++;
		$vehicle_id[$v_s]=$dt['vehicle_id'];
		$vehicle_name[$v_s]=$dt['vehicle_name'];
	}
	
	if($numrows > 0)
	{
	echo'<br>
		<input type="hidden" id="common_id">
		<input type="hidden" id="action_name" value="vehicle">
	<center>
		<table width="70%" align="center">
			<tr>
				<td>
					<fieldset class=\'assignment_manage_fieldset\'>
						<legend>';						
            	 echo'<strong>Select Vehicle</strong>';
						
            echo'</legend>';
			echo "<div style='width=400px;height:300px;overflow:auto;'>
			<fieldset class='manage_cal_vehicle'>
				<legend>
					<strong>
						Vehicle
					</strong>
				</legend> 
				<table border=0 cellspacing=0 cellpadding=0 class='module_left_menu'>
					<tr>
						<td colspan='3'>
							&nbsp;<INPUT TYPE='checkbox' name='all_vehicle' onclick='javascript:select_all_assigned_vehicle(this.form);'>
							<font size='2'>
								Select All
							</font>"."																				</td>																														
					</tr>";
					get_user_vehicle($root,$common_id1);
				echo '</table>	
			</fieldset>		
		    </div>						
					</fieldset>
				</td>
			</tr>
		</table>';
		echo '<br><br><br>';
		if($flag_substation)
		{
			echo '<br><br><input type="button" id="enter_button" name="enter_button" Onclick="javascript:return action_manage_vehicle_substation(manage1,\'deassign\')" value="DeAssign">';
		}
		else if($flag_raw_milk || $flag_hindalco_invoice){//transporter
			echo '<br><br><input type="button" id="enter_button" name="enter_button" Onclick="javascript:return action_manage_vehicle_transporter(manage1,\'deassign\')" value="DeAssign">';
		}
		echo '</div>
		
		<div align="center" id="portal_vehicle_information" style="display:none;"></div><br>			
	</center>';
	}
	
	function common_function_for_vehicle($vehicle_imei,$vehicle_id,$vehicle_name,$option_name)
	{	
		//echo "<br>In Common VehicleID";
		//$td_cnt++;
		global $td_cnt;
		if($td_cnt==1)
		{
			echo'<tr>';
		}
		
		//date_default_timezone_set('Asia/Calcutta');
		$current_date = date('Y-m-d');

		$xml_file = "../../../xml_vts/xml_data/".$current_date."/".$vehicle_imei.".xml";
		//echo "xml_file=".$xml_file."<br>";
	
		if(file_exists($xml_file))
		{
		echo'<td align="left">&nbsp;<INPUT TYPE="checkbox"  name="vehicle_id[]" VALUE="'.$vehicle_id.'"></td>
			   <td class=\'text\'>
				 <font color="darkgreen">'.$vehicle_name.'('.$vehicle_id.')</font>                
			   </td>';
		}
		else
		{
			echo'<td align="left">&nbsp;<INPUT TYPE="checkbox"  name="vehicle_id[]" VALUE="'.$vehicle_id.'"></td>
				<td class=\'text\'>
				  <font color="grey">'.$vehicle_name.'('.$vehicle_id.')</font>
				</td>';
		}
		if($td_cnt==4)
		{ 
			echo'</tr>';
		}
	}
			
	function get_user_vehicle($AccountNode,$account_id)
	{
		global $vehicleid;
		global $vehicle_cnt;
		global $td_cnt;
		global $DbConnection;
		if($AccountNode->data->AccountID==$account_id)
		{
			//echo "<br>Matched:Vcnt=".$AccountNode->data->VehicleCnt." ,AC=".$account_id;
			$td_cnt =0;
			for($j=0;$j<$AccountNode->data->VehicleCnt;$j++)
			{			    
				$vehicle_id = $AccountNode->data->VehicleID[$j];
				$vehicle_name = $AccountNode->data->VehicleName[$j];
				$vehicle_imei = $AccountNode->data->DeviceIMEINo[$j];
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
						//echo "<br>Found";
						$vehicleid[$vehicle_cnt]=$vehicle_id;
						$vehicle_cnt++;
						$td_cnt++;
						//$query="SELECT vehicle_id FROM escalation_alert_assignment WHERE vehicle_id='$vehicle_id' AND status='1'";
						//echo "query=".$query;
						//$result=mysql_query($query,$DbConnection);
						//$num_rows=mysql_num_rows($result);
						//if($num_rows==0)
						//{							
							common_function_for_vehicle($vehicle_imei,$vehicle_id,$vehicle_name,$AccountNode->data->AccountGroupName);
						//}
						if($td_cnt==4)
						{
							$td_cnt=0;
						}
					}
				}
			}
		}
		$ChildCount=$AccountNode->ChildCnt;
		for($i=0;$i<$ChildCount;$i++)
		{ 
			get_user_vehicle($AccountNode->child[$i],$account_id);
		}
	}
?>	