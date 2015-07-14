<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
class class_push_road{  
    
    function set_road($data_road_array)
    {
        //============================//
        include('gis_connection.php');
        $flag_db_conn=0;
        if(!$db_connection){
          echo "Error : Unable to open database\n";
          return "Database connectivity Failed";
        } else {
          echo "Opened database successfully\n";
          $flag_db_conn=1;
        }
        if($flag_db_conn==1)
        {
            date_default_timezone_set("Asia/Kolkata");            
            $date= date("Y-m-d H:i:s");
            //---------------------PUSH ROAD--------------------------------------------------------------------
            //before push road ,first get max id and max feature_id from table openstreetmap
            $result1 = pg_query($db_connection, "SELECT max(gid) as max_gid, max(id) as max_id FROM openstreetmap");
            //$numrows = pg_numrows($result);
            //while($row = pg_fetch_row($result))
            $row1=pg_fetch_object($result1);
            $maxgid=$row1->max_gid;
            $maxid=$row1->max_id;
            //echo "Maxgid=".$maxgid;
            //echo " Maxid=".$maxid;
            //as table not auto increment the id so we need to increment the gid and id
            $new_max_gid=$maxgid+1;
            $new_max_id=$maxid+1;
            
            $param_road=array();	
            $solr_road_param=array();
            //=====================data from main form=======================================================================================================================================================================================//
            /*
                $data_road_array[]=array('lat'=>$lat,'lng'=>$lng,'lnglatseries'=>$lnglatseries,'location'=>$location,'streettype'=>$streettype,'length'=>$length,'oneway_t_f'=>$oneway_t_f,'countrycode'=>$countrycode,'isin'=>$isin);
                example:
                $countryCode="IN";
                $isIn="Kanpur, UP";
                $lat_new="26.50318739187539";
                $lng_new="80.24729490280151";
                $lng_lat_series="80.24960160255432 26.507440705757638,80.24931192398071 26.507167077436733,80.24919390678406  26.506960655640555, 80.24870038032532 26.506432598194902,80.24861991405487 26.506296382982715,80.24858236312866 26.505931541442614, 80.24843484163284 26.505456285593556,80.24816393852234 26.504457761849785,80.24800837039948 26.50403290656754, 80.24755775928497 26.503483232713055,80.24594843387604 26.501660769036295"; //comma sep
                $location_base='SRID=4326;POINT('.$lng_new.' '.$lat_new.')';
                $location_name="Gooba Garden Road";
                //$shape_base='SRID=4326;LINESTRING('.$lng_new.' '.$lat_new.','.$lng_new.' '.$lat_new.')';
                $shape_base='SRID=4326;LINESTRING('.$lng_lat_series.')';
                $streetType='ROAD';
                $textSearchName="gooba garden road" ;// in small caps of location name
                $road_length="450.0"; //meter in double
                $one_way='f'; //true or false             
             */
            //=================XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX==========================================================================================================================================//
            foreach($data_road_array as $data_road)
            {
               $lat_new=$data_road['lat'];
               $lng_new=$data_road['lng']; 
               $lng_lat_series=$data_road['lnglatseries'];
               $shape_base='SRID=4326;LINESTRING('.$lng_lat_series.')';
               $location_base='SRID=4326;POINT('.$lng_new.' '.$lat_new.')';
               $location_name=$data_road['location'];
               $streetType=$data_road['streettype'];
               $textSearchName=strtolower($location_name) ;// in small caps of location name
               $road_length=$data_road['length']; //meter in double
               $one_way=$data_road['oneway_t_f']; //true or false
               $countryCode=$data_road['countrycode'];
               $isIn=$data_road['isin'];
               $param_road[]=array('countryCode'=>$countryCode,'isIn'=>$isIn,'location_base'=>$location_base,'location_name'=>$location_name,'shape_base'=>$shape_base,'streetType'=>$streetType,'textSearchName'=>$textSearchName,'lat'=>$lat_new,'lng'=>$lng_new,'length'=>$road_length,'oneway'=>$one_way);                             
            }
            //print_r($param_road);
            $push_road_result=push_road($param_road,$new_max_gid,$new_max_id);//paramteres 
            return $push_road_result;
        }
    }
    //=== into Database table openstreetmap===//
    function push_road($param_road_arr,$new_max_gid,$new_max_id)
    {
        global $db_connection;
        global $solr_road_param;
        $result_return_db="Db insertion Not Successfull";
        //$result_return_solr="Solr Insertion Not Successfull";
        foreach($param_road_arr as $param_row)
        {
            $countryCode=$param_row['countryCode'];
            $isIn=$param_row['isIn'];			
            $location_base=$param_row['location_base'];
            $location_name=$param_row['location_name'];
            $shape_base=$param_row['shape_base'];
            $streetType=$param_row['streetType'];
            $textSearchName=$param_row['textSearchName'];
            $length=$param_row['length'];
            $one_way=$param_row['oneway'];
            $lat=$param_row['lat'];
            $lng=$param_row['lng'];

            $query_string="insert into OpenStreetMap (countrycode, gid, isIn, length, location, name, oneWay, openstreetmapId, partialSearchName, shape, streetType, textSearchName, id) values ('$countryCode', $new_max_gid, '$isIn', '$length', '$location_base', '$location_name', '$one_way', NULL, NULL, '$shape_base', '$streetType', '$textSearchName', $new_max_id)";
            //echo "query_string=".$query_string;
            $result_insert=pg_query($db_connection,$query_string);


            if(!$result_insert)
            {
              echo pg_last_error($db_connection);
            } 
            else 
            {
               //echo "Records created successfully\n";
               $result_return_db="DB Records created successfully";
               //part 2 added in Solr Apache xml database
               //making array to save in solr db
                if($one_way=='f')
                {
                        $one_way='false';
                }
                if($one_way=='t')
                {
                        $one_way='true';
                }
                $solr_road_param[]=array('country_code'=>$countryCode,'country_flag_url'=>"/images/flags/IN.png",'feature_id'=>$new_max_gid,'is_in'=>$isIn,'lat'=>$lat,'length'=>$length,'lng'=>$lng,'name'=>$location_name,'one_way'=>$one_way,'placetype'=>'Street','street_type'=>$streetType);
                $new_max_gid=$new_max_gid+1;
                $new_max_id=$new_max_id+1;		
            }		
        }

        pg_close($db_connection);
        //print_r($solr_road_param);
        //------------------Inserting into Solr database
        require_once( 'Apache/Solr/Service.php' );
        // 
        // 
        // Try to connect to the named server, port, and url
        // 
        $solr = new Apache_Solr_Service( '127.0.0.1', '8080', '/solr' );
        if ( ! $solr->ping() ) {
              echo 'Solr service not responding.';
              exit;
        }
        /*
              $docs = array(
              'doc_no1' => array(
                "country_code"   => "IN",
                      "country_flag_url"   => "/images/flags/IN.png",
                      "feature_id"   => "100220618",
                      "is_in"   => "Kanpur UP India",
                      "lat"   => "26.4821117235667",
                      "length"   => "0.0",
                      "lng"   => "80.3013038635254",
                      "name"   => "Rawatpur Crossing",
                      "one_way"   => "false",
                      "placetype"   => "Street",
                      "street_type" => "ROAD",
              ),

        );*/

        $documents = array();
        foreach ( $solr_road_param as $item => $fields ) {
        $part = new Apache_Solr_Document();
        foreach ( $fields as $key => $value ) {
            if ( is_array( $value ) ) {
                  foreach ( $value as $data ) {
                    $part->setMultiValue( $key, $data );
                  }
            }
            else {
                  $part->$key = $value;
            }
        }
        $documents[] = $part;
      }
        // Load the documents into the index      
        try {
              $solr->addDocuments( $documents );
              $solr->commit();
              $solr->optimize();
        }
        catch ( Exception $e ) {
              echo $e->getMessage();
        }
        return $result_return_db;
    }
}
?>
