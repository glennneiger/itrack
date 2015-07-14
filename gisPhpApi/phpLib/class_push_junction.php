<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
class class_push_junction{
       function set_junction($data_junction_array)
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
                
                //------------------------PUSH CHAURAHA---------------------------
                

                $max_fid1=array();
                $max_id1=array();
                $result2 = pg_query($db_connection, "select max(r.featureid),max(r.id)  from road as r union select max(a.featureid),max(a.id) from adm as a union select max(p.featureid),max(p.id) from politicalentity as p union select max( g.featureid),max(g.id) from gisfeature as g union Select max(c.featureid),max(c.id) from country as c union Select max(rg.featureid),max(rg.id) from religious as rg union Select max(w.featureid),max(w.id) from waterbody as w union Select max(b.featureid),max(b.id) from bay as b union Select max(i.featureid),max(i.id) from ice as i union Select max(s.featureid),max(s.id) from stream as s union Select max(aq.featureid),max(aq.id) from aqueduc as aq union Select max(fh.featureid),max(fh.id) from fishingarea as fh union Select max(fj.featureid),max(fj.id) from fjord as fj union Select max(fl.featureid),max(fl.id) from falls as fl union Select max(gu.featureid),max(gu.id) from gulf as gu union Select max(po.featureid),max(po.id) from port as po union Select max(lk.featureid),max(lk.id) from lake as lk union Select max(pnd.featureid),max(pnd.id) from pond as pnd union Select max(ocn.featureid),max(ocn.id) from ocean as ocn union Select max(ref.featureid),max(ref.id) from reef as ref union Select max(se.featureid),max(se.id) from sea as se union Select max(sp.featureid),max(sp.id) from spring as sp union Select max(st.featureid),max(st.id) from strait as st union Select max(ms.featureid),max(ms.id) from marsh as ms union Select max(tnl.featureid),max(tnl.id) from tunnel as tnl union Select max(ap.featureid),max(ap.id) from amusepark as ap union Select max(mit.featureid),max(mit.id) from military as mit union Select max(prk.featureid),max(prk.id) from park as prk union Select max(ctn.featureid),max(ctn.id) from continent as ctn union Select max(fld.featureid),max(fld.id) from field as fld union Select max(mne.featureid),max(mne.id) from mine as mne union Select max(os.featureid),max(os.id) from oasis as os union Select max(rs.featureid),max(rs.id) from reserve as rs union Select max(ft.featureid),max(ft.id) from forest as ft union Select max(ct.featureid),max(ct.id) from city as ct union Select max(ctsd.featureid),max(ctsd.id) from citysubdivision as ctsd union Select max(rl.featureid),max(rl.id) from rail as rl union Select max(bl.featureid),max(bl.id) from building as bl union Select max(ar.featureid),max(ar.id) from airport as ar union Select max(tr.featureid),max(tr.id) from theater as tr union Select max(std.featureid),max(std.id) from stadium as std union Select max(atms.featureid),max(atms.id) from atm as atms union Select max(bnk.featureid),max(bnk.id) from bank as bnk union Select max(brg.featureid),max(brg.id) from bridge as brg union Select max(cem.featureid),max(cem.id) from cemetery as cem union Select max(bs.featureid),max(bs.id) from busstation as bs  union Select max(vi.featureid),max(vi.id) from vineyard as vi union Select max(cp.featureid),max(cp.id) from camp as cp union Select max(cs.featureid),max(cs.id) from casino as cs union Select max(cst.featureid),max(cst.id) from castle as cst union Select max(cus.featureid),max(cus.id) from customspost as cus union Select max(ch.featureid),max(ch.id) from courthouse as ch union Select max(ho.featureid),max(ho.id) from hospital as ho  union Select max(dm.featureid),max(dm.id) from dam as dm union Select max(plt.featureid),max(plt.id) from plantation as plt union Select max(frm.featureid),max(frm.id) from farm as frm union Select max(grd.featureid),max(grd.id) from garden as grd union Select max(hs.featureid),max(hs.id) from house as hs union Select max(ht.featureid),max(ht.id) from hotel as ht union Select max(qy.featureid),max(qy.id) from quay as qy union Select max(lib.featureid),max(lib.id) from library as lib union Select max(lh.featureid),max(lh.id) from lighthouse as lh union Select max(ml.featureid),max(ml.id) from mall as ml union Select max(fc.featureid),max(fc.id) from factory as fc union Select max(mil.featureid),max(mil.id) from mill as mil union Select max(mn.featureid),max(mn.id) from monument as mn union Select max(mol.featureid),max(mol.id) from mole as mol union Select max(met.featureid),max(met.id) from metrostation as met union Select max(mus.featureid),max(mus.id) from museum as mus union Select max(obs.featureid),max(obs.id) from observatorypoint as obs union Select max(opera.featureid),max(opera.id) from operahouse as opera union Select max(pk.featureid),max(pk.id) from parking as pk union Select max(po.featureid),max(po.id) from postoffice as po union Select max(pp.featureid),max(pp.id) from policepost as pp union Select max(pr.featureid),max(pr.id) from prison as pr union Select max(py.featureid),max(py.id) from pyramid as py union Select max(glf.featureid),max(glf.id) from golf as glf union Select max(rc.featureid),max(rc.id) from ranch as rc union Select max(rldt.featureid),max(rldt.id) from railroadstation as rldt union Select max(sch.featureid),max(sch.id) from school as sch union Select max(tow.featureid),max(tow.id) from tower as tow union Select max(zo.featureid),max(zo.id) from zoo as zo union Select max(lke.featureid),max(lke.id) from lake as lke union Select max(br.featureid),max(br.id) from bar as br union Select max(bh.featureid),max(bh.id) from beach as bh union Select max(clf.featureid),max(clf.id) from cliff as clf union Select max(cny.featureid),max(cny.id) from canyon as cny union Select max(crq.featureid),max(crq.id) from cirque as crq union Select max(dsrt.featureid),max(dsrt.id) from desert as dsrt union Select max(grg.featureid),max(grg.id) from gorge as grg union Select max(hl.featureid),max(hl.id) from hill as hl union Select max(il.featureid),max(il.id) from island as il union Select max(md.featureid),max(md.id) from mound as md union Select max(mnt.featureid),max(mnt.id) from mountain as mnt union Select max(vlc.featureid),max(vlc.id) from volcano as vlc union Select max(usea.featureid),max(usea.id) from undersea as usea union Select max(tre.featureid),max(tre.id) from tree as tre");
                while($row2 = pg_fetch_row($result2))
                {	
                        $max_fid1[]=$row2[0];
                        $max_id1[]=$row2[1];
                }
                $maxfid=max($max_fid1);
                $maxid=max($max_id1);

