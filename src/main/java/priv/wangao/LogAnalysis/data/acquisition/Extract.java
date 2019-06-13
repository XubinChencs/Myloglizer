package priv.wangao.LogAnalysis.data.acquisition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import priv.wangao.LogAnalysis.constant.Common;
import priv.wangao.LogAnalysis.util.IOHelper;
import priv.wangao.LogAnalysis.util.RegExpHelper;

public class Extract {

	public Extract() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String curLine = null;
		int pointer = 0;
		JSONObject json = null;
		String rawMessage = null;
		Pattern pattern = Pattern.compile(RegExpHelper.ACT_NAT);
		;
		Matcher matcher = null;
		int count = 0;
		while ((curLine = IOHelper.getInstance().readFromFile("act-nat", pointer)) != null) {
			json = JSONObject.fromObject(curLine);
			if (json.containsKey("message")) {
				rawMessage = json.getString("message");
			} else {
				rawMessage = json.getString("act");
			}

			matcher = pattern.matcher(rawMessage);
			count++;
			if (matcher.find()) {
				System.err.println(count + " : " + matcher.groupCount() + "<>" + matcher.group(1) + "->"
						+ matcher.group(2) + "->" + matcher.group(3) + "->" + matcher.group(4));
			} else {
				System.err.println(count + " : " + rawMessage);
				if (json.containsKey("message")) {
					rawMessage = "message: " + rawMessage;
 				} else {
 					rawMessage = "act: " + rawMessage;
 				}
				IOHelper.getInstance().writeToFile(rawMessage + Common.LOCAL_LINE_SEPARATOR, "error.txt", true);
			}
			rawMessage += Common.LOCAL_LINE_SEPARATOR;
			pointer += (curLine + Common.LOCAL_LINE_SEPARATOR).getBytes().length;
		}
	}

}
