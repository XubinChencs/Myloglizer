package priv.wangao.LogAnalysis.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.StringTokenizer;

public class SystemHelper {
	
	private static SystemHelper instance = new SystemHelper();
	private static String pid = "";
	private SystemHelper() {
		getJvmPIDOnWindows();
	}

	public static SystemHelper getInstance() {
		return instance;
	}
	
	
	/** 
	* @Title: getJvmPIDOnWindows 
	* @Description: 获取windows下jvm的pid
	* @return: void
	*/ 
	private String getJvmPIDOnWindows() {
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		return runtime.getName().split("@")[0];
	}
	
	/** 
	* @Title: getMemoryForWindows 
	* @Description: 获取windows下jvm的内存占用量
	* @return 
	* @return: String
	*/ 
	public String getMemoryForWindows() {
		pid = getJvmPIDOnWindows();
		String command = "TASKLIST /NH /FO CSV /FI \"PID EQ " + pid + " \"";
		String remCount = ""; // jvm物理内存占用量
		BufferedReader in = null;
		String result = "";
		try {
			Process pro = Runtime.getRuntime().exec(command);
			in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			StringTokenizer ts = new StringTokenizer(in.readLine(), "\"");
			int i = 1;
			while (ts.hasMoreTokens()) {
				i++;
				ts.nextToken();
				if (i == 9) {
					remCount = ts.nextToken().replace(",", "").replace("K", "").trim();
				}
			}
			long physicalJvmMem = Long.parseLong(remCount) / 1024; // jvm物理内存占用量(MB)
			System.out.println(physicalJvmMem);
			in.close();
			pro.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
