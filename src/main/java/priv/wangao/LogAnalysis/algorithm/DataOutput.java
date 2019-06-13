package priv.wangao.LogAnalysis.algorithm;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Set;

import priv.wangao.LogAnalysis.data.dao.LogDAO;
import priv.wangao.LogAnalysis.data.factory.LogGenFactory;

public class DataOutput {
	private String outDirectory;
	public DataOutput(String outDirectory) {
		this.outDirectory = outDirectory;
		delAllFile(outDirectory);
	}
	
	
	public void updateElasticsearch(Set<LogEntry> logSet, String event) {
		LogDAO logDao = LogGenFactory.build("act-logger-es", "10.1.1.205:9300");
		for (LogEntry logEntry : logSet) {
			logDao.addUpdateRequest(logEntry.getIndex(), logEntry.getType(), logEntry.getId(), "eventType", event);
		}
		logDao.updateRequests();
	}
	//输出每组的日志
	public void outputByGroups(int groupNum, Set<LogEntry> logSet, String event) {
		File groupDirectory = new File(outDirectory + "\\group");
		if (!groupDirectory.exists() || groupDirectory.isFile())
			groupDirectory.mkdirs();
		RandomAccessFile raf;
		RandomAccessFile rafid;
		RandomAccessFile rafmap;
		try {
			raf = new RandomAccessFile(outDirectory + "\\group\\" + String.valueOf(groupNum) + ".log", "rw");
			rafid = new RandomAccessFile(outDirectory + "\\group\\" + String.valueOf(groupNum) + "_id.log", "rw");
			rafmap = new RandomAccessFile(outDirectory + "\\group\\" + "map.log", "rw");
			raf.seek(raf.length());
			rafid.seek(rafid.length());
			rafmap.seek(rafmap.length());
			StringBuilder str = new StringBuilder();
//			str.append("\r\n");
//			if (raf.length() == 0) raf.write(str.toString().getBytes());
			for (LogEntry logEntry : logSet) {
				//str = new StringBuilder(logEntry.getCnt());
				str = new StringBuilder(logEntry.getOrigin());
				str.append("\r\n");
				raf.write(str.toString().getBytes());
				str = new StringBuilder(logEntry.getCnt());
				str.append("\r\n");
				rafid.write(str.toString().getBytes());
				str = new StringBuilder(logEntry.getCnt() + "\t" + String.valueOf(groupNum));
				str.append("\r\n");
				rafmap.write(str.toString().getBytes());
			}
			raf.close();
			rafid.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeStrToFile(String fileName, String content) {
		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(outDirectory + "\\" + fileName, "rw");
			raf.seek(raf.length());
			StringBuilder str = new StringBuilder(content);
			str.append("\r\n");
			raf.write(str.toString().getBytes());
			raf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void delFolder(String folderPath) {
	     try {
	        delAllFile(folderPath); //删除完里面所有内容
	        String filePath = folderPath;
	        filePath = filePath.toString();
	        java.io.File myFilePath = new java.io.File(filePath);
	        myFilePath.delete(); //删除空文件夹
	     } catch (Exception e) {
	       e.printStackTrace(); 
	     }
	}
	
	private boolean delAllFile(String path) {
	       boolean flag = false;
	       File file = new File(path);
	       if (!file.exists()) {
	         return flag;
	       }
	       if (!file.isDirectory()) {
	         return flag;
	       }
	       String[] tempList = file.list();
	       File temp = null;
	       for (int i = 0; i < tempList.length; i++) {
	          if (path.endsWith(File.separator)) {
	             temp = new File(path + tempList[i]);
	          } else {
	              temp = new File(path + File.separator + tempList[i]);
	          }
	          if (temp.isFile()) {
	             temp.delete();
	          }
	          if (temp.isDirectory()) {
	             delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
	             delFolder(path + "/" + tempList[i]);//再删除空文件夹
	             flag = true;
	          }
	       }
	       return flag;
	     }
}
