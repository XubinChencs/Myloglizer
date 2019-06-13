package priv.wangao.LogAnalysis.data.dao;

import java.util.List;
import java.util.Map;

/**
 * @author WangAo 数据访问接口
 */
public interface LogDAO {

	/**
	 * @Title: matchAllEntry
	 * @Description: 无条件获取指定字段指定大小的数据，输出到目标路径
	 * @param indices
	 *            目标索引，为null则查询所有
	 * @param includes
	 *            需要的字段集
	 * @param excludes
	 *            不需要的字段集
	 * @param outputPath
	 *            目标路径
	 * @param maxCnt
	 *            最大获取长度，为0则获取所有
	 * @return: void
	 */
	void matchAllEntry(String[] indices, String[] includes, String[] excludes, String outputPath, int maxCnt);

	/**
	 * @Title: filterTermsEntry
	 * @Description: 根据特定字段的精确值查询，并指定排序方式
	 * @param indices
	 *            目标索引，为null则查询所有
	 * @param terms
	 *            精确值查询字段集
	 * @param sorts
	 *            需要排序的字段集和排序方式的映射
	 * @param includes
	 *            需要的字段集
	 * @param excludes
	 *            不需要的字段集
	 * @param outputPath
	 *            目标路径
	 * @param maxCnt
	 *            最大获取长度，为0则获取所有
	 * @return: void
	 */
	void filterTermsEntry(String[] indices, Map<String, String> terms, Map<String, String> sorts, String[] includes,
			String[] excludes, String outputPath, int maxCnt);

	/**
	 * @Title: rangeEntry
	 * @Description: 查找指定范围内日志
	 * @param indices
	 *            目标索引，为null则查询所有
	 * @param term
	 *            目标字段
	 * @param from
	 *            起始点
	 * @param to
	 *            终止点
	 * @param includes
	 *            需要的字段集
	 * @param excludes
	 *            不需要的字段集
	 * @param outputPath
	 *            目标路径
	 * @param maxCnt
	 *            最大获取长度，为0则获取所有
	 * @return: void
	 */
	String rangeEntry(String[] indices, String term, String from, String to, String[] includes, String[] excludes,
			String outputPath, int maxCnt);

	/**
	 * @Title: containTermEntry
	 * @Description: 执行数组查询
	 * @param indices
	 *            目标索引，为null则查询所有
	 * @param key
	 *            查找字段
	 * @param values
	 *            查找值集合
	 * @param sorts
	 *            需要排序的字段集和排序方式的映射
	 * @param includes
	 *            需要的字段集
	 * @param excludes
	 *            不需要的字段集
	 * @param outputPath
	 *            目标路径
	 * @param maxCnt
	 *            最大获取长度，为0则获取所有
	 * @return: void
	 */
	void containTermEntry(String[] indices, String key, List<String> values, Map<String, String> sorts,
			String[] includes, String[] excludes, String outputPath, int maxCnt);

	/**
	 * @Title: getIndices
	 * @Description: 获取所有索引的索引名
	 * @return 索引名数组
	 * @return: String[]
	 */
	String[] getIndices();

	/**
	 * @Title: getIndices
	 * @Description: 获取索引名命中正则表达式的索引名
	 * @param patterns
	 *            正则表达式数组
	 * @return 索引名数组
	 * @return: String[]
	 */
	String[] getIndices(String[] patterns);

	/**
	 * @Title: deleteIndices
	 * @Description: 根据索引名称数组删除指定索引
	 * @param indices
	 *            索引名称数组
	 * @return
	 * @return: boolean
	 */
	boolean deleteIndices(String[] indices);
	
	/**
	 * @Title: addRequest
	 * @Description: 根据传入的索引名，类型名，文档编号更新某个字段的值，将更新请求加入列表
	 * @param index  ES中的索引_index
	 * @param type   ES中的类型_type
	 * @param id     ES中的编号_id
	 * @param field  需要更新的字段名
	 * @param value  新的字段值
	 * @return: void
	 */
	public void addUpdateRequest(String index, String type, String id, String field, String value);
	
	/**
	 * @Title: updateRequests
	 * @Description: 批量执行更新列表中的请求
	 */
	public void updateRequests();

}
