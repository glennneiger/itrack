<?php
    set_time_limit(300);    
    date_default_timezone_set("Asia/Kolkata");
    include_once("main_vehicle_information_1.php");
    include_once('Hierarchy.php');
    include_once('util_session_variable.php');
    include_once('util_php_mysql_connectivity.php');
    $root=$_SESSION["root"];
    //include_once("sort_xml_vtslog.php");
    include_once("calculate_distance.php");
    include("user_type_setting.php");
    include_once('xmlParameters.php');
    include_once("report_title.php");
    include_once('parameterizeData.php');
    include_once('data.php');
    include_once("sortXmlData.php");
    include_once("getXmlData.php");

    $account_id_local1 = $_POST['account_id_local'];
    $query = "SELECT user_id FROM account WHERE account_id='$account_id_local1'";
    //echo $query."<br>";
    $result = mysql_query($query,$DbConnection);
    $row = mysql_fetch_object($result);
    $user_id = $row->user_id;

    $DEBUG = 0;
    $device_str = $_POST['vehicleserial'];
    //echo "<br>devicestr=".$device_str;
    $vserial = explode(',',$device_str);
    $vsize=count($vserial);
    $id = $_REQUEST['id'];
    $rec = $_POST['radio_value'];  
    $date1 = $_POST['start_date'];
    $date2 = $_POST['end_date'];

    $date1 = str_replace("/","-",$date1);
    $date2 = str_replace("/","-",$date2);
    $date_1 = explode(" ",$date1);
    $date_2 = explode(" ",$date2);
    $datefrom = $date_1[0];
    $dateto = $date_2[0];

    $userInterval = "0";
    $sortBy="h";
    $firstDataFlag=0;
    $requiredData="All";
    $endDateTS=strtotime($date2);

    $parameterizeData=new parameterizeData();
    $parameterizeData->messageType='a';
    $parameterizeData->version='b';
    $parameterizeData->fix='c';
    $parameterizeData->latitude='d';
    $parameterizeData->longitude='e';
    $parameterizeData->speed='f';
    //$parameterizeData->cellName='ci';
    $parameterizeData->io1='i';
    $parameterizeData->supVoltage='r';
    $parameterizeData->io8='p';
    $parameterizeData->dataLog='yes';
    if($account_id=="1594")
    {
        $parameterizeData->axParam = 'ax';
        $parameterizeData->ayParam = 'ay';
        $parameterizeData->azParam = 'az';
        $parameterizeData->mxParam = 'mx';
        $parameterizeData->myParam = 'my';
        $parameterizeData->mzParam = 'mz';
        $parameterizeData->bxParam = 'bx';
        $parameterizeData->byParam = 'by';
        $parameterizeData->bzParam = 'bz';
    }

    get_All_Dates($datefrom, $dateto, $userdates);    
    $date_size = sizeof($userdates);

    for($i=0;$i<$vsize;$i++)
    {
        $vehicle_info=get_vehicle_info($root,$vserial[$i]);
        $vehicle_detail_local=explode(",",$vehicle_info);
        $finalVNameArr[$i]=$vehicle_detail_local[0];
        $finalVTypeArr[$i]=$vehicle_detail_local[1];
        //echo "vehcileName=".$finalVNameArr[$i]." vSerial=".$vehicle_detail_local[0]."<br>";

        $distance =0.0;    
        $firstdata_flag =0;        
        $flag =0;
        $rec_count =0;
        for($di=($date_size-1);$di>=0;$di--)
        {
            if(($flag == 1) && ($rec!="all"))
            {
                    break;   // BREAK LOOP AFTER TAKING ONE DAY RECORDS- LAST 30/100/ALL
            }
            $SortedDataObject=null;
            $SortedDataObject=new data();
            if($date_size==1)
            {
                $dateRangeStart=$date1;
                $dateRangeEnd=$date2;
            }
            else if($di==($date_size-1))
            {
                $dateRangeStart=$userdates[$di]." 00:00:00";
                $dateRangeEnd=$date2;
            }
            else if($di==0)
            {
                $dateRangeStart=$date1;
                $dateRangeEnd=$userdates[$di]." 23:59:59";
            }
            else
            {
                $dateRangeStart=$userdates[$di]." 00:00:00";
                $dateRangeEnd=$userdates[$di]." 23:59:59";
            }
        
            //echo "vSerial=".$vserial[$i]." dateRangeStart=".$dateRangeStart." dateRangeEnd=".$dateRangeEnd."<br>";
            deviceDataBetweenDates($vserial[$i],$dateRangeStart,$dateRangeEnd,$sortBy,$parameterizeData,$SortedDataObject);
        
            //var_dump($SortedDataObject);
            if(count($SortedDataObject->deviceDatetime)>0)
            {
                //echo "in sorted=".$SortedDataObject->deviceDatetime."<br><br><br><br><br><br>";
                $prevSortedSize=sizeof($SortedDataObject->deviceDatetime);
                //echo "prevSortedSize=".$prevSortedSize."<br>";
                for($obi=0;$obi<$prevSortedSize;$obi++)
                {        
                    $limit0 = ($prevSortedSize+1)-10;
                    $limit1 = ($prevSortedSize+1)-30;
                    $limit2 = ($prevSortedSize+1)-100;
                    $rec_count++;
                    //echo "rec_count=".$rec_count." limit1=".$limit1." innerSize=".$prevSortedSize."<br>";        
                    //echo "<br>flag=".$flag." ,rec_count=".$rec_count." ,limit=".$limit1." rec=".$rec." id=".$id." strlen=".strlen($line);
                    if( ( ($rec_count == $limit1) || ($prevSortedSize<30) ) && ($rec=="30") && (($id == 1)||($id == 2)||($id == 3)||($id == 4)) ) 
                    {                   
                        //echo "<br>in 30 ,rec_count=".$rec_count;
                        $flag =1;
                    }
                    else if( ( ($rec_count == $limit2) || ($prevSortedSize<100) )  && ($rec=="100") && (($id == 1)||($id == 2)||($id == 3)||($id == 4)) ) 
                    {
                        //echo "<br>in 100";
                        $flag =1;
                    }
                    else if( ( ($rec_count == $limit0) || ($prevSortedSize<10) )  && ($rec=="10") && (($id == 1)||($id == 2)||($id == 3)||($id == 4)) ) 
                    {
                        //echo "<br>in 10";
                        $flag =1;
                    }
                    else if(($rec=="all") && (($id == 1)||($id == 2)||($id == 3)||($id == 4)) )
                    {
                        //echo "<br>in all";
                        $flag =1;
                    }
                    //echo "flag=".$flag."<br>";
                    if($flag == 1)
                    {    
                        //echo "in flag=".$flag."<br>";
                        // $linetowrite = "\n< marker sts=\"".$sts."\" msgtype=\"".$msgtype."\" ver=\"".$ver."\" fix=\"".$fix."\" imei=\"".$vehicleserial."\" vname=\"".$vname."\" userid=\"".$user_id."\" datetime=\"".$datetime."\" lat=\"".$lat."\" lng=\"".$lng."\" alt=\"".$alt."\" speed=\"".$speed."\" fuel=\"".$fuel."\" vehicletype=\"".$vehicletype."\" no_of_sat=\"".$no_of_sat."\" cellname=\"".$cellname."\" distance=\"".$distance."\" io8=\"".$io8."\" sig_str=\"".$sig_str."\" sup_v=\"".$sup_v."\" speed_a=\"".$speed_a."\" geo_in_a=\"".$geo_in_a."\" geo_out_a=\"".$geo_out_a."\" stop_a=\"".$stop_a."\" move_a=\"".$move_a."\" lowv_a=\"".$lowv_a."\" />";                                                                          
                        //echo "wrote<br>";
                        $sts[]=$SortedDataObject->serverDatetime[$obi];
                        $msgtype[]=$SortedDataObject->messageTypeData[$obi];
                        $ver[]=$SortedDataObject->versionData[$obi];
                        $fix[]=$SortedDataObject->fixData[$obi];
                        $imei[]=$vserial[$i];
                        $vname[]=$vehicle_detail_local[0];
                        $vtype[]=$vehicle_detail_local[1];
                        $userid[]=$user_id;
                        $datetime[]=$SortedDataObject->deviceDatetime[$obi];
                        $lat[]=$SortedDataObject->latitudeData[$obi];
                        $lng[]=$SortedDataObject->longitudeData[$obi];
                        $speed[]=$SortedDataObject->speedData[$obi];
                        $cellname[]=$SortedDataObject->io1Data[$obi];
                        $io8[]=$SortedDataObject->io8Data[$obi];
                        $sig_str[]=$SortedDataObject->sigStrData[$obi];
                        $sup_v[]=$SortedDataObject->supVoltageData[$obi];                    
                        if($account_id=='1594')
                        {                      
                            $ax[]=$SortedDataObject->axParamData[$obi];
                            $ay[]=$SortedDataObject->ayParamData[$obi];
                            $az[]=$SortedDataObject->azParamData[$obi];
                            $mx[]= $SortedDataObject->mxParamData[$obi];
                            $my[]=$SortedDataObject->myParamData[$obi];
                            $mz[]=$SortedDataObject->mzParamData[$obi];
                            $bx[]= $SortedDataObject->bxParamData[$obi];
                            $by[]=$SortedDataObject->byParamData[$obi];
                            $bz[]=$SortedDataObject->bzParamData[$obi];    
                        }
                    }
         
                }
            }            
        }
    }
