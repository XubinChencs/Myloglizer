package priv.wangao.LogAnalysis.algorithm;

import org.json.JSONArray;
import org.json.JSONObject;

// 日志预处理，用于读取在文件中以不同方式存储的日志
public class Preprocessor {
	private static Preprocessor instance = new Preprocessor();
	
	private Preprocessor() {
	}

	public static Preprocessor getInstance() {
		return instance;
	}
	
	public String jsonLinuxOperationProcessor(String str) {
		JSONObject jo = new JSONObject(str);
        String line = jo.getJSONObject("_source").getString("@message");
        line = line.substring(20, line.length());
        line = line.substring(0, line.indexOf("2018")).replace(":", " ");
        return line;
	}
	
	public String jsonProcessor(String str) {
		JSONObject jo = new JSONObject(str);
        String line = jo.getString("message");
        String label = jo.getString("severity_label");
        if (label.equals("Warning")) {
        	System.out.println(line + " ======= " + label);
        }
        return line;
	}
	
	public LogEntry esJsonProcessor(String str) {
		JSONObject jo = new JSONObject(str);
        String line = jo.getString("message");
        JSONArray ja = new JSONArray();
        ja = (JSONArray) jo.get("_id");
        String id = (String)ja.get(0);
        ja = (JSONArray) jo.get("_type");
        String type = (String)ja.get(0);
        ja = (JSONArray) jo.get("_index");
        String index = (String)ja.get(0);
        LogEntry logEntry = new LogEntry(index, type, id, line, null, null);
		return logEntry;
	}
	
	public String syslogProcessor(String str) {
		return str;
		
	}
	
	public String webVpnProcessor(String str) {
		String[] split = str.split(" ");
		StringBuilder sb = new StringBuilder();
		sb.append(split[3]);
		for (int i = 4; i < split.length; i++) {
			sb.append(" ");
			sb.append(split[i]);
		}
		return sb.toString();
	}
	
	public String hierarchicalProcessor(String str) {
		StringBuilder sb =  new StringBuilder(str);
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == '[' || sb.charAt(i) == ']' || sb.charAt(i) == '(' || sb.charAt(i) == ')'
					||  sb.charAt(i) == '=' ) {
				if (i != sb.length() - 1) {
					sb.insert(i+1, ' ');
				}
				if (i != 0) {
					sb.insert(i, ' ');
					i++;
				}
			}
		}
		return sb.toString();
	}
	
}
