import pandas as pd
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time
import threading

# Function to listen for user input to stop the script
def listen_for_stop():
    input("Press Enter to stop the scrolling...\n")
    global stop_scroll
    stop_scroll = True

# Initialize the WebDriver (use ChromeDriver or GeckoDriver for Firefox)
driver = webdriver.Chrome()

# Open the newspaper website
driver.get("https://thethao247.vn/")

# Global flag to stop scrolling
stop_scroll = False

# Start the input listening in a separate thread
thread = threading.Thread(target=listen_for_stop)
thread.start()

# Wait for the page to load and check for the "Xem thêm" button
wait = WebDriverWait(driver, 10)

while True:
    if stop_scroll:
        print("Scrolling stopped.")
        break

    # Check if the 'Xem thêm' link exists and is clickable using element_to_be_clickable
    try:
        # Wait for the button to be clickable using CSS Selector (replace with your actual class)
        load_more_link = wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, ".btn_loadmore")))  # Replace with your actual class names
        
        # If it's clickable, perform the click action
        load_more_link.click()
        print("Clicked the 'Xem thêm' link...")
        time.sleep(3)  # Wait for new content to load after clicking
    except Exception as e:
        print("No 'Xem thêm' link found or link not clickable:", e)

# After clicking the 'Xem thêm' button and loading new content, extract article titles
articles = driver.find_elements(By.TAG_NAME, "h3")

# Create a list of article titles (remove duplicates using a set)
article_titles = list(set(article.text for article in articles))

# Create a DataFrame from the list of titles
df = pd.DataFrame(article_titles, columns=["Title"])

# Save the DataFrame to an Excel file
df.to_excel("article_titles1.xlsx", index=False, engine='openpyxl')

print("Data has been saved to 'article_titles.xlsx'.")

# Close the browser
driver.quit()
