package priv.wangao.LogAnalysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import priv.wangao.LogAnalysis.constant.Common;
import priv.wangao.LogAnalysis.util.HdfsHelper;
import priv.wangao.LogAnalysis.util.IOHelper;
import priv.wangao.LogAnalysis.vo.IPVisited;

/**
 * Hello world!
 *
 */

public class App {

	public static void main(String[] args) {
		HdfsHelper hdfs = new HdfsHelper();
		List<String> m_ips = IOHelper.getInstance().readFromCSV("malicious_ip.csv", "ip", ",");
		Set<String> m_set = new HashSet<String>(m_ips);
		// hdfs.put("E:\\Coding\\Java\\eclipse-workspace\\LogAnalysis\\act-nat-2018.10.1__.txt",
		// "/wangao/LogAnalysis");
		List<String> result = hdfs.readFromHdfs("/wangao/LogAnalysis/MR/part-r-00000");
		List<IPVisited> ips = new ArrayList<IPVisited>();
		for (String msg : result) {
			String[] split = msg.split("\t");
			ips.add(new IPVisited(split[0], Integer.parseInt(split[1])));
		}

		System.err.println("IPS' size: " + ips.size());

		Collections.sort(ips, new Comparator<IPVisited>() {

			@Override
			public int compare(IPVisited o1, IPVisited o2) {
				if (o1.getCount() > o2.getCount()) {
					return -1;
				} else if (o1.getCount() == o2.getCount()) {
					return 0;
				} else {
					return 1;
				}
			}

		});

		int malicious_count = 0;
		
		for (IPVisited ip : ips) {
			IOHelper.getInstance().writeToFile(ip.toString() + Common.LOCAL_LINE_SEPARATOR, "IPVisited.txt", true);
			if (m_set.contains(ip.getIP())) {
				System.err.println("恶意IP: " + ip.toString());
				IOHelper.getInstance().writeToFile(ip.toString() + Common.LOCAL_LINE_SEPARATOR, "malicious_ip_find.txt",
						true);
				malicious_count += ip.getCount();
			}
			if (!ip.getCountry().equals("中国")) {
				IOHelper.getInstance().writeToFile(ip.toString() + Common.LOCAL_LINE_SEPARATOR,
						"Non Continental Address.txt", true);
			}
		}
		
		System.err.println("恶意访问总数:" + malicious_count);

	}
}
