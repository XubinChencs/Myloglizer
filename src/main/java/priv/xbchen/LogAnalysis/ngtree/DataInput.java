package priv.xbchen.LogAnalysis.ngtree;

import java.math.BigDecimal;
import java.util.List;

import priv.wangao.LogAnalysis.util.IOHelper;

public class DataInput {

	private static DataInput instance = new DataInput();

	public static DataInput getInstance() {
		return instance;
	}

	private DataInput() {

	}
	
	public Double[] gangliaCSV(String filePath) {
		List<String> lines = IOHelper.getInstance().readFromFile(filePath);
		Double[] sequence = new Double[lines.size() - 1];
		for (int i = 1; i < lines.size(); i++) {
			String str = lines.get(i);
			String[] split = str.split(",");
			BigDecimal bd = new BigDecimal(split[1]);
			Double value = Double.parseDouble(bd.toPlainString());
			sequence[i - 1] = value;
			System.out.println(value);
		}
		return sequence;
	}
	
}
