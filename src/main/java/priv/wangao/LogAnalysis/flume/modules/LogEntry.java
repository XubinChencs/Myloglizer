package priv.wangao.LogAnalysis.flume.modules;
import java.util.HashMap;
import java.util.Map;

import org.apache.flume.Event;

public class LogEntry {
	
	private Map<String, String> headers = new HashMap<String, String>();
	private byte[] body = null;
	

	public LogEntry(Event event) {
		this.headers = event.getHeaders();
		this.body = event.getBody();
	}
	
	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	public Map<String, String> getHeaders() {
		return this.headers;
	}

}
