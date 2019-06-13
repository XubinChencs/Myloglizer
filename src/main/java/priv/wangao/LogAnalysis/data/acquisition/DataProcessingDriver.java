package priv.wangao.LogAnalysis.data.acquisition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import priv.wangao.LogAnalysis.constant.Common;
import priv.wangao.LogAnalysis.util.IOHelper;
import priv.wangao.LogAnalysis.vo.IPVisited;

public class DataProcessingDriver {

	public DataProcessingDriver() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DataProcessingImpl dp = new DataProcessingImpl();
//		try {
//			dp.runMR();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		dp.pullFromEs();
//		dp.putToHdfs();
//		dp.dealData(dp.getMRResult());
//		List<String> result = IOHelper.getInstance().readFromFile("IPVisited1.txt");
//		List<IPVisited> ips = new ArrayList<IPVisited>();;
//		for (String msg : result) {
//			String[] split = msg.split("\\s+");
//			ips.add(new IPVisited(split[0], Integer.parseInt(split[6])));
//		}
//
//		System.err.println("IPS' size: " + ips.size());
//
//		Collections.sort(ips, new Comparator<IPVisited>() {
//
//			@Override
//			public int compare(IPVisited o1, IPVisited o2) {
//				if (o1.getCount() > o2.getCount()) {
//					return -1;
//				} else if (o1.getCount() == o2.getCount()) {
//					return 0;
//				} else {
//					return 1;
//				}
//			}
//
//		});
//
//		int malicious_count = 0;
//
//		List<String> m_ips = IOHelper.getInstance().readFromCSV("malicious_ip.csv", "ip", ",");
//		Set<String> m_set = new HashSet<String>(m_ips);
//
//		for (IPVisited ip : ips) {
//			IOHelper.getInstance().writeToFile(ip.toString() + Common.LOCAL_LINE_SEPARATOR, "IPVisited.txt", true);
//			if (m_set.contains(ip.getIP())) {
//				System.err.println("恶意IP: " + ip.toString());
//				IOHelper.getInstance().writeToFile(ip.toString() + Common.LOCAL_LINE_SEPARATOR, "malicious_ip_find.txt",
//						true);
//				malicious_count += ip.getCount();
//			}
//			if (!ip.getCountry().equals("中国")) {
//				IOHelper.getInstance().writeToFile(ip.toString() + Common.LOCAL_LINE_SEPARATOR,
//						"Non Continental Address.txt", true);
//			}
//		}
//
//		System.err.println("恶意访问总数:" + malicious_count);
	}
}
