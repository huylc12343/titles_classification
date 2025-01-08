import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//import java.util.StringTokenizer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.LongWritable;
//import org.apache.hadoop.io.NullWritable;

public class MNB_TrainMapper extends Mapper<LongWritable, Text, Text, IntWritable> 
{
	

//	private Text WordToken = new Text();
	String delimiter,decreteVariables;
	int targetVariable,numColumns;
//	Map<String,Integer> hm = new HashMap<>();
	public void configure(JobConf conf) {
		delimiter = conf.get("delimiter",",");
		numColumns = conf.getInt("numColumns",2);
		targetVariable = conf.getInt("targetVariable", 2);
//		decreteVariables = conf.get("decreteVariables",{"1"});
		
	}
    public void map(LongWritable key, Text value, Context context) 
    				throws IOException, InterruptedException
    
    {
    	
        if (key.get() == 0) {
            return; // Bỏ qua dòng Content,Label
        }
    	
        String line = value.toString();
        System.out.println("Processing line: " + line);
        String [] features = line.split(",");
        String [] words = features[0].split(" ");
        String label = features[1];
        for(String word: words) {
//        	hm.put(word+"-"+label, 1);
        	System.out.println(word+"-"+label+",1");
        	String labeled_word =  word+"-"+label;
        	String llb = "Count_"+label;
        	context.write(new Text("Count_Text"), new IntWritable(1));
        	context.write(new Text(llb), new IntWritable(1));
        	context.write(new Text(labeled_word),new IntWritable(1));
        }
        
//        String keyprint = key.toString();
//        System.out.println("Key: "+keyprint);
        
        
    }

   

}
