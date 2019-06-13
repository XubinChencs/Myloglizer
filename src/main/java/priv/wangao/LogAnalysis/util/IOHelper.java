package priv.wangao.LogAnalysis.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import priv.wangao.LogAnalysis.constant.Common;

public class IOHelper {

	private static class LazyHolder {
		private static final IOHelper INSTANCE = new IOHelper();
	}

	private final static Charset CHARSET = Charset.forName("UTF-8");

	/**
	 * @Title: getInstance
	 * @Description: 懒汉单例模式静态内部类
	 * @return
	 * @return: IOHelper
	 */
	public static IOHelper getInstance() {
		return LazyHolder.INSTANCE;
	}

	public void writeToFile(String context, String filePath, boolean append) {
		try (FileOutputStream fos = new FileOutputStream(new File(filePath), append);
				OutputStreamWriter osw = new OutputStreamWriter(fos);
				BufferedWriter bw = new BufferedWriter(osw);) {
			bw.write(context);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeToFile(List<String> context, String filePath, boolean append) {
		File file = new File(filePath);
		if (file.getParentFile() != null && file.getParentFile().exists() == false) {
			file.getParentFile().mkdirs();
		}
		try (FileOutputStream fos = new FileOutputStream(file, append);
				OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("UTF-8"));
				BufferedWriter bw = new BufferedWriter(osw);) {
			for (String line : context) {
				bw.write(line + Common.LOCAL_LINE_SEPARATOR);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeToFile(Map<String, Integer> context, String filePath, boolean append) {
		File file = new File(filePath);
		if (file.getParentFile() != null && file.getParentFile().exists() == false) {
			file.getParentFile().mkdirs();
		}
		try (FileOutputStream fos = new FileOutputStream(file, append);
				OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("UTF-8"));
				BufferedWriter bw = new BufferedWriter(osw);) {
			for (Map.Entry<String, Integer> entry : context.entrySet()) {
				bw.write(entry.toString() + Common.LOCAL_LINE_SEPARATOR);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> readFromCSV(String filePath, int target, String separator) {
		List<String> result = new ArrayList<String>();
		try (FileInputStream fis = new FileInputStream(new File(filePath));
				InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
				BufferedReader br = new BufferedReader(isr);) {
			String curLine = null;

			String[] split = null;
			System.err.println(target);
			while ((curLine = br.readLine()) != null) {
				split = curLine.split(separator);
				if (split.length > target) {
					result.add(curLine.split(separator)[target]);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<String> readFromCSV(String filePath, String col, String separator) {
		List<String> result = new ArrayList<String>();
		try (FileInputStream fis = new FileInputStream(new File(filePath));
				InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
				BufferedReader br = new BufferedReader(isr);) {
			String curLine = br.readLine();

			String[] split = curLine.split(separator);
			int target = Arrays.binarySearch(split, col);
			System.err.println(target);
			while ((curLine = br.readLine()) != null) {
				split = curLine.split(separator);
				if (split.length > target) {
					result.add(curLine.split(separator)[target]);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String readFromFile(String filePath, long pointer) {
		String result = null;
		try (RandomAccessFile raf = new RandomAccessFile(filePath, "r");) {
			raf.seek(pointer);
			result = raf.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<String> readFromFile(String filePath) {
		List<String> result = new ArrayList<String>();
		try (FileInputStream fis = new FileInputStream(new File(filePath));
				InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
				BufferedReader br = new BufferedReader(isr);) {
			String curLine = null;
			// int count = 0;
			while ((curLine = br.readLine()) != null) {
				result.add(curLine);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<String> readFromFile(File file) {
		List<String> result = new ArrayList<String>();
		try (FileInputStream fis = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
				BufferedReader br = new BufferedReader(isr);) {
			String curLine = null;
			// int count = 0;
			while ((curLine = br.readLine()) != null) {
				result.add(curLine);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Map<String, String> getUserMeta(String filePath) {
		Path path = Paths.get(filePath);
		UserDefinedFileAttributeView view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
		Map<String, String> map = new HashMap<String, String>();
		try {
			List<String> metaKeys = view.list();
			for (String metaKey : metaKeys) {
				ByteBuffer buff = ByteBuffer.allocate(view.size(metaKey));
				view.read(metaKey, buff);
				buff.flip();
				map.put(metaKey, CHARSET.decode(buff).toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	public void writeUserMeta(String filePath, String key, String value) {
		Path path = Paths.get(filePath);
		UserDefinedFileAttributeView view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
		try {
			view.write(key, CHARSET.encode(value));
			view.write("date", CHARSET.encode(new Date().toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
