package priv.wangao.LogAnalysis.util;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.json.JSONObject;

import priv.wangao.LogAnalysis.constant.Common;

/**
 * @author WangAo
 *
 */
public class EsHelper {

	// client : Es客户端
	private TransportClient client = null;
	// cluster : 集群名称
	private String cluster = null;
	// addrs : 集群地址数组
	private List<String> addrs = new ArrayList<String>();
	// DEFAULT_BATCHSIZE : 滚动查询批量默认大小
	private final int DEFAULT_BATCHSIZE = 10000;
	// DEFAULT_BATCHSIZE : 滚动查询默认超时时间
	private final int DEFAULT_TTLSECOND = 1200;
	// batchSize : 滚动查询批量大小
	private int batchSize;
	// ttlSecond : 滚动查询超时时间
	private int ttlSecond;
	// updateList : 更新请求列表 
	private static List<UpdateRequest> updateList = new ArrayList<UpdateRequest>();
	private static List<IndexRequest> insertList = new ArrayList<IndexRequest>(); 
	/**
	 * @Title:EsHelper
	 * @Description: 指定集群名称和集群地址构建客户端的构造函数
	 * @param cluster
	 *            集群名称
	 * @param addrs
	 *            集群地址
	 * @throws Exception
	 *             集群地址无效时，抛出异常
	 */
	public EsHelper(String cluster, String addrs) throws Exception {
		updateList.clear();
		String[] split = addrs.split(";");

		if (split == null || split.length == 0) {
			throw new Exception("无效的集群地址");
		}

		for (String addr : split) {
			if (RegExpHelper.getInstance().isAddr(addr) == false) {
				throw new Exception("无效的地址: " + addr);
			}
			this.addrs.add(addr);
		}

		this.cluster = cluster;
		this.client = this.getClientInstance();
		this.batchSize = DEFAULT_BATCHSIZE;
		this.ttlSecond = DEFAULT_TTLSECOND;
	}

	/**
	 * @Title: getClientInstance
	 * @Description: 获取ES客户端实例
	 * @return ES客户端实例
	 * @throws NumberFormatException
	 * @throws UnknownHostException
	 * @return: TransportClient
	 */
	private TransportClient getClientInstance() throws NumberFormatException, UnknownHostException {
		if (this.client == null) {
			Settings settings = Settings.builder().put("cluster.name", this.cluster).build();
			// this.client = new PreBuiltTransportClient(settings);
			this.client = TransportClient.builder().settings(settings).build();

			for (String add : addrs) {
				String[] split = add.split(":");
				// this.client.addTransportAddress(
				// new TransportAddress(InetAddress.getByName(split[0]),
				// Integer.parseInt(split[1])));
				this.client.addTransportAddress(
						new InetSocketTransportAddress(InetAddress.getByName(split[0]), Integer.parseInt(split[1])));

			}
		}
		return this.client;
	}

	/**
	 * @Title: getBatchSize
	 * @Description: 获取批量查询超时时间
	 * @return 批量查询超时时间
	 * @return: int
	 */
	public int getBatchSize() {
		return batchSize;
	}

	/**
	 * @Title: setBatchSize
	 * @Description: 设置批量查询超时时间
	 * @param batchSize
	 *            批量查询超时时间
	 * @return: void
	 */
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	/**
	 * @Title: getTtlSecond
	 * @Description: 获取滚动查询超时时间
	 * @return 滚动查询超时时间
	 * @return: int
	 */
	public int getTtlSecond() {
		return ttlSecond;
	}

	/**
	 * @Title: setTtlSecond
	 * @Description: 设置滚动查询超时时间
	 * @param ttlSecond
	 *            滚动查询超时时间
	 * @return: void
	 */
	public void setTtlSecond(int ttlSecond) {
		this.ttlSecond = ttlSecond;
	}

	/**
	 * @Title: executeGetIndices
	 * @Description: 根据正则表达式获取索引名称，如果表达式 数组为 null，则返回所有的索引名称
	 * @param patterns
	 *            正则表达式数组
	 * @return 索引名称列表
	 * @return: List<String>
	 */
	public String[] executeGetIndices(String[] patterns) {
		String[] indices = this.client.admin().indices().prepareGetIndex().execute().actionGet().indices();
		if (patterns != null && patterns.length > 0) {
			List<String> result = new ArrayList<String>();
			for (String index : indices) {
				if (RegExpHelper.getInstance().isMatch(patterns, index)) {
					result.add(index);
				}
			}
			indices = result.toArray(new String[result.size()]);
		}
		return indices;
	}

