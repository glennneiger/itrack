<?php ?>
<tr valign="top">
  <td>
    <table border="0" class='module_left_menu' width=100%>
   <!--<tr>
        <td colspan=''>
            Select Vehicles
        </td>
      </tr>
      <tr>
        <td>
          Group By
				</td>  				
				<td>
				  None
				</td>
				<td>
				  Type
				</td>
				<td>
				  Tag
				</td> 				
      </tr>-->
	  <tr id='vehicleloadmessage'>
			<td>
				<table align="center" cellspacing='1' cellpadding='1' valign="top" border=0>
					<tr>
						<td>
						<image src='images/ajax-loader.png'>						
						</td>		
						<td>
					
						<font color='green'><b>&nbsp;&nbsp;Loading Please wait&nbsp;.......</b></font>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	    <tr valign="top" id='vehicleloadmessage1'>
        <td colspan=''>
            <div id="show_vehicle" style="display:none;height:200px;width:100%;overflow:auto;"></div>
			
			<div id="blackout_3"> </div>
				<div id="divpopup_3">
					<table border="0" class="module_left_menu" width="100%">
						  <tr>
							<td class="manage_interfarce" align="right" colspan="7"><a href="#" onclick="javascript:close_main_vehicle_information()" class="hs3"><img src="images/close.png" type="image" style="border-style:none;"></a>&nbsp;&nbsp;</td> 													
						  </tr>
					</table>
					 <div id="main_vehicle_information" style="display:none;"></div>          
			</div>
        </td>
      </tr>
    </table>
  </td>
</tr>

