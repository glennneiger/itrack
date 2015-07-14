package com.iespl.gisgraphy.example;
import com.iespl.gisgraphy.*;
import java.util.ArrayList;

import java.util.ArrayList;

public class ex_push_junction {

	public static void main(String[] argv){
		//DocumentPushJunction(String lat,String lng,String name,String featureclass,String featurecode,String placetype,String countrycode,String source,String countryname){
		ArrayList<DocumentPushJunction> dataJunction = new ArrayList<DocumentPushJunction>();
		String lat;String lng;String name;String featureclass;String featurecode;String placetype;String countrycode;String source;String countryname;
		countrycode="IN"; //alter but FIXED
		source="PERSONAL"; //FIXED
		lat="26.479975014372428"; //alter
		lng="80.30555248260498"; //alter
		name="Gol Churaha, Kanpur"; //alter
		featureclass="R"; //(FIXED)
		featurecode="RDJCT"; //(FIXED)junction
		placetype="ROAD"; //(FIXED) please note ROAD is not actual road and junction type is road and is fixed
		countryname="India";// (alter but fixed)
		//this you will save in loop
		DocumentPushJunction objJunction= new DocumentPushJunction(lat,lng, name, featureclass, featurecode, placetype, countrycode, source, countryname);
		dataJunction.add(objJunction);
		//end of loop
		
		System.out.println("Pushing Junction");
		class_push_junction jct_push= new class_push_junction(dataJunction);

		
	}
}