package priv.xbchen.Ngram;

import java.util.Arrays;
import java.util.List;

import priv.wangao.LogAnalysis.constant.Common;
import priv.wangao.LogAnalysis.service.SeqPredict;
import priv.wangao.LogAnalysis.util.IOHelper;
import priv.xbchen.LogAnalysis.ngtree.MatlabTool;
import priv.xbchen.LogAnalysis.ngtree.NGramModel;
import priv.xbchen.LogAnalysis.ngtree.PredictResult;

public class TestPredict {

	public static int depth = 2;
	
	public static void PredictResult(String normalPath, String abnormalPath) {
		String fn = "ngram_model.ser";
		NGramModel<String> ngm = new NGramModel<String>().loadModel(fn);
		List<String> normalLogs = IOHelper.getInstance().readFromFile(normalPath);
		double FN = 0;
		double TN = 0;
		double TP = 0;
		double FP = 0;
		String[] seq_normal = (String[])normalLogs.toArray(new String[normalLogs.size()]); 
		for (int i = 0; i < seq_normal.length - depth; i++) {
			String[] subSeq = Arrays.copyOfRange(seq_normal, i, i + depth + 1);
			if (!ngm.isSessionNormal(subSeq)) {
				FP += 1;
			}else {
				TN += 1;
			}
		}
		
		List<String> abnormalLogs = IOHelper.getInstance().readFromFile(abnormalPath);
		String[] seq_abnormal = (String[])abnormalLogs.toArray(new String[abnormalLogs.size()]); 
		for (int i = 0; i < seq_abnormal.length - depth; i++) {
			String[] subSeq = Arrays.copyOfRange(seq_abnormal, i, i + depth + 1);
			if (!ngm.isSessionNormal(subSeq)) {
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
	
	public static void Model(String trainPath) {
		MatlabTool mlt = new MatlabTool();
		NGramModel<String> ngm_src = mlt.getStringModel();
		List<String> trainSeq = IOHelper.getInstance().readFromFile(trainPath);
		String[] seq_train = (String[])trainSeq.toArray(new String[trainSeq.size()]); 
		ngm_src.train(seq_train);
		
		String fn = "ngram_model.ser";
	    ngm_src.saveModel(fn);
	    System.out.println("Model saved.");
	}
	
	public static void testSet(NGramModel<String> ngm, String testPath) {
		List<String> logList = IOHelper.getInstance().readFromFile(testPath);
		String[] logs = (String[])logList.toArray(new String[logList.size()]);  
		String[] seq = SeqPredict.getInstance().logToNum(logs);
//		for (int i = 0; i < seq.length; i++) {
//			System.out.println(seq[i]);
//		}
		System.out.println("accuracy: " +  ngm.getAccuracy(seq) + "%");
	}
	
	public static void main(String[] args) {
//		String fn = "ngram_model.ser";
//		NGramModel<String> ngm = new NGramModel<String>().loadModel(fn);
		////////////////////////////////////////////
//		String[] test_seq = {"1", "2", "3"};
//		PredictResult<String> pdt = ngm.predict(test_seq);
//        pdt.display();
//		///////////////////////////////////////////////////////
//        List<String> logSeqList = IOHelper.getInstance().readFromFile(Common.DRAIN_RESULT_PATH + "\\sequence.log");
//		String[] logSequence = new String[logSeqList.size() / 2 + 1];
//		System.out.println(logSequence.length);
//		for (int i = logSeqList.size() / 2, j = 0; i < logSeqList.size(); i ++, j++) {
//			logSequence[j] = new String(logSeqList.get(i));
//		}
//        System.out.println("accuracy: " +  ngm.getAccuracy(logSequence) + "%");
		/////////////////////////////////////////////////////////////////////
		String dir = "E:\\logData\\test-FP\\bd-33-41";
		Model(dir + "\\train_normal_id.txt");
		PredictResult(dir + "\\test_normal_id.txt", dir + "\\test_abnormal_id.txt");
	}
}
