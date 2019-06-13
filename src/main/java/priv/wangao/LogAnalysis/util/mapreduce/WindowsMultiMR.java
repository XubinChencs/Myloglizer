package priv.wangao.LogAnalysis.util.mapreduce;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;
import priv.wangao.LogAnalysis.constant.Common;

public class WindowsMultiMR {

	private static Logger logger = LoggerFactory.getLogger(WindowsMultiMR.class);

	public static class WindowsMultiMRMapper extends Mapper<LongWritable, Text, Text, NullWritable> {

		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			JSONObject json = JSONObject.fromObject(value.toString());
			if (json.containsKey("server-IP") && json.getString("server-IP").trim().length() > 0) {
				context.write(new Text("target:" + json.getString("server-IP")), NullWritable.get());
			}
			if (json.containsKey("source-IP") && json.getString("source-IP").trim().length() > 0) {
				context.write(new Text("source:" + json.getString("source-IP")), NullWritable.get());
			}
			if (json.containsKey("content") && json.getString("content").trim().length() > 0) {
				context.write(new Text("content:" + json.getString("content")), NullWritable.get());
				if (json.containsKey("operation") && json.getString("operation").trim().length() > 0) {
					context.write(
							new Text("operation:" + json.getString("operation") + "->" + json.getString("content")),
							NullWritable.get());
				}
			}
		}
	}

	public static class WindowsMultiMRReducer extends Reducer<Text, NullWritable, Text, IntWritable> {

		@Override
		protected void reduce(Text key, Iterable<NullWritable> values, Context context)
				throws IOException, InterruptedException {
			String[] split = key.toString().split(":");

			int count = 0;

			for (NullWritable val : values) {
				count++;
			}
			if (split.length > 1 && split[1].length() > 0) {
				context.write(new Text(split[1]), new IntWritable(count));
			}
		}
	}

	public static class WindowsMultiMRPartitioner extends Partitioner<Text, NullWritable> {

		@Override
		public int getPartition(Text key, NullWritable value, int numPartitions) {
			// TODO Auto-generated method stub
			String[] split = key.toString().split(":");

			int result = 0;

			switch (split[0]) {
			case "source":
				result = 0;
				break;
			case "content":
				result = 1;
				break;
			case "target":
				result = 2;
				break;
			case "operation":
				result = 3;
				break;
			default:
				result = 4;
				break;
			}

			return result;
		}

	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", Common.HDFS_ADDR);
		FileSystem fs = FileSystem.get(conf);
		Job job = Job.getInstance();

		job.setJarByClass(WindowsMultiMR.class);
		job.setMapperClass(WindowsMultiMRMapper.class);
		job.setReducerClass(WindowsMultiMRReducer.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(NullWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setPartitionerClass(WindowsMultiMRPartitioner.class);
		job.setNumReduceTasks(5);

		Path inputPath = new Path("hdfs://wangao001.ics.com:8020/wangao/LogAnalysis/niclog-4th-2018.10.16.txt");
		FileInputFormat.setInputPaths(job, inputPath);

		Path outputPath = new Path("hdfs://wangao001.ics.com:8020/wangao/LogAnalysis/MR");
		if (fs.exists(outputPath)) {
			fs.delete(outputPath, true);
		}
		FileOutputFormat.setOutputPath(job, outputPath);

		BasicConfigurator.configure();

		boolean isDone = job.waitForCompletion(true);
		System.exit(isDone ? 0 : 1);
	}

}
