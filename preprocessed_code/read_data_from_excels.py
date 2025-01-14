import pandas as pd
import re
import os
from pyvi import ViTokenizer

# Hàm đọc stopwords từ file txt
def load_stopwords(stopwords_file):
    stopwords = set()
    try:
        with open(stopwords_file, 'r', encoding='utf-8') as file:
            stopwords = set(line.strip().lower() for line in file)
    except Exception as e:
        print(f"Lỗi khi đọc stopwords: {e}")
    return stopwords

def remove_punctuation(text):
    text = text.lower()
    # Loại bỏ số
    text = re.sub(r'\d+', '', text)  # Loại bỏ số

    # Loại bỏ ngày tháng (định dạng dd/mm/yyyy hoặc yyyy-mm-dd)
    text = re.sub(r'\b\d{1,2}[-/]\d{1,2}[-/]\d{2,4}\b', '', text)  # Loại bỏ ngày tháng dạng dd/mm/yyyy hoặc dd-mm-yyyy
    text = re.sub(r'\b\d{4}[-/]\d{1,2}[-/]\d{1,2}\b', '', text)  # Loại bỏ ngày tháng dạng yyyy-mm-dd hoặc yyyy/mm/dd

    # Loại bỏ các dấu câu
    pattern = r'[.,!?(){}\/~`’“”:"\'*&–\[\]+%$#;@=-]+|\.\.\.'  
    cleaned_text = re.sub(pattern, '', text)  
    cleaned_text = re.sub(r'(?<=\s)-(?=\s)', ' ', cleaned_text)


    # Tách từ và loại bỏ stopwords
    words = cleaned_text.split()  # Tách chuỗi thành các từừ
    words = [word for word in words if word not in stopwords]  # Loại bỏ stopwords
    cleaned_text = " ".join(words)  # Ghép lại các từ thành chuỗi

    cleaned_text += " "
    return cleaned_text

def read_data_from_excels(data_dir):
    data = []
    data_test = []
    
    for categories in os.listdir(data_dir):
        file_path = os.path.join(data_dir, categories)
        print(f"Đang xử lý file: {categories}")
        i = 0
        for file in os.listdir(file_path):
            if file.endswith('.xlsx'):
                try:
                    print("Đang đọc file: ", file,"trong folder: ", categories)
                    df = pd.read_excel(file_path + '/' + file)
                    for text in df['Title']:
                        tokenized_text = ViTokenizer.tokenize(text)  # Tokenize văn bản
                        new_string = tokenized_text.replace("\n", "")  # Loại bỏ xuống dòngdòng
                        new_string = remove_punctuation(new_string)
                        if(i<=10000): 
                            data.append([new_string, categories])
                            i+=1
                        else:
                            data_test.append([new_string])
                            i+=1
                except Exception as e:
                    print(f"{file_path + '/' + file} Không đọc được: {e}")
    return data,data_test


stopwords = load_stopwords("D:\\NLP\\titles_classification\\vietnamese-stopwords-dash.txt")  

processed_data,processed_data_test = read_data_from_excels("D:\\NLP\\titles_classification\\raw_data")

# print(processed_data)

# output_file = "preprocessed_data_updated.csv"
output_file = "D:\\NLP\\titles_classification\\data_train.csv"
output_file_test = "D:\\NLP\\titles_classification\\data_test.csv"
try:
    # Tạo DataFrame từ dữ liệu đã xử lý
    df = pd.DataFrame(processed_data, columns=["Content", "Label"])
    df.to_csv(output_file, index=False, encoding='utf-8-sig')  # Lưu dữ liệu vào CSV
    print(f"Dữ liệu đã được lưu vào {output_file}")
    df_test = pd.DataFrame(processed_data_test, columns=["Content"])
    df_test.to_csv(output_file_test, index=False, encoding='utf-8-sig')
    print(f"Dữ liệu đã được lưu vào {output_file_test}")
    
except Exception as e:
    print(f"Lỗi khi lưu dữ liệu: {e}")