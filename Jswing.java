import javax.swing.*;
import java.awt.*;

public class SwingInspector {
    public static void main(String[] args) throws Exception {
        for (Window window : Window.getWindows()) {
            if (window instanceof JFrame) {
                System.out.println("Frame: " + ((JFrame) window).getTitle());
                printComponents((Container) window, "  ");
            }
        }
    }

    private static void printComponents(Container container, String indent) {
        for (Component component : container.getComponents()) {
            System.out.println(indent + component.getClass().getSimpleName() + " - Name: " + component.getName());
            if (component instanceof Container) {
                printComponents((Container) component, indent + "  ");
            }
        }
    }
}


import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

public class SoapUiAutomationTest {
    private FrameFixture window;

    @BeforeEach
    public void setUp() {
        JFrame soapUiFrame = GuiActionRunner.execute(() -> {
            // Simulate the SoapUI main window (replace with actual JFrame reference)
            JFrame frame = new JFrame("SoapUI");
            JButton button = new JButton("Run");
            button.setName("runButton"); // Example name
            frame.add(button);
            frame.pack();
            return frame;
        });
        window = new FrameFixture(soapUiFrame);
        window.show();
    }

    @AfterEach
    public void tearDown() {
        window.cleanUp();
    }

    @Test
    public void testRunButtonClick() {
        window.button("runButton").click(); // Locate by name
    }
}



import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;

public class SoapUITest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;

    @Override
    protected void onSetUp() {
        // Find SoapUI's main JFrame
        JFrame soapUiFrame = findSoapUiFrame();
        window = new FrameFixture(robot(), soapUiFrame);
    }

    private JFrame findSoapUiFrame() {
        for (Window window : Window.getWindows()) {
            if (window instanceof JFrame && ((JFrame) window).getTitle().contains("SoapUI")) {
                return (JFrame) window;
            }
        }
        throw new RuntimeException("SoapUI main window not found!");
    }

    @Override
    protected void onTearDown() {
        window.cleanUp();
    }

    @Test
    public void testRunButton() {
        // Click the "Run" button by its name or text
        window.button("runButton").click(); // Assuming the button has a name "runButton"
    }

    @Test
    public void testEnterText() {
        // Interact with a text field by name or another property
        window.textBox("requestField").enterText("Sample API Request");
    }

    @Test
    public void testOpenMenu() {
        // Click a menu item
        window.menuItemWithPath("File", "New SOAP Project").click();
    }
}

window.button().requireText("Run").click(); // Locate a button with text "Run"


window.panel("requestPanel").textBox("requestField").enterText("Sample");



JButton runButton = new JButton("Run");
runButton.setName("runButton"); // Assign a unique name


window.textBox("responseField").requireText("Success");

window.button("runButton").click();
window.textBox("inputField").enterText("Test Input");
window.checkBox("acceptTerms").check();
