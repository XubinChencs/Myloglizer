package priv.wangao.LogAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.json.JSONObject;

import priv.wangao.LogAnalysis.util.IOHelper;

public class FileDiv {
	
	static void diffSource() {
		String path = "E:\\logData\\test-with label\\bd33-40\\";
		Map<String, List<String>> sourceMap = new HashMap<String, List<String>>(); 
		List<String> list = IOHelper.getInstance().readFromFile(path + "20190323.log");
    	for (int i = 0; i < list.size(); i++) {	
			JSONObject jo = new JSONObject(list.get(i));
    		String source = jo.getString("logsource");
    		if (!sourceMap.containsKey(source)) {
    			sourceMap.put(source, new ArrayList<String>());
    		}
    		sourceMap.get(source).add(list.get(i));
    	}
    	
    	for (String key : sourceMap.keySet()) {
    		IOHelper.getInstance().writeToFile(sourceMap.get(key), path + "分成不同日志源\\" + key + ".log", true);
    	}
	}
	
	static void diffLabel() {
		String path = "E:\\logData\\test-with label\\bd33-40\\";
		Map<String, Integer> Labels = new HashMap<String, Integer>(); 
		List<String> list = IOHelper.getInstance().readFromFile(path + "20190528.log");
    	for (int i = 0; i < list.size(); i++) {	
			JSONObject jo = new JSONObject(list.get(i));
    		String label = jo.getString("severity_label");
    		if (!Labels.containsKey(label)) {
    			Labels.put(label, new Integer(0));
    		}
    		Integer temp = Labels.get(label) + 1;
    		Labels.put(label, temp);
    	}
    	System.out.println("all: " + list.size());
    	for (String label : Labels.keySet()) {
    		System.out.println(label + ": " + Labels.get(label));
    	}
	}
	
	static void maxNormalSeq() {
		String path = "E:\\logData\\test-with label\\bd33-40\\分成不同日志源\\";
		List<String> list = IOHelper.getInstance().readFromFile(path + "bd35.log");
		int head = 0;
		int begin = 0;
		int end = 0;
		int maxLen = 0;
		for (int i = 0; i < list.size(); i++) {
			JSONObject jo = new JSONObject(list.get(i));
    		String label = jo.getString("severity_label");
    		if (label.equals("Informational")) {
    			continue;
    		} else {
    			if (i - head > maxLen) {
    				maxLen = i - head;
    				begin = head;
    				end = i - 1;
    			}
				head = i + 1;
    		}
		}
		
		System.out.println("begin: " + begin);
		System.out.println("end: " + end);
		System.out.println("maxLen: " + maxLen);
	}
	
	static void extractLogs(int begin, int end) {
		String path = "E:\\logData\\test-with label\\bd33-40\\分成不同日志源\\";
		List<String> list = IOHelper.getInstance().readFromFile(path + "bd35.log");
		List<String> out = new ArrayList<String>();
		for (int i = begin; i <= end; i++) {
			out.add(list.get(i));
		}
		IOHelper.getInstance().writeToFile(out, path + "bd35_MaxNormalSeq.log", false);
	}
	
	static void extractLogs(int begin) {
		String path = "E:\\logData\\test-with label\\bd33-40\\分成不同日志源\\";
		List<String> list = IOHelper.getInstance().readFromFile(path + "bd35.log");
		List<String> out = new ArrayList<String>();
		for (int i = begin; i < list.size(); i++) {
			out.add(list.get(i));
		}
		IOHelper.getInstance().writeToFile(out, path + "bd35_test.log", false);
	}
	
	static void samePid() {
		String path = "E:\\logData\\test-with label\\bd33-40\\分成不同日志源\\";
		List<String> list = IOHelper.getInstance().readFromFile(path + "bd35_seq.log");
		Map<String, List<String>> pidMap = new HashMap<String, List<String>>(); 
		int cnt = 0;
		for (int i = 0; i < list.size(); i++) {
			JSONObject jo = new JSONObject(list.get(i));
			if (!jo.has("pid")) {
				continue;
			}
    		String pid = jo.getString("pid");
    		if (!pidMap.containsKey(pid)) {
    			cnt++;
    			pidMap.put(pid, new ArrayList<String>());
    		}
    		pidMap.get(pid).add(list.get(i));
		}
		System.out.println("total: " + cnt);
		cnt = 0;
		List<String> normalList = new ArrayList<String>();
		List<String> abnormalList = new ArrayList<String>();
		for (String pid : pidMap.keySet()) {
			System.out.println(pidMap.get(pid).size());
			if (pidMap.get(pid).size() > 7) {
				boolean isNormal = true;
				StringBuilder sb = new StringBuilder("");
				//生成session，并生成label
				for (int i = 0; i < pidMap.get(pid).size(); i++) {
					JSONObject jo = new JSONObject(pidMap.get(pid).get(i));
					sb.append(jo.getString("eventId"));
					if (i < pidMap.get(pid).size() - 1)
						sb.append(" ");
					if (!jo.getString("severity_label").equals("Informational")) {
						isNormal = false;
					}
				}
				
				if (isNormal) {
					normalList.add(sb.toString());
				}else {
					abnormalList.add(sb.toString());
				}
				cnt++;
			}
		}
		IOHelper.getInstance().writeToFile(normalList, path + "normalSession.log", false);
		IOHelper.getInstance().writeToFile(abnormalList, path + "abnormalSession.log", false);
		System.out.println("total: " + cnt);
	}
	
	public static void readCSV() {
		Pattern pattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)");
		String path = "E:\\logData\\domin\\0_label_data.csv";
		List<String> list = IOHelper.getInstance().readFromFile(path);
		Map<String, List<String>> dsts = new HashMap<String, List<String>>(); 
		int cnt  = 0;
		for (int i = 0; i < list.size(); i++) {
			String[] split = list.get(i).split(",");// 1:dst, 9:source
			if (!pattern.matcher(split[1]).find() || !pattern.matcher(split[9]).find()) {
        		continue;
        	}
			
			if (!dsts.containsKey(split[9])) {
    			dsts.put(split[9], new ArrayList<String>());
    			cnt++;
    		}
    		dsts.get(split[9]).add(split[1]);
			
		}
		int qualified = 0;
		for (String key : dsts.keySet()) {
			System.out.println(key + "----------" + dsts.get(key).size());
			if (dsts.get(key).size() >= 5) {
				qualified++;
			}
		}
		System.out.println(cnt);
		System.out.println(qualified);
	}
	
	public static void main(String[] args) {
		diffLabel();
    	
	}
}
