package priv.wangao.LogAnalysis.data.dao.impl;

import java.util.List;
import java.util.Map;

import priv.wangao.LogAnalysis.data.dao.LogDAO;
import priv.wangao.LogAnalysis.util.EsHelper;

public class LogDAOImpl implements LogDAO {

	private EsHelper esHelper;

	public LogDAOImpl(String clusterName, String addrs) {
		try {
			this.esHelper = new EsHelper(clusterName, addrs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * priv.wangao.LogAnalysis.data.dao.LogDAO#matchAllEntry(java.lang.String[],
	 * java.lang.String[], java.lang.String[], java.lang.String, int)
	 */
	@Override
	public void matchAllEntry(String[] indices, String[] includes, String[] excludes, String outputPath, int maxCnt) {

		this.esHelper.executeMatchAllQuery(indices, includes, excludes, outputPath, maxCnt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * priv.wangao.LogAnalysis.data.dao.LogDAO#filterTermsEntry(java.lang.String[],
	 * java.util.Map, java.util.Map, java.lang.String[], java.lang.String[],
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void filterTermsEntry(String[] indices, Map<String, String> terms, Map<String, String> sorts,
			String[] includes, String[] excludes, String outputPath, int maxCnt) {
		this.esHelper.executeTermsFilter(indices, terms, sorts, includes, excludes, outputPath, maxCnt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see priv.wangao.LogAnalysis.data.dao.LogDAO#getIndices()
	 */
	@Override
	public String[] getIndices() {
		return this.esHelper.executeGetIndices(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see priv.wangao.LogAnalysis.data.dao.LogDAO#getIndices(java.lang.String[])
	 */
	@Override
	public String[] getIndices(String[] patterns) {
		return this.esHelper.executeGetIndices(patterns);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * priv.wangao.LogAnalysis.data.dao.LogDAO#deleteIndices(java.lang.String[])
	 */
	@Override
	public boolean deleteIndices(String[] indices) {
		return this.esHelper.executeDeleteIndices(indices);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * priv.wangao.LogAnalysis.data.dao.LogDAO#containTermEntry(java.lang.String[],
	 * java.lang.String, java.util.List, java.util.Map, java.lang.String[],
	 * java.lang.String[], java.lang.String, int)
	 */
	@Override
	public void containTermEntry(String[] indices, String key, List<String> values, Map<String, String> sorts,
			String[] includes, String[] excludes, String outputPath, int maxCnt) {
		this.esHelper.executeContainTermFilter(indices, key, values, sorts, includes, excludes, outputPath, maxCnt);
	}

	/* (non-Javadoc)
	 * @see priv.wangao.LogAnalysis.data.dao.LogDAO#rangeEntry(java.lang.String[], java.lang.String, java.lang.String, java.lang.String, java.lang.String[], java.lang.String[], java.lang.String, int)
	 */
	@Override
	public String rangeEntry(String[] indices, String term, String from, String to, String[] includes, String[] excludes,
			String outputPath, int maxCnt) {
		// TODO Auto-generated method stub
		return this.esHelper.executeRangeTermFilter(indices, term, from, to, includes, excludes, outputPath, maxCnt);
	}

	@Override
	public void addUpdateRequest(String index, String type, String id, String field, String value) {
		// TODO Auto-generated method stub
		this.esHelper.addUpdateRequest(index, type, id, field, value);
	}

	@Override
	public void updateRequests() {
		// TODO Auto-generated method stub
		this.esHelper.updateRequests();
	}

}
