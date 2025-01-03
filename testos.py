import pandas as pd

import os
data_dir = 'excels'
for categories in os.listdir(data_dir):
    file_path = os.path.join(data_dir, categories)
    for file in os.listdir(file_path):
        if file.endswith('.xlsx'):
            try:
                df = pd.read_excel(file_path + '/' + file)
            except:
                print(f"{file_path + '/' + file} Không đọc được")
        