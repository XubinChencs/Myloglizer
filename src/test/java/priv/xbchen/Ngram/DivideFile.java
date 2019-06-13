package priv.xbchen.Ngram;

import java.util.ArrayList;
import java.util.List;

import priv.wangao.LogAnalysis.util.IOHelper;


//将正常日志集划分为训练集和测试集
public class DivideFile {
	public static void main(String[] args) {
		List<String> eventIds = IOHelper.getInstance().readFromFile("E:\\logData\\abnormal-authlog\\all_normal_id.txt");
		int i = 0;
		double trainRatio = 0.9;
		List<String> trainList = new ArrayList<String>();
		List<String> testList = new ArrayList<String>();
		for (; i < eventIds.size() * trainRatio;i++) {
			trainList.add(eventIds.get(i));
		}
		for (;i < eventIds.size(); i++) {
			testList.add(eventIds.get(i));
		}
		IOHelper.getInstance().writeToFile(trainList, "E:\\logData\\abnormal-authlog\\train_normal_id.txt", false);
		IOHelper.getInstance().writeToFile(testList, "E:\\logData\\abnormal-authlog\\test_normal_id.txt", false);
	}
	
}
