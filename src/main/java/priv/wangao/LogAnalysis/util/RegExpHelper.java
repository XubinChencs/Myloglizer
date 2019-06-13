package priv.wangao.LogAnalysis.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author WangAo
 * 正则表达式帮助类
 */
public class RegExpHelper {

	private static final RegExpHelper instance = new RegExpHelper();

	// IPV6_REG : IPV6 正则表达式
	public static final String IPV6_REG = "(([\\da-fA-F]{1,4}):){7}([\\da-fA-F]{1,4})";//"((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?";	
	// IPV4_REG : IPV4正则表达式
	public static final String IPV4_REG = "((\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))\\.){3}(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))";//"(?<![0-9])(?:(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[.](?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[.](?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[.](?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2}))(?![0-9])";
	// IP_REG : IPv4正则表达式
	public static final String IP_REG = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})";//"(?:" + IPV6_REG + "|" + IPV4_REG + ")";
	// PORT_REG : 端口正则表达式
	public static final String PORT_REG = "(?:[1-9]\\d{1,5})";
	// MONTH_REG : 月份正则表达式
	public static final String MONTH_REG = "(?:Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)";
	// MONTHNUM : 月份数字正则表达式
	public static final String MONTHNUM_REG = "(?:0?[1-9]|1[0-2])";
	// MONTHDAY : 月份数字正则表达式
	public static final String MONTHDAY_REG = "(?:(?:0[1-9])|(?:[12][0-9])|(?:3[01])|[1-9])";
	// DAY : 星期正则表达式
	public static final String DAY_REG = "(?:Mon(?:day)?|Tue(?:sday)?|Wed(?:nesday)?|Thu(?:rsday)?|Fri(?:day)?|Sat(?:urday)?|Sun(?:day)?)";
	public static final String YEAR_REG = "(?>\\d\\d){1,2}";
	public static final String HOUR_REG = "(?:2[0123]|[01]?[0-9])";
	public static final String MINUTE_REG = "(?:[0-5][0-9])";
	public static final String SECOND_REG = "(?:(?:[0-5][0-9]|60)(?:[:.,][0-9]+)?)";
	public static final String DATE_REG = "(\\d{4}-\\d{2}-\\d{2})";
	public static final String TIME_REG = "(\\d{2}:\\d{2}:\\d{2})";
	
	public static final String UUID_REG = "([A-Fa-f0-9]{8}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{12})";
	

	// ANY_REG : 匹配可见字符的正则表达式
	private static final String ANY_REG = "(.+)";
	// INT_REG : 匹配整数的正则表达式
	private static final String INT_REG = "([+-]?[0-9]+)";
	public static final String ACT_NAT = "(\\S+);((?:\\d{1,3}\\.){3}\\d{1,3});\\d+;((?:\\d{1,3}\\.){3}\\d{1,3});\\d+\\-\\-\\-\\>((?:\\d{1,3}\\.){3}\\d{1,3});\\d+;";
	
	private RegExpHelper() {
	}

	/** 
	* @Title: getInstance 
	* @Description: 饿汉单例模式 
	* @return 
	* @return: RegExpHelper
	*/ 
	public static RegExpHelper getInstance() {
		return instance;
	}

	/** 
	* @Title: isAddr 
	* @Description: 判断是否为网络地址[IP:PORT] 
	* @param addr 网络地址
	* @return 是否为网络地址，true 表示是，false 表示否
	* @return: boolean
	*/ 
	public boolean isAddr(String addr) {
		String[] split = addr.split(":");
		if (split.length != 2)
			return false;
		else
			return this.isIPv4(split[0]) && this.isPort(split[1]);
	}

	/** 
	* @Title: isIPv4 
	* @Description: 判断是否为 IPv4 地址 
	* @param ipv4 IPv4 地址 
	* @return 是否为 IPv4 地址 ，true 表示是，false 表示否
	* @return: boolean
	*/ 
	public boolean isIPv4(String ipv4) {
		return this.match(IP_REG, ipv4);
	}

	public boolean isPort(String port) {
		return this.match(PORT_REG, port);
	}

	public boolean isAny(String any) {
		return this.match(ANY_REG, any);
	}
	
	public boolean isInt(String integer) {
		return this.match(INT_REG, integer);
	}
	
	public boolean isUUID(String UUID) {
		return this.match(UUID_REG, UUID);
	}
	
	public boolean isTime(String time) {
		return this.match(TIME_REG, time);
	}
	
	public boolean isDate(String Date) {
		return this.match(DATE_REG, Date);
	}
	
	/** 
	* @Title: match 
	* @Description: 跟定正则表达式和目标文本，判断是否匹配正则表达式 
	* @param regex 正则表达式
	* @param target 目标文本
	* @return 是否匹配正则表达式 ，true 表示是，false 表示否
	* @return: boolean
	*/ 
	private boolean match(String regex, String target) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(target);
		
