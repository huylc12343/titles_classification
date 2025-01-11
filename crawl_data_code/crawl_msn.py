import os
import time
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.edge.service import Service
from selenium.webdriver.edge.options import Options
import pandas as pd
from tqdm import tqdm  # Thư viện hiển thị tiến trình

# Cấu hình Selenium với Edge
edge_options = Options()
edge_options.add_argument("--headless")  # Chạy dưới chế độ ẩn (bỏ dòng này nếu muốn thấy trình duyệt)
edge_options.add_argument("--disable-gpu")  # Tắt GPU để tăng hiệu suất
edge_service = Service("D:\\edgedriver_win64 (1)\\msedgedriver.exe")  # Đường dẫn tới Edge WebDriver

# Khởi tạo trình duyệt
driver = webdriver.Edge(service=edge_service, options=edge_options)
driver.get("https://thanhnien.vn/the-thao.htm")

# Danh sách để lưu dữ liệu
data = []

# Hàm cuộn trang và thu thập dữ liệu
def fetch_titles(driver, max_scroll=100):
    scroll_count = 0
    pbar = tqdm(total=max_scroll, desc="Đang cào dữ liệu", unit="lần cuộn")  # Tạo thanh tiến trình

    while scroll_count < max_scroll:
        # Lấy các tiêu đề hiện tại
        h3_tags = driver.find_elements(By.CSS_SELECTOR, "h3.box-title-text a.box-category-link-title")
        for h3_tag in h3_tags:
            title = h3_tag.get_attribute("title")
            if title not in data:  # Chỉ thêm nếu chưa tồn tại
                data.append(title)

        # Kiểm tra và nhấn nút "Xem thêm" nếu xuất hiện
        try:
            view_more_button = driver.find_element(By.CSS_SELECTOR, "a.list__center.view-more.list__viewmore")
            if view_more_button.is_displayed():
                view_more_button.click()
                time.sleep(2)  # Chờ dữ liệu tải thêm
        except Exception:
            pass  # Bỏ qua nếu không tìm thấy nút

        # Cuộn xuống cuối trang
        driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
        time.sleep(2)  # Chờ trang tải thêm nội dung

        scroll_count += 1
        pbar.update(1)  # Cập nhật thanh tiến trình

    pbar.close()
    return data

# Lấy tiêu đề từ trang
print("Bắt đầu cào dữ liệu...")
titles = fetch_titles(driver, max_scroll=100)

# Đóng trình duyệt
driver.quit()

# Tạo DataFrame và lưu vào Excel
df = pd.DataFrame({"Title": data})  # Không cần chuyển đổi vì `data` đã là danh sách
output_dir = "D:\\NLP\\titles_classification\\excels"
os.makedirs(output_dir, exist_ok=True)
output_file = os.path.join(output_dir, "the_thao_2.xlsx")
df.to_excel(output_file, index=False)

print(f"Tiêu đề đã được lưu vào file Excel tại: {output_file}")
