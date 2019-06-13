package priv.wangao.LogAnalysis.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import priv.wangao.LogAnalysis.constant.Common;

public class HdfsHelper {

	private Configuration conf = null;

	// /**
	// * @Title: getInstance
	// * @Description: 懒汉单例模式 加同步
	// * @return
	// * @return: HdfsHelper
	// */
	// public static synchronized HdfsHelper getInstance() {
	// if (instance == null) {
	// instance = new HdfsHelper();
	// }
	// return instance;
	// }

	public HdfsHelper() {
		this.conf = new Configuration();
		this.conf.set("fs.defaultFS", Common.HDFS_ADDR);
	}

	public HdfsHelper(String url) {
		this.conf = new Configuration();
		this.conf.set("fs.defaultFS", url);
	}

	/**
	 * @Title: put
	 * @Description: 上传文件到指定位置，目标为目录时创建同名文件，目标为文件时写入该文件，文件不存在则创造文件并写入
	 * @param localPath
	 *            本地路径
	 * @param remotePath
	 *            远程路径
	 * @return: void
	 */
	public void put(String localPath, String remotePath) {
		try (FileSystem fs = FileSystem.get(conf);) {
			fs.copyFromLocalFile(new Path(localPath), new Path(remotePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title: upload
	 * @Description: 上传文件到指定位置，目标为目录时创建同名文件，目标为文件时写入该文件，文件不能存在
	 * @param localPath
	 *            本地路径
	 * @param remotePath
	 *            远程路径
	 * @return: void
	 */
	public void upload(String localPath, String remotePath) {
		try (FileSystem fs = FileSystem.get(conf);
				FileInputStream fis = new FileInputStream(new File(localPath));
				FSDataOutputStream fsdos = fs.create(new Path(remotePath));) {
			IOUtils.copyBytes(fis, fsdos, 4096, true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> readFromHdfs(String remotePath) {
		List<String> result = new ArrayList<String>();
		try (
			FileSystem fs = FileSystem.get(conf);
			FSDataInputStream fsdis = fs.open(new Path(remotePath));
		) {
			byte[] byt = new byte[fsdis.available()];
			fsdis.readFully(byt);
			String text = new String(byt);
			result = Arrays.asList(text.split(Common.REMOTE_LINE_SEPARATOR));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
