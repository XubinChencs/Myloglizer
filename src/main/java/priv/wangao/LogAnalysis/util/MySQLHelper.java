package priv.wangao.LogAnalysis.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import priv.wangao.LogAnalysis.constant.Common;

/**
 * @author WangAo MySQL 帮助类
 */
public class MySQLHelper {

	private static final String driver_class = "com.mysql.jdbc.Driver";

	private Connection conn = null;
	private PreparedStatement pst = null;
	private ResultSet rst = null;

	/**
	 * @Title:MySQLHelper
	 * @Description: 单例模式饿汉模式
	 */
	private MySQLHelper() {
		this.conn = this.getConnection();
	}
	
	@Override
	public void finalize() throws Throwable {
		super.finalize();
		this.clone();
	}
	
	public void close() {
		try {
			if (pst != null) {
				pst.close();
			}
			if (rst != null) {
				rst.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Connection getConnection() {
		if (this.conn == null) {
			try {
				Class.forName(driver_class);
				this.conn = DriverManager.getConnection(Common.DEFAULT_URL, Common.DEFAULT_USER,
						Common.DEFAULT_PASSWORD);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return this.conn;
	}

	/** 
	* @Title: executeQuery 
	* @Description: 执行查询返回结果集 
	* @param sql sql 语句
	* @param sqlValues 查询值
	* @return 结果集
	* @return: ResultSet
	*/ 
	public ResultSet executeQuery(String sql, ArrayList<String> sqlValues) {
		try {
			this.pst = this.conn.prepareStatement(sql);
			if (sqlValues != null && sqlValues.size() > 0) {
				for (int i = 0; i < sqlValues.size(); i++) {
					this.pst.setObject(i + 1, sqlValues.get(i));
				}
			}
			this.rst = pst.executeQuery();
		} catch (SQLException e) {
		}

		return this.rst;
	}

}
