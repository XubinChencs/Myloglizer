package priv.wangao.LogAnalysis.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

import com.mysql.fabric.xmlrpc.base.Array;
import com.tdunning.math.stats.ArrayDigest;

import net.sf.json.JSONObject;
import priv.wangao.LogAnalysis.constant.Common;
import priv.wangao.LogAnalysis.data.dao.LogDAO;
import priv.wangao.LogAnalysis.data.factory.LogGenFactory;
import priv.wangao.LogAnalysis.util.IOHelper;
import priv.wangao.LogAnalysis.util.RegExpHelper;

public class IPStatistics {

	private static String[] filePath = { "act\\act-nat-2018.10.29.txt", "act\\act-nat-2018.10.30.txt",
			"act\\act-nat-2018.10.31.txt", "act\\act-nat-2018.11.01.txt", "act\\act-nat-2018.11.02.txt",
			"act\\act-nat-2018.11.03.txt", "act\\act-nat-2018.11.04.txt", };

	private static Set<String> outer = new HashSet<String>();
	private static Set<String> inner = new HashSet<String>();

	private static Map<String, Integer> edgeCnt = new HashMap<String, Integer>();

	public IPStatistics() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LogDAO logGen = LogGenFactory.build("act-multi-logs", "192.168.105.132:9300");
		
		List<String> all = new ArrayList<String>(Arrays.asList(logGen.getIndices()));
		System.err.println(all.size());
		List<String> remove = Arrays.asList(logGen.getIndices(new String[] {
			".*-2018.11.12"
		}));
		all.removeAll(remove);
		System.err.println(String.join(",", all.toArray(new String[] {})));
//		Pattern pattern = Pattern.compile(RegExpHelper.ACT_NAT);
//		JSONObject json = null;
//		String rawMessage = null;
//		String edge = null;
//		List<String> context = null;
//		for (String path : filePath) {
//			context = IOHelper.getInstance().readFromFile(path);
//			for (String line : context) {
//				json = JSONObject.fromObject(line);
//				if (json.containsKey("message")) {
//					rawMessage = json.getString("message");
//				} else if (json.containsKey("act")) {
//					rawMessage = json.getString("act");
//				} else {
//					System.err.println("error type 1 message: " + line);
//				}
//
//				Matcher matcher = pattern.matcher(rawMessage);
//				if (matcher.find()) {
//					outer.add(matcher.group(2));
//					inner.add(matcher.group(3));
//					edge = matcher.group(2) + "-" + matcher.group(3);
//					if (!edgeCnt.containsKey(edge)) {
//						edgeCnt.put(edge, 1);
//					} else {
//						edgeCnt.put(edge, edgeCnt.get(edge) + 1);
//					}
//				} else {
//					System.err.println("error type 2 message: " + line);
//				}
//			}
//
//		}
//
//		Set<String> mIPs = new HashSet<String>(IOHelper.getInstance().readFromCSV("ips_list.csv", 1, ","));
//
//		List<String> edgeSet = new ArrayList<String>(edgeCnt.keySet());
//
//		Collections.sort(edgeSet, new Comparator<String>() {
//
//			@Override
//			public int compare(String o1, String o2) {
//				// TODO Auto-generated method stub
//				return edgeCnt.get(o2) - edgeCnt.get(o1);
//			}
//
//		});
//
//		for (String key : edgeSet) {
//			String[] split = key.split("-");
//			if (mIPs.contains("\"" + split[0] + "\"")) {
//				IOHelper.getInstance().writeToFile(
//						split[0] + "\t" + split[1] + "\t" + edgeCnt.get(key) + "\t" + "Y" + Common.LOCAL_LINE_SEPARATOR,
//						"analysis\\new-nat-2018.10.29-11.04.txt", true);
//			} else {
//				IOHelper.getInstance().writeToFile(
//						split[0] + "\t" + split[1] + "\t" + edgeCnt.get(key) + "\t" + "N" + Common.LOCAL_LINE_SEPARATOR,
//						"analysis\\new-nat-2018.10.29-11.04.txt", true);
//			}
//		}
	}

}
