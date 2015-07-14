package com.iespl.gisgraphy.example;
/* for more information see 
 * https://books.google.co.in/books?id=cpC_2Z0QwvMC&pg=PT160&lpg=PT160&dq=solr+3.1+code+to+insert+document+in+java&source=bl&ots=XfHiCb6nCz&sig=TqDqKT_AHG_lUZrqrW1fPWjSbXc&hl=en&sa=X&ei=wyKeVfHnIY-jugSdiYaoCA&ved=0CDcQ6AEwBA#v=onepage&q=solr%203.1%20code%20to%20insert%20document%20in%20java&f=false
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrInputDocument;



public class testpush {
	public static void main(String[] args) throws Exception{
		
	   
		
		CommonsHttpSolrServer server = new CommonsHttpSolrServer("http://52.74.144.159:8080/solr");
		server.setParser( new XMLResponseParser());
		
		  SolrInputDocument doc = new SolrInputDocument();
		
		  doc.addField("country_code", "IN");
	      doc.addField("country_flag_url", "/images/flags/IN.png");
	      doc.addField("country_name", "India");
	      doc.addField("feature_class", "R");
	      doc.addField("feature_code", "RDJCT");
	      doc.addField("feature_id", "20000006");
	      doc.addField("fully_qualified_name", "Gol Churaha, Kanpur,UP");     
	      doc.addField("lat", "26.479975014372428");
	      doc.addField("lng", "80.30555248260498");
	      doc.addField("name", "Gol Churaha, Kanpur,UP");
	      doc.addField("placetype", "Road");
	      doc.addField("timezone", "");
		
	      Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
	      docs.add(doc);
		server.add(docs);
		server.commit();
		
		
	}
	
}
