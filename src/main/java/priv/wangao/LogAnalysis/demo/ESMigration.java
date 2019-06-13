package priv.wangao.LogAnalysis.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import priv.wangao.LogAnalysis.data.dao.LogDAO;
import priv.wangao.LogAnalysis.data.factory.LogGenFactory;

public class ESMigration {

	public ESMigration() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LogDAO logGen = LogGenFactory.build("act-multi-logs", "192.168.105.132:9300");
		
		List<String> allIndices = new ArrayList<String>(Arrays.<String>asList(logGen.getIndices(new String[] {
			"act-nat-.*"
		})));
		
		List<String> removeIndices = new ArrayList<String>(Arrays.<String>asList(logGen.getIndices(new String[] {
				".*-2018.11.12",
				".*-2018.11.13",
				".*-2018.11.14",
		})));
		
		allIndices.removeAll(removeIndices);
		
		System.err.println(allIndices);
		
		logGen.deleteIndices(allIndices.toArray(new String[] {}));
		
	}

}
