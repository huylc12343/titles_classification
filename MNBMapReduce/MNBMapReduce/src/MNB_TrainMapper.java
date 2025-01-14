import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MNB_TrainMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private String delimiter;
    private int targetVariable;
    private int numColumns;

    @Override
    protected void setup(Context context) {
        // Lấy các tham số từ cấu hình
        delimiter = context.getConfiguration().get("delimiter", ",");
        targetVariable = context.getConfiguration().getInt("targetVariable", 2);
        numColumns = context.getConfiguration().getInt("numColumns", 2);
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // Bỏ qua dòng đầu tiên nếu cần
        if (key.get() == 0) {
            return;
        }

        String line = value.toString();
        System.out.println("Processing line: " + line);
        String[] features = line.split(delimiter);
        System.out.println("Dau cau: "+delimiter);
        // Đảm bảo số cột hợp lệ
        if (features.length != numColumns) {
            return;
        }

        // Xử lý từng từ trong thuộc tính văn bản
        String[] words = features[0].split(" ");
        String label = features[targetVariable - 1]; // Trừ 1 vì mảng bắt đầu từ 0

        for (String word : words) {
        	System.out.println(word+"-"+label+",1");
            String labeledWord = word + "-" + label;
            String labelCountKey = "Count_" + label;

            // Xuất dữ liệu
            context.write(new Text("Count_Text"), new IntWritable(1));
            context.write(new Text(labelCountKey), new IntWritable(1));
            context.write(new Text(labeledWord), new IntWritable(1));
        }
    }
}
