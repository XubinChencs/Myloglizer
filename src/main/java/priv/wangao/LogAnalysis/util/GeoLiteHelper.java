package priv.wangao.LogAnalysis.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;

import priv.wangao.LogAnalysis.vo.IP2City;

public class GeoLiteHelper {

	private static GeoLiteHelper instance = null;
	private static final String mmdb = "GeoLite2-City.mmdb";

	/**
	 * @Title: getInstance
	 * @Description: 懒汉单例模式 双重检验锁
	 * @return
	 * @return: GeoLiteHelper
	 */
	public static GeoLiteHelper getInstance() {
		if (instance == null) {
			synchronized (GeoLiteHelper.class) {
				if (instance == null) {
					instance = new GeoLiteHelper();
				}
			}
		}
		return instance;
	}

	private DatabaseReader reader = null;

	private GeoLiteHelper() {
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(mmdb);) {
			reader = new DatabaseReader.Builder(is).build();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title: getCity
	 * @Description: 根据 IP 地址获取城市信息
	 * @param IP
	 *            IP 地址
	 * @return 城市信息
	 * @return: IP2City
	 */
	public IP2City getCity(String IP) {

		IP2City result = new IP2City(IP);

		try {
			InetAddress ipAddress = InetAddress.getByName(IP);
			CityResponse response = reader.city(ipAddress);
			Country country = response.getCountry();
			if (country != null && country.getNames() != null && country.getNames().get("zh-CN") != null) {
				// if (IP.equals("185.40.235.55")) {
				// System.err.println(country.getName());
				// System.exit(0);
				// }
				result.setCountry(country.getNames().get("zh-CN"));
			}
			City city = response.getCity();
			if (city != null && city.getNames() != null && city.getNames().get("zh-CN") != null) {
				result.setCity(city.getNames().get("zh-CN"));
			}
			Location location = response.getLocation();
			if (location != null) {
				result.setLatitude(location.getLatitude());
				result.setLongitude(location.getLongitude());
			}
		} catch (UnknownHostException e) {
			System.err.println("1AddressNotFoundException");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("2AddressNotFoundException");
			e.printStackTrace();
		} catch (GeoIp2Exception e) {
			System.err.println("3AddressNotFoundException");
		} catch (Exception e) {
			System.err.println("4AddressNotFoundException");
		}
		return result;
	}

	public static void main(String[] args) {
		IP2City result = GeoLiteHelper.getInstance().getCity("91.189.92.150");
		System.out.println(result.getCity());
	}

}
