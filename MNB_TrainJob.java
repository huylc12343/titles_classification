import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class MNB_TrainJob extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new MNB_TrainJob(), args);
        System.exit(exitCode);
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();

        // Parse input and output paths from command-line arguments
        String inputPath = conf.get("input");
        String outputPath = conf.get("output");

        if (inputPath == null || outputPath == null) {
            System.err.println("Error: Input and Output paths must be provided using -Dinput=<path> -Doutput=<path>");
            return 1;
        }

        Job job = Job.getInstance(conf, "Training");
        job.setJarByClass(MNB_TrainJob.class);

        // Set Mapper, Reducer, and Combiner classes
        job.setMapperClass(MNB_TrainMapper.class);
//        job.setCombinerClass(MNB_TrainReducer.class);
        job.setReducerClass(MNB_TrainReducer.class);

        // Set output key and value types
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // Set input and output paths
        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        // Wait for job completion
        return job.waitForCompletion(true) ? 0 : 1;
    }
}
