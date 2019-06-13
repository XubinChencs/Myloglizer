package priv.wangao.LogAnalysis.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.avro.file.DataFileStream;

import net.sf.json.JSONObject;

public class DataHelper {

	private static DataHelper instance = new DataHelper();

	public static DataHelper getInstance() {
		return instance;
	}

	private DataHelper() {

	}

	public JSONObject stringToJson(String data) {
		return JSONObject.fromObject(data);
	}

	public List<String> jsonsToList(List<String> data, String... cols) {
		List<String> result = new ArrayList<String>();
		data.forEach(item -> {
			JSONObject json = JSONObject.fromObject(item);
			StringBuilder curLine = new StringBuilder();
			for (String col : cols) {
				// System.out.println(json.getString(col).substring(35));
				curLine.append(json.getString(col));
			}
			result.add(curLine.toString());
		});
		return result;
	}

	public String getUTCDate() {
		String dateStr = "";
		Date date = null;
		String months = "", days = "", hours = "", sec = "", minutes = "";
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		StringBuffer UTCTimeBuffer = new StringBuffer();
		Calendar cal = Calendar.getInstance();
		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
		cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		if (month < 10) {
			months = "0" + String.valueOf(month);
		} else {
			months = String.valueOf(month);
		}
		if (minute < 10) {
			minutes = "0" + String.valueOf(minute);
		} else {
			minutes = String.valueOf(minute);
		}
		if (day < 10) {
			days = "0" + String.valueOf(day);
		} else {
			days = String.valueOf(day);
		}
		if (hour < 10) {
			hours = "0" + String.valueOf(hour);
		} else {
			hours = String.valueOf(hour);
		}
		if (second < 10) {
			sec = "0" + String.valueOf(second);
		} else {
			sec = String.valueOf(second);
		}
		UTCTimeBuffer.append(year).append("-").append(months).append("-").append(days);
		UTCTimeBuffer.append("T").append(hours).append(":").append(minutes).append(":").append(sec).append("Z");
		try {
			date = format.parse(UTCTimeBuffer.toString());
			dateStr = format.format(date).replaceAll("-", ".");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateStr;
	}

}
