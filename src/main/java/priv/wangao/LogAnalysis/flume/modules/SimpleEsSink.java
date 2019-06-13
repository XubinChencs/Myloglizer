package priv.wangao.LogAnalysis.flume.modules;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import priv.wangao.LogAnalysis.data.dao.impl.EsDaoImpl;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;

public class SimpleEsSink extends AbstractSink implements Configurable {
	
	private static final Logger logger = LoggerFactory.getLogger(SimpleEsSink.class);
	
	private static final String HOST_NAMES = "hostNames";
	private static final String DEFAULT_INDEX_NAME = "defaultIndexName";	
	private static final String TYPE_NAME = "typeName";
	private static final String CLUSTER_NAME = "clusterName";
	private static final String MAX_INSERT_THREADS = "maxInsertThreads";
    private ExecutorService exectors = null;
    private BulkProcessor bulkProcessor;
	private EsDaoImpl esDaoImpl = null;
	private String defaultIndexName = null;
	private String typeName = null;
	private static List<IndexRequest> insertList = new ArrayList<IndexRequest>(); 
	public SimpleEsSink() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void configure(Context context) {
		// TODO Auto-generated method stub
		insertList.clear();
		try {
			this.esDaoImpl = new EsDaoImpl(context.getString(CLUSTER_NAME), context.getString(HOST_NAMES));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.defaultIndexName = context.getString(DEFAULT_INDEX_NAME, "test");
		this.typeName = context.getString(TYPE_NAME) != null ? context.getString(TYPE_NAME):"defalutType";
		this.exectors = Executors.newFixedThreadPool(context.getInteger(MAX_INSERT_THREADS));
		bulkProcessor = new BulkProcessorBuilder().buildBulkProcessor(context, this.esDaoImpl.getClient());
	}
	

	@Override
	public Status process() throws EventDeliveryException {
		// TODO Auto-generated method stub
		Channel channel = getChannel();
		Transaction transaction = channel.getTransaction();
		Event event = null;
		transaction.begin();
		while ((event = channel.take()) == null);
		try {
//			System.out.println("EsSink: " + event.toString());
			logger.debug("Get event...");
//			this.exectors.execute(new InsertTask(event));
			System.out.println(event.toString());
			String index = "test-" + new SimpleDateFormat("yyyyMMdd").format(new Date()).toString();
			String type = typeName;
			XContentBuilder json = esDaoImpl.addEntry(index, type, new LogEntry(event));
			bulkProcessor.add(new IndexRequest(index, type).source(json));
			transaction.commit();
			return Status.READY;
		} catch (Throwable  e) {
			// TODO Auto-generated catch block
			transaction.rollback();
			
            if (e instanceof Error) {
                throw (Error) e;
            } else {
                throw new EventDeliveryException(e);
            }
		} finally {
			transaction.close();
		}
	}

	public class InsertTask implements Runnable {

		private Event event = null;

		public InsertTask(Event event) {
			this.event  = event;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Calendar ca = Calendar.getInstance();
			ca.setTime(new Date());
			String logType = event.getHeaders().getOrDefault("logType", defaultIndexName);
			event.getHeaders().remove("eventType");
			esDaoImpl.addEntry("test-" + new SimpleDateFormat("yyyyMMdd").format(new Date()).toString(),
					typeName, new LogEntry(event));
//			esDaoImpl.addEntry(logType.replaceAll("-", "_") + "_" + new SimpleDateFormat("yyyyMMdd").format(new Date()).toString(),
//					typeName, new LogEntry(event));
		}
	}
	
}
