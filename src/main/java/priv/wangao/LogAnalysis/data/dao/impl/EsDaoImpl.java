package priv.wangao.LogAnalysis.data.dao.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import priv.wangao.LogAnalysis.flume.modules.AllField;
import priv.wangao.LogAnalysis.flume.modules.LogEntry;

import priv.wangao.LogAnalysis.data.dao.EsDao;
import priv.wangao.LogAnalysis.util.EsHelper;

public class EsDaoImpl implements EsDao {
	
	private static final Charset charset = Charset.forName("Utf-8");
	private EsHelper esHelper = null;
	
	
	public EsDaoImpl(String clusterName, String addrs) throws Exception {
		this.esHelper = new EsHelper(clusterName, addrs);
	}

	@Override
	public XContentBuilder addEntry(String index, String type, Object content) {
		// TODO Auto-generated method stub
		Map<String, String> headers = ((LogEntry)content).getHeaders();
		byte[] body = ((LogEntry)content).getBody();
		Map<String, String> map = new HashMap<String, String>();
		XContentBuilder json = null;
		try {
			json = XContentFactory.jsonBuilder().startObject();
//			for (Map.Entry<String, String> entry : headers.entrySet()) {
//				if(!entry.getKey().equals("timestamp"))
//				json.field(entry.getKey(), entry.getValue());
//				else
//				{
//					SimpleDateFormat ft=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					json.field(entry.getKey(), ft.format(Long.parseLong(entry.getValue())));
//				}
//				//ContentBuilderUtil.appendField(json, entry.getKey(), entry.getValue());
//				map.put(entry.getKey(), AllField.getInstance().getMap().getOrDefault(entry.getKey(), "string"));
//			}
			json.field("@message", new String(body, charset));
			//ContentBuilderUtil.appendField(json, "@message", body);
			map.put("@message", "String");
			json.field("Event", new String(headers.get("Event")));
			//ContentBuilderUtil.appendField(json, "@message", body);
			map.put("Event", "String");
			json.endObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		///////////////////////////////////////////////
		this.esHelper.isTypeExists(index, type, map);
		//this.esHelper.addInsertRequest(index, type, json);
		//this.esHelper.executeInsert(index, type, json);
		return json;
	}

	@Override
	public void getEntry() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delEntry() {
		// TODO Auto-generated method stub
		
	}
	
	public TransportClient getClient() {
		return this.esHelper.getClient();
	}
//	public void query(String index, int batchSize, int ttlMinutes, 
//			String targetDir, String columnName, int totalNum) {
//		this.esHelper.executeQuery(index, batchSize, ttlMinutes, targetDir, columnName, totalNum);
//	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		EsDaoImpl esDaoImpl = new EsDaoImpl("my-application", "192.168.1.172:9200");
		//esDaoImpl.query("udp-2018-08-08", 100, 1, "" ,"@message", 0);
		String longtext = "<14>Aug  8 10:51:02 DESKTOP-P4US2OO Microsoft-Windows-Security-Auditing[860]: 已成功登录帐户。    使用者:  	安全 ID:		S-1-5-18  	帐户名称:		DESKTOP-P4US2OO$  	帐户域:		WORKGROUP  	登录 ID:		0x3E7    登录信息:  	登录类型:		5  	受限制的管理员模式:	-  	虚拟帐户:		否  	提升的令牌:		是    模拟级别:		模拟    新登录:  	安全 ID:		S-1-5-18  	帐户名称:		SYSTEM  	帐户域:		NT AUTHORITY  	登录 ID:		0x3E7  	链接的登录 ID:		0x0  	网络帐户名称:	-  	网络帐户域:	-  	登录 GUID:		{00000000-0000-0000-0000-000000000000}    进程信息:  	进程 ID:		0x340  	进程名称:		C:\\Windows\\System32\\services.exe    网络信息:  	工作站名称:	-  	源网络地址:	-  	源端口:		-    详细的身份验证信息:  	登录进程:		Advapi    	身份验证数据包:	Negotiate  	传递的服务:	-  	数据包名(仅限 NTLM):	-  	密钥长度:		0    创建登录会话时，将在被访问的计算机上生成此事件。    “使用者”字段指示本地系统上请求登录的帐户。这通常是一个服务(例如 Server 服务)或本地进程(例如 Winlogon.exe 或 Services.exe)。    “登录类型”字段指示发生的登录类型。最常见的类型是 2 (交互式)和 3 (网络)。    “新登录”字段指示新登录是为哪个帐户创建的，即已登录的帐户。    “网络”字段指示远程登录请求源自哪里。“工作站名称”并非始终可用，并且在某些情况下可能会留空。    “模拟级别”字段指示登录会话中的进程可以模拟到的程度。    “身份验证信息”字段提供有关此特定登录请求的详细信息。  	- “登录 GUID”是可用于将此事件与 KDC 事件关联起来的唯一标识符。  	-“传递的服务”指示哪些中间服务参与了此登录请求。  	-“数据包名”指示在 NTLM 协议中使用了哪些子协议。  	-“密钥长度”指示生成的会话密钥的长度。如果没有请求会话密钥，则此字段将为 0。";
		String text = "MSWinEventLog	1	Security	136	Wed Aug 08 17:19:05 2018	4624	Microsoft-Windows-Security-Auditing	N/A	N/A	Success Audit	DESKTOP-P4US2OO	Logon		已成功登录帐户。    使用者:   安全 ID:  S-1-5-18   帐户名称:  DESKTOP-P4US2OO$   帐户域:  WORKGROUP   登录 ID:  0x3E7    登录信息:   登录类型:  5   受限制的管理员模式: -   虚拟帐户:  否   提升的令牌:  是    模拟级别:  模拟    新登录:   安全 ID:  S-1-5-18   帐户名称:  SYSTEM   帐户域:  NT AUTHORITY   登录 ID:  0x3E7   链接的登录 ID:  0x0   网络帐户名称: -   网络帐户域: -   登录 GUID:  {00000000-0000-0000-0000-000000000000}    进程信息:   进程 ID:  0x340   进程名称:  C:\\Windows\\System32\\services.exe    网络信息:   工作站名称: -   源网络地址: -   源端口:  -    详细的身份验证信息:   登录进程:  Advapi     身份验证数据包: Negotiate   传递的服务: -   数据包名(仅限 NTLM): -   密钥长度:  0    创建登录会话时，将在被访问的计算机上生成此事件。    “使用者”字段指示本地系统上请求登录的帐户。这通常是一个服务(例如 Server 服务)或本地进程(例如 Winlogon.exe 或 Services.exe)。    “登录类型”字段指示发生的登录类型。最常见的类型是 2 (交互式)和 3 (网络)。    “新登录”字段指示新登录是为哪个帐户创建的，即已登录的帐户。    “网络”字段指示远程登录请求源自哪里。“工作站名称”并非始终可用，并且在某些情况下可能会留空。    “模拟级别”字段指示登录会话中的进程可以模拟到的程度。    “身份验证信息”字段提供有关此特定登录请求的详细信息。   - “登录 GUID”是可用于将此事件与 KDC 事件关联起来的唯一标识符。   -“传递的服务”指示哪些中间服务参与了此登录请求。   -“数据包名”指示在 NTLM 协议中使用了哪些子协议。   -“密钥长度”�";
	}

	@Override
	public void createIndex(String index, String type, Object content) {
		// TODO Auto-generated method stub
		
	}
}
