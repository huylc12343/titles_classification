import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

public class MNB_TestJob extends Configured implements Tool{
	@Override
	public int run(String[] args)throws Exception{
		Configuration c = getConf();
		if (c.get("modelPath") == null || c.get("input") == null || c.get("output") == null) {
            throw new IllegalArgumentException("Missing required configurations: input, output, or modelPath.");
        }
		Job job = Job.getInstance(c,"NMB Test");
		job.setJarByClass(MNB_TestJob.class);
		
		job.addCacheFile(new Path(c.get("modelPath")+"/part-r-00000").toUri());
		
		job.setMapperClass(MNB_TestMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		FileInputFormat.addInputPath(job, new Path(c.get("input")));
		FileOutputFormat.setOutputPath(job, new Path(c.get("output")));
		
		return job.waitForCompletion(true) ? 0: 1;
		
	}
	public static void main(String[] args) throws Exception
	{
		int res = ToolRunner.run(new Configuration(), new MNB_TestJob(), args);
		System.exit(res);
	}
}
