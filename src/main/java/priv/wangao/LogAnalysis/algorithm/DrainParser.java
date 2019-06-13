package priv.wangao.LogAnalysis.algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONObject;

import priv.wangao.LogAnalysis.algorithm.DrainTree.Node;
import priv.wangao.LogAnalysis.algorithm.LevenshteinDistance.AlignStringPair;
import priv.wangao.LogAnalysis.algorithm.LevenshteinDistance.EditPath;
import priv.wangao.LogAnalysis.algorithm.LevenshteinDistance.LevenshteinDistance;
import priv.wangao.LogAnalysis.constant.Common;
import priv.wangao.LogAnalysis.util.IOHelper;
import priv.wangao.LogAnalysis.util.RegExpHelper;
import priv.wangao.LogAnalysis.util.SystemHelper;


// 日志解析算法Drain
public class DrainParser {
	private final static int MAX_DEPTH = 4;  //最大深度
	private final static double THRESHOLD = 0.66; //相似度判断阈值
	private static String seperator = "[\\s=:]+"; //切分日志用的分隔符
	private List<DrainLogGroup> logGroupList = new ArrayList<DrainLogGroup>(); 
	private DrainTree<String> tree = new DrainTree<String>(null, MAX_DEPTH, 10, THRESHOLD); //字典树
	private String fileName = "E:\\logData\\test-with label\\bd33-40\\all_normal_NoCMD.txt"; //需要处理的日志文件的路径
	private String exportPath = "E:\\drain"; //日志解析结果的输出目录
	private DataOutput out;
	private static double cnt = 0;
	
	public DrainParser(){
		out = new DataOutput(exportPath);
	}
	
	// 构造函数中指定日志文件路径和输出目录
	public DrainParser(String LogFilePath, String exportPath){
		this.fileName = LogFilePath;
		this.exportPath = exportPath;
		out = new DataOutput(this.exportPath);
	}
	
	// 对日志进行处理
	private String process(LogEntry logEntry, double ID) {
		Node<String> leafNode = traverse(logEntry);
		if (leafNode == null) return null;
		DrainLogGroup logGroup = leafNode.findGroup(logEntry);
		if (logGroup == null) {
			logGroup = new DrainLogGroup(logEntry, ID);
			//logGroupList.add(logGroup);
			//if (whetherAdd(logGroup, logEntry)) {
			logGroupList.add(logGroup);
			leafNode.addGroup(logGroup);
			//}
		} else {
			logGroup.updateAndAdd(logEntry);
		}
		return logGroup.toString();
		//System.out.println(logGroup.toString());
	}
	
	private String processForFlume(LogEntry logEntry, double ID) {
		Node<String> leafNode = traverse(logEntry);
		if (leafNode == null) return null;
		DrainLogGroup logGroup = leafNode.findGroup(logEntry);
		if (logGroup == null) {
			logGroup = leafNode.addGroup(logEntry, ID);
			logGroupList.add(logGroup);
		} else {
			logGroup.update(logEntry);
		}
		return logGroup.toString();
		//System.out.println(logGroup.toString());
	}
	
