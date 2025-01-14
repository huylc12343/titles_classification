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

        // Kiểm tra đường dẫn input và output
        String inputPath = conf.get("input");
        String outputPath = conf.get("output");

        if (inputPath == null) {
            System.err.println("Error: Input path not provided! Use -Dinput=<path>");
            return 1;
        }
        if (outputPath == null) {
            System.err.println("Error: Output path not provided! Use -Doutput=<path>");
            return 1;
        }


        // Cấu hình Job
        Job job = Job.getInstance(conf, "Multinomial Naive Bayes Training");
        job.setJarByClass(MNB_TrainJob.class);

        // Cấu hình Mapper và Reducer
        job.setMapperClass(MNB_TrainMapper.class);
        job.setReducerClass(MNB_TrainReducer.class);

        // Cấu hình kiểu đầu ra
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // Cấu hình số mapper và reducer
        job.getConfiguration().setInt("mapreduce.job.maps", conf.getInt("num_mappers", 4));
        job.getConfiguration().setInt("mapreduce.job.reduces", conf.getInt("num_reducers", 1));
//        job.setNumMapTasks(4);  // các phiên bản hadoop mới sẽ tự động phân chia số lượng map dựa theo dung lượng file đầu vào
//        job.setNumReduceTasks(1);  // Số lượng Reduce tasks

        // Cấu hình đường dẫn input và output
        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        // Chờ job hoàn thành
        return job.waitForCompletion(true) ? 0 : 1;
    }
}


