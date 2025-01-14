import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MNB_TestMapper extends Mapper<LongWritable, Text, Text, Text> {

    static double alpha = 1.0;
    String delimiter, discreteVariables;
    String[] targetClasses = {};
    int targetVariable, numColumns;
    HashMap<String, String> hm;

    @Override
    protected void setup(Context context) throws IOException {
        Configuration conf = context.getConfiguration();
        delimiter = conf.get("delimiter",",");
        numColumns = conf.getInt("numColumns", 0);//bị thừa
        discreteVariables = conf.get("decereteVariables");//bị thừa
        targetVariable = conf.getInt("targetVariable", 0);//bị thừa
        String targetClassesString = conf.get("targetClasses");//ok dùng đc
        if (targetClassesString != null) {
            targetClasses = targetClassesString.split(delimiter);// targetVariable có dạng là String {label1,label2,label3} 
																// và các dấu phân cách có thể dùng dấu bất kỳ sau đó truyền delimiter ở đây
        }													
        hm = new HashMap<>();
        try {
            URI[] filesInCache = context.getCacheFiles();
            for (URI file : filesInCache) {
                BufferedReader fis = new BufferedReader(new FileReader(new Path(file.toString()).getName()));
//                FileSystem fs = FileSystem.get(conf); // sửa lại vẫn không viết được ra utf-8 nè?
//                BufferedReader fis = new BufferedReader(new InputStreamReader(fs.open(new Path(file.toString())), "UTF-8"));

                String record;
                while ((record = fis.readLine()) != null) {
                    StringTokenizer tokRecord = new StringTokenizer(record);
                    if (tokRecord.countTokens() < 2) continue;
                    String key = tokRecord.nextToken();
                    String value = tokRecord.nextToken();
                    hm.put(key, value);
                }
                fis.close();
            }
        } catch (IOException e) {
            throw new IOException("Error reading CacheFiles", e);
        }
    }

    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
    	if (key.get() == 0) {
            return;//bỏ dòng Content,Label
        }
        double maxProbability = -Double.MAX_VALUE;
        String bestLabel = "";
        String line = value.toString();

        String[] words = line.split(" ");
        String vocabSize = hm.get("Count_Text");
        if (vocabSize == null) throw new IOException("Count_Text not found in the model.");

        for (String label : targetClasses) {
            String countLabel = hm.get("Count_" + label);
            if (countLabel == null) countLabel = "0";

            double labelProbability = calculateLabelProbability(Integer.parseInt(countLabel), Integer.parseInt(vocabSize));
            double wordProbability = 1.0;

            for (String word : words) {
                String labeledWord = word + "-" + label;
                String countWord = hm.getOrDefault(labeledWord, "0");// lấy value(số lần xuất hiện trong nhãn) dựa theo key(word-label)
                
                wordProbability *= calculateWordProbability(Integer.parseInt(countWord), Integer.parseInt(countLabel),
                        Integer.parseInt(vocabSize));
            }
            //ví dụ: X = send us your account
            //c1 = ham
            //c2 = spam
            // P(X|c1) = P(ham)*P(send|ham)*P(us|ham)*P(your|ham)*P(account|ham)
            // 			= P(ham/vocab)*P([count{send|ham}+alpha]/[ham+alpha*vocab])
            //P(X|c2) = P(spam)*P(send|spam)*P(us|spam)*P(your|spam)*P(account|spam)
            double totalProbability = labelProbability * wordProbability;
            if (totalProbability > maxProbability) {
                bestLabel = label;
                maxProbability = totalProbability;
            }
	        }
	        if (bestLabel.isEmpty()) {
	            bestLabel = "UNKNOWN"; // Gán nhãn mặc định nếu không có nhãn tốt nhất
	        }
	//        System.out.println("line"+value.toString()+": "+bestLabel);
//	        System.out.println("Line: " + value.toString() + " - Predicted Label: " + bestLabel);
	        context.write(value, new Text(bestLabel));
    }

    public static double calculateLabelProbability(int countLabel, int vocabSize) {
        return (double) countLabel / vocabSize;
    }

    public static double calculateWordProbability(int countWord, int countWordLabel, int vocabSize) {
        return (double) (countWord + alpha) / (countWordLabel + alpha * vocabSize);
    }
}