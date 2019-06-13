package priv.wangao.LogAnalysis.util.mapreduce;

import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import net.sf.json.JSONObject;
import priv.wangao.LogAnalysis.constant.Common;
import priv.wangao.LogAnalysis.util.RegExpHelper;

public class ActNatMR {

	public static class ActNatMRMapper extends Mapper<LongWritable, Text, Text, NullWritable> {

		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			JSONObject json = JSONObject.fromObject(value.toString());
			String rawMessage = "";
			Pattern pattern = Pattern.compile(RegExpHelper.ACT_NAT);
			if (json.containsKey("message")) {
				rawMessage = json.getString("message");
			} else if (json.containsKey("act")) {
				rawMessage = json.getString("act");
			} else {
				System.err.println(value + "vs" + json);
			}

			Matcher matcher = pattern.matcher(rawMessage);
			if (matcher.find()) {
				context.write(new Text("source:" + matcher.group(2)), NullWritable.get());
				context.write(new Text("inner:" + matcher.group(3)), NullWritable.get());
				context.write(new Text("target:" + matcher.group(4)), NullWritable.get());
			} else {
				context.write(new Text("error:" + rawMessage), NullWritable.get());
			}
		}
	}

	public static class ActNatMRReducer extends Reducer<Text, NullWritable, Text, IntWritable> {

		@Override
		protected void reduce(Text key, Iterable<NullWritable> values, Context context)
				throws IOException, InterruptedException {
			String[] split = key.toString().split(":");

			int count = 0;

			for (NullWritable val : values) {
				count++;
			}
			context.write(new Text(split[1]), new IntWritable(count));
		}
	}

	public static class ActNatMRPartitioner extends Partitioner<Text, NullWritable> {

		@Override
		public int getPartition(Text key, NullWritable value, int numPartitions) {
			// TODO Auto-generated method stub
			String[] split = key.toString().split(":");

			int result = 0;

			switch (split[0]) {
			case "source":
				result = 0;
				break;
			case "inner":
				result = 1;
				break;
			case "target":
				result = 2;
				break;
			default:
				result = 3;
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

		job.setJarByClass(ActNatMR.class);
		job.setMapperClass(ActNatMRMapper.class);
		job.setReducerClass(ActNatMRReducer.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(NullWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		job.setPartitionerClass(ActNatMRPartitioner.class);
		job.setNumReduceTasks(4);

		Path inputPath = new Path("hdfs://wangao001.ics.com:8020/wangao/LogAnalysis/act-nat-2018.10.1__.txt");
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