                $new_max_fid=$maxfid+1;
                $new_max_id=$maxid+1;
                
                $param_junction=array();	
                $solr_junction_param=array();
                
                /*
                $data_junction_array[]=array('lat'=>$lat,'lng'=>$lng,'location'=>$location,'feature_class'=>$feature_class,'feature_code'=>$feature_code,'placetype'=>$placetype,'countrycode'=>$countrycode,'source'=>$source,'countryname'=>$countryname);
                $countryCode="IN";
                $source="PERSONAL";
                $lat_new="26.479975014372428";
                $lng_new="80.30555248260498";
                $location_base='SRID=4326;POINT('.$lng_new.' '.$lat_new.')';
                $location_name="Gol Churaha, Kanpur";
                $feature_class="R";
                $feature_code="RDJCT"; //junction
                $placetype='ROAD';
                $country_name="India";
                $modificationdate=$date;	
                */
                foreach($data_junction_array as $data_junction)
                {
                    $lat_new=$data_junction['lat'];
                    $lng_new=$data_junction['lng'];                     
                    $location_base='SRID=4326;POINT('.$lng_new.' '.$lat_new.')';
                    $location_name=$data_junction['location'];
                    $feature_class=$data_junction['feature_class'];//R                   
                    $feature_code=$data_junction['feature_code']; //RDJCT
                    $placetype=$data_junction['placetype']; //ROAD
                    $countryCode=$data_junction['countrycode'];
                    $source=$data_junction['source'];
                    $country_name=$data_junction['countryname'];
                    
                    $modificationdate=$date;
                    $param_junction[]=array('countrycode'=>$countryCode,'source'=>$source,'location'=>$location_base,'name'=>$location_name,'featurecode'=>$feature_code,'featureclass'=>$feature_class,'modificationdate'=>$modificationdate,'lat'=>$lat_new,'lng'=>$lng_new,'country_name'=>$country_name,'placetype'=>$placetype);	
                }
                //===================================================================//	
                //print_r($param_junction);
                $push_junction_result=push_junction($param_junction,$new_max_fid,$new_max_id);//paramters
                return $push_junction_result;
            }
       }
       //=== into Database table road===//	
	function push_junction($param_junction_arr,$new_max_fid,$new_max_id)
	{
		global $db_connection;
		global $solr_junction_param;
                $result_return_db="Db insertion Not Successfull";
		foreach($param_junction_arr as $param_row)
		{
			
			$countryCode=$param_row['countryCode'];
			$source=$param_row['source'];			
			$location=$param_row['location'];
			$name=$param_row['name'];
			$featurecode=$param_row['featurecode'];
			$featureclass=$param_row['featureclass'];
			$modificationdate=$param_row['modificationdate'];
			
			$placetype=$param_row['placetype'];
			$country_name=$param_row['country_name'];
			
			$lat=$param_row['lat'];
			$lng=$param_row['lng'];
			
			$query_string="insert into Road (countrycode, featureid, featureclass, featurecode, location, name, modificationdate, source, id) values ('$countryCode', $new_max_fid, '$featureclass', '$featurecode', '$location', '$name', '$modificationdate', '$source', $new_max_id)";
			
			//echo $query_string;
			
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
				$solr_junction_param[]=array('country_code'=>$countryCode,'country_flag_url'=>"/images/flags/IN.png",'feature_id'=>$new_max_fid,'country_name'=>$country_name,'lat'=>$lat,'lng'=>$lng,'name'=>$name,'fully_qualified_name'=>$name,'feature_class'=>$featureclass,'feature_code'=>$featurecode,'placetype'=>$placetype,"timezone" => "","google_map_url"=>"","yahoo_map_url"=>"");
				
				$new_max_fid=$new_max_fid+1;
				$new_max_id=$new_max_id+1;		
			}
			
		}
		pg_close($db_connection);
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
				"country_name"   => "India",
				"feature_class"   => "R",
				"feature_code"   => "RD",
				"feature_id"   => "20000004",
				"fully_qualified_name"   => "Purani Shivli Road , Kanpur, UP",
				"google_map_url" =>"http://maps.google.com/maps?f=q&amp;ie=UTF-8&amp;iwloc=addr&amp;om=1&amp;z=12&amp;q=Purani+Shivli+Road+%2C+Kanpur%2C+UP&amp;ll=26.531777648925782,80.25174713134766",
				"lat"   => "26.50177764892578",		
				"lng"   => "80.25174713134766",
				"name"   => "Purani Shivli Road , Kanpur, UP, India",		
				"placetype"   => "Road",
				"timezone" => "",
				"yahoo_map_url"=>"http://maps.yahoo.com/broadband?mag=6&amp;mvt=m&amp;lon=80.25174713134766&amp;lat=26.50177764892578",
			),
		   
		  );*/
		  
		  $documents = array();
		  
		  foreach ( $solr_junction_param as $item => $fields ) {
			
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
			
		  //
		  //
		  // Load the documents into the index
		  // 
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
