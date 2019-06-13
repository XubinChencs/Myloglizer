package priv.wangao.LogAnalysis.vo;

import priv.wangao.LogAnalysis.constant.Common;

/**
 * @author WangAo IP 地址转 城市信息值对象
 */
public class IP2City {

	// IP : IP 地址
	private String IP;
	// country : 国家名称
	private String country;
	// city : 城市名称
	private String city;
	// latitude : 纬度
	private double latitude;
	// Longitude : 经度
	private double Longitude;

	/**
	 * @Title:IP2City
	 * @Description: 指定 IP地址创建城市信息
	 * @param IP
	 *            IP地址
	 */
	public IP2City(String IP) {
		this.IP = IP;
		this.country = Common.DEFAULT_COUNTRY;
		this.city = Common.DEFAULT_CITY;
		this.latitude = Common.DEFAULT_LATITUDE;
		this.Longitude = Common.DEFAULT_LONGITUDE;
	}

	/**
	 * @Title:IP2City
	 * @Description: 指定 IP地址、国家、城市、经纬度创建城市信息
	 * @param IP
	 *            IP地址
	 * @param country
	 *            国家
	 * @param city
	 *            城市
	 * @param latitude
	 *            纬度
	 * @param longitude
	 *            经度
	 */
	public IP2City(String IP, String country, String city, double latitude, double longitude) {
		this.IP = IP;
		this.country = country;
		this.city = city;
		this.latitude = latitude;
		this.latitude = longitude;
	}

	/**
	 * @Title: getIP
	 * @Description: 获取 IP 地址
	 * @return IP 地址
	 * @return: String
	 */
	public String getIP() {
		return IP;
	}

	/**
	 * @Title: getCountry
	 * @Description: 获取国家名称
	 * @return 国家名称
	 * @return: String
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @Title: getCity
	 * @Description: 获取城市名称
	 * @return 城市名称
	 * @return: String
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @Title: getLatitude
	 * @Description: 获取纬度
	 * @return 纬度
	 * @return: String
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @Title: getLongitude
	 * @Description: 获取经度
	 * @return 经度
	 * @return: String
	 */
	public double getLongitude() {
		return Longitude;
	}

	/**
	 * @Title: setIP
	 * @Description: 设置 IP 地址
	 * @param IP
	 *            IP 地址
	 * @return: void
	 */
	public void setIP(String IP) {
		this.IP = IP;
	}

	/**
	 * @Title: setCountry
	 * @Description: 设置国家名称
	 * @param country
	 *            国家名称
	 * @return: void
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @Title: setCity
	 * @Description: 设置城市名称
	 * @param city
	 *            城市名称
	 * @return: void
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @Title: setLatitude
	 * @Description: 设置纬度
	 * @param latitude
	 *            纬度
	 * @return: void
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @Title: setLongitude
	 * @Description: 设置经度
	 * @param longitude
	 *            经度
	 * @return: void
	 */
	public void setLongitude(double longitude) {
		Longitude = longitude;
	}

	@Override
	public String toString() {
		return this.IP + " : " + this.country + "\t" + this.city + "\t" + this.latitude + "\t" + this.Longitude;
	}

}
