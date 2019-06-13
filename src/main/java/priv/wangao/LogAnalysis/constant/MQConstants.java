package priv.wangao.LogAnalysis.constant;

import java.io.File;

interface MQConstants {
	
	// MAX_SIZE : 消息队列消息缓存最大数量
	int MAX_SIZE = 100000;
	// TMP_FILE_PATH : 超过缓存时，暂存文件的路径
	String TMP_FILE_PATH = "tmp" + File.separator + "mq.txt";
	
}
