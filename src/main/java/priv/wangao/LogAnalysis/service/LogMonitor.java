package priv.wangao.LogAnalysis.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import priv.wangao.LogAnalysis.constant.Common;
import priv.wangao.LogAnalysis.data.dao.LogDAO;
import priv.wangao.LogAnalysis.data.factory.LogGenFactory;
import priv.wangao.LogAnalysis.util.DataHelper;
import priv.wangao.LogAnalysis.util.IOHelper;

public class LogMonitor {

	private final String path = this.getClass().getResource("/time-point").getPath();
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

	public LogMonitor() {

	}

	private List<String> getIndices() {
		//
		// System.err.println(DataHelper.getInstance().getUTCDate());
		// String date = DataHelper.getInstance().getUTCDate().split("T")[0];
		// String[] indices = logDao.getIndices(new String[] { ".*-" + date });
		// System.err.println(Arrays.asList(indices));
		List<String> indices = new ArrayList<String>();
		String[] split = null;
		for (String line : IOHelper.getInstance().readFromFile(path)) {
			split = line.split("\t");
			System.err.println(line);
			indices.add(split[0] + split[1].split("T")[0].replace("-", "."));
		}
		return indices;
	}

	private Map<String, String> getPoint() {
		Map<String, String> points = new HashMap<String, String>();
		String[] split = null;
		for (String line : IOHelper.getInstance().readFromFile(path)) {
			split = line.split("\t");
			System.err.println(line);
			points.put(split[0] + split[1].split("T")[0].replace("-", "."), split[1]);
		}
		return points;
	}

	private void setPoint(Map<String, String> point) {
		List<String> context = new ArrayList<String>();
		Pattern pattern = Pattern.compile("(.*)\\d{4}\\.\\d{2}\\.\\d{2}");
		Matcher matcher = null;
		for (Map.Entry<String, String> entry : point.entrySet()) {
			matcher = pattern.matcher(entry.getKey());
			if (matcher.matches()) {
				context.add(matcher.group(1) + "\t" + entry.getValue());
			}
			
		}
		IOHelper.getInstance().writeToFile(context, path, false);
	}

	public void getLog() {
		LogDAO logDao = LogGenFactory.build("act-logger-es", "10.1.1.205:9300");
		Map<String, String> point = this.getPoint();
		String[] includes = {"message"};
		for (Map.Entry<String, String> entry : point.entrySet()) {
			point.put(entry.getKey(), logDao.rangeEntry(new String[] { entry.getKey() }, "@timestamp", entry.getValue(),
					null, null , null, "act/", 0));
		}
		this.setPoint(point);
	}

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		LogMonitor monitor = new LogMonitor();
		// monitor.setPoint();
		// Thread.sleep(5000);
		// monitor.getLog();
//		monitor.getIndices();
//		monitor.getLog();
		
		ScheduledExecutorService  executorService = Executors.newScheduledThreadPool(1);
		
		executorService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				monitor.getLog();
			}
			
		}, 1, 300, TimeUnit.SECONDS);
		
	}

}
