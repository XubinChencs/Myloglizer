package priv.xbchen.Ngram;

import java.util.List;

import priv.wangao.LogAnalysis.util.IOHelper;
import priv.xbchen.LogAnalysis.ngtree.DataInput;
import priv.xbchen.LogAnalysis.ngtree.MatlabTool;
import priv.xbchen.LogAnalysis.ngtree.NGramModel;
import priv.xbchen.LogAnalysis.ngtree.PredictResult;

public class TestGanglia {
	
	public static void testSet(NGramModel<Double> ngm, String testPath) {
		Double[] seq = DataInput.getInstance().gangliaCSV(testPath);
//		for (int i = 0; i < seq.length; i++) {
//			System.out.println(seq[i]);
//		}
		System.out.println("accuracy: " +  ngm.getDoubleAccuracy(seq) + "%");
	}
	
	
	public static void main(String[] args) {
		 MatlabTool mlt = new MatlabTool();
		 NGramModel<Double> ngm_src = mlt.getDoubleModel();
		 Double[] sequence = DataInput.getInstance().gangliaCSV("E:\\logData\\ganglia\\part4 test cluster\\test cluster\\test01.test.ivic.org.cn\\csv 15s\\bytes_in.csv");
		 ngm_src.train(sequence);
		 
		 String fn = "ganglia_ngram_model.ser";
	     ngm_src.saveModel(fn);
	     System.out.println("Model saved.");
	     
//	     Double[] test_seq = {199.986,
//	    		 			  199.986,
//	    		 			  0.0};
//	     PredictResult<Double> pdt = ngm_src.predict(test_seq);
//	     pdt.display();
	     testSet(ngm_src, "E:\\logData\\ganglia\\part4 test cluster\\test cluster\\test02.test.ivic.org.cn\\csv 15s\\bytes_in.csv");
	 }
}
