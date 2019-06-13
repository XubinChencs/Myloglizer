package priv.wangao.LogAnalysis.data.factory;

import priv.wangao.LogAnalysis.data.dao.LogDAO;
import priv.wangao.LogAnalysis.data.dao.impl.LogDAOImpl;

public class LogGenFactory {

	public LogGenFactory() {
		// TODO Auto-generated constructor stub
	}

	public static LogDAO build(String clusterName, String addrs) {
		return new LogDAOImpl(clusterName, addrs);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
