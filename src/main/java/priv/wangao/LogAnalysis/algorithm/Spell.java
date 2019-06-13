package priv.wangao.LogAnalysis.algorithm;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import priv.wangao.LogAnalysis.util.DataHelper;
import priv.wangao.LogAnalysis.util.IOHelper;
import priv.wangao.LogAnalysis.util.RegExpHelper;

public class Spell {

	private class LCS {

		boolean valid = true;
		private List<String> seq;
		private List<String> logs;
		private List<List<String>> tokens;
		private List<List<String>> delimiters;
		private List<String> regexp;

		public LCS() {
			this.seq = new ArrayList<String>();
			this.logs = new ArrayList<String>();
			this.tokens = new ArrayList<List<String>>();
			this.delimiters = new ArrayList<List<String>>();
			this.regexp = new ArrayList<String>();
		}

		public void append(String log, List<String> token, List<String> delimiter) {
			this.logs.add(log);
			this.tokens.add(token);
			this.delimiters.add(delimiter);
		}

		public List<String> getSeq() {
			return seq;
		}

		public void setSeq(List<String> seq) {
			this.seq = seq;
		}

		public List<String> getLogs() {
			return logs;
		}

		public boolean isEqual(LCS other) {
			if (this.seq.size() != other.seq.size())
				return false;
			for (int i = 0; i < this.seq.size(); i++) {
				if (!this.seq.get(i).equals(other.seq.get(i)))
					return false;
			}
			return true;
		}

		public void merge(LCS other) {
			other.valid = false;
			int len = other.logs.size();
			for (int i = 0; i < len; i++) {
				this.logs.add(other.logs.get(i));
				this.tokens.add(other.tokens.get(i));
				this.delimiters.add(other.delimiters.get(i));
			}
			other.logs.clear();
			other.tokens.clear();
			other.delimiters.clear();
		}

	}

	private List<LCS> LCSMap;
	private final String DEFAULT_DELIMITERS = "[\\s=]+";
	private String delimiter;

	public Spell() {
		this.LCSMap = new ArrayList<LCS>();
	}

