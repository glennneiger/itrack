<?php
	include_once('Hierarchy.php');
	include_once('util_session_variable.php');
	include_once('util_php_mysql_connectivity.php');

	$i=0;	
	echo "
	<form name='download_files' action='src/php/report_download_files_1.php' target='_blank' method='POST'>
	<input type='hidden' id='download_file_id' name='download_file_id'>
	<table width='100%' border =0 height='100%'>
			<tr>
				<td height='1%' align='center'>					
				</td>
			</tr>
			<tr>
				<td align='center' valign='top'>				
					<table border=0 align='center' width='100%'>			
						<tr>
							<td colspan='4' class='report_heading' align='center'>
								<b>Download Files</b>
							</td>				
						</tr>
						<tr>
							<td height='2%' colspan='4'></td>				
						</tr>
						<tr>
							<td align='center'>
								<table border=1 width='70%' bordercolor='#A1BBDA' class='menu' align='center' rules='all' cellpadding='3' cellspacing='3'>
									<tr bgcolor='#0E2C52'>
										<td><font color='white'><b>&nbsp;Serial</font></td>
										<td><font color='white'><b>&nbsp;File Name</font></td>
										<td><font color='white'><b>&nbsp;Uploaded Date Time</font></td>
										<td><font color='white'><b>&nbsp;Download</font></td>
										<td><font color='white'><b>&nbsp;Delete</font></td>
									</tr>";
										foreach(glob('gps_report/'.$account_id."/download/".$file_name."/"."*.*") as $filename)
										//foreach(glob('gps_report/'.$account_id."/download/"."*.*") as $filename)
										{										
											$stat = stat($filename);										
											$date = date_create();
											date_timestamp_set($date, $stat['mtime']);												$filename1=explode("/",$filename);
											$i++;
											if($i%2==0)
											{
												echo'<tr bgcolor="#D1E8FF" id="delete_file_tr_'.$i.'">';
											}
											else
											{
												echo'<tr bgcolor="#F9FBFD" id="delete_file_tr_'.$i.'">';
											}											
										echo'
										<td>&nbsp;'.$i.'</td>
										<td>&nbsp;'.$filename1[4].'</td>
										<td>&nbsp;'.date_format($date, 'Y-m-d H:i:s').'</td>
										<td>&nbsp;<a href="#" onclick=javascript:download_this_file("'.$filename.'")>Download</a></td>
										<td>&nbsp;
											<a href="#" style="text-decoration:none;" onclick=javascript:delete_this_file("'.$filename.'","delete_file_tr_'.$i.'");>
												<img src="images/icon/drop.png"  style="border:none;">
											</a>				
										</td>
									</tr>';													
										}	
							echo"</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</form>";
?>
