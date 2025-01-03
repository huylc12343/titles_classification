import os
import requests
from bs4 import BeautifulSoup
import pandas as pd

# Hàm để lấy tiêu đề bài báo từ một trang
def get_titles_from_page(url):
    titles = []
    try:
        response = requests.get(url, timeout=10)  # Thêm timeout
        response.raise_for_status()  # Kiểm tra HTTP status code
        html_content = response.content
        soup = BeautifulSoup(html_content, "html.parser")
        
        # Tìm tất cả các thẻ h3 với class 'title-news'
        h3_tags = soup.find_all("h2", class_="title-news")
        for h3_tag in h3_tags:
            a_tag = h3_tag.find("a")
            if a_tag and a_tag.get_text():
                titles.append(a_tag.get_text().strip())
    except requests.exceptions.RequestException as e:
        print(f"Error fetching {url}: {e}")
    return titles

# Hàm để lấy tiêu đề từ nhiều trang
def get_titles_from_multiple_pages(base_url, num_pages):
    all_titles = []
    for page in range(1, num_pages + 1):
        url = f"{base_url}-p{page}"  # URL của từng trang
        print(f"Fetching: {url}")
        titles = get_titles_from_page(url)
        all_titles.extend(titles)  # Thêm tất cả tiêu đề vào danh sách
    return all_titles

# Thay link của chuyên mục cần lấy vào đây
base_url = "https://vnexpress.net/thoi-su/chinh-tri"

# Số trang muốn lấy dữ liệu
num_pages = 20

# Lấy tiêu đề từ nhiều trang
titles = get_titles_from_multiple_pages(base_url, num_pages)

# Tạo DataFrame từ danh sách các tiêu đề
df = pd.DataFrame({"Title": titles})

# Kiểm tra và tạo thư mục nếu chưa tồn tại
output_dir = "D:\\NLP\\titles_classification\\excels"
os.makedirs(output_dir, exist_ok=True)

# Lưu DataFrame vào file Excel
output_file = os.path.join(output_dir, "chinh_tri.xlsx")
df.to_excel(output_file, index=False)        

print(f"Tiêu đề đã được lưu vào file Excel tại: {output_file}")
