package priv.wangao.LogAnalysis.data.dao;

import org.elasticsearch.common.xcontent.XContentBuilder;

public interface EsDao {
	
	public void createIndex(String index, String type, Object content);
	
	public XContentBuilder addEntry(String index, String type, Object content);
	
	public void getEntry();
	
	public void delEntry();
}