echo'<center>
        <div style="width:1025px;height:600px;overflow:auto;" align="center">';       
            if(sizeof($imei)==0)
            {
            echo'<br>
                <font color=red>
                    <strong>
                        Sorry No record found
                    </strong>
                </font>';
            }  
            else
            {
                $sts = array_reverse($sts);
                $msgtype = array_reverse($msgtype);
                $ver = array_reverse($ver);
                $fix = array_reverse($fix);
                $imei = array_reverse($imei);
                $vname = array_reverse($vname);
                $userid = array_reverse($userid); 
                $datetime = array_reverse($datetime);
                $lat = array_reverse($lat);
                $lng = array_reverse($lng);
                //$alt = array_reverse($alt);
                $speed = array_reverse($speed);
                //$fuel = array_reverse($fuel);
                $vtype = array_reverse($vtype);
                // $no_of_sat = array_reverse($no_of_sat);
                $cellname = array_reverse($cellname);
                //$cbc = array_reverse($cbc);
                //$distance = array_reverse($distance);
                $io8 = array_reverse($io8);
                $sig_str = array_reverse($sig_str);
                $sup_v = array_reverse($sup_v);

                if($account_id=='1594')
                {
                    $ax = array_reverse($ax);
                    $ay = array_reverse($ay);
                    $az = array_reverse($az);
                    $mx = array_reverse($mx);
                    $my = array_reverse($my);
                    $mz = array_reverse($mz);
                    $bx = array_reverse($bx);
                    $by = array_reverse($by);
                    $bz = array_reverse($bz);        
                }
                /*$speed_a = array_reverse($speed_a);
                 $geo_in_a = array_reverse($geo_in_a);
                 $geo_out_a = array_reverse($geo_out_a);
                 $stop_a = array_reverse($stop_a);
                 $move_a = array_reverse($move_a);
                 $lowv_a = array_reverse($lowv_a);*/
            }                      
         
            for($i=0;$i<sizeof($imei);$i++)
            {                                              
                if(($i===0) || (($i>0) && ($imei[$i-1] != $imei[$i])) )
                {
                    $sno = 1;
                    $title="VTS DataLog For UserID : ".$userid[$i].", ".$report_type." : ".$vname[$i]." &nbsp;<font color=red>(".$imei[$i].")</font>";
                    echo"<input TYPE=\"hidden\" VALUE=\"$title1\" NAME=\"title\">";
                     echo"<input TYPE=\"hidden\" VALUE=\"$title1\" NAME=\"title\">";
                      
            echo'<form  name="text_data_report" method="post" target="_blank">
                    <br>
                    <table align="center">
                            <tr>
                                <td class="text" align="center"><b>
                                        '.$title.'
                                    </b> 
                                    <div style="height:8px;"></div>
                                </td>
                            </tr>
                    </table>
                    <table border="1" id="filter_block" width="98%" rules="all" bordercolor="#e5ecf5" align="center" cellspacing="0" cellpadding="1">    
                        <tr bgcolor="#C9DDFF"> 
                            <td class="text">
                                <b><font size="1">SNo</font></b>
                            </td>   
                            <td class="text">
                                <b><font size="1">STS</font></b>
                            </td>  
                            <td class="text">
                                <b><font size="1">DateTime</font></b>
                            </td>';           
                            if($report_type=='Person')
                            {
                                //echo'<th class="text"><b><font size="1">Person Name</font></b></td>'; 
                            }
                            else
                            {
                            echo'<!--<td class="text"><b><font size="1">VehName</font></b></td>-->  
                                <td class="text"><b><font size="1">VehTp</font></b></td>';  
                               
                            }           
                        echo'<!--<td class="text"><b><font size="1">UserID</font></b></td>-->
                            <!--<td class="text"><b><font size="1">IMEI</font></b></td>-->      
                            <td class="text"><b><font size="1">MsgTp</font></b></td>  
                            <td class="text"><b><font size="1">Fix</font></b></td>   
                            <td class="text"><b><font size="1">Latitude</font></b></td>    
                            <td class="text"><b><font size="1">Longitude</font></b></td>';
                           

                            if($report_type!='Person')
                            {            
                                echo'<td class="text"><b><font size="1">Speed</font></b></td>   
                                    <td class="text"><b><font size="1">Distance</font></b></td>';
                               
                            }   
                            if($report_type=='Person')
                            {            
                                echo'<td class="text">
                                        <b><font size="1">Boot Status</font></b>
                                    </td>';
                            
                            }                
                            echo'<td class="text">
                                    <b><font size="1">Version</font></b>
                                </td>';
                           
                            if($report_type!="Person")
                            {
                                echo'<td class="text">
                                        <b><font size="1">SupplyV</font></b>
                                    </td>  
                                    <td class="text">
                                        <b><font size="1">SgnlSt</font></b>
                                    </td>';
                            }      
                            if($account_id=='1594')
                            {
                                echo'<td class="text"><b><font size="1">AX &nbsp;&nbsp;</font></b></td>';
                                echo'<td class="text"><b><font size="1">AY &nbsp;&nbsp;</font></b></td>';
                                echo'<td class="text"><b><font size="1">AZ &nbsp;&nbsp;</font></b></td>';
                                echo'<td class="text"><b><font size="1">MX &nbsp;&nbsp;</font></b></td>';
                                echo'<td class="text"><b><font size="1">MY &nbsp;&nbsp;</font></b></td>';
                                echo'<td class="text"><b><font size="1">MZ &nbsp;&nbsp;</font></b></td>';
                                echo'<td class="text"><b><font size="1">BX &nbsp;&nbsp;</font></b></td>';
                                echo'<td class="text"><b><font size="1">BY &nbsp;&nbsp;</font></b></td>';
                                echo'<td class="text"><b><font size="1">BZ &nbsp;&nbsp;</font></b></td>';
                               
                            }       

                            if($report_type!='Person')
                            {          
                                if($login == "iesplweb") 
                                {
                                    echo'<td class="text"><b><font size="1">IO8</font></b></td>';
                                   
                                }
                            }             
                    echo'</tr>';
                   
                }    

                if ($sno%2==0)
                {
            echo'<tr bgcolor="#F7FCFF">';
                }                                        
                else 
                {
            echo'<tr bgcolor="#E8F6FF">';    
                }                                           
                    echo'<td class="text">'.$sno.'</td>';
                    echo'<td class="text">'.$sts[$i].'</td>';
                    echo'<td class="text">'.$datetime[$i].'</td>';      
                    //echo'<td class="text"><font color="green">'.$vname[$i].'</font></td>';         
                if($report_type!='Person')
                {
                    echo'<td class="text">'.$vtype[$i].'</td>';
                }                            
                //echo'<td class="text"><font size="2">'.$userid[$i].'</td>';
                //echo'<td class="text">'.$imei[$i].'</td>';
                echo'<td class="text">'.$msgtype[$i].'</td>';
                echo'<td class="text">'.$fix[$i].'</td>';
                echo'<td class="text">'.$lat[$i].'</td>';
                echo'<td class="text">'.$lng[$i].'</td>';
    
                if($report_type!='Person')
                {     
                    echo'<td class="text">'.$speed[$i].'</td>'; 
                    echo'<td class="text">'.$distance[$i].'</td>';
                }
                if($report_type=='Person')
                { 
                    //echo "cellName=".$cellname[$i]."<br>";
                    $tmpCellNameArr=explode(",",$cellname[$i]);
                    $bootStatus=$tmpCellNameArr[sizeof($tmpCellNameArr)-1];
                    echo'<td class="text">'.$bootStatus.'</td>';
                }
                echo'<td class="text">'.$ver[$i].'</td>';
                if($report_type!="Person")
                {
                    echo'<td class="text">'.$sup_v[$i].'</td>';
                    echo'<td class="text">'.$sig_str[$i].'</td>';    
                }

                if($report_type!='Person')
                { 
                    if($login == "iesplweb") 
                    {
                    echo'<td class="text">'.$io8[$i].'</td>';
                    }
                }

                if($account_id=='1594')
                {
                     echo'<td class="text">'.$ax[$i].'</td>';
                     echo'<td class="text">'.$ay[$i].'</td>';
                     echo'<td class="text">'.$az[$i].'</td>';
                     echo'<td class="text">'.$mx[$i].'</td>';
                     echo'<td class="text">'.$my[$i].'</td>';
                     echo'<td class="text">'.$mz[$i].'</td>';
                     echo'<td class="text">'.$bx[$i].'</td>';
                     echo'<td class="text">'.$by[$i].'</td>';
                     echo'<td class="text">'.$bz[$i].'</td>';
                } 
            echo'</tr>'; 
      
                ///////////For PDF Report Only For mobile application /////////            
                echo"<input TYPE=\"hidden\" VALUE=\"$sno\" NAME=\"temp[$i][SNo]\">";
                echo"<input TYPE=\"hidden\" VALUE=\"$sts[$i]\" NAME=\"temp[$i][STS]\">";
                echo"<input TYPE=\"hidden\" VALUE=\"$datetime[$i]\" NAME=\"temp[$i][DateTime]\">";
                
                if($report_type!='Person')
                {
                echo"<input TYPE=\"hidden\" VALUE=\"$vtype[$i]\" NAME=\"temp[$i][Vehicle Type]\">";
                $csv_string = $csv_string.$vtype[$i].",";
                }
                echo"<input TYPE=\"hidden\" VALUE=\"$msgtype[$i]\" NAME=\"temp[$i][MsgTp]\">";
                echo"<input TYPE=\"hidden\" VALUE=\"$fix[$i]\" NAME=\"temp[$i][Fix]\">";
                echo"<input TYPE=\"hidden\" VALUE=\"$lat[$i]\" NAME=\"temp[$i][Latitude]\">";
                echo"<input TYPE=\"hidden\" VALUE=\"$lng[$i]\" NAME=\"temp[$i][Longitude]\">";
               
                if($report_type!='Person')
                {
                echo"<input TYPE=\"hidden\" VALUE=\"$speed[$i]\" NAME=\"temp[$i][Speed]\">";
                echo"<input TYPE=\"hidden\" VALUE=\"$distance[$i]\" NAME=\"temp[$i][Distance]\">";
               
                }
                if($report_type=="Person")
                {
                echo"<input TYPE=\"hidden\" VALUE=\"$bootStatus\" NAME=\"temp[$i][Boot Status]\">";
            
                }
                echo"<input TYPE=\"hidden\" VALUE=\"$ver[$i]\" NAME=\"temp[$i][Version]\">";
               
                if($report_type!="Person")
                {
                    echo"<input TYPE=\"hidden\" VALUE=\"$sup_v[$i]\" NAME=\"temp[$i][Supply Voltage]\">";
                    echo"<input TYPE=\"hidden\" VALUE=\"$sig_str[$i]\" NAME=\"temp[$i][SgnlSt]\">";
              
                }
                if($account_id=='1594')
                {                   
                    echo"<input TYPE=\"hidden\" VALUE=\"$ax[$i]\" NAME=\"temp[$i][AX]\">";
                    echo"<input TYPE=\"hidden\" VALUE=\"$ay[$i]\" NAME=\"temp[$i][AY]\">";
                    echo"<input TYPE=\"hidden\" VALUE=\"$az[$i]\" NAME=\"temp[$i][AZ]\">";
                    echo"<input TYPE=\"hidden\" VALUE=\"$mx[$i]\" NAME=\"temp[$i][MX]\">";
                    echo"<input TYPE=\"hidden\" VALUE=\"$my[$i]\" NAME=\"temp[$i][MY]\">";
                    echo"<input TYPE=\"hidden\" VALUE=\"$mz[$i]\" NAME=\"temp[$i][MZ]\">";
                    echo"<input TYPE=\"hidden\" VALUE=\"$bx[$i]\" NAME=\"temp[$i][BX]\">";
                    echo"<input TYPE=\"hidden\" VALUE=\"$by[$i]\" NAME=\"temp[$i][BY]\">";
                    echo"<input TYPE=\"hidden\" VALUE=\"$bz[$i]\" NAME=\"temp[$i][BZ]\">";
                    
                    
                } 
                if($report_type!='Person')
                { 
                    if($login == "iesplweb") 
                    {
                        echo"<input TYPE=\"hidden\" VALUE=\"$io8[$i]\" NAME=\"temp[$i][IO8]\">";
                        
                    }
                }
               
                //$csv_string = $csv_string.$sno.','.$sts[$i].','.$datetime[$i].','.$msgtype[$i].','.$fix[$i].','.$lat[$i].','.$lng[$i].','.$bootStatus.','.$ver[$i]."\n";
          
        if( (($i>0) && ($imei[$i+1] != $imei[$i])) )
        {
        echo'</table>';
            //if($report_type=="Person")
            {
           echo'<input TYPE="hidden" VALUE="datalog" NAME="csv_type">';            
            }
        }                                          
        $sno++;
    }
                echo'</div>
                </center>
            </tbody>
        </table>
    </div>
    <br>
    <center>
    <input type="button" onclick="javascript:report_csv(\'src/php/report_getpdf_type3.php?size=1\');" value="Get PDF" class="noprint">&nbsp;
    <input type="button" onclick="javascript:report_csv_submit(\'src/php/report_csv.php\');" value="Get CSV" class="noprint">
    </center>
