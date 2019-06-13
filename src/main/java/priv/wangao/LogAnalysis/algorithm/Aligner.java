package priv.wangao.LogAnalysis.algorithm;

import priv.wangao.LogAnalysis.algorithm.LevenshteinDistance.AlignStringPair;
import priv.wangao.LogAnalysis.algorithm.LevenshteinDistance.EditPath;
import priv.wangao.LogAnalysis.algorithm.LevenshteinDistance.LevenshteinDistance;

public class Aligner {

	
	public static boolean isSimilar(DrainLogGroup group1, DrainLogGroup group2) {
		String[] word1 = group1.getEventSeq();
		String[] word2 = group2.getEventSeq();
		int[][] distance = LevenshteinDistance.getMinDistanceMatrix(word1, word2);
		EditPath editPath = LevenshteinDistance.getOnePath(word1, word2, distance);
		AlignStringPair alignStringPair = LevenshteinDistance.align(word1, word2, editPath);
		//System.out.println(alignStringPair.getWord1());
		//System.out.println(alignStringPair.getWord2());
		String[] seq1 = alignStringPair.getWord1().split(" ");
		String[] seq2 = alignStringPair.getWord2().split(" ");
		double total = seq1.length;
		double cnt = 0;
		for (int i = 0; i < seq1.length; i++) {
			if (seq1[i].equals(seq2[i])) {
				cnt++;
			}
		}
		if (cnt / total > 0.8) {
			for (LogEntry logEntry : group2.getLogSet()) {
					group1.getLogSet().add(logEntry);
					//group1.update(logEntry);
			}
			return true;
		}
		return false;
	}
	
	public static boolean isSimilar(String[] word1, String[] word2, int id1, int id2) {
		int[][] distance = LevenshteinDistance.getMinDistanceMatrix(word1, word2);
		EditPath editPath = LevenshteinDistance.getOnePath(word1, word2, distance);
		AlignStringPair alignStringPair = LevenshteinDistance.align(word1, word2, editPath);
		//System.out.println(alignStringPair.getWord1());
		//System.out.println(alignStringPair.getWord2());
		String[] seq1 = alignStringPair.getWord1().split(" ");
		String[] seq2 = alignStringPair.getWord2().split(" ");
		double total = seq1.length;
		double cnt = 0;
		for (int i = 0; i < seq1.length; i++) {
			if (seq1[i].equals(seq2[i])) {
				cnt++;
			}
		}
		if (cnt / total > 0.8) {
			return true;
		}
		return false;
	}
}
