package priv.wangao.LogAnalysis.flume.modules;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import priv.wangao.LogAnalysis.algorithm.DrainParser;
import priv.wangao.LogAnalysis.algorithm.Preprocessor;

public class ExtractEventInterceptor implements Interceptor{
	//private Map<String, String> typeToFile = new HashMap<String, String>();
	private DrainParser dp;
	private double cnt;
	long startTime;
	long endTime;
	public ExtractEventInterceptor() {

	}
	
	@Override
    public void initialize() {
		dp = new DrainParser();
		cnt = 0;
	}

	
    @Override
    //事件处理入口
    public Event intercept(Event event) {
    	cnt++;
    	if (cnt == 1) {
    		startTime=System.nanoTime(); 
    	}
    	String body = new String(event.getBody(), Charset.forName("UTF-8"));
    	String message = Preprocessor.getInstance().jsonProcessor(body);
    	String eventType = dp.entranceFlume(message);
    	event.setBody(message.getBytes());
    	event.getHeaders().put("Event", eventType);
    	endTime=System.nanoTime();
    	System.out.println("cnt-->" + cnt + " 运行时间-->"+(endTime-startTime)/1000000000+"s");
    	
       	return event;
    }
    
    
    @Override
    public List<Event> intercept(List<Event> events) {
        List<Event> intercepted = new ArrayList<>(events.size());
        for (Event event : events) {
            Event interceptedEvent = intercept(event);
            if (interceptedEvent != null) {
                intercepted.add(interceptedEvent);
            }
        }
        return intercepted;
    }

    @Override
    public void close() {}

    public static class Builder implements Interceptor.Builder{
    	String filename;
        @Override
        public Interceptor build() {
            return new ExtractEventInterceptor();
        }

        @Override
        public void configure(Context context) {
        	filename = context.getString(Constants.FILENAME, Constants.FILENAME_DEFAULT);
        }
    }
    
    public static class Constants {
        public static final String FILENAME = "filename";
        public static final String FILENAME_DEFAULT = "none";
      }

}