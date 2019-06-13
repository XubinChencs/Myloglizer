package priv.wangao.LogAnalysis.data.acquisition;

import java.io.IOException;
import java.util.List;

/**
 * @author WangAo
 * 数据处理定义流程
 */
interface IDataProcessing {

	void pullFromEs();
	
	void putToHdfs();
	
	void runMR() throws IOException, ClassNotFoundException, InterruptedException;
	
	List<String> getMRResult();
	
	void dealData(List<String> result);
	
	void saveDealResult(List<String> result);
	
}