	/**
	 * @Title: executeDeleteIndices
	 * @Description: 根据索引名称数组删除索引
	 * @param indices
	 *            索引名称数组
	 * @return 是否删除成功，true 表示成功，false 表示失败
	 * @return: boolean
	 */
	public boolean executeDeleteIndices(String[] indices) {
		return this.client.admin().indices().prepareDelete(indices).execute().actionGet().isAcknowledged();
	}

	/**
	 * @Title: executeMatchAllQuery
	 * @Description: 执行无条件查询
	 * @param indices
	 *            索引名
	 * @param includes
	 *            包含列
	 * @param excludes
	 *            不包含列
	 * @param outputPath
	 *            输出路径
	 * @param maxCnt
	 *            获取最大个数限制
	 * @return: void
	 */
	public void executeMatchAllQuery(String[] indices, String[] includes, String[] excludes, String outputPath,
			int maxCnt) {
		SearchRequestBuilder searchQuery = indices == null ? this.client.prepareSearch()
				: this.client.prepareSearch(indices);
		searchQuery.setFetchSource(includes, excludes);
		searchQuery.addSort("_doc", SortOrder.ASC).setScroll(TimeValue.timeValueSeconds(this.ttlSecond))
				.setQuery(this.matchAllQuery());

		SearchResponse scrollResp = searchQuery.get();
		doScroll(scrollResp, outputPath, maxCnt);
	}

	/**
	 * @Title: executeRangeTermFilter
	 * @Description: 执行范围查询
	 * @param indices
	 *            索引名
	 * @param term
	 *            目标字段
	 * @param from
	 *            起始值，为 null 则从最初开始
	 * @param to
	 *            终止值，为 null 则到最后结束
	 * @param includes
	 *            包含列
	 * @param excludes
	 *            不包含列
	 * @param outputPath
	 *            输出路径
	 * @param maxCnt
	 *            获取最大个数限制
	 * @return 
	 * @return: String
	 */
	public String executeRangeTermFilter(String[] indices, String term, String from, String to, String[] includes,
			String[] excludes, String outputPath, int maxCnt) {
		// TODO Auto-generated method stub
		SearchRequestBuilder searchQuery = indices == null ? this.client.prepareSearch()
				: this.client.prepareSearch(indices);
		searchQuery.setFetchSource(includes, excludes);
		searchQuery.setScroll(TimeValue.timeValueSeconds(this.ttlSecond)).setQuery(this.rangeQuery(term, from, to));
		SearchResponse scrollResp = searchQuery.get();
		return doScroll(scrollResp, outputPath, maxCnt);
	}

	/**
	 * @Title: executeTermsFilter
	 * @Description: 有条件过滤查询
	 * @param indices
	 *            索引名
	 * @param terms
	 *            Map 结构的过滤条件
	 * @param sorts
	 *            排序方式
	 * @param includes
	 *            包含列
	 * @param excludes
	 *            不包含列
	 * @param outputPath
	 *            输出路径
	 * @param maxCnt
	 *            获取最大个数限制
	 * @return: void
	 */
	public void executeTermsFilter(String[] indices, Map<String, String> terms, Map<String, String> sorts,
			String[] includes, String[] excludes, String outputPath, int maxCnt) {
		SearchRequestBuilder searchQuery = indices == null ? this.client.prepareSearch()
				: this.client.prepareSearch(indices);
		searchQuery.setFetchSource(includes, excludes);
		searchQuery.setScroll(TimeValue.timeValueSeconds(this.ttlSecond)).setQuery(this.multTermQuery(terms));

		for (Map.Entry<String, String> sort : sorts.entrySet()) {
			if (sort.getValue().equals("desc")) {
				searchQuery.addSort(sort.getKey(), SortOrder.DESC);
			} else {
				searchQuery.addSort(sort.getKey(), SortOrder.ASC);
			}
		}
		SearchResponse scrollResp = searchQuery.get();
		doScroll(scrollResp, outputPath, maxCnt);
	}