	// 用当前日志遍历字典树，建立新的节点，或者加入已有的分组
	private Node<String> traverse(LogEntry logEntry){
		Node<String> node;
		//String[] seq = log.split(ParseConstant.PRIMARY_SEPARATOR);
		String[] seq = new String[logEntry.getLogSeq().length];
		for (int i = 0; i < logEntry.getLogSeq().length; i++) {
			seq[i] = new String(logEntry.getLogSeq()[i]);
		}
		//对开头字符串就是数字的日志进行预处理
		//List<String> seqList = new ArrayList<String>();
		//seq = Preprocessor.getInstance().hierarchicalProcessor(String.join(" ", seq)).split(ParseConstant.PRIMARY_SEPARATOR);
//		for (int i = 0; i < ((MAX_DEPTH - 2) <  seq.length ? (MAX_DEPTH - 2) : seq.length); i++) {
//		for (int i = 0; i < seq.length; i++) {
//			seq[i] = Util.transformDigit(seq[i]);
//			seqList.add(seq[i]);
//		}
//		System.out.println(String.join(" ", seq));
//		System.exit(0);
		
		String len = String.valueOf(seq.length);
		node = tree.root().findChild(len);
		Pattern pattern = Pattern.compile("([0-9]+)");
		if (node == null) {
			node = tree.addNode(len, tree.root());
			if (node == null) {
				System.err.println("分支数达最大，无匹配日志长度");
				return null;
			}
		}
		Node<String> child_node;
		try {
			for (int i = 0; i < ((MAX_DEPTH - 2) <  seq.length ? (MAX_DEPTH - 2) : seq.length); i++) {
				boolean digit = pattern.matcher(seq[i]).find();
				child_node = node.findChild(seq[i]);
				if (child_node == null) {
					if (digit) {
						 child_node = tree.addNode("*", node);
					}else child_node = tree.addNode(seq[i], node);
					if (child_node == null) {
						System.err.println("分支数达最大，无匹配token");
						return null;
					}
				}
				node = child_node;
			}
		}catch (Exception e){
			System.out.println(logEntry.toString());
		}
		return node;
	}
	
