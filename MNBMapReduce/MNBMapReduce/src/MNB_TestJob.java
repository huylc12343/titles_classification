import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.io.Text;

public class MNB_TestJob extends Configured implements Tool {

    @Override
    public int run(String[] args) throws Exception {
        Configuration c = getConf();
        if (c.get("modelPath") == null || c.get("input") == null || c.get("output") == null) {
        	System.out.println("model path: "+c.get("modelPath"));
        	System.out.println("input: "+c.get("input"));
        	System.out.println("ouput: "+c.get("output"));
            throw new IllegalArgumentException("Missing required configurations: input, output, or modelPath.");
        }

        Path outputPath = new Path(c.get("output"));
        FileSystem fs = FileSystem.get(c);
        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }

        Job job = Job.getInstance(c, "NMB Test");
        job.setJarByClass(MNB_TestJob.class);

        job.addCacheFile(new Path(c.get("modelPath") + "/part-r-00000").toUri());

        job.setMapperClass(MNB_TestMapper.class);
        
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        // Cấu hình số mapper và reducer
        job.getConfiguration().setInt("mapreduce.job.maps", c.getInt("num_mappers", 4));
        job.getConfiguration().setInt("mapreduce.job.reduces", c.getInt("num_reducers", 1));

        FileInputFormat.addInputPath(job, new Path(c.get("input")));
        FileOutputFormat.setOutputPath(job, outputPath);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new MNB_TestJob(), args);
        System.exit(res);
    }
}
