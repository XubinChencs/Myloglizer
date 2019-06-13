package priv.wangao.LogAnalysis;

import priv.wangao.LogAnalysis.algorithm.LogEntry;

public class ParserTest {

	public static void main(String[] args) {
		String log = "<189>date=2018-12-27 time=13:33:28 devname=RG-WALL devid=RGFW37TK18000192 logid=0000000013 type=traffic subtype=forward level=notice vd=root srcip=172.20.0.37 srcport=42281 srcintf=\"lan\" dstip=13.209.220.108 dstport=8080 dstintf=\"wan1\" poluuid=83961642-6a25-51e8-dcf9-95cfb5a3aa28 sessionid=68256803 proto=6 action=timeout policyid=1 policytype=policy dstcountry=\"Korea, Republic of\" srccountry=\"Reserved\" trandisp=snat transip=172.30.1.2 transport=42281 service=\"tcp/8080\" duration=17 sentbyte=240 rcvdbyte=0 sentpkt=4 rcvdpkt=0 appcat=\"unscanned\" crscore=5 craction=262144 crlevel=low"; 
		LogEntry le = new LogEntry(null, null, null, log, null, log);
		String[] seq  = le.getLogSeq();
		for (int i = 0; i < seq.length; i ++) {
			System.out.println(seq[i]);
		}
	}
	
}
