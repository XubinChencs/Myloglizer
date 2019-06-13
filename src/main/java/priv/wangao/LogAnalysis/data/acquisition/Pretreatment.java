package priv.wangao.LogAnalysis.data.acquisition;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import priv.wangao.LogAnalysis.constant.Common;
import priv.wangao.LogAnalysis.util.IOHelper;

public class Pretreatment {

	public Pretreatment() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String input = "act-nat";
		String output = "act-nat-raw";
		File file = new File(output);
		if (file.exists()) {
			file.delete();
		}
		JSONObject json = null;
		String message = null;
		String rawMessage = null;
		int count = 0;
		long pointer = 0L;
		while ((message = IOHelper.getInstance().readFromFile(input, pointer)) != null) {
			json = JSONObject.fromObject(message);
			if (json.containsKey("message")) {
				rawMessage = json.getString("message");
			} else {
				rawMessage = json.getString("act");
			}
			count++;
			System.out.println(count + " -> " + rawMessage);
			rawMessage += Common.LOCAL_LINE_SEPARATOR;
			IOHelper.getInstance().writeToFile(rawMessage, output, true);
			pointer += (message + Common.LOCAL_LINE_SEPARATOR).getBytes().length;
		}
	}

}