//		while(matcher.find()){
//			for (int i = 0; i < matcher.regionEnd(); i++)
//			System.out.println(matcher.group(i)); 
//		}

		return matcher.matches();
	}
	
	/** 
	* @Title: isMatch 
	* @Description: 跟定正则表达式数组和目标文本，判读目标文本是否命中其中一个正则表达式 
	* @param patterns 正则表达式数组
	* @param target 目标文本
	* @return 目标文本是否命中其中一个正则表达式  ，true 表示是，false 表示否
	* @return: boolean
	*/ 
	public boolean isMatch(String[] patterns, String target) {
		for (String pattern : patterns) {
			if (this.match(pattern, target)) {
				return true;
			}
		}
		return false;
	}

	public boolean isMatch(String pattern, String target) {
		if (this.match(pattern, target)) {
				return true;
		}
		return false;
	}
	
	public String isMatch(String target) {
		if (this.match(IP_REG, target)) return IP_REG;
		if (this.match(TIME_REG, target)) return TIME_REG;
		if (this.match(DATE_REG, target)) return DATE_REG;
		if (this.match(INT_REG, target)) return INT_REG;
		if (this.match(UUID_REG, target)) return UUID_REG;
		if (this.match(ANY_REG, target)) return ANY_REG;
		return target;
	}
	
	public static void main(String[] args) {
		String test = "\"asdasd";
		String str = "[6777482.556019] EDAC MC0: 1 CE Read error on mc#0branch#0channel#1slot#0 (branch:0 channel:1 slot:0 page:0x0 offset:0x0 grain:8 syndrome:0x0 - Rank=0 Bank=0 RDWR=Read RAS=2488 CAS=928, CE Err=0x2000 (Correctable Non-Mirrored Demand Data ECC)))";
		String pattern = "(.*)[\\s:\\[\\]]+(.*)[\\s:\\[\\]]+EDAC[\\s:\\[\\]]+MC0[\\s:\\[\\]]+(.*)[\\s:\\[\\]]+CE[\\s:\\[\\]]+Read[\\s:\\[\\]]+error[\\s:\\[\\]]+on[\\s:\\[\\]]+unknown[\\s:\\[\\]]+memory[\\s:\\[\\]]+\\(branch[\\s:\\[\\]]+(.*)[\\s:\\[\\]]+channel[\\s:\\[\\]]+(.*)[\\s:\\[\\]]+slot[\\s:\\[\\]]+(.*)[\\s:\\[\\]]+page[\\s:\\[\\]]+(.*)[\\s:\\[\\]]+offset[\\s:\\[\\]]+(.*)[\\s:\\[\\]]+grain[\\s:\\[\\]]+(.*)[\\s:\\[\\]]+syndrome[\\s:\\[\\]]+(.*)[\\s:\\[\\]]+-[\\s:\\[\\]]+(.*)[\\s:\\[\\]]+(.*)[\\s:\\[\\]]+RDWR=Read[\\s:\\[\\]]+(.*)[\\s:\\[\\]]+(.*)[\\s:\\[\\]]+CE[\\s:\\[\\]]+(.*)[\\s:\\[\\]]+\\(Correctable[\\s:\\[\\]]+Non-Mirrored[\\s:\\[\\]]+Demand[\\s:\\[\\]]+Data[\\s:\\[\\]]+ECC\\)\\)\\)";
		//String pattern = "(root) CMD ( /usr/sbin/ivic-monitor 2>> /var/log/ivic/monitor.log)";
		System.out.println(RegExpHelper.getInstance().isMatch(pattern,str));
		//System.out.println(pattern.replaceAll("\"", "&quot;"));
	}

}