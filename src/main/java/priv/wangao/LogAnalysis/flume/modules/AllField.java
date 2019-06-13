package priv.wangao.LogAnalysis.flume.modules;

import java.util.HashMap;
import java.util.Map;

public final class AllField {
	
	private static AllField instance = new AllField();
	
	private Map<String, String> map = new HashMap<String, String>();
	
	public static AllField getInstance() {
		return instance;
	}
	
	public Map<String, String> getMap() {
		return this.map;
	}

	private AllField() {
		this.map.put("timestamp", "date");
		this.map.put("ceventname", "string");
		this.map.put("ceventdigest", "string");
		this.map.put("ceventtype", "string");
		this.map.put("ieventlevel", "byte");
		this.map.put("iprotocol", "byte");
		this.map.put("csrcname", "string");
		this.map.put("csrcmac", "string ");
		this.map.put("csrcip", "string");
		this.map.put("csrctip", "string");
		this.map.put("isrcport", "integer");
		this.map.put("isrctport", "integer");
		this.map.put("cdstname", "string");
		this.map.put("cdstmac", "string");
		this.map.put("cdstip", "string");
		this.map.put("cdsttip", "string");
		this.map.put("idstport", "integer");
		this.map.put("idsttport", "integer");
		this.map.put("cusername", "string");
		this.map.put("cprogram", "string");
		this.map.put("coperation", "string");
		this.map.put("cobject", "string"); //text
		this.map.put("iresult", "byte");
		this.map.put("ireponse", "byte");
		this.map.put("cdevname", "string");
		this.map.put("cdevtype", "string");
		this.map.put("cdevip", "string");
		this.map.put("loccurtime", "date");
		this.map.put("dmonitorvalue", "float");
		this.map.put("corilevel", "string");
		this.map.put("coritype", "string");
		this.map.put("lsend", "long");
		this.map.put("lreceive", "long");
		this.map.put("lduration", "long");
		this.map.put("crequestmsg", "string");
		this.map.put("istandby1", "byte");
		this.map.put("istandby2", "byte");
		this.map.put("lstandby1", "long");
		this.map.put("lstandby2", "long");
		this.map.put("dstandby1", "float");
		this.map.put("dstandby2", "float");
		this.map.put("cstandby1", "string");
		this.map.put("cstandby2", "string");
		this.map.put("cstandby3", "string");
		this.map.put("cstandby4", "string");
		this.map.put("cstandby5", "string");
		this.map.put("cstandby6", "string");
		this.map.put("timestamp", "date");
		this.map.put("logType", "string");
		this.map.put("@message", "string"); //text
	}

}
