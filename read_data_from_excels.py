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
    text = re.sub(r'\d+', '', text)  # Loại bỏ tất cả các số

    # Loại bỏ ngày tháng (định dạng dd/mm/yyyy hoặc yyyy-mm-dd)
    text = re.sub(r'\b\d{1,2}[-/]\d{1,2}[-/]\d{2,4}\b', '', text)  # Loại bỏ ngày tháng dạng dd/mm/yyyy hoặc dd-mm-yyyy
    text = re.sub(r'\b\d{4}[-/]\d{1,2}[-/]\d{1,2}\b', '', text)  # Loại bỏ ngày tháng dạng yyyy-mm-dd hoặc yyyy/mm/dd

    # Loại bỏ các dấu câu
    pattern = r'[.,!?(){}\/~`’“”:"\'*&–\[\]+%$#]+|\.\.\.|(?<=\s)-(?=\s)'  
    cleaned_text = re.sub(pattern, '', text)  

    # Tách từ và loại bỏ stopwords
    words = cleaned_text.split()  # Tách chuỗi thành các từ
    words = [word for word in words if word not in stopwords]  # Loại bỏ stopwords
    cleaned_text = " ".join(words)  # Ghép lại các từ thành chuỗi

    cleaned_text += " "
    return cleaned_text

def read_data_from_excels(data_dir):
    data = []

    data_dir = 'excels'
    for categories in os.listdir(data_dir):
        file_path = os.path.join(data_dir, categories)
        print(f"Đang xử lý file: {categories}")
        for file in os.listdir(file_path):
            if file.endswith('.xlsx'):
                try:
                    print("Đang đọc file: ", file)
                    df = pd.read_excel(file_path + '/' + file)
                    for text in df['Title']:
                        tokenized_text = ViTokenizer.tokenize(text)  # Tokenize văn bản
                        new_string = tokenized_text.replace("\n", "")  # Loại bỏ dòng mới
                        new_string = remove_punctuation(new_string)
                        data.append([new_string, categories])
                except Exception as e:
                    print(f"{file_path + '/' + file} Không đọc được: {e}")
    return data


stopwords = load_stopwords("vietnamese-stopwords-dash.txt")  

processed_data = read_data_from_excels("excels")

print(processed_data)

output_file = "preprocessed_data.csv"
try:
    # Tạo DataFrame từ dữ liệu đã xử lý
    df = pd.DataFrame(processed_data, columns=["Content", "Label"])
    df.to_csv(output_file, index=False, encoding='utf-8-sig')  # Lưu dữ liệu vào CSV
    print(f"Dữ liệu đã được lưu vào {output_file}")
    
    
except Exception as e:
    print(f"Lỗi khi lưu dữ liệu: {e}")