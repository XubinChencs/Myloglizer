package priv.wangao.LogAnalysis.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONObject;
import priv.wangao.LogAnalysis.util.IOHelper;

public class TestLogSig {

	// termPairMap : 日志短语对映射，用于分组时使用
	private Map<String, List<String>> termPairMap;
	// termMap : 日志短语映射，用于分组后的签名生成
	private Map<String, List<String>> termMap;
	private String sourcePath;
	private String groupPrefix;

	/**
	 * @Title:TestLogSig
	 * @Description: 构造函数
	 * @param sourcePath
	 *            数据源路径
	 * @param groupPrefix
	 *            分组中间结果存储路径前缀
	 */
	public TestLogSig(String sourcePath, String groupPrefix) {
		this.sourcePath = sourcePath;
		this.groupPrefix = groupPrefix;
		this.termPairMap = new HashMap<String, List<String>>();
		this.termMap = new HashMap<String, List<String>>();
	}

	/**
	 * @Title: randomInit
	 * @Description: 局部搜搜获取初始解，更新每条日志所属组号
	 * @param messages
	 *            日志集合
	 * @param K
	 *            目标划分组数
	 * @param groupIndex
	 *            每条日志对应的组号
	 * @return 分组后的二维数组
	 * @return: List<List<String>>
	 */
	public List<List<String>> randomInit(List<String> messages, int K, List<Integer> groupIndex) {
		List<List<String>> result = new ArrayList<List<String>>();
		Random rand = new Random(25);
		int len = messages.size();
		for (int i = 0; i < K; i++) {
			result.add(new ArrayList<String>());
		}
		for (int i = 0; i < len; i++) {
			int group = rand.nextInt(K);
			groupIndex.set(i, group);
			result.get(group).add(messages.get(i));
		}
		return result;
	}

	/**
	 * @Title: genTermPairs
	 * @Description: 根据一条日志生成短语对数组，通过 termPairMap 缓存增加运算速度
	 * @param message
	 *            一条日志
	 * @return 生成的短语对数组
	 * @return: List<String>
	 */
	private List<String> genTermPairs(String message) {
		if (this.termPairMap.containsKey(message)) {
			return this.termPairMap.get(message);
		}
		String[] split = message.split("\\s+");
		List<String> result = new ArrayList<String>();
		int len = split.length;
		for (int i = 0; i < len; i++) {
			for (int j = i + 1; j < len; j++) {
				result.add(split[i] + " " + split[j]);
			}
		}
		this.termPairMap.put(message, result);
		return result;
	}

	/**
	 * @Title: genTerms
	 * @Description: 根据一条日志生成短语数组，通过 termMap 缓存增加运算速度
	 * @param message
	 *            一条日志
	 * @return 生成的短语数组
	 * @return: List<String>
	 */
	private List<String> genTerms(String message) {
		if (this.termMap.containsKey(message)) {
			return this.termMap.get(message);
		}
		String[] split = message.split("\\s+");
		List<String> result = Arrays.asList(split);
		this.termMap.put(message, result);
		return result;
	}

	/**
	 * @Title: updateUnion
	 * @Description: 根据集合更新映射，应用于 termMap 和 termPairMap
	 * @param terms
	 *            集合
	 * @param union
	 *            映射
	 * @return: void
	 */
	private void updateUnion(List<String> terms, Map<String, Integer> union) {
		for (String term : terms) {
			if (union.containsKey(term) == true) {
				union.put(term, union.get(term) + 1);
			} else {
				union.put(term, 1);
			}
		}
	}

	/**
	 * @Title: getNrC
	 * @Description: 根据短语对获取N(r, C)
	 * @param term
	 *            短语对
	 * @param union
	 *            短语对数量映射
	 * @return N(r, C)
	 * @return: int
	 */
	private int getNrC(String termPair, Map<String, Integer> union) {
		if (union.containsKey(termPair) == false)
			return 0;
		return union.get(termPair);
	}

	/**
	 * @Title: getGroupTermPairUnion
	 * @Description: 根据每组的日志获取生成的短语对集合映射
	 * @param group
	 *            每组的日志
	 * @return 每组的日志获取生成的短语对集合映射
	 * @return: Map<String,Integer>
	 */
	private Map<String, Integer> getGroupTermPairUnion(List<String> group) {
		Map<String, Integer> union = new HashMap<String, Integer>();
		for (String message : group) {
			this.updateUnion(this.genTermPairs(message), union);
		}
		return union;
	}

	/**
	 * @Title: getGroupTermUnion
	 * @Description: 根据每组的日志获取生成的短语集合映射
	 * @param group
	 *            每组的日志
	 * @return 每组的日志获取生成的短语集合映射
	 * @return: Map<String,Integer>
	 */
	private Map<String, Integer> getGroupTermUnion(List<String> group) {
		Map<String, Integer> union = new LinkedHashMap<String, Integer>();
		for (String message : group) {
			this.updateUnion(this.genTerms(message), union);
		}
		return union;
	}

	/**
	 * @Title: getGroupPhi
	 * @Description: 获取每组的分组分数
	 * @param group
	 *            一组日志
	 * @param union
	 *            一组日志的短语对数量映射
	 * @return 每组的分组分数
	 * @return: double
	 */
	private double getGroupPhi(List<String> group, Map<String, Integer> union) {
		double ans = 0.0D;
		for (Map.Entry<String, Integer> entry : union.entrySet()) {
			int NrC = entry.getValue();
			ans += (double) NrC * NrC * NrC / group.size() / group.size();
		}
		return ans;
	}

	/**
	 * @Title: getPhi
	 * @Description: 获取整体的分组分数
	 * @param groups
	 *            整体分组二维数组
	 * @param unions
	 *            整体分组的短语对数量映射
	 * @return 整体的分组分数
	 * @return: double
	 */
	public double getPhi(List<List<String>> groups, List<Map<String, Integer>> unions) {
		double ans = 0.0D;
		int K = groups.size();
		for (int i = 0; i < K; i++) {
			ans += this.getGroupPhi(groups.get(i), unions.get(i));
		}
		return ans;
	}

	/**
	 * @Title: getRealDelta
	 * @Description: TODO
	 * @param groupIndex
	 * @param messages
	 * @param groups
	 * @param unions
	 * @param messageIndex
	 * @param targetGroup
	 * @return
	 * @return: double
	 */
	private double getRealDelta(List<Integer> groupIndex, List<String> messages, List<List<String>> groups,
			List<Map<String, Integer>> unions, int messageIndex, int targetGroup) {
		double ans = 0.0D;
		int sourceGroup = groupIndex.get(messageIndex);
		String message = messages.get(messageIndex);
		if (sourceGroup == targetGroup) {
			return ans;
		}

		List<String> Cjt = new ArrayList<String>();
		List<String> Cj0 = new ArrayList<String>();
		List<String> Cit = new ArrayList<String>();
		List<String> Ci0 = new ArrayList<String>();

		boolean find = false;

		for (int i = 0; i < groups.get(sourceGroup).size(); i++) {
			if (find == false && groups.get(sourceGroup).get(i).equals(message)) {
				find = true;
				Ci0.add(groups.get(sourceGroup).get(i));
			} else {
				Cit.add(groups.get(sourceGroup).get(i));
				Ci0.add(groups.get(sourceGroup).get(i));
			}
		}
		for (int i = 0; i < groups.get(targetGroup).size(); i++) {
			Cjt.add(groups.get(targetGroup).get(i));
			Cj0.add(groups.get(targetGroup).get(i));
		}
		Cjt.add(message);

		Map<String, Integer> Ujt = this.getGroupTermPairUnion(Cjt);
		Map<String, Integer> Uj0 = this.getGroupTermPairUnion(Cj0);
		Map<String, Integer> Uit = this.getGroupTermPairUnion(Cit);
		Map<String, Integer> Ui0 = this.getGroupTermPairUnion(Ci0);

		return this.getGroupPhi(Cjt, Ujt) - this.getGroupPhi(Cj0, Uj0) - this.getGroupPhi(Ci0, Ui0)
				+ this.getGroupPhi(Cit, Uit);
	}

	/**
	 * @Title: getDelta
	 * @Description: 获取将第 messageIndex 条日志转入第 targetGroup 组的分数变化
	 * @param groupIndex
	 *            每条日志对应的组号数组
	 * @param messages
	 *            日志集合
	 * @param groups
	 *            分组情况
	 * @param unions
	 *            分组映射情况
	 * @param messageIndex
	 *            日志序号
	 * @param targetGroup
	 *            目标组号
	 * @return 分数变化
	 * @return: double
	 */
	private double getDelta2(List<Integer> groupIndex, List<String> messages, List<List<String>> groups,
			List<Map<String, Integer>> unions, int messageIndex, int targetGroup) {
		double ans = 0.0D;
		int sourceGroup = groupIndex.get(messageIndex);
		String message = messages.get(messageIndex);
		if (sourceGroup == targetGroup) {
			return ans;
		}

		List<String> terms = this.genTermPairs(message);
		List<String> source = groups.get(sourceGroup);
		Map<String, Integer> sourceUnion = unions.get(sourceGroup);
		int sourceSize = source.size();
		List<String> target = groups.get(targetGroup);
		Map<String, Integer> targetUnion = unions.get(targetGroup);
		int targetSize = target.size();
		int Rx = terms.size();
		double b = Math.pow(this.getGroupPhi(source, sourceUnion) * sourceSize * sourceSize, 1.0 / 3.0);
		double a = Math.pow(this.getGroupPhi(target, targetUnion) * targetSize * targetSize, 1.0 / 3.0);
		ans = (3.0 * a * a * Rx + 3.0 * a * Rx * Rx + Rx * Rx * Rx) / targetSize / targetSize
				- (3.0 * b * b * Rx - 3 * b * Rx * Rx + Rx * Rx * Rx) / sourceSize / sourceSize;
		return ans;
	}

	/**
	 * @Title: getDelta
	 * @Description: 获取将第 messageIndex 条日志转入第 targetGroup 组的分数变化
	 * @param groupIndex
	 *            每条日志对应的组号数组
	 * @param messages
	 *            日志集合
	 * @param groups
	 *            分组情况
	 * @param unions
	 *            分组映射情况
	 * @param messageIndex
	 *            日志序号
	 * @param targetGroup
	 *            目标组号
	 * @return 分数变化
	 * @return: double
	 */
	private double getDelta(List<Integer> groupIndex, List<String> messages, List<List<String>> groups,
			List<Map<String, Integer>> unions, int messageIndex, int targetGroup) {
		double ans = 0.0D;
		int sourceGroup = groupIndex.get(messageIndex);
		String message = messages.get(messageIndex);
		if (sourceGroup == targetGroup) {
			return ans;
		}

		List<String> source = groups.get(sourceGroup);
		double sourceSize = (double) source.size() * 1.0D;
		Map<String, Integer> sourceUnion = unions.get(sourceGroup);
		List<String> target = groups.get(targetGroup);
		double targetSize = (double) target.size() * 1.0D;
		Map<String, Integer> targetUnion = unions.get(targetGroup);

		List<String> terms = this.genTermPairs(message);
		double NrCi = 0, NrCj = 0;
		double prCi = 0.0D, prCj = 0.0D;
		for (String term : terms) {
			NrCi = (double) this.getNrC(term, sourceUnion) * 1.0D;
			NrCj = (double) this.getNrC(term, targetUnion) * 1.0D;

			prCi = NrCi / sourceSize;
			prCj = NrCj / targetSize;

			ans += (prCj * prCj - prCi * prCi);
		}
		return ans * 3.0;
	}

	/**
	 * @Title: updateGroups
	 * @Description: 将第 messageIndex 条日志转入第 targetGroup 组，更新相应状态
	 * @param groupIndex
	 *            每条日志对应的组号数组
	 * @param messages
	 *            日志集合
	 * @param groups
	 *            分组情况
	 * @param unions
	 *            分组映射情况
	 * @param messageIndex
	 *            日志序号
	 * @param targetGroup
	 *            目标组号
	 * @param deltaCache
	 *            日志变化缓存，二维数组，表示第i条日志转入第j组的分数变化值
	 * @return: void
	 */
	private void updateGroups(List<Integer> groupIndex, List<String> messages, List<List<String>> groups,
			List<Map<String, Integer>> unions, int messageIndex, int targetGroup, double[][] deltaCache) {
		String message = messages.get(messageIndex);
		int sourceGroup = groupIndex.get(messageIndex);
		int messageCnt = messages.size();
		groups.get(sourceGroup).remove(message);
		groups.get(targetGroup).add(message);
		groupIndex.set(messageIndex, targetGroup);
		List<String> terms = this.genTermPairs(message);
		for (String term : terms) {
			int tmp = unions.get(sourceGroup).get(term) - 1;
			if (tmp == 0) {
				unions.get(sourceGroup).remove(term);
			} else {
				unions.get(sourceGroup).put(term, tmp);
			}
		}
		for (String term : terms) {
			int tmp = unions.get(targetGroup).containsKey(term) == true ? unions.get(targetGroup).get(term) + 1 : 1;
			unions.get(targetGroup).put(term, tmp);
		}

		double tmp = 0.0D;
		String tmpMessage = null;
		List<String> tmpTermPairs = null;
		int tmpGroup = 0, NrCi = 0, NrCj = 0;
		double prCi = 0.0D, prCj = 0.0D;
		for (int i = 0; i < messageCnt; i++) {
			tmp = 0.0D;
			tmpMessage = messages.get(i);
			tmpTermPairs = this.genTermPairs(tmpMessage);
			tmpGroup = groupIndex.get(i);
			for (String term : tmpTermPairs) {
				NrCi = this.getNrC(term, unions.get(tmpGroup));
				NrCj = this.getNrC(term, unions.get(sourceGroup));
				prCi = (double) NrCi / groups.get(tmpGroup).size();
				prCj = (double) NrCj / groups.get(sourceGroup).size();
				tmp += prCj * prCj - prCi * prCi;
			}
			deltaCache[i][sourceGroup] = tmp * 3.0D;
		}
		for (int i = 0; i < messageCnt; i++) {
			tmp = 0.0D;
			tmpMessage = messages.get(i);
			tmpTermPairs = this.genTermPairs(tmpMessage);
			tmpGroup = groupIndex.get(i);
			for (String term : tmpTermPairs) {
				NrCi = this.getNrC(term, unions.get(tmpGroup));
				NrCj = this.getNrC(term, unions.get(targetGroup));
				prCi = (double) NrCi / groups.get(tmpGroup).size();
				prCj = (double) NrCj / groups.get(targetGroup).size();
				tmp += prCj * prCj - prCi * prCi;
			}
			deltaCache[i][targetGroup] = tmp * 3.0D;
		}
	}

	/**
	 * @Title: localSearch
	 * @Description: 局部搜索算法
	 * @param groupIndex
	 *            每条日志对应的组号数组
	 * @param messages
	 *            日志集合
	 * @param groups
	 *            分组情况
	 * @param unions
	 *            分组映射情况
	 * @param maxIteration
	 *            最大迭代次数
	 * @return 获取的最大分组匹配分数
	 * @return: double
	 */
	private double localSearch(List<Integer> groupIndex, List<String> messages, List<List<String>> groups,
			List<Map<String, Integer>> unions, int maxIteration) {
		int messageCnt = messages.size(), groupCnt = groups.size();

		double[][] deltaCache = new double[messageCnt][groupCnt];

		for (int from = 0; from < messageCnt; from++) {
			for (int to = 0; to < groupCnt; to++) {
				deltaCache[from][to] = this.getDelta(groupIndex, messages, groups, unions, from, to);
			}
		}
		double max = (double) Integer.MIN_VALUE;
		int messageIndex = -1, targetGroup = -1;
		for (int i = 0; i < maxIteration; i++) {
			max = (double) Integer.MIN_VALUE;
			messageIndex = -1;
			targetGroup = -1;

			for (int from = 0; from < messageCnt; from++) {
				for (int to = 0; to < groupCnt; to++) {
					double delta = this.getRealDelta(groupIndex, messages, groups, unions, from, to);
					System.out.println(from + "(" + groupIndex.get(from) + ") -> " + to + " " + delta + " vs "
							+ deltaCache[from][to] + " vs "
							+ this.getDelta2(groupIndex, messages, groups, unions, from, to));
					if (deltaCache[from][to] > max) {
						max = deltaCache[from][to];
						messageIndex = from;
						targetGroup = to;
					}
				}
			}

			System.out.println("maxDelta: " + max + " " + messageIndex + " -> " + targetGroup);
			if (i == 0)
				System.out.println(deltaCache[1220][0]);
			else if (i == 1)
				System.out.println("215->1:" + deltaCache[215][1]);
			else if (i == 2)
				System.out.println("234->1:" + deltaCache[234][1]);
			else if (i == 3)
				System.out.println("210->1:" + deltaCache[210][1]);
			else if (i == 4)
				System.out.println("6->1:" + deltaCache[6][1]);
			else if (i == 5)
				System.out.println("7->1:" + deltaCache[7][1]);
			else if (i == 6)
				System.out.println("16->1:" + deltaCache[16][1]);
			else if (i == 7)
				System.out.println("20->1:" + deltaCache[20][1]);
			else if (i == 8)
				System.out.println("30->1:" + deltaCache[30][1]);
			else if (i == 9)
				System.out.println("41->1:" + deltaCache[41][1]);
			else if (i == 10)
				System.out.println("47->1:" + deltaCache[47][1]);
			else if (i == 11)
				System.out.println("48->1:" + deltaCache[48][1]);
			else if (i == 12)
				System.out.println("53->1:" + deltaCache[53][1]);
			else if (i == 13)
				System.out.println("54->1:" + deltaCache[54][1]);

			if (max > 0.0D) {
				// System.out.println("Before Updating: " + this.getPhi(groups, unions));
				this.updateGroups(groupIndex, messages, groups, unions, messageIndex, targetGroup, deltaCache);
				// System.out.println("After Updating: " + this.getPhi(groups, unions));
			} else {
				break;
			}
			System.out.println("Round: " + i + " best Phi value: " + this.getPhi(groups, unions));
		}
		return this.getPhi(groups, unions);
	}

	/**
	 * @Title: getBestGroupNum
	 * @Description: 穷举获取最佳分组数
	 * @param messages
	 *            日志集合
	 * @param groupIndex
	 *            每条日志对应的组号数组
	 * @return 获取最佳分组数
	 * @return: int
	 */
	private int getBestGroupNum(List<String> messages, List<Integer> groupIndex) {
		double maxPhi = 0.0D;
		int maxGroup = 1;
		for (int groupNum = 2; groupNum <= 10; groupNum++) {
			List<List<String>> init = this.randomInit(messages, groupNum, groupIndex); // 二维存储分组后的日志
			List<Map<String, Integer>> unions = new ArrayList<Map<String, Integer>>(); // 每组对应的term对集合
			for (int i = 0; i < groupNum; i++) {
				unions.add(this.getGroupTermPairUnion(init.get(i)));
			}
			System.out.println("分组数为 " + groupNum + " 的随机初始分数: " + this.getPhi(init, unions));
			double ls = this.localSearch(groupIndex, messages, init, unions, 20000);
			if ((ls / (double) groupNum) > maxPhi) {
				maxPhi = ls / (double) groupNum;
				maxGroup = groupNum;
			}
		}
		List<List<String>> init = this.randomInit(messages, maxGroup, groupIndex); // 二维存储分组后的日志
		List<Map<String, Integer>> unions = new ArrayList<Map<String, Integer>>(); // 每组对应的term对集合
		for (int i = 0; i < maxGroup; i++) {
			unions.add(this.getGroupTermPairUnion(init.get(i)));
		}
		double finalLs = this.localSearch(groupIndex, messages, init, unions, 20000);
		System.out.println("最佳划分组数: " + maxGroup);
		System.out.println("最佳匹配值: " + finalLs);
		for (int i = 0; i < maxGroup; i++) {
			String filePath = this.groupPrefix + i + ".txt";
			IOHelper.getInstance().writeToFile(init.get(i), filePath, false);
		}
		return maxGroup;
	}

	/**
	 * @Title: logSigGrouping
	 * @Description: 执行logSig的分组算法
	 * @return: void
	 */
	public void logSigGrouping() {
		List<String> messages = IOHelper.getInstance().readFromFile(this.sourcePath); // 所有日志信息的数组
		messages = this.pretreat(messages);
		int len = messages.size();
		System.out.println("日志数: " + len);
		List<Integer> groupIndex = Arrays.asList(new Integer[len]); // 日志数组每条日志对应的组号
		this.getBestGroupNum(messages, groupIndex);
	}

	private List<String> pretreat(List<String> messages) {
		List<String> result = new ArrayList<String>();
		messages.forEach(item -> {
			JSONObject json = JSONObject.fromObject(item);
			result.add(json.getString("@message"));
		});
		return result;
	}

	/**
	 * @Title: getGroupSign
	 * @Description: 根据分组获取该组的签名
	 * @param group
	 *            特定组日志集合
	 * @return 该组的签名
	 * @return: String
	 */
	public String getGroupSign(List<String> group) {
		StringBuilder result = new StringBuilder();
		Map<String, Integer> union = this.getGroupTermUnion(group);
		for (Map.Entry<String, Integer> entry : union.entrySet()) {
			if (entry.getValue() * 2 > group.size()) {
				result.append(entry.getKey()).append(" ");
			}
		}
		return result.toString();
	}

	public static void main(String[] args) {
		TestLogSig logSig = new TestLogSig("RHZZ-syslog.txt", "RHZZ-syslog");
		logSig.logSigGrouping();
		// for (int i = 0; i < 9; i++) {
		// System.out.println("Group " + i + "\'s Signature:");
		// System.out.println(logSig.getGroupSign(IOHelper.getInstance().readFromFile("mobileagt"
		// + i + ".txt")));
		// }
	}

}
