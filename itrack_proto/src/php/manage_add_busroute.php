<?php 
	include_once('Hierarchy.php');
	include_once('util_session_variable.php');
	include_once('util_php_mysql_connectivity.php');
	include_once('get_group.php');
  $group_cnt=0; 
  $root=$_SESSION['root'];  
  echo "add##";
  $DEBUG=0;    
  echo'<table align="center">
        <tr>
          <td>
              <fieldset class=\'assignment_manage_fieldset\'>
                <legend>
                    <strong>Group</strong>
                </legend>
                <table border="0" class="manage_interface">
					';GetGroup($root);echo'</table>
              </fieldset>	
          </td>
        </tr>
      </table>';
 ?>  
	<br>
	<fieldset class="report_fieldset">
	<legend><strong>Add Busroute</strong></legend>
	<table border="0" class="manage_interface">
			<!--
    <tr>
			<td>Name</td>
			<td> :</td>
			<td><input type="text" name="add_school_name" id="add_school_name" onkeyup="manage_availability(this.value, 'school')" onmouseup="manage_availability(this.value, 'school')" onchange="manage_availability(this.value, 'school')"></td>
		</tr>
	
		<tr>
			<td>Coord</td>
			<td> :</td>
			<td><textarea readonly="readonly" style="width:350px;height:60px" name="route_coord" id="route_coord" onclick="javascript:showCoordinateInterface('route');"></textarea></td>	
		</tr>
		-->
		<tr>
			<td>Busroute Name</td>
			<td> :</td>
			<td><input type="text" name="add_busroute_name" id="add_busroute_name" onkeyup="manage_availability(this.value, 'busroute')" onmouseup="manage_availability(this.value, 'busroute')" onchange="manage_availability(this.value, 'busroute')"></td>
		</tr>   		
		 		
		<tr>
			<td colspan="3" align="center">
				<input type="button" id="enter_button" value="Save" onclick="javascript:return action_manage_busroute('add')"/>&nbsp;<input type="reset"" value="Clear" />
			</td>
		</tr>
	</table>
	</fieldset>
	<?php
		include_once('availability_message_div.php');
	?>

  