<?php
	include_once('util_session_variable.php');
	include_once('util_php_mysql_connectivity.php'); 
	include_once('user_type_setting.php');	
	$js_function_name = "manage_show_file";    // FUNCTION NAME
	$page_debug=0;
  	if($page_debug==1)
	{
		echo'<br><table border="1" class="manage_interface" align="center">
				<tr>
					<td><b>Add Files</b></td>	<td><b>Edit Files</b></td>	 <td><b>Register Files</b></td> <td><b>De Register Files</td>	<td><b>Assignment/De-Assignment Files</b></td>
				</tr>
				<tr>
					<td>manage_add_vehicle.php</td>	<td>manage_edit_vehicle_prev.php</td>	 <td>manage_device_vehicle_register_prev.php</td> <td>manage_device_vehicle_deregister_prev.php</td>	<td>manage_vehicle_assignment_prev.php</td>
				</tr>
				<tr>
					<td>manage_checkbox_account_new.php</td>	<td>manage_unassigned_vehicle_option.php</td>	 <td>manage_unassigned_vehicle_option.php</td> <td>manage_unassigned_vehicle_option.php</td>	<td>manage_vehicle_assignment.php</td>
				</tr>
				<tr>
					<td>action_manage_vehicle.php</td>	<td>manage_select_vehicle.php</td>	 <td>manage_device_vehicle_register.php</td> <td>manage_device_vehicle_deregister.php</td>	<td>manage_show_account.php</td>
				</tr>
				<tr>
					<td>&nbsp;</td>	<td>manage_edit_vehicle.php</td>	 <td>action_manage_vehicle.php</td> <td>action_manage_vehicle.php</td>	<td>manage_show_account.php</td>
				</tr>
				<tr>
					<td>&nbsp;</td>	<td>action_manage_vehicle.php</td>	 <td>action_manage_vehicle.php</td> <td>action_manage_vehicle.php</td>	<td>&nbsp;</td>
				</tr>
			</table>';
	}
        echo'<form name="manage1">
      <div class="alert-info" style="background-color:#f7fafc;padding:1px 15px;border-radius:0px">
                    <table width=100% cellspacing=0 cellpading=0>
                        <tr>
                            <td width=10%><span class="active"><b>'.$report_type.'</b></span></td>
                            <td>
                               <div id="tab" class="btn-group" data-toggle="buttons" >
                                    <a onclick="'.$js_function_name.'(\'src/php/manage_add_vehicle.php\')" class="btn btn-default" data-toggle="tab" style="padding: 1px 12px;">
                                       <input type="radio" name="new_exist" value="new" onclick="'.$js_function_name.'(\'src/php/manage_add_vehicle.php\')"/><i class="fa fa-plus-square" aria-hidden="true"></i> Add
                                    </a>
                                    <a onclick="'.$js_function_name.'(\'src/php/manage_edit_vehicle_prev.php\')" class="btn btn-default" data-toggle="tab" style="padding: 1px 12px;">
                                      <input type="radio" name="new_exist" value="exist" onclick="'.$js_function_name.'(\'src/php/manage_edit_vehicle_prev.php\')"/><i class="fa fa-pencil" aria-hidden="true"></i> Edit &nbsp; <i class="fa fa-minus-square" aria-hidden="true"></i> Delete
                                    </a>

                                    <a onclick="'.$js_function_name.'(\'src/php/manage_device_vehicle_register_prev.php\')" class="btn btn-default" data-toggle="tab" style="padding: 1px 12px;">
                                      <input type="radio" name="new_exist" value="exist" onclick="'.$js_function_name.'(\'src/php/manage_device_vehicle_register_prev.php\')"/><i class="fa fa-check-square-o" aria-hidden="true"></i> Register
                                    </a>

                                    <a onclick="'.$js_function_name.'(\'src/php/manage_device_vehicle_deregister_prev.php\')" class="btn btn-default" data-toggle="tab" style="padding: 1px 12px;">
                                      <input type="radio" name="new_exist" value="exist" onclick="'.$js_function_name.'(\'src/php/manage_device_vehicle_deregister_prev.php\')"/><i class="fa fa-circle" aria-hidden="true"></i> De-Register
                                    </a>

                                    <a onclick="'.$js_function_name.'(\'src/php/manage_vehicle_assignment_prev.php\')" class="btn btn-default" data-toggle="tab" style="padding: 1px 12px;">
                                      <input type="radio" name="new_exist" value="exist" onclick="'.$js_function_name.'(\'src/php/manage_vehicle_assignment_prev.php\')"/> <i class="fa fa-link" aria-hidden="true"></i> Assignment &nbsp;<i class="fa fa-chain-broken" aria-hidden="true"></i> De-Assignment
                                    </a>

                            </div>
                            </td>
                       </tr>
                    </table>
                  </div>
     ';

 echo'
	
            
			
                <center>
                    <div style"display:none;" id="edit_div"> </div>
                </center>
	</form>
'; 
/*			
echo'<center>
	<form name="manage1">
		<fieldset class="manage_fieldset">
			<legend><strong>'.$report_type.'</strong></legend>
				<table border="0" class="manage_interface" align="center">
					<tr>
						<td>
							<input type="radio" name="new_exist" value="new" onclick="'.$js_function_name.'(\'src/php/manage_add_vehicle.php\')"/> Add &nbsp;&nbsp;&nbsp;
							<input type="radio" name="new_exist" value="exist" onclick="'.$js_function_name.'(\'src/php/manage_edit_vehicle_prev.php\')"/> Edit / Delete&nbsp;&nbsp;&nbsp;
							<input type="radio" name="new_exist" value="exist" onclick="'.$js_function_name.'(\'src/php/manage_device_vehicle_register_prev.php\')"/> Register &nbsp;&nbsp;&nbsp;
							<input type="radio" name="new_exist" value="exist" onclick="'.$js_function_name.'(\'src/php/manage_device_vehicle_deregister_prev.php\')"/> De-Register &nbsp;&nbsp;&nbsp;
							<input type="radio" name="new_exist" value="exist" onclick="'.$js_function_name.'(\'src/php/manage_vehicle_assignment_prev.php\')"/> Assignment/De-Assignment &nbsp;&nbsp;&nbsp;
							<!--<input type="radio" name="new_exist" value="exist" onclick="'.$js_function_name.'(\'src/php/manage_vehicle_deassignment_prev.php\')"/> De-Assignment-->
						</td>
					</tr>
				</table>     
		</fieldset>
		<div style"display:none;" id="edit_div"> </div>
	</form>
</center>'; */
?>
	
