package priv.wangao.LogAnalysis.algorithm;

import java.util.HashSet;
import java.util.Set;

import priv.wangao.LogAnalysis.constant.ParseConstant;

// 叶子节点中的日志组类
public class DrainLogGroup {
	private String[] eventSeq;
	private Set<LogEntry> logIDs = new HashSet<LogEntry>();
	LogEntry reservedLog = null;
	
	public DrainLogGroup() {
		
	}
	
	public DrainLogGroup (LogEntry logEntry, double ID) {
		//找出日志中的key=value对，将value替换为*
//		StringBuilder sb =  new StringBuilder("");
//		String[] logSeq = logEntry.toString().split("\\s+");
//		for (int i = 0; i < logSeq.length; i++ ) {
//			
//		}
		//eventSeq = logEntry.toString().split(ParseConstant.PRIMARY_SEPARATOR);
		eventSeq = logEntry.getLogSeq();
		for (int i = 0; i < eventSeq.length; i++) {    
			eventSeq[i] = Util.transformDigit(eventSeq[i]);
		}
		reservedLog = new LogEntry(logEntry.getIndex(), logEntry.getType(), logEntry.getId(), logEntry.toString(), "-1", logEntry.getOrigin());
		logIDs.add(logEntry);
	}
	//计算日志与该组的日志事件的相似度
	public double calSimilarity(LogEntry logEntry) {
		//String[] seq1 = logData.split(ParseConstant.PRIMARY_SEPARATOR);
		String[] seq1 = logEntry.getLogSeq();
		double total = seq1.length;
		double cnt = 0;
		for (int i = 0; i < Util.min(seq1.length, eventSeq.length); i++) {
			if (seq1[i].equals(eventSeq[i])) {
				cnt++;
			}else if (eventSeq[i].equals("*")) {
				total--;
			}
		}
		double simSeq = cnt / total;
		return simSeq;
	}
	//更新日志事件并将日志加入该组
	public void updateAndAdd(LogEntry logEntry) {
		logIDs.add(logEntry);
		String[] seq1 = logEntry.getLogSeq();
		for (int i = 0; i < Util.min(seq1.length, eventSeq.length); i++) {
			if (!seq1[i].equals(eventSeq[i])) {
				eventSeq[i] = new String("*");
			}
		}
	}
	
	public void update(LogEntry logEntry) {
		String[] seq1 = logEntry.getLogSeq();
		for (int i = 0; i < Util.min(seq1.length, eventSeq.length); i++) {
			if (!seq1[i].equals(eventSeq[i])) {
				eventSeq[i] = new String("*");
			}
		}
	}
	
	public Set<LogEntry> getLogSet() {
		return logIDs;
	}
	
	public String[] getEventSeq() {
		return eventSeq;
	}
	
	public void clearGroup() {
		if (!logIDs.isEmpty()) {
			for (LogEntry log : logIDs) {
				reservedLog = new LogEntry(log.getIndex(), log.getType(), log.getId(),log.toString(), "-1", log.getOrigin());
				break;
			}
		}
		logIDs.clear();
	}
	
	public String getRepresentation() {
		return reservedLog.toString();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < eventSeq.length; i++) {
			sb.append(eventSeq[i]+ " ");
		}
		return sb.toString();
	}
}
