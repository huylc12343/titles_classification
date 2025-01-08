import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
public class MNB_TestMapper extends Mapper<LongWritable,Text,Text,Text>{

	static double alpha = 1.0;
	String delimiter,decreteVariables;	
	String[] targetClasses ={"chinh_tri","giai_tri","the_thao"};
	int targetVariable,numColums;
	HashMap<String,String> hm;
	@Override
	protected void setup(Context context) {
		Configuration conf = context.getConfiguration();
		delimiter = conf.get("delimiter");
	    numColums = conf.getInt("numColumns", 0);
	    decreteVariables = conf.get("decereteVariables");
	    targetVariable = conf.getInt("targetVariable", 0);

	    hm = new HashMap<>();
	    try {
	    	URI[] filesIncache = context.getCacheFiles();
	    	System.out.println("in ra file incache:"+filesIncache[0].getPath().toString());
	    	for (int i = 0; i < filesIncache.length; i++) {
	            BufferedReader fis = new BufferedReader(new FileReader(filesIncache[i].getPath().toString()));
	            String record;
	            while ((record = fis.readLine()) != null) {
	                String key, value;
	                StringTokenizer tokRecord = new StringTokenizer(record);
	                key = tokRecord.nextToken();
	                value = tokRecord.nextToken();
//	                System.out.println("key: "+key+", value: "+value);
	                hm.put(key, value);
	            }
	        }
	    }catch(IOException e) {
	    	System.out.println("Bi mac loi: "+ e);
	    }
	}
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		double max_probability = -Double.MAX_VALUE;
		String best_label = "";
		String line = value.toString();
		System.out.println("Processing line: " + line);
        String [] words = line.split(" ");
        int i = 0;
        String vocabSize = hm.get("Count_Text");
        for (String label: targetClasses) {
        	String countLabel = hm.get("Count_"+label); 
        	double labelProbability = calculateLabelProbability(Integer.parseInt(countLabel),Integer.parseInt(vocabSize));
        	System.out.println("P("+label+"): "+labelProbability);
        	double word_probability = 1.0;
        	for(String word: words) {
//            	hm.put(word+"-"+label, 1);
//            	System.out.println(word+"-"+label+",1");
        		String labeled_word = word+"-"+label;
            	String count_word = hm.getOrDefault(labeled_word, "0");
            	word_probability = word_probability *  calculateWordProbability(Integer.parseInt(count_word),Integer.parseInt(countLabel),Integer.parseInt(vocabSize));
            	System.out.println(labeled_word+": "+ word_probability);
//            	String labeled_word =  word+"-"+label;
//            	String predicted_label = "";
//            	i++;
//            	if (i>=50) {
//            		break;
//            	}
//            	context.write(value, value)
            	double totalProbability = labelProbability*word_probability; 
//            	System.out.println("Total prob:"+totalProbability);
            	if(totalProbability > max_probability) {
            		best_label = label;
            		max_probability = totalProbability;
            	}
        }        	
//        	context.write(new Text(labeled_word), new Text(predicted_label));
//        	context.write(value, new Text(predicted_label));
        }
        System.out.println(value.toString());
        System.out.println("Best label: "+best_label);
    	System.out.println("Prob: "+max_probability);
    	context.write(value, new Text(best_label));
//    	context.write(value,new Text(best_label));
	}
	public static double calculateLabelProbability(int count_label, int vocab_size) {
		return (double) count_label/vocab_size;
	}
	public static double calculateWordProbability(int count_word,int count_word_label,int vocab_size) {
		return (double)(count_word+alpha)/(count_word_label+alpha*vocab_size);
	}
}