	/**
	 * @Title: executeContainTermFilter
	 * @Description: 数组查询某一字段是否在指定范围内
	 * @param indices
	 *            索引名
	 * @param key
	 *            查找字段名称
	 * @param values
	 *            查找值集合
	 * @param sorts
	 *            排序方式
	 * @param includes
	 *            包含列
	 * @param excludes
	 *            不包含列
	 * @param outputPath
	 *            输出路径
	 * @param maxCnt
	 *            获取最大个数限制
	 * @return: void
	 */
	public void executeContainTermFilter(String[] indices, String key, List<String> values, Map<String, String> sorts,
			String[] includes, String[] excludes, String outputPath, int maxCnt) {
		SearchRequestBuilder searchQuery = indices == null ? this.client.prepareSearch()
				: this.client.prepareSearch(indices);
		searchQuery.setFetchSource(includes, excludes);
		searchQuery.setScroll(TimeValue.timeValueSeconds(this.ttlSecond)).setQuery(this.containTermQuery(key, values));

		for (Map.Entry<String, String> sort : sorts.entrySet()) {
			if (sort.getValue().equals("desc")) {
				searchQuery.addSort(sort.getKey(), SortOrder.DESC);
			} else {
				searchQuery.addSort(sort.getKey(), SortOrder.ASC);
			}
		}
		SearchResponse scrollResp = searchQuery.get();
		doScroll(scrollResp, outputPath, maxCnt);
	}

	/**
	 * @Title: doScroll
	 * @Description: 执行滚动查询
	 * @param scrollResp
	 *            滚动查询请求结果
	 * @param outputPath
	 *            结果输出路径
	 * @param maxCnt
	 *            获取最大个数限制
	 * @return: void
	 */
	private String doScroll(SearchResponse scrollResp, String outputPath, int maxCnt) {
		if (outputPath != null) {
			File file = new File(outputPath);
			if (file.exists() == true) {
				file.delete();
			}
		}
		
		String result = null;

		System.out.println("命中总数量：" + scrollResp.getHits().getTotalHits());

		ExecutorService executorService = Executors.newFixedThreadPool(4);
		List<String> batch = new ArrayList<String>();

		int count = 0;
		do {
			batch.clear();
			for (SearchHit hit : scrollResp.getHits().getHits()) {
				JSONObject jsStr = new JSONObject(hit.getSourceAsString());
				jsStr.append("_id", hit.getId());
				jsStr.append("_index", hit.getIndex());
				jsStr.append("_type", hit.getType());
				//System.out.println(count + "->" + jsStr.get("message"));
				System.out.println(jsStr.get("program") + " " + count + "->" + jsStr.get("message"));
				String program = (String)jsStr.get("program");
				if (outputPath != null) {
					IOHelper.getInstance().writeToFile((String)jsStr.get("message") + Common.LOCAL_LINE_SEPARATOR, outputPath + program.replace("/", "-") + ".log", true);
					batch.add((String)jsStr.get("message"));
					//batch.add(hit.getSourceAsString());
					result = (String) hit.getSource().get("@timestamp");
				}
				count++;
				if (maxCnt > 0 && count >= maxCnt) {
					break;
				}
			}
			if (outputPath != null) {
				IOHelper.getInstance().writeToFile(batch, outputPath + "all.log", true);
			}

			if (maxCnt > 0 && count >= maxCnt) {
				break;
			}
			scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
					.setScroll(TimeValue.timeValueSeconds(this.ttlSecond)).execute().actionGet();
		} while (scrollResp.getHits().getHits().length != 0);

		executorService.shutdown();
		
		return result;
	}

	/**
	 * @Title: matchAllQuery
	 * @Description: 构造无条件查询语句
	 * @return 无条件查询语句
	 * @return: QueryBuilder
	 */
	private QueryBuilder matchAllQuery() {
		return QueryBuilders.matchAllQuery();
	}

	private QueryBuilder rangeQuery(String term, String from, String to) {
		RangeQueryBuilder result = QueryBuilders.rangeQuery(term);
		if (from != null) {
			result.from(from);
		}

		if (to != null) {
			result.to(to);
		}

		return result;
	}

