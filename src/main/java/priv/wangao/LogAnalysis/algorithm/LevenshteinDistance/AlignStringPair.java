package priv.wangao.LogAnalysis.algorithm.LevenshteinDistance;

/**
 * 对齐的字符串对;
 * word1中出现的_,表示word1中该位置需要插入一个元素；
 * word2中出现_,表示word1中对应位置的元素应该删除；
 * word1和word2中如果对应位置的元素不相同，说明这里是将
 * word1中位置的元素改为word2中对应位置的元素
 *
 */
public class AlignStringPair {
	private String[] word1;
	private String[] word2;

	public AlignStringPair(String[] word1, String[] word2) {
		super();
		this.word1 = word1;
		this.word2 = word2;
	}

	public String getWord1() {
		StringBuilder sb = new StringBuilder();
		sb.append(word1[0]);
		for (int i = 1; i < word1.length; i++) {
			sb.append(" ").append(word1[i]);
		}
		return sb.toString();
	}

	public void setWord1(String[] word1) {
		this.word1 = word1;
	}

	public String getWord2() {
		StringBuilder sb = new StringBuilder();
		sb.append(word2[0]);
		for (int i = 1; i < word2.length; i++) {
			sb.append(" ").append(word2[i]);
		}
		return sb.toString();
	}

	public void setWord2(String[] word2) {
		this.word2 = word2;
	}

	@Override
	public String toString() {
		return "AlignStringPair [word1=" + getWord1() + ", word2=" + getWord2() + "]";
	}

}