</form>';
                
echo'<form  name="csvForm" method="post" target="_blank">';                
$csv_string = ""; 
for($i=0;$i<sizeof($imei);$i++)
{                                              
    if(($i===0) || (($i>0) && ($imei[$i-1] != $imei[$i])) )
    {
        $sno = 1;
        $title="VTS DataLog For UserID : ".$userid[$i].", ".$report_type." : ".$vname[$i]." &nbsp;<font color=red>(".$imei[$i].")</font>";
        $csv_string = $csv_string.$title1."\n";
        $csv_string = $csv_string."SNo,STS,DateTime,";
        if($report_type=='Person')
        {
            //echo'<th class="text"><b><font size="1">Person Name</font></b></td>'; 
        }
        else
        {        
            $csv_string = $csv_string."VehTp,";
        }
        $csv_string = $csv_string."MsgTp,Fix,Latitude,Longitude,";
        if($report_type!='Person')
        {
            $csv_string = $csv_string."Speed,Distance,";
        }   
        if($report_type=='Person')
        { 
            $csv_string = $csv_string."Boot Status,";
        }                
        $csv_string = $csv_string."Version,";
        if($report_type!="Person")
        {
           $csv_string = $csv_string."SupplyV,SgnlSt,";
        }       
        if($account_id=='1594')
        {                               
            $csv_string = $csv_string."AX,AY,AZ,MX,MY,MZ,BX,BY,BZ,";
        } 
        if($report_type!='Person')
        {          
            if($login == "iesplweb") 
            {
                $csv_string = $csv_string."IO8,";
            }
        }
        $csv_string=substr($csv_string,0,-1);
        $csv_string = $csv_string."\n";
    } 
                $csv_string = $csv_string.$sno.','.$sts[$i].','.$datetime[$i].',';
                if($report_type!='Person')
                {
                $csv_string = $csv_string.$vtype[$i].",";
                }
                $csv_string = $csv_string.$msgtype[$i].",".$fix[$i].",".$lat[$i].",".$lng[$i].",";
                if($report_type!='Person')
                {
                $csv_string = $csv_string.$speed[$i].",".$distance[$i].",";
                }
                if($report_type=="Person")
                {
                $csv_string = $csv_string.$bootStatus[$i].",";
                }
                $csv_string = $csv_string.$ver[$i].",";
                if($report_type!="Person")
                {
                    $csv_string = $csv_string.$sup_v[$i].",".$sig_str[$i].",";
                }
                if($account_id=='1594')
                {                    
                    $csv_string = $csv_string.$ax[$i].",".$ay[$i].",".$az[$i].",".$mx[$i].",".$my[$i].",".$mz[$i].",".$bx[$i].",".$by[$i].",".$bz[$i].",";
                } 
                if($report_type!='Person')
                { 
                    if($login == "iesplweb") 
                    {
                        $csv_string = $csv_string.$io8[$i].",".$sig_str[$i].",";
                    }
                }
                $csv_string=substr($csv_string,0,-1);
                $csv_string = $csv_string."\n";
               
        if( (($i>0) && ($imei[$i+1] != $imei[$i])) )
        {
 
            //if($report_type=="Person")
            {
            echo'<input TYPE="hidden" VALUE="datalog" NAME="csv_type">';
            echo'<input TYPE="hidden" VALUE="'.$csv_string.'" NAME="csv_string">';           
            }
        }                                          
        $sno++;
    }
echo'</form>';
if(sizeof($imei)>0)
{
  echo'
  <script type="text/javascript">
    //alert("k");
    var table3Filters = {
      paging:true,
      sort_select:true,                
      col_1:"select",
      col_2:"select",
      col_3:"none",
      col_4:"none"
  }
  setFilterGrid("table1",0,table3Filters); 
  </script>
  ';
}

echo'<center>        
        <a href="javascript:showReportPrevPageNew();" class="back_css">
            &nbsp;<b>Back</b>
        </a>
    </center>';     
//echo "test";

?>