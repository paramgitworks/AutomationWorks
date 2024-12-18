from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

class BasePage:
    def __init__(self, driver):
        self.driver = driver

    def find_element(self, locator, timeout=10):
        """Wait for an element to be visible and return it."""
        return WebDriverWait(self.driver, timeout).until(EC.visibility_of_element_located(locator))

    def click(self, locator, timeout=10):
        """Wait for an element to be clickable and click it."""
        element = WebDriverWait(self.driver, timeout).until(EC.element_to_be_clickable(locator))
        element.click()

    def enter_text(self, locator, text, timeout=10):
        """Wait for an element to be visible and enter text."""
        element = self.find_element(locator, timeout)
        element.clear()
        element.send_keys(text)

**************

from selenium.webdriver.common.by import By
from app.pages.base_page import BasePage

class LoginPage(BasePage):
    # Define locators
    USERNAME_INPUT = (By.ID, "username")
    PASSWORD_INPUT = (By.ID, "password")
    LOGIN_BUTTON = (By.ID, "login")

    # Define methods for actions
    def login(self, username, password):
        self.enter_text(self.USERNAME_INPUT, username)
        self.enter_text(self.PASSWORD_INPUT, password)
        self.click(self.LOGIN_BUTTON)


**************


import pytest
from selenium import webdriver
from selenium.webdriver.chrome.service import Service

@pytest.fixture
def driver():
    # Set up ChromeDriver
    service = Service("C:/path/to/chromedriver.exe")
    driver = webdriver.Chrome(service=service)
    driver.maximize_window()
    yield driver
    driver.quit()

****************

from app.pages.login_page import LoginPage

def test_login(driver):
    # Navigate to the login page
    driver.get("https://www.example.com/login")
    
    # Use the LoginPage object
    login_page = LoginPage(driver)
    login_page.login("testuser", "password123")

    # Add assertions
    assert "Welcome" in driver.page_source



**************


optional
class LoginPage(BasePage):
    @property
    def username_input(self):
        return self.find_element((By.ID, "username"))

    @property
    def password_input(self):
        return self.find_element((By.ID, "password"))

    @property
    def login_button(self):
        return self.find_element((By.ID, "login"))

    def login(self, username, password):
        self.username_input.send_keys(username)
        self.password_input.send_keys(password)
        self.login_button.click()
