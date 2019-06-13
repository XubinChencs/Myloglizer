package priv.wangao.LogAnalysis.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author WangAo 消息队列类
 */
public class MessageQueue {

	private static MessageQueue instance = new MessageQueue();
	private BlockingQueue<String> mq = new LinkedBlockingQueue<String>();

	private MessageQueue() {
	}

	public static MessageQueue getInstance() {
		return instance;
	}

	public void push(String msg) {
		try {
			this.mq.put(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String get() {
		String result = null;
		try {
			result = this.mq.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int size() {
		return this.mq.size();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
