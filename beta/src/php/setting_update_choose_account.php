<?php
	include_once('Hierarchy.php');
	include_once('util_session_variable.php');
	include_once('util_php_mysql_connectivity.php');
	$root=$_SESSION['root'];	
	
	include_once('tree_hierarchy_information.php');
	include_once('setting_display_accounts.php');
	echo'<center>			
			<input type="button" value="Enter" onclick="javascript:setting_account_detail(\'src/php/setting_account_detail.php\',\'second_stage\');">&nbsp;			
		</center>';
?>
