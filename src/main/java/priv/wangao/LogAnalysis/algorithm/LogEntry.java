package priv.wangao.LogAnalysis.algorithm;

import java.util.ArrayList;
import java.util.List;

import priv.wangao.LogAnalysis.constant.Common;
import priv.wangao.LogAnalysis.constant.ParseConstant;

/**
 * @author xbchen
 *  存储每条日志信息的数据结构
 */
public class LogEntry {
	private String id;     //_id   （es中的标签，不用则为null）
	private String type;   //_type（es中的标签，不用则为null）
	private String index;  //_index（es中的标签，不用则为null）
	private String logStr;  //预处理之后的日志
	private String cnt;     //日志序号
	private String origin_str;  //源日志
	private String[] logSeq;  //日志切分后的字段序列
	public LogEntry(String index, String type, String id, String log, String cnt, String origin){
		this.index = index;
		this.type = type;
		this.id = id;
		this.logStr = log;
		this.cnt = cnt;
		this.origin_str = origin;
		//this.logSeq = logStr.split(ParseConstant.PRIMARY_SEPARATOR);
	}
	
	public LogEntry(String log, String cnt, String origin, String separator){
		this.logStr = log;
		this.cnt = cnt;
		this.origin_str = origin;
		logSeq = logStr.split(separator);
		//createLogSeq(separator);
	}
	
	private void createLogSeq(String separator) {
		if (this.logStr == null)
			return;
		List<String> temp = new ArrayList<String>();
		String[] simpleSeq = logStr.split(separator);
		for (int i = 0; i < simpleSeq.length; i ++) {
			String word = simpleSeq[i];
			if (simpleSeq[i].startsWith("\"") && !simpleSeq[i].endsWith("\"")) {
				StringBuilder sb = new StringBuilder("");
				sb.append(simpleSeq[i]);
				for (int j = i + 1; j < Util.min(simpleSeq.length, i + 6); j ++) {
					sb.append(" ").append(simpleSeq[j]);
					if (simpleSeq[j].endsWith("\"")) {
						word = sb.toString();
						i = j;
						break;
					}
				}
			}
			temp.add(word);
		}
		logSeq = new String[temp.size()];
		for (int i = 0; i < temp.size(); i++) {
			logSeq[i] = new String(temp.get(i));
		}
	}
	
	public String[] getLogSeq() {
		return logSeq;
	}
	
	public String getOrigin() {
		return origin_str;
	}
	
	public String getCnt() {
		return this.cnt;
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getIndex() {
		return this.index;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String toString() {
		return new String(logStr);
	}
}
