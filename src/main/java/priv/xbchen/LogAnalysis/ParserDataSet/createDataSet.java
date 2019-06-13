package priv.xbchen.LogAnalysis.ParserDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import priv.wangao.LogAnalysis.util.IOHelper;

public class createDataSet {
	public static void main(String[] args) {
		int index = 0;
		Map<String, Integer> eventToId = new HashMap<String, Integer>();
		String idPath = "E:\\logData\\benchmark\\dataSet\\Zookeeper\\id.csv";
		String exportPath = "E:\\logData\\benchmark\\dataSet\\Zookeeper\\";
		List<Set<String>> groupList = new ArrayList<Set<String>>();
		List<String> idList = IOHelper.getInstance().readFromFile(idPath);
		for (int i = 1; i < idList.size(); i++) {
			String[] split = idList.get(i).split(",");
			if (!eventToId.containsKey(split[1])) {
				eventToId.put(split[1], new Integer(index));
				groupList.add(new HashSet<String>());
				index++;
			}
			groupList.get(eventToId.get(split[1])).add(split[0]);
		}
		List<String> out = new ArrayList<String>();
		System.out.println(groupList.size());
		for (int i = 0; i < groupList.size(); i++) {
			out.clear();
			for (String id : groupList.get(i)) {
				out.add(id);
			}
			IOHelper.getInstance().writeToFile(out, exportPath + (i+1) + "_id.log", false);
		}
	}
}