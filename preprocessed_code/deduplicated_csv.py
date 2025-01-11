import pandas as pd

# Đường dẫn đến tệp Excel cần xử lý
input_file = "D:\\NLP\\titles_classification\\preprocessed_data_test_updated.csv"
# input_file = "D:\\NLP\\titles_classification\\excels\\chinh_tri\\chinh_tri_2.xlsx"
output_file = "D:\\NLP\\titles_classification\\preprocessed_data_test_updated_deduplicated.csv"

# Đọc tệp Excel vào DataFrame
df = pd.read_csv(input_file)

# Loại bỏ các hàng trùng lặp
df_deduplicated = df.drop_duplicates()

# Ghi DataFrame đã loại bỏ trùng lặp vào tệp Excel mới
df_deduplicated.to_csv(output_file, index=False)

print(f"Dữ liệu đã được loại bỏ trùng lặp và lưu vào file: {output_file}")
