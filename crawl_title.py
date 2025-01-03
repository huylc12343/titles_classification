import requests
from bs4 import BeautifulSoup
import pandas as pd

# Hàm để lấy tiêu đề bài báo từ một trang
def get_titles_from_page(url):
    titles = []
    try:
        response = requests.get(url)
        html_content = response.content
        soup = BeautifulSoup(html_content, "html.parser")
        
        # Tìm tất cả các thẻ h2 với class 'title-news'
        h2_tags = soup.find_all("h3", class_="title-news")
        for h2_tag in h2_tags:
            a_tag = h2_tag.find("a")
            if a_tag and a_tag.get_text():
                titles.append(a_tag.get_text().strip())
    except Exception as e:
        print("Error:", e)
    return titles

# Hàm để lấy tiêu đề từ nhiều trang
def get_titles_from_multiple_pages(base_url, num_pages):
    all_titles = []
    for page in range(1, num_pages + 1):
        url = f"{base_url}-p{page}"  # URL của từng trang
        titles = get_titles_from_page(url)
        all_titles.extend(titles)  # Thêm tất cả tiêu đề vào danh sách
    return all_titles

# Thay link của chuyên mục cần lấy vào đây
base_url = "https://vnexpress.net/the-gioi"

# Số trang muốn lấy dữ liệu
num_pages = 20

# Lấy tiêu đề từ nhiều trang
titles = get_titles_from_multiple_pages(base_url, num_pages)

# Tạo DataFrame từ danh sách các tiêu đề
df = pd.DataFrame({"Title": titles})

# Lưu DataFrame vào file Excel
df.to_excel("the_gioi_titles.xlsx", index=False)

print("Tiêu đề đã được lưu vào file Excel.")
