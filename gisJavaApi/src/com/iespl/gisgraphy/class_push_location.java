package com.iespl.gisgraphy;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrInputDocument;

public class class_push_location {
	//private ArrayList<DocumentPushJunction> dataJunction=new ArrayList<DocumentPushJunction>();
	
	gis_connection gis_con = new gis_connection();
	Long new_max_gid;
	Long new_max_id;
	Long maxgid;
	Long maxid;
	String lat;
	String lng;
	String latlngseries;
	String name;
	String streettype;
	String length;
	String oneway;
	String countrycode;
	String isin;
	String shapebase;
	String locationbase;
	String textsearchname;
	String countryflagurl;
	String featureclass;
	String featurecode;
	String placetypeA;
	String source;
	String countryname;
	String modificationdate;
	
	ArrayList<Long> max_fid1 =new ArrayList<Long>();
	ArrayList<Long> max_id1 =new ArrayList<Long>();
	public class_push_location(ArrayList<DocumentPushJunction> dataJunction){
		
		SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal1 = Calendar.getInstance();
		Date dateA = new Date();
		String date = inputDateFormat.format(dateA);
		
		if(gis_con.connection !=null){
			 try {
				 ArrayList<DocumentPushJunctionSolr> solrJunction=new ArrayList<DocumentPushJunctionSolr>();
				 //before push junction ,first get max id and max feature_id from table road
				 Statement stmt = gis_con.connection.createStatement();
				 String query="select max(r.featureid),max(r.id)  from road as r union select max(a.featureid),max(a.id) from adm as a union select max(p.featureid),max(p.id) from politicalentity as p union select max( g.featureid),max(g.id) from gisfeature as g union Select max(c.featureid),max(c.id) from country as c union Select max(rg.featureid),max(rg.id) from religious as rg union Select max(w.featureid),max(w.id) from waterbody as w union Select max(b.featureid),max(b.id) from bay as b union Select max(i.featureid),max(i.id) from ice as i union Select max(s.featureid),max(s.id) from stream as s union Select max(aq.featureid),max(aq.id) from aqueduc as aq union Select max(fh.featureid),max(fh.id) from fishingarea as fh union Select max(fj.featureid),max(fj.id) from fjord as fj union Select max(fl.featureid),max(fl.id) from falls as fl union Select max(gu.featureid),max(gu.id) from gulf as gu union Select max(po.featureid),max(po.id) from port as po union Select max(lk.featureid),max(lk.id) from lake as lk union Select max(pnd.featureid),max(pnd.id) from pond as pnd union Select max(ocn.featureid),max(ocn.id) from ocean as ocn union Select max(ref.featureid),max(ref.id) from reef as ref union Select max(se.featureid),max(se.id) from sea as se union Select max(sp.featureid),max(sp.id) from spring as sp union Select max(st.featureid),max(st.id) from strait as st union Select max(ms.featureid),max(ms.id) from marsh as ms union Select max(tnl.featureid),max(tnl.id) from tunnel as tnl union Select max(ap.featureid),max(ap.id) from amusepark as ap union Select max(mit.featureid),max(mit.id) from military as mit union Select max(prk.featureid),max(prk.id) from park as prk union Select max(ctn.featureid),max(ctn.id) from continent as ctn union Select max(fld.featureid),max(fld.id) from field as fld union Select max(mne.featureid),max(mne.id) from mine as mne union Select max(os.featureid),max(os.id) from oasis as os union Select max(rs.featureid),max(rs.id) from reserve as rs union Select max(ft.featureid),max(ft.id) from forest as ft union Select max(ct.featureid),max(ct.id) from city as ct union Select max(ctsd.featureid),max(ctsd.id) from citysubdivision as ctsd union Select max(rl.featureid),max(rl.id) from rail as rl union Select max(bl.featureid),max(bl.id) from building as bl union Select max(ar.featureid),max(ar.id) from airport as ar union Select max(tr.featureid),max(tr.id) from theater as tr union Select max(std.featureid),max(std.id) from stadium as std union Select max(atms.featureid),max(atms.id) from atm as atms union Select max(bnk.featureid),max(bnk.id) from bank as bnk union Select max(brg.featureid),max(brg.id) from bridge as brg union Select max(cem.featureid),max(cem.id) from cemetery as cem union Select max(bs.featureid),max(bs.id) from busstation as bs  union Select max(vi.featureid),max(vi.id) from vineyard as vi union Select max(cp.featureid),max(cp.id) from camp as cp union Select max(cs.featureid),max(cs.id) from casino as cs union Select max(cst.featureid),max(cst.id) from castle as cst union Select max(cus.featureid),max(cus.id) from customspost as cus union Select max(ch.featureid),max(ch.id) from courthouse as ch union Select max(ho.featureid),max(ho.id) from hospital as ho  union Select max(dm.featureid),max(dm.id) from dam as dm union Select max(plt.featureid),max(plt.id) from plantation as plt union Select max(frm.featureid),max(frm.id) from farm as frm union Select max(grd.featureid),max(grd.id) from garden as grd union Select max(hs.featureid),max(hs.id) from house as hs union Select max(ht.featureid),max(ht.id) from hotel as ht union Select max(qy.featureid),max(qy.id) from quay as qy union Select max(lib.featureid),max(lib.id) from library as lib union Select max(lh.featureid),max(lh.id) from lighthouse as lh union Select max(ml.featureid),max(ml.id) from mall as ml union Select max(fc.featureid),max(fc.id) from factory as fc union Select max(mil.featureid),max(mil.id) from mill as mil union Select max(mn.featureid),max(mn.id) from monument as mn union Select max(mol.featureid),max(mol.id) from mole as mol union Select max(met.featureid),max(met.id) from metrostation as met union Select max(mus.featureid),max(mus.id) from museum as mus union Select max(obs.featureid),max(obs.id) from observatorypoint as obs union Select max(opera.featureid),max(opera.id) from operahouse as opera union Select max(pk.featureid),max(pk.id) from parking as pk union Select max(po.featureid),max(po.id) from postoffice as po union Select max(pp.featureid),max(pp.id) from policepost as pp union Select max(pr.featureid),max(pr.id) from prison as pr union Select max(py.featureid),max(py.id) from pyramid as py union Select max(glf.featureid),max(glf.id) from golf as glf union Select max(rc.featureid),max(rc.id) from ranch as rc union Select max(rldt.featureid),max(rldt.id) from railroadstation as rldt union Select max(sch.featureid),max(sch.id) from school as sch union Select max(tow.featureid),max(tow.id) from tower as tow union Select max(zo.featureid),max(zo.id) from zoo as zo union Select max(lke.featureid),max(lke.id) from lake as lke union Select max(br.featureid),max(br.id) from bar as br union Select max(bh.featureid),max(bh.id) from beach as bh union Select max(clf.featureid),max(clf.id) from cliff as clf union Select max(cny.featureid),max(cny.id) from canyon as cny union Select max(crq.featureid),max(crq.id) from cirque as crq union Select max(dsrt.featureid),max(dsrt.id) from desert as dsrt union Select max(grg.featureid),max(grg.id) from gorge as grg union Select max(hl.featureid),max(hl.id) from hill as hl union Select max(il.featureid),max(il.id) from island as il union Select max(md.featureid),max(md.id) from mound as md union Select max(mnt.featureid),max(mnt.id) from mountain as mnt union Select max(vlc.featureid),max(vlc.id) from volcano as vlc union Select max(usea.featureid),max(usea.id) from undersea as usea union Select max(tre.featureid),max(tre.id) from tree as tre";
				 //System.out.println("SQL="+query);
				 ResultSet rs1 = stmt.executeQuery(query);
				 //System.out.println("DEBUG0");
				 while (rs1.next()){
					 //System.out.println("DEBUG1");
					 max_fid1.add(rs1.getLong(1));
					 max_id1.add(rs1.getLong(2)); 
					 //System.out.println("DEBUG2");
				 }
				 maxgid= Collections.max(max_fid1);
				 maxid= Collections.max(max_id1);
				 //System.out.println("Sizeof data="+dataJunction.size());
				 if(dataJunction.size()>0)
				 	{ 
					 	for(DocumentPushJunction data : dataJunction){
				    		lat=data.getLat();
				    		lng=data.getLng();				    		
				    		locationbase="SRID=4326;POINT("+lng+" "+lat+")";
				    		name=data.getName();				    		
				    		featureclass =data.getFeatureclass();
				    		featurecode =data.getFeaturecode();
				    		placetypeA=data.getPlacetype();
				    		countrycode=data.getCountrycode();
				    		source=data.getSource();
				    		countryname=data.getCountryname();
				    		modificationdate=date;				    		
				    		new_max_gid =maxgid+1;
					        new_max_id=maxid+1;
					        //===query to insert into road table
					        String queryInsert="insert into gisfeature (countrycode, featureid, featureclass, featurecode, location, name, modificationdate, source, id) values ('"+countrycode+"',"+new_max_gid+",'"+featureclass+"','"+featurecode+"','"+locationbase+"','"+name+"','"+modificationdate+"','"+source+"',"+new_max_id+")";
					        
					        //System.out.println("SQL="+queryInsert);
							int okInsert=stmt.executeUpdate(queryInsert);
							//System.out.println(okInsert);
							if(okInsert==1)
							{
								System.out.println("Added Successfully");
								
				                countryflagurl="/images/flags/IN.png";				                
				                String featureid=new_max_gid.toString();
				                String fullyqualifiedname=name;
				                String timezone="";
				                String googlemapurl="";
				                String yahoomapurl="";
				                DocumentPushJunctionSolr objSolr = new DocumentPushJunctionSolr(countrycode,countryflagurl,featureid,countryname,lat,lng,name,fullyqualifiedname,featureclass,featurecode,placetypeA,timezone,googlemapurl,yahoomapurl);
				    			solrJunction.add(objSolr);
				    			
				    			maxgid++;
							    maxid++;
							}
					       
					    }
				 }
				 else
				 {
					 System.out.println("No Data to add!");
				 }
				    
			 }catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
			 try{
				 ArrayList<DocumentPushJunctionSolr> solrInsertData=new ArrayList<DocumentPushJunctionSolr>();
				 CommonsHttpSolrServer server = new CommonsHttpSolrServer("http://52.74.144.159:8080/solr");
				 server.setParser( new XMLResponseParser());
				 Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
				 System.out.println("inside solr"+solrInsertData.size());
				 if(solrInsertData.size() >0)
				 {
					 for(DocumentPushJunctionSolr solrData : solrInsertData){
						 System.out.println("inside for loop");
						  SolrInputDocument doc = new SolrInputDocument();
						  doc.addField("country_code", solrData.getCountrycode());
					      doc.addField("country_flag_url", solrData.getCountryflagurl());
					      doc.addField("country_name", solrData.getCountryname());
					      doc.addField("feature_class", solrData.getFeatureclass());
					      doc.addField("feature_code", solrData.getFeaturecode());
					      doc.addField("feature_id", solrData.getFeatureid());
					      doc.addField("fully_qualified_name", solrData.getFullyqualifiedname());
					      doc.addField("google_map_url", solrData.getGooglemapurl());
					      doc.addField("lat", solrData.getLat()); 
					      doc.addField("lng", solrData.getLng());     
					      doc.addField("name", solrData.getName());				      
					      doc.addField("placetype", solrData.getPlacetype());
					      doc.addField("timezone", solrData.getTimezone());	
					      doc.addField("yahoo_map_url", solrData.getYahoomapurl());
					      docs.add(doc);
					 }
					 server.add(docs);
					 server.commit();
				 }
			 }catch(Exception es){
				 es.printStackTrace();
			 }
		}
	}
}
