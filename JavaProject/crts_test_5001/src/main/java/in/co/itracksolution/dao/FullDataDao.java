package in.co.itracksolution.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

import in.co.itracksolution.model.FullData;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class FullDataDao {

	protected PreparedStatement insertStatement1, deleteStatement1, selectbyImeiAndDateStatement1, selectbyImeiAndDateTimeSliceStatement1;
	protected PreparedStatement insertStatement2, deleteStatement2, selectbyImeiAndDateStatement2, selectbyImeiAndDateTimeSliceStatement2;
	protected Session session;
	 
	public FullDataDao(Session session) {
		super();
		this.session = session;
		prepareStatement1();
		prepareStatement2();
	}

	protected void prepareStatement1(){
		insertStatement1 = session.prepare(getInsertStatement1());
		deleteStatement1 = session.prepare(getDeleteStatement1());
		selectbyImeiAndDateStatement1 = session.prepare(getSelectByImeiAndDateStatement1());
		selectbyImeiAndDateTimeSliceStatement1 = session.prepare(getSelectByImeiAndDateTimeSliceStatement1());
	}

	protected void prepareStatement2(){
		insertStatement2 = session.prepare(getInsertStatement2());
		deleteStatement2 = session.prepare(getDeleteStatement2());
		selectbyImeiAndDateStatement2 = session.prepare(getSelectByImeiAndDateStatement2());
		selectbyImeiAndDateTimeSliceStatement2 = session.prepare(getSelectByImeiAndDateTimeSliceStatement2());
	}

	protected String getInsertStatement1(){
		return "INSERT INTO "+FullData.TABLE_NAME1+
				" (imei, date, dtime, data, stime)"
				+ " VALUES ("+
				"?,?,?,?,?);";
	}
	
	protected String getInsertStatement2(){
		return "INSERT INTO "+FullData.TABLE_NAME2+
				" (imei, date, dtime, data, stime)"
				+ " VALUES ("+
				"?,?,?,?,?);";
	}
	
	protected String getDeleteStatement1(){
		return "DELETE FROM "+FullData.TABLE_NAME1+" WHERE imei = ? AND date = ? AND dtime = ?;";
	}

	protected String getDeleteStatement2(){
		return "DELETE FROM "+FullData.TABLE_NAME2+" WHERE imei = ? AND date = ? AND stime = ?;";
	}

	protected String getSelectByImeiAndDateStatement1(){
		return "SELECT * FROM "+FullData.TABLE_NAME1+" WHERE imei = ? AND date = ?;";
	}

	protected String getSelectByImeiAndDateStatement2(){
		return "SELECT * FROM "+FullData.TABLE_NAME2+" WHERE imei = ? AND date = ?;";
	}

	protected String getSelectByImeiAndDateTimeSliceStatement1(){
		return "SELECT * FROM "+FullData.TABLE_NAME1+" WHERE imei = ? AND date IN ? AND dtime >= ? AND dtime <= ? ;";
	}
	
	protected String getSelectByImeiAndDateTimeSliceStatement2(){
		return "SELECT * FROM "+FullData.TABLE_NAME2+" WHERE imei = ? AND date IN ? AND stime >= ? AND stime <= ? ;";
	}
	
	public void insert(FullData data){
		BoundStatement boundStatement1 = new BoundStatement(insertStatement1);
		BoundStatement boundStatement2 = new BoundStatement(insertStatement2);
		session.execute(boundStatement1.bind(
				data.getImei(),
				data.getDate(),
				data.getDTime(),
				data.getData(),
				data.getSTime()
				));
		session.execute(boundStatement2.bind(
				data.getImei(),
				data.getDate(),
				data.getDTime(),
				data.getData(),
				data.getSTime()
				));
	}
	
	public void delete(FullData data)
	{
		BoundStatement boundStatement1 = new BoundStatement(deleteStatement1);
		BoundStatement boundStatement2 = new BoundStatement(deleteStatement2);
		session.execute(boundStatement1.bind(data.getImei(), data.getDate(), data.getDTime()));
		session.execute(boundStatement2.bind(data.getImei(), data.getDate(), data.getDTime()));
	}
	
	public ResultSet selectByImeiAndDate(String imei, String date)
	{
		BoundStatement boundStatement = new BoundStatement(selectbyImeiAndDateStatement1);
		ResultSet rs = session.execute(boundStatement.bind(imei, date));
		return rs;
	}
	
	public ArrayList<FullData> selectByImeiAndDateTimeSlice(String imei, String startDateTime, String endDateTime, Boolean deviceTime)
	{
		BoundStatement boundStatement = (deviceTime)?new BoundStatement(selectbyImeiAndDateTimeSliceStatement1):new BoundStatement(selectbyImeiAndDateTimeSliceStatement2);
		ArrayList dateList = new ArrayList();

		int days = 1;
		LocalDate sDate = new LocalDate();
		LocalDate eDate = new LocalDate();
		long sEpoch=0, eEpoch=0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {	
			sDate = LocalDate.parse(startDateTime.substring(0,10));
			eDate = LocalDate.parse(endDateTime.substring(0,10));
			sEpoch = sdf.parse(startDateTime).getTime();
			eEpoch = sdf.parse(endDateTime).getTime();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		Date sDateTime = new Date(sEpoch); // TODO TimeZone
		Date eDateTime = new Date(eEpoch);
		//System.out.println("sDateTime = "+sdf.format(sDateTime));
		//System.out.println("eDateTime = "+sdf.format(eDateTime));

		days = Days.daysBetween(sDate, eDate).getDays();
		for (int i=0; i<days+1; i++)
		{
			LocalDate d = sDate.plusDays(i);
			dateList.add(d.toString("yyyy-MM-dd"));
		}	

		//System.out.println(dateList);
		ResultSet rs = session.execute(boundStatement.bind(imei, dateList, sDateTime, eDateTime));
		List<Row> rowList = rs.all();
	
		FullData fullData = new FullData();
		ArrayList<FullData> fullDataList = new ArrayList<FullData>();

		//ArrayList<ArrayList> parsedList = new ArrayList<ArrayList>();
		String data;
		final String DELIMITER = ";";
		for (Row row : rowList)
		{
			//ArrayList parsedRow = new ArrayList();
			//parsedRow.add(0,row.getString("imei"));
			//parsedRow.add(1,row.getDate("dtime"));
			//parsedRow.add(2,row.getDate("stime"));
			fullData.setImei(row.getString("imei"));
			fullData.setDTime(row.getDate("dtime"));
			fullData.setSTime(row.getDate("stime"));
			
			data = row.getString("data");
			//System.out.println("data = "+data);
			String[] tokens = data.split(DELIMITER);
			int i = 0;
			for(String token : tokens)
			{
				//parsedRow.add(i++,token);
				fullData.pMap.put(fullData.fullParams[i++], token);
			}
			//parsedList.add(parsedRow);
			fullDataList.add(fullData);
		}

		return fullDataList;
	}
}