package com.iespl.gisgraphy.example;
import com.iespl.gisgraphy.*;
import java.util.ArrayList;

import java.util.ArrayList;

public class ex_push_road {

	public static void main(String[] argv){
		//DocumentPushRoad(String lat,String lng,String latlngseries,String name,String streettype,String length,String onewaytf,String countrycode,String isin)
		ArrayList<DocumentPushRoad> dataRoad = new ArrayList<DocumentPushRoad>();
		String lat;String lng;String latlngseries;String name;String streettype;String length;String onewaytf;String countrycode;String isin;
		countrycode="IN"; //fixed but alter
		isin="Kanpur, UP"; //alter
		lat="26.50318739187539"; //alter
		lng="80.24729490280151"; //alter
		latlngseries="80.24960160255432 26.507440705757638,80.24931192398071 26.507167077436733,80.24919390678406  26.506960655640555, 80.24870038032532 26.506432598194902,80.24861991405487 26.506296382982715,80.24858236312866 26.505931541442614, 80.24843484163284 26.505456285593556,80.24816393852234 26.504457761849785,80.24800837039948 26.50403290656754, 80.24755775928497 26.503483232713055,80.24594843387604 26.501660769036295"; //alter comma sep
		name="Gooba Garden Road";
		streettype="ROAD";
		length="450.0"; //meter in double
		onewaytf="f"; //true or false
		//this you will save in loop
		DocumentPushRoad objRoad= new DocumentPushRoad( lat, lng, latlngseries, name, streettype, length, onewaytf, countrycode, isin);
		dataRoad.add(objRoad);
		//end of loop
		
		System.out.println("Pushing Road");
		class_push_road jct_push= new class_push_road(dataRoad);

		
	}
}