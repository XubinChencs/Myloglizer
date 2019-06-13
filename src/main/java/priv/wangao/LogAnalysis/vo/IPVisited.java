package priv.wangao.LogAnalysis.vo;

import priv.wangao.LogAnalysis.util.GeoLiteHelper;

/**
 * @author WangAo IP 地址访问信息值对象
 */
public class IPVisited implements Comparable {

	// IP : 访问的 IP 地址
	private String IP = null;
	// count : 访问次数
	private int count = 0;
	// city : 根据 IP 地址获取地理信息
	private IP2City city = null;

	public IPVisited(String IP, int count) {
		this.IP = IP;
		this.count = count;
		this.city = GeoLiteHelper.getInstance().getCity(IP);
	}

	public String getIP() {
		return IP;
	}

	public int getCount() {
		return count;
	}
	
	public String getCountry() {
		return this.city.getCountry();
	}

	@Override
	public String toString() {
		return this.IP + " : " + this.city.getCountry() + "\t" + this.city.getCity() + "\t(" + this.city.getLatitude()
				+ "," + this.city.getLongitude() + ")" + "\tcount: " + count;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

}
