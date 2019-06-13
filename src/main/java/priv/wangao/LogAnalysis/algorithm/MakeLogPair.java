package priv.wangao.LogAnalysis.algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import priv.wangao.LogAnalysis.util.IOHelper;

// 计算日志解析结果的准确率、召回率等。
public class MakeLogPair {
	private static final String FILE_PATH_1 = "E:\\logData\\benchmark\\dataSet\\Android\\"; //标准数据
	private static final String FILE_PATH_2 = "E:\\drain\\group\\"; //待评估数据
	private static long TP = 0;
	private static long FP = 0;
	private static long TN = 0;
	private static long FN = 0;
	private static Map<Integer, Integer> benchMark = new HashMap<Integer, Integer>();  
	private static Map<Integer, Integer> result = new HashMap<Integer, Integer>();
	private static final long DATA_SIZE = 2000;
	private static final int GROUP_SIZE_1 = 166;
	private static final int GROUP_SIZE_2 = 169;
	
	public static void formMaps() {
		for (int i = 1; i <= GROUP_SIZE_1; i++ ) {
			List<String> idList = IOHelper.getInstance().readFromFile(FILE_PATH_1 + String.valueOf(i) + "_id.log");
			for (int j = 0; j < idList.size(); j++) {
				benchMark.put(Integer.parseInt(idList.get(j)), new Integer(i));
			}
			idList.clear();
		}
		
		for (int i = 1; i <= GROUP_SIZE_2; i++ ) {
			List<String> idList = IOHelper.getInstance().readFromFile(FILE_PATH_2 + String.valueOf(i) + "_id.log");
			for (int j = 0; j < idList.size(); j++) {
				if (!idList.get(j).matches("\\d+")) continue;
				result.put(Integer.parseInt(idList.get(j)), new Integer(i));
			}
			idList.clear();
		}
		//System.out.println(result.get(82));
	}
	
	public static void calIndicators() {
		for (int i = 1; i < DATA_SIZE; i++) {
//			if (result.get(i) == null) {
//				System.out.println(i);
//				continue;
//			}
			System.out.println(i);
			for (int j = i + 1; j < DATA_SIZE; j++) {
//				if (result.get(j) == null) {
//					continue;
//				}
				if (result.get(i).equals(result.get(j))) {
					if (benchMark.get(i).equals(benchMark.get(j)))
						TP++;
					else 
						FP++;
				}else {
					if (benchMark.get(i).equals(benchMark.get(j)))
						FN++;
					else 
						TN++;
				}
			}
		}
		double accuracy = (double)(TP + TN) / ( FP + FN + TP + TN);
		double precision = (double) TP / (TP + FP);
		double recall = (double)TP / (TP + FN);
		double FM = 2 * precision * recall / (precision + recall);
		System.out.println("TP: " + TP);
		System.out.println("FP: " + FP);
		System.out.println("TN: " + TN);
		System.out.println("FN: " + FN);
		System.out.println("accuracy: " + accuracy);
		System.out.println("precision: " + precision);
		System.out.println("recall: " + recall);
		System.out.println("FM: " + FM);
	}
	
	public static void main(String[] args) {
		MakeLogPair.formMaps();
		long startTime=System.nanoTime();   //获取开始时间 
		MakeLogPair.calIndicators();
		long endTime=System.nanoTime(); //获取结束时间  
		System.out.println("程序运行时间： "+(endTime-startTime)/1000000000+"s");
	}
	
}
