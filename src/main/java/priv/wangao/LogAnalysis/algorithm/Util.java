package priv.wangao.LogAnalysis.algorithm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONObject;

import priv.wangao.LogAnalysis.constant.Common;
import priv.wangao.LogAnalysis.constant.ParseConstant;
import priv.wangao.LogAnalysis.util.IOHelper;
import priv.wangao.LogAnalysis.util.RegExpHelper;


// 日志解析算法中用到的工具
public class Util {
	
	// 将包含数字的字段转化为“*”
	static String transformDigit(String token) {
		char[] chars = token.toCharArray();
		int count_num = 0;
		for (int i = 0; i < chars.length; i++) {
			if(chars[i] >= 48 && chars[i] <= 57){
				count_num++;
			}
		}
		if (count_num >= chars.length * 0.5) return "*";
		return token;
	}
	// 将日志事件转化为正则表达式
	static String eventToRegex(String[] eventSeq, DrainLogGroup logGroup, String separator) {
		List<String> regexSeq = new ArrayList<String>();
		String edge_char = separator.replace("+", "?");
		String log = logGroup.getRepresentation();
		//System.out.println(log);
		eventSeq = String.join(" ", eventSeq).split(separator);
		String[] logSeq = log.split(separator);
		for (int i = 0; i < eventSeq.length; i ++) {
			if (eventSeq[i].equals("*")) {
				//regexSeq.add(RegExpHelper.getInstance().isMatch(logSeq[i]));
				regexSeq.add(("(\\S+)"));
			}else {
				regexSeq.add(eventSeq[i].replaceAll("\\\\", "\\\\\\\\").replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll("\\*", "\\\\*")
						.replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]").replaceAll("\\+", "\\\\+").replaceAll("\\?", "\\\\?")
						.replaceAll("\\{", "\\\\{").replaceAll("\\}", "\\\\}").replaceAll("\\|", "\\\\|").replaceAll("\\\"", "\\\\\"")
						.replace("$", "\\$").replace(".", "\\.")
						);
			}
		} 
		String regex = String.join(separator, regexSeq);
		return regex;
		//return regex.replaceAll("&", "&amp;").replaceAll("\"", "&quot;").replaceAll(">", "&gt;").replaceAll("<", "&lt;");
	}
	
	//检查源日志是否能被生成的正则表达式全部匹配
	static void checkRegex() {
		String exportPath = "E:\\drain";
		int wrongCnt = 0;
		int totalCnt = 0;
		List<String> regexList = IOHelper.getInstance().readFromFile(exportPath + "\\group\\groups.log");
		for (int i = 0; i < regexList.size(); i++) {
			String pattern = regexList.get(i);
			List<String> logList = IOHelper.getInstance().readFromFile(exportPath + "\\group\\" + String.valueOf(i+1) + ".log");
			for (int j = 0; j < logList.size() - 1; j++) {
				totalCnt ++;
				//System.out.println(totalCnt);
				String str = logList.get(j+1);
				boolean result = RegExpHelper.getInstance().isMatch(pattern,str);
				if (!result) {
					wrongCnt++;
					System.out.println(i+1);
					//System.exit(0);
				}
			}
		}
		System.out.println("error: "+wrongCnt);
	}
	
	// 将日志序列变为日志事件id序列
	public static void createLogSequence(String file, String exportPath) throws IOException {
		List<String> sequence = new ArrayList<String>();
		List<String> regexList = IOHelper.getInstance().readFromFile("E:\\drain\\group\\groups.log");
		FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        String str = null;     
        int num  = 0;
        while ((str = br.readLine()) != null) {  
        	//String line = str.split("]: ")[1];
        	num++;
        	int i = 0;
        	for (i = 0; i < regexList.size(); i ++) {
        		boolean res = RegExpHelper.getInstance().isMatch(regexList.get(i), str);
        		if (res) {
        			sequence.add(String.valueOf(i + 1));
        			break;
        		}
        	}
        	if (i >= regexList.size()) {
    			sequence.add("0");
        	}	
        }
        IOHelper.getInstance().writeToFile(String.join(Common.LOCAL_LINE_SEPARATOR, sequence), exportPath + "\\sequence.log", false);
	}
	// 将Json格式的日志序列变为日志事件id序列
	public static void createLogSequenceFromJson(String file, String exportPath) throws IOException {
		List<String> sequence = new ArrayList<String>();
		List<String> regexList = IOHelper.getInstance().readFromFile("E:\\drain\\group\\groups.log");
		FileReader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        String str = null;     
        int num  = 0;
        Pattern pattern = Pattern.compile("(CMD)");
        while ((str = br.readLine()) != null) {  
        	//String line = str.split("]: ")[1];
        	JSONObject jo = new JSONObject(str);
            str = jo.getString("message");
        	if (pattern.matcher(str).find())
        		continue;
        	
        	num++;
        	int i = 0;
        	for (i = 0; i < regexList.size(); i ++) {
        		boolean res = RegExpHelper.getInstance().isMatch(regexList.get(i), str);
        		if (res) {
        			jo.put("eventId", String.valueOf(i + 1));
        			sequence.add(jo.toString());
        			//sequence.add(String.valueOf(i + 1));
        			break;
        		}
        	}
        	if (i >= regexList.size()) {
        		System.out.println(jo.getString("severity_label")+ " : "+num + ": "+str);
        		jo.put("eventId", "0");
    			sequence.add(jo.toString());
        	}	
        }
        IOHelper.getInstance().writeToFile(String.join(Common.LOCAL_LINE_SEPARATOR, sequence), exportPath + "\\sequence_20190528.log", false);
	}
	
	public static int min(int a, int b) {
		if (a >= b) return b;
		return a;
	}
	
	
	public static void main(String[] args) throws IOException {
//		Pattern pattern = Pattern.compile("([0-9]+)");
//		boolean digit = pattern.matcher("option[123123]").find();
//		System.out.println(digit);
		createLogSequenceFromJson("E:\\logData\\test-with label\\bd33-40\\20190528.log", "E:\\logData\\test-with label\\bd33-40\\");
	}

}
