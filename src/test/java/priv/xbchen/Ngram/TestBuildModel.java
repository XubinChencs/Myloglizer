package priv.xbchen.Ngram;

import java.util.List;

import priv.wangao.LogAnalysis.constant.Common;
import priv.wangao.LogAnalysis.util.IOHelper;
import priv.xbchen.LogAnalysis.ngtree.MatlabTool;
import priv.xbchen.LogAnalysis.ngtree.NGramModel;

//建立一个模型
public class TestBuildModel {
	 public static void main(String[] args) {
		 MatlabTool mlt = new MatlabTool();
		 List<String> logSeqList = IOHelper.getInstance().readFromFile(Common.DRAIN_RESULT_PATH + "\\sequence.log");
		 String[] logSequence = new String[logSeqList.size()];
		 for (int i = 0; i < logSeqList.size(); i ++) {
			 logSequence[i] = new String(logSeqList.get(i));
		 }
		 NGramModel<String> ngm_src = mlt.getStringModel();
		 ngm_src.train(logSequence);
		 
		 String fn = "ngram_model.ser";
	     ngm_src.saveModel(fn);
	     System.out.println("Model saved.");
	 }
}