	@SuppressWarnings("resource")
	//输出更新es
	public void entranceTest() {
        String filepath = "act/test.log";
        long startIndex = 0;
		double cnt = 0;
        while (true) {
            RandomAccessFile randomAccessFile = null;
            try {
                File file = new File(filepath);
                try {
                    randomAccessFile = new RandomAccessFile(file, "r");
                } catch (Exception e) {
                    e.printStackTrace();
                    //文件还未创建，等待
                    Thread.sleep(1 * 1000);
                    continue;
                } 
                //long startTime=System.nanoTime();   //获取开始时间 
                //获取当前文件的长度
                long fileLength = randomAccessFile.length();
                if (startIndex == 0) {
                    System.out.println("start");
                    startIndex = 0;
                } else if (startIndex > fileLength){
                	startIndex = 0;
                }
                randomAccessFile.seek(startIndex);
                String line = null;                
                while ((line = randomAccessFile.readLine()) != null) {
                    try {
                    	LogEntry logEntry = Preprocessor.getInstance().esJsonProcessor(line);
                    	System.out.println("readFromFile:\n " + logEntry.toString());
        	            process(logEntry, cnt);
        	            cnt++;
        	            if (cnt % 10000 == 9999) {
        	            	//cnt = 0;
        	            	updateElasticsearch();
        	            }
                    } catch (Exception e) {
                        //print log
                        e.printStackTrace();
                    }
                }
                //exportToFile();
                //long endTime=System.nanoTime(); //获取结束时间  
        		//System.out.println("程序运行时间： "+(endTime-startTime)/1000000000+"s");
                //获取下次循环的开始指针
                startIndex = randomAccessFile.getFilePointer();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    randomAccessFile.close();
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
	//输出到本地文件的算法入口，执行日志解析算法，指定分隔符
	public void entrance(String separator) throws IOException {
		SystemHelper.getInstance().getMemoryForWindows();
		double cnt = 0;
		int num =0;
		long startTime=System.nanoTime();   //获取开始时间 
		System.out.println("Running...");
		try {   
	        FileReader reader = new FileReader(fileName);
	        BufferedReader br = new BufferedReader(reader);
	        String str = null;     
        	List<String> context = new ArrayList<String>();
        	Pattern pattern = Pattern.compile("(CMD)");
        	// 逐行读取和处理日志文件中的日志
	        while ((str = br.readLine()) != null) {  
//	        	num++;
	        	//String line = Preprocessor.getInstance().jsonProcessor(str);
	        	
//	        	JSONObject jo = new JSONObject(str);
//	            String line = jo.getString("message");
//	            
//	        	if (pattern.matcher(line).find()) {
//	        		continue;
//	        	}
//	            String label = jo.getString("severity_label");
//	            
//	            if (!label.equals("Informational")) {
//	            	continue;
//	            }
	            
	        	//String line = Preprocessor.getInstance().hierarchicalProcessor(new String(str.trim()));
	        	String line = new String(str.trim());
	        	//System.out.println(line);
	        	//context.add(line);
//	        	String[] seq = Preprocessor.getInstance().hierarchicalProcessor(line).split(ParseConstant.PRIMARY_SEPARATOR);
	        	//IOHelper.getInstance().writeToFile(line + "\r\n", "E:\\logData\\act-nat-2018.11.20-message.log", true);
	        	LogEntry logEntry = new LogEntry(line, String.valueOf(num), str, separator);
	            process(logEntry, cnt);
	            System.out.println(cnt);
	            cnt++;
	            // 每处理10000条日志输出到本地一次
	            if (cnt % 10000 == 9999) {
	            	exportToFile(separator);
	            }
//	            if ( cnt > 300000)
//	            	break;
	        }
	        exportToFile(separator);
	        br.close();
	        reader.close();
	        //IOHelper.getInstance().writeToFile(context, "E:\\logData\\test-with label\\bd33-40\\all_normal_NoCMD.txt", false);
	      }
	      catch(FileNotFoundException e) {
	            e.printStackTrace();
	      }
	      catch(IOException e) {
	            e.printStackTrace();
	      }
		System.out.println(cnt);
		long endTime=System.nanoTime(); //获取结束时间  
		System.out.println("程序运行时间： "+(endTime-startTime)/1000000000+"s");
		SystemHelper.getInstance().getMemoryForWindows();
		System.out.println("creating log sequence ...");
		Util.createLogSequence(fileName, exportPath);
		System.out.println("finished.");
		//exportToFile();
	}

	public String entranceFlume(String message) {
		cnt++;
		LogEntry logEntry = new LogEntry(null, null, null, message, String.valueOf(cnt), message.trim());
		return processForFlume(logEntry, cnt);
		
	}
	
	//将日志解析的结果输出到文件中
	private void exportToFile(String separator) {
		int groupNum = 0;
		//按不同的日志集合输出分组的结果
		List<String> eventList = new ArrayList<String>();
		//日志事件转化成的正则表达式
		List<String> regexList = new ArrayList<String>();
		for (int i = 0; i < logGroupList.size(); i++) {
			groupNum++;
			eventList.add(logGroupList.get(i).toString()); 
			regexList.add(Util.eventToRegex(logGroupList.get(i).getEventSeq(), logGroupList.get(i), separator));
			//out.writeStrToFile("events.log", groupList.get(j).toString());
			out.outputByGroups(groupNum, logGroupList.get(i).getLogSet(), logGroupList.get(i).toString());
			logGroupList.get(i).clearGroup();
		}
		IOHelper.getInstance().writeToFile(String.join(Common.LOCAL_LINE_SEPARATOR, eventList), 
				exportPath + "\\event.log", false);
		IOHelper.getInstance().writeToFile(String.join(Common.LOCAL_LINE_SEPARATOR, regexList), 
				exportPath + "\\group\\groups.log", false);
	}
	
	private void updateElasticsearch() {
		List<Node<String>> nodes = tree.nodes;;
		//更新es
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).getDepth() == MAX_DEPTH) {
				List<DrainLogGroup> groupList = nodes.get(i).groupList;
				for (int j = 0; j < groupList.size(); j++) {
					out.updateElasticsearch(groupList.get(j).getLogSet(), groupList.get(j).toString());
					groupList.get(j).clearGroup();
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		DrainParser parser = new DrainParser();
		
		//parser.entranceTest();
		parser.entrance(seperator);
		//Util.createLogSequence("E:\\logData\\journalctl日志\\192.168.105.147\\192.168.105.147 journalctl  2019-04-17 正常情况下.txt", "E:\\drain");
	}
}
