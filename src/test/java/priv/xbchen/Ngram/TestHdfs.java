package priv.xbchen.Ngram;

import java.util.List;

import org.apache.thrift.TProcessor;

import priv.wangao.LogAnalysis.util.IOHelper;
import priv.xbchen.LogAnalysis.ngtree.MatlabTool;
import priv.xbchen.LogAnalysis.ngtree.NGramModel;

public class TestHdfs {
	
	
	public static void Model(String trainPath) {
		MatlabTool mlt = new MatlabTool();
		NGramModel<String> ngm_src = mlt.getStringModel();
		List<String> trainSessions = IOHelper.getInstance().readFromFile(trainPath);
		for (int i = 0; i < trainSessions.size(); i++) {
			String[] seq = trainSessions.get(i).split("\\s");
			ngm_src.train(seq);
		}
		
		String fn = "hdfs_ngram_model.ser";
	    ngm_src.saveModel(fn);
	    System.out.println("Model saved.");
	}
	
	
	public static void PredictResult(String normalPath, String abnormalPath) {
		String fn = "hdfs_ngram_model.ser";
		NGramModel<String> ngm = new NGramModel<String>().loadModel(fn);
		List<String> normalSessions = IOHelper.getInstance().readFromFile(normalPath);
		double FN = 0;
		double TN = 0;
		double TP = 0;
		double FP = 0;
		for (int i = 0; i < normalSessions.size(); i++) {
			String[] seq = normalSessions.get(i).split("\\s");
			if (!ngm.isSessionNormal(seq)) {
				FP += 1;
			}else {
				TN += 1;
			}
		}
		
		List<String> abnormalSessions = IOHelper.getInstance().readFromFile(abnormalPath);
		for (int i = 0; i < abnormalSessions.size(); i++) {
			String[] seq = abnormalSessions.get(i).split("\\s");
			if (!ngm.isSessionNormal(seq)) {
				TP += 1;
			}else {
				FN += 1;
			}
		}
		double P = 100 * TP / (TP + FP);
		double R = 100 * TP / (TP + FN);
		double F1 = 2 * P * R / (P + R);
		
		
		System.out.println("True positive (TP): " + TP);
		System.out.println("True negative (TN): " + TN);
		System.out.println("false positive (FP): " + FP);
		System.out.println("false negative (FN): " + FN);
		System.out.println("Precision: " + P + "%");
		System.out.println("Recall: " + R + "%");
		System.out.println("F1-measure: " + F1 + "%");
	}
	
	public static void main(String[] args) {
		Model("data\\hdfs_train");
		PredictResult("data\\hdfs_test_normal", "data\\hdfs_test_abnormal");
	}
}
