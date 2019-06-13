package priv.wangao.LogAnalysis.service;

import java.util.List;

import priv.wangao.LogAnalysis.constant.Common;
import priv.wangao.LogAnalysis.util.IOHelper;
import priv.wangao.LogAnalysis.util.RegExpHelper;
import priv.xbchen.LogAnalysis.ngtree.MatlabTool;
import priv.xbchen.LogAnalysis.ngtree.NGramModel;
import priv.xbchen.LogAnalysis.ngtree.PredictResult;

public class SeqPredict {

	private static class LazyHolder {
		private static final SeqPredict INSTANCE = new SeqPredict();
	}
	
	public static SeqPredict getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	public void buildModel() {
		MatlabTool mlt = new MatlabTool();
		 List<String> logSeqList = IOHelper.getInstance().readFromFile(Common.DRAIN_RESULT_PATH + "\\sequence.log");
		 String[] logSequence = new String[logSeqList.size()];
		 for (int i = 0; i < logSeqList.size(); i ++) {
			 logSequence[i] = new String(logSeqList.get(i));
		 }
		 NGramModel<String> ngm_src = mlt.getStringModel();
		 ngm_src.train(logSequence);
		 
		 String fn = "web_ngram_model.ser";
	     ngm_src.saveModel(fn);
	     System.out.println("Model saved.");
	}
	
	public List<String> predict(String[] sequence) {
		String fn = "web_ngram_model.ser";
		NGramModel<String> ngm = new NGramModel<String>().loadModel(fn);
		PredictResult<String> pdt = ngm.predict(sequence);
		 List<String> res = pdt.display();
		return res;
	}
	
	public String[] logToNum(String[] logSeq) {
		String[] numSeq = new String[logSeq.length];
		List<String> regexList = IOHelper.getInstance().readFromFile(Common.DRAIN_RESULT_PATH + "\\group\\groups.log");
		for (int i = 0; i < logSeq.length; i++) {
			numSeq[i] = String.valueOf(0);
			for (int j = 0; j < regexList.size(); j++) {
				boolean isMatch = RegExpHelper.getInstance().isMatch(regexList.get(j), logSeq[i]);
				if (isMatch) {
					numSeq[i] = String.valueOf(j+1);
					break;
				}
			}
		}
		return numSeq;
	}
}
