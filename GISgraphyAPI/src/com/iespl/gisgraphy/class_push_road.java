package com.iespl.gisgraphy;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrInputDocument;

public class class_push_road {
	//private ArrayList<DocumentPushRoad> dataRoad=new ArrayList<DocumentPushRoad>();
	
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
	public class_push_road(ArrayList<DocumentPushRoad> dataRoad){
		
		SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal1 = Calendar.getInstance();
		Date dateA = new Date();
		String date = inputDateFormat.format(dateA);
		
		if(gis_con.connection !=null){
			 try {
				 ArrayList<DocumentPushRoadSolr> solrRoad=new ArrayList<DocumentPushRoadSolr>();
				 //before push road ,first get max id and max feature_id from table openstreetmap
				 Statement stmt = gis_con.connection.createStatement();
				 String query="SELECT max(gid) as max_gid, max(id) as max_id FROM openstreetmap";
				 ResultSet rs1 = stmt.executeQuery(query);
				 while (rs1.next()){
				        //System.out.println("Name= " + rs.getString("name") + " Code= " + rs.getString("featureId"));
					 	maxgid=Long.parseLong(rs1.getString("max_gid"));
				    	maxid=Long.parseLong(rs1.getString("max_id"));
				 }
				 if(dataRoad.size()>0)
				 {
				    	for(DocumentPushRoad data : dataRoad){
				    		lat=data.getLat();
				    		lng=data.getLng();
				    		latlngseries=data.getLatlngseries();
				    		shapebase="SRID=4326;LINESTRING("+latlngseries+")";
				    		locationbase="SRID=4326;POINT("+lng+" "+lat+")";
				    		name=data.getName();
				    		streettype=data.getStreettype();
				    		textsearchname=name.toLowerCase();
				    		length=data.getLength();
				    		oneway=data.getOnewaytf();
				    		countrycode=data.getCountrycode();
				    		isin=data.getIsin();
				    		new_max_gid =maxgid+1;
					        new_max_id=maxid+1;
					        //===query to insert into openstreetmap table
					        String queryInsert="insert into OpenStreetMap (countrycode, gid, isIn, length, location, name, oneWay, openstreetmapId, partialSearchName, shape, streetType, textSearchName, id) values ('"+countrycode+"',"+new_max_gid+",'"+isin+"',"+length+",'"+locationbase+"','"+name+"','"+oneway+"',NULL, NULL,'"+shapebase+"','"+streettype+"','"+textsearchname+"',"+new_max_id+")";
						    System.out.println("SQL="+queryInsert);
							/*int okInsert=stmt.executeUpdate(queryInsert);
							//System.out.println(okInsert);
							if(okInsert==1)
							{
								System.out.println("Added Successfully");
								
							    if(oneway.equals('f'))
				                {
				                   oneway="false";
				                }
				                if(oneway.equals('t'))
				                {
				                   oneway="true";				                        
				                }
				                countryflagurl="/images/flags/IN.png";
				                String placetype="Street";
				                String featureid=new_max_gid.toString();
				                DocumentPushRoadSolr objSolr = new DocumentPushRoadSolr(countrycode,countryflagurl,featureid,isin,lat,length,lng,name,oneway,placetype,streettype);
				    			solrRoad.add(objSolr);
				    			
				    			maxgid++;
							    maxid++;
							}
					       */
					    }
			 	}
				 else
				 {
					 System.out.println("No Data to Push");
				 }
				    
			 }catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
			 try{
				 ArrayList<DocumentPushRoadSolr> solrInsertData=new ArrayList<DocumentPushRoadSolr>();
				 CommonsHttpSolrServer server = new CommonsHttpSolrServer("http://52.74.144.159:8080/solr");
				 server.setParser( new XMLResponseParser());
				 Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
				 if(solrInsertData.size()>0)
				 {
					 for(DocumentPushRoadSolr solrData : solrInsertData){
						  SolrInputDocument doc = new SolrInputDocument();
						  doc.addField("country_code", solrData.getCountrycode());
					      doc.addField("country_flag_url", solrData.getCountryflagurl());
					      doc.addField("feature_id", solrData.getFeatureid());
					      doc.addField("is_in", solrData.getIsin());
					      doc.addField("lat", solrData.getLat());
					      doc.addField("length", solrData.getLength());
					      doc.addField("lng", solrData.getLng());     
					      doc.addField("name", solrData.getName());
					      doc.addField("one_way", solrData.getOnewaytf());
					      doc.addField("placetype", solrData.getPlacetype());
					      doc.addField("street_type", solrData.getStreettype());				      
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
