Additional Methods for the Base Class

1. Wait Methods

Wait for Visibility

def wait_for_visibility(self, locator, timeout=10):
    """Wait until the element is visible."""
    return WebDriverWait(self.driver, timeout).until(EC.visibility_of_element_located(locator))

Wait for Invisibility

def wait_for_invisibility(self, locator, timeout=10):
    """Wait until the element is invisible."""
    return WebDriverWait(self.driver, timeout).until(EC.invisibility_of_element(locator))

Wait for Element Presence

def wait_for_presence(self, locator, timeout=10):
    """Wait until the element is present in the DOM."""
    return WebDriverWait(self.driver, timeout).until(EC.presence_of_element_located(locator))



---

2. Dropdown Methods

Select by Visible Text

from selenium.webdriver.support.ui import Select

def select_by_visible_text(self, locator, text):
    """Select an option from a dropdown by visible text."""
    element = self.wait_for_visibility(locator)
    Select(element).select_by_visible_text(text)

Select by Value

def select_by_value(self, locator, value):
    """Select an option from a dropdown by value."""
    element = self.wait_for_visibility(locator)
    Select(element).select_by_value(value)

Select by Index

def select_by_index(self, locator, index):
    """Select an option from a dropdown by index."""
    element = self.wait_for_visibility(locator)
    Select(element).select_by_index(index)



---

3. Checkbox and Radio Button Methods

Check

def check(self, locator):
    """Check a checkbox if not already checked."""
    element = self.wait_for_visibility(locator)
    if not element.is_selected():
        element.click()

Uncheck

def uncheck(self, locator):
    """Uncheck a checkbox if already checked."""
    element = self.wait_for_visibility(locator)
    if element.is_selected():
        element.click()

Is Selected

def is_selected(self, locator):
    """Check if a checkbox or radio button is selected."""
    element = self.wait_for_visibility(locator)
    return element.is_selected()



---

4. Keyboard and Mouse Actions

Hover Over an Element

from selenium.webdriver.common.action_chains import ActionChains

def hover_over(self, locator):
    """Hover over an element."""
    element = self.wait_for_visibility(locator)
    ActionChains(self.driver).move_to_element(element).perform()

Double Click

def double_click(self, locator):
    """Double click on an element."""
    element = self.wait_for_visibility(locator)
    ActionChains(self.driver).double_click(element).perform()

Right Click

def right_click(self, locator):
    """Right click on an element."""
    element = self.wait_for_visibility(locator)
    ActionChains(self.driver).context_click(element).perform()



---

5. JavaScript Executor Methods

Execute Script

def execute_script(self, script, *args):
    """Execute JavaScript on the page."""
    return self.driver.execute_script(script, *args)

Scroll to Element

def scroll_to_element(self, locator):
    """Scroll to an element."""
    element = self.wait_for_visibility(locator)
    self.execute_script("arguments[0].scrollIntoView(true);", element)

Scroll to Bottom

def scroll_to_bottom(self):
    """Scroll to the bottom of the page."""
    self.execute_script("window.scrollTo(0, document.body.scrollHeight);")



---

6. Frame and Window Methods

Switch to Frame

def switch_to_frame(self, locator):
    """Switch to an iframe by its locator."""
    element = self.wait_for_visibility(locator)
    self.driver.switch_to.frame(element)

Switch to Default Content

def switch_to_default_content(self):
    """Switch back to the main content."""
    self.driver.switch_to.default_content()

Switch to Window by Title

def switch_to_window_by_title(self, title):
    """Switch to a window by its title."""
    for handle in self.driver.window_handles:
        self.driver.switch_to.window(handle)
        if self.driver.title == title:
            return
    raise Exception(f"Window with title '{title}' not found.")



---

7. Alert Methods

Accept Alert

def accept_alert(self):
    """Accept a JavaScript alert."""
    WebDriverWait(self.driver, 10).until(EC.alert_is_present()).accept()

Dismiss Alert

def dismiss_alert(self):
    """Dismiss a JavaScript alert."""
    WebDriverWait(self.driver, 10).until(EC.alert_is_present()).dismiss()

Get Alert Text

def get_alert_text(self):
    """Get text from a JavaScript alert."""
    return WebDriverWait(self.driver, 10).until(EC.alert_is_present()).text



---

8. Screenshot Methods

Take Screenshot

def take_screenshot(self, file_path):
    """Take a screenshot of the current page."""
    self.driver.save_screenshot(file_path)



---

9. Miscellaneous Methods

Check if Element Exists

def is_element_present(self, locator, timeout=5):
    """Check if an element is present on the page."""
    try:
        WebDriverWait(self.driver, timeout).until(EC.presence_of_element_located(locator))
        return True
    except TimeoutException:
        return False

Get Page Title

def get_page_title(self):
    """Get the title of the current page."""
    return self.driver.title

Refresh Page

def refresh_page(self):
    """Refresh the current page."""
    self.driver.refresh()