	/**
	 * @Title: multTermQuery
	 * @Description: 根据传入的 Map 构造多短语过滤查询语句
	 * @param terms
	 *            Map 结构的查询条件
	 * @return 多短语过滤查询语句
	 * @return: QueryBuilder
	 */
	private QueryBuilder multTermQuery(Map<String, String> terms) {
		BoolQueryBuilder result = QueryBuilders.boolQuery();
		if (terms != null) {
			for (Map.Entry<String, String> entry : terms.entrySet()) {
				result.filter(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
			}
		}

		return result;
	}

	private QueryBuilder containTermQuery(String key, List<String> values) {
		BoolQueryBuilder result = QueryBuilders.boolQuery();
		if (values != null) {
			for (String value : values) {
				result.should(QueryBuilders.termQuery(key, value));
			}
		}
		return result;
	}

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
	public void addUpdateRequest(String index, String type, String id, String field, String value) {
		UpdateRequest uRequest = new UpdateRequest()
                .index(index)
                .type(type)
                .id(id)
                .doc(field, value);
		updateList.add(uRequest);
		//client.update(uRequest).get(); 单次提交
	}
	
	/**
	 * @Title: updateRequests
	 * @Description: 批量执行更新列表中的请求
	 */
	public void updateRequests() {
		 BulkRequestBuilder bulkRequest = client.prepareBulk();
	     for (UpdateRequest uprequest : updateList) {
	         bulkRequest.add(uprequest);
	     }
	     BulkResponse bulkResponse = bulkRequest.execute().actionGet();
	     if (bulkResponse.hasFailures()) {
	         System.out.println("批量错误！");
	     }
	     updateList.clear();
	}
	
	public void addInsertRequest(String index, String type, XContentBuilder builder) {
		IndexRequest iRequest = new IndexRequest(index, type)
                .source(builder);
		insertList.add(iRequest);
		//client.update(uRequest).get(); 单次提交
	}
	
	public void insertRequests() {
		 BulkRequestBuilder bulkRequest = client.prepareBulk();
	     for (IndexRequest iprequest : insertList) {
	         bulkRequest.add(iprequest);
	     }
	     BulkResponse bulkResponse = bulkRequest.execute().actionGet();
	     if (bulkResponse.hasFailures()) {
	         System.out.println("批量错误！");
	     }
	     insertList.clear();
	}
	
	synchronized public boolean isTypeExists(String index, String type, Map<String, String> map) {
		IndicesExistsRequest request = new IndicesExistsRequest(index);
		IndicesExistsResponse response = client.admin().indices().exists(request).actionGet();
		if(response.isExists() == false) {
			executrCreateIndex(index, type, map);
		}
		return response.isExists();
	}
	
	public CreateIndexResponse executrCreateIndex(String index, String type, Map<String, String> map) {
		XContentBuilder builder = null;
		try {
			builder = XContentFactory.jsonBuilder().startObject().startObject(type).startObject("properties");
			for (Map.Entry<String, String> entry : map.entrySet()) {
				builder.startObject(entry.getKey()).field("type", entry.getValue());
				if (entry.getValue().equals("date") == true) {
					builder.field("format", "yyyy-MM-dd'T'HH:mm:ss");
				}
				builder.endObject();
			}
			builder.endObject().endObject().endObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Settings settings = Settings.builder().put("number_of_shards", 5).put("number_of_replicas", 0).build();

		return client.admin().indices().prepareCreate(index).addMapping(type, builder).setSettings(settings).execute()
				.actionGet();
	}

	public void executeInsert(String index, String type, XContentBuilder builder) {
		IndexResponse response = client.prepareIndex(index, type).setSource(builder).get();
		//RestStatus status = ((StatusToXContent) response).status();
	}
	
	public TransportClient getClient() {
		return this.client;
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		EsHelper esHelper = new EsHelper("my-cluster", "192.168.1.78:9300");
		Map<String, String> terms = new HashMap<String, String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				put("host", "192.168.1.102");
			}
		};
		Map<String, String> sorts = new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				put("timestamp", "asc");
			}
		};
		esHelper.executeTermsFilter(null, terms, sorts, new String[] { "@message", "timestamp" }, null,
				"RHZZ-syslog.txt", 300000);
//		EsHelper esHelper = new EsHelper("act-logger-es", "10.1.1.205:9300");
//		esHelper.addRequest("act-shahe-hp-inspur-2018.12.11", "syslog", "AWeakRzSZpHnQVlAaQpB", 
//								"eventType", "123");
//		esHelper.addRequest("act-shahe-hp-inspur-2018.12.11", "syslog", "AWeakRzSZpHnQVlAaQpB", 
//				"message", "456");
//		esHelper.updateRequests();
		
	}

}