	protected List<String> tokenize(String log, String delimiters, List<String> delimiterList) {
		Pattern pattern = Pattern.compile(RegExpHelper.DATE_REG);
		Matcher matcher = pattern.matcher(log);
		if (matcher.find()) {
			log = log.replaceAll(pattern.pattern(), "DATE_REG");
		}
		pattern = Pattern.compile(RegExpHelper.TIME_REG);
		matcher = pattern.matcher(log);
		if (matcher.find()) {
			log = log.replaceAll(pattern.pattern(), "TIME_REG");
		}
		pattern = Pattern.compile(RegExpHelper.IP_REG);
		matcher = pattern.matcher(log);
		if (matcher.find()) {
			log = log.replaceAll(pattern.pattern(), "IP_REG");
		}
		pattern = Pattern.compile(RegExpHelper.UUID_REG);
		matcher = pattern.matcher(log);
		if (matcher.find()) {
			log = log.replaceAll(pattern.pattern(), "UUID_REG");
		}
		List<String> result = new ArrayList<String>();
		if (delimiters == null) {
			result = Arrays.asList(log.split(DEFAULT_DELIMITERS));
			pattern = Pattern.compile(DEFAULT_DELIMITERS);

		} else {
			result = Arrays.asList(log.split(delimiters));
			pattern = Pattern.compile(delimiters);
		}
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i).equals("DATE_REG")) {
				result.set(i, RegExpHelper.DATE_REG);
			} else if (result.get(i).equals("TIME_REG")) {
				result.set(i, RegExpHelper.TIME_REG);
			} else if (result.get(i).equals("IP_REG")) {
				result.set(i, RegExpHelper.IP_REG);
			} else if (result.get(i).equals("UUID_REG")) {
				result.set(i, RegExpHelper.UUID_REG);
			}
		}
		matcher = pattern.matcher(log);
		for (int i = 0; matcher.find(); i++) {
			String tmp = matcher.group();
			delimiterList.add(tmp);
			result.set(i, result.get(i));
		}
		return result;
	}

	private boolean canUpdate(LCS lcs, List<String> seq) {
		int len = lcs.logs.size();
		for (int i = 0; i < len; i++) {
			if (seq.size() * 3 / 2 <= lcs.tokens.get(i).size())
				return false;
		}
		return true;
	}

	private void findLCS(String log) {
		boolean find = false;
		// System.err.println("Before: " + log);
		List<String> delimiters = new ArrayList<String>();
		List<String> tokens = this.tokenize(log, null, delimiters);
		// System.err.println(tokens);
		// System.err.println("After: " + log);
		List<String> lcsSeq = null;
		int maxSeqLen = 0;
		LCS targetLCS = null;
		int targetIndex = -1;
		for (int i = 0; i < this.LCSMap.size(); i++) {
			if (this.LCSMap.get(i).valid == false)
				continue;
			lcsSeq = this.getLcsSeq(this.LCSMap.get(i).getSeq(), tokens);
			if (lcsSeq.size() > maxSeqLen) {
				maxSeqLen = lcsSeq.size();
				targetLCS = this.LCSMap.get(i);
				targetIndex = i;
			} else if (lcsSeq.size() == maxSeqLen && targetLCS != null
					&& targetLCS.getSeq().size() < this.LCSMap.get(i).getSeq().size()) {
				maxSeqLen = lcsSeq.size();
				targetLCS = this.LCSMap.get(i);
				targetIndex = i;
			}
		}

		if (maxSeqLen * 3 / 2 > tokens.size() && targetLCS != null) {
			List<String> oldLCS = targetLCS.getSeq();
			List<String> newLCS = this.getLcsSeq(targetLCS.getSeq(), tokens);
			if (newLCS.indexOf("proto")+1 == newLCS.indexOf("1")) {
				System.err.println(oldLCS);
				System.err.println(newLCS);
				System.err.println(targetLCS.getSeq());
				System.err.println(tokens);
			}
			// if (newLCS.size() > oldLCS.size()) {
			if (this.canUpdate(targetLCS, newLCS)) {
				targetLCS.setSeq(newLCS);
				targetLCS.append(log, tokens, delimiters);
				for (int i = 0; i < this.LCSMap.size(); i++) {
					if (i == targetIndex)
						continue;
					if (this.LCSMap.get(i).valid == false)
						continue;
					if (targetLCS.isEqual(this.LCSMap.get(i))) {
						this.LCSMap.get(i).merge(targetLCS);
					}
				}
			} else {
				LCS lcs = new LCS();
				lcs.setSeq(tokens);
				lcs.append(log, tokens, delimiters);
				this.LCSMap.add(lcs);
			}

			// }
		} else {
			LCS lcs = new LCS();
			lcs.setSeq(tokens);
			lcs.append(log, tokens, delimiters);
			this.LCSMap.add(lcs);
		}
	}

	public void compute(List<String> logs) {
		int i = 0;
		for (String log : logs) {
			this.findLCS(log);
			// System.err.println(i++);
		}
		System.err.println(this.getLcsSeq(
				Arrays.<String>asList(new String[] {"sessionid", "proto", "action", "policyid", "1"}), 
				Arrays.<String>asList(new String[] {"sessionid", "17387469", "proto", "1", "action", "accept", "policyid", "1"})));
		
		
	}

	private List<String> getLcsSeq(List<String> A, List<String> B) {
		List<String> result = new ArrayList<String>();
		int lenA = A.size(), lenB = B.size();
		int[][] dp = new int[lenA + 1][lenB + 1];
		String[][][] seq = new String[lenA + 1][lenB + 1][Math.max(lenA, lenB) + 1];
		for (int i = 0; i <= lenB; i++) {
			dp[0][i] = 0;
		}
		for (int i = 0; i <= lenA; i++) {
			dp[i][0] = 0;
		}
		for (int i = 1; i <= lenA; i++) {
			for (int j = 1; j <= lenB; j++) {
				if (A.get(i - 1).equals(B.get(j - 1)) == true) {
					
					dp[i][j] = dp[i - 1][j - 1] + 1;
					seq[i][j] = Arrays.copyOf(seq[i-1][j-1], Math.max(lenA, lenB) + 1);
					seq[i][j][dp[i][j] - 1] = A.get(i - 1);
				} else {
					if (dp[i][j - 1] > dp[i - 1][j]) {
						dp[i][j] = dp[i][j - 1];
						seq[i][j] = Arrays.copyOf(seq[i][j - 1], Math.max(lenA, lenB) + 1);
					} else {
						dp[i][j] = dp[i - 1][j];
						seq[i][j] = Arrays.copyOf(seq[i-1][j], Math.max(lenA, lenB) + 1);
					}
				}
				
			}
		}
		for (int i = 0; i < dp[lenA][lenB]; i++) {
			result.add(seq[lenA][lenB][i]);
		}
		return result;
	}

	public void display() {
		File file = new File("Group-fw/groups.txt");
		if (file.exists() == true) {
			file.delete();
		}
		int count = 0;
		for (LCS lcs : this.LCSMap) {
			if (lcs.valid == false)
				continue;
			this.genRegExp(lcs);
			System.out.println("Group " + count + ":-> "
					+ String.join("(" + this.DEFAULT_DELIMITERS + ")(.*)", lcs.getSeq()) + "(.*)");
			IOHelper.getInstance().writeToFile(
					count + ":->" + String.join("(" + this.DEFAULT_DELIMITERS + ")(.*)", lcs.getSeq()) + "(.*)\r\n",
					"Group-fw/groups.txt", true);
			IOHelper.getInstance().writeToFile(lcs.getLogs(), "Group-fw/" + count + ".txt", true);
			count++;
		}
	}

	private void genRegExp(LCS lcs) {
		int len = lcs.logs.size();
		String regex = String.join("(" + this.DEFAULT_DELIMITERS + ")(.*)", lcs.getSeq()) + "(.*)";
		Pattern pattern = Pattern.compile(regex);
		Pattern delimiter = Pattern.compile(this.DEFAULT_DELIMITERS);
		Matcher matcher = null;
		Matcher matcher2 = null;
		boolean hasError = false;
		for (int i = 0; i < len; i++) {
			matcher = pattern.matcher(lcs.logs.get(i));
			if (matcher.matches()) {
				for (int j = 0; j < matcher.groupCount(); j++) {
					matcher2 = delimiter.matcher(matcher.group(j+1));
					if (matcher2.matches())	continue;
					if (matcher.group(j + 1).trim().length() > 0) {
						System.out.print("\t(" + j + "->" + matcher.group(j + 1).trim() + ")\t");
					}
				}
				System.out.println();
			} else {
				hasError = true;
				IOHelper.getInstance().writeToFile(lcs.logs.get(i) + "\r\n", "Group-fw/error.log", true);
//				System.err.println(lcs.logs.get(i));
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Date start = new Date();
		List<String> logs = IOHelper.getInstance().readFromFile("act/ruijie_fw.log");

		System.err.println("日志总数:" + logs.size());
		// logs = DataHelper.getInstance().jsonsToList(logs, "message");
		Spell spell = new Spell();
		spell.compute(logs);
		spell.display();
		Date end = new Date();
		System.err.println((end.getTime() - start.getTime() + "ms"));
		
		System.err.println();
	}

}
