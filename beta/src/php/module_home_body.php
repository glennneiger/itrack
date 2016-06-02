<?php    
$options = array(
    'center' => true,
	'navigationControl' => true,
	'mapTypeControl' => true,
	'scaleControl' => true,
	'scrollwheel' => true,
    'zoom' => 5,	
    'type' => 'R', // Roadmap, Satellite, Hybrid, Terrain
    'div'=> array('height'=>'100%', 'width'=>'100%'),//$user_height1'div'=> array('height'=>'613', 'width'=>'auto'),
    'position'=>'relative',
    'id' => 'map_canvas',
	'chillingPlant' => $flag_chilling_plant,
    'lat'=> 22.755920681486,
    'lng'=> 78.2666015625,
	'style' => ''
);
//echo "3";
echo' 
   <span style="position:absolute; right:2px; top:0%;z-index:99;"> 
    <table border="0" class="" width=300px >
             <tr>
                <td class="menu ">
                    <table>
                        <tr>
                            <td>
                                <A HREF="javascript:window.print()"><i class="fa fa-print" aria-hidden="true"></i></a>
                                <i class="fa fa-map-pin" aria-hidden="true"></i>
                                <select name="mouse_action" onchange="show_data_on_map(\'map_report\');">
                                   <option value="click">Mouse Click</option>
                                   <option value="mouseover">Mouse Over</option>
                                </select>
                            </td>
                       
                        ';
                        
                            include('module_latlng.php');
                            //include('module_station.php');
                        echo'
                        </tr>
                        <tr>
                            <td colspan=2 align=center>
                                <em>Search <font color="green">Landmark</font></em> &nbsp;     
                                <input type="text" id="landmark_search_text" size="10" onkeypress="return runScriptEnter_landmark(event)"/ >
                              </td>
                        </tr>
                    </table>                  
                  </td>
                
                   
             </tr>
             



              ';
               //include('module_landmark.php');
            echo'
    </table>
 </span>
 <span style="position:absolute; left:10px; top:77%;z-index:99;"> 
    <table border="0" class="" width=250px class="alert alert-warning">
             <tr data-toggle="collapse"  >
                 ';
                   if($flag_station==1)
                        {
                          include('module_station.php');
                        }
                        if($flag_visit_track==1)
                        {
                         include('module_schedule_location.php');
                        } 
                  echo'
                  
             </tr>
             ';
              
            echo'
    </table>
 </span>
 ';
echo'<span style="position:absolute; right:50px; top:87%;z-index:99;">';include('module_speed_symbol.php');echo'</span>';
echo'<a href="#menu-toggle" class="btn btn-default" id="menu-toggle" style="position:absolute; left:0px; top:40%;z-index:99;"><span class="glyphicon glyphicon-circle-arrow-left" aria-hidden="true"></span></a>';
echo'<input id="pac-input" class="controls" type="text" placeholder="Search Box">';
$googleMapthisapi=new GoogleMapHelper();
echo $googleMapthisapi->map($options);
echo'<p id="prepage" style="position:absolute; font-family:arial; font-size:16; left:40%; top:220px; layer-background-color:#e5e3df; height:10%; width:20%; visibility:hidden"><img src="images/load_data.gif">';		
echo '<div id="dummy_div" style=""/>';


?>