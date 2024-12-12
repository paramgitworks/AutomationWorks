import javax.tools.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;

public class DynamicCodeGenerator {
    public static void main(String[] args) throws Exception {
        // Step 1: Generate Java source code as a String
        String className = "DynamicVariables";
        int variableCount = 100; // Number of variables to create
        String javaSource = generateJavaSource(className, variableCount);

        // Save the generated source code to a file (optional, for debugging)
        Files.write(Paths.get(className + ".java"), javaSource.getBytes());

        // Step 2: Compile the generated Java code
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(className + ".java");
        compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();
        fileManager.close();

        // Step 3: Load the compiled class
        ClassLoader classLoader = DynamicCodeGenerator.class.getClassLoader();
        Class<?> dynamicClass = classLoader.loadClass(className);

        // Step 4: Instantiate the dynamic class and use reflection to access variables
        Object instance = dynamicClass.getDeclaredConstructor().newInstance();

        // Access and modify the dynamically created variables
        for (int i = 1; i <= variableCount; i++) {
            String variableName = "var" + i;
            Field field = dynamicClass.getDeclaredField(variableName);
            field.setAccessible(true);
            field.set(instance, "Value for " + variableName); // Set a value
            System.out.println(variableName + ": " + field.get(instance)); // Get the value
        }
    }

    // Method to generate Java source code dynamically
    private static String generateJavaSource(String className, int variableCount) {
        StringBuilder source = new StringBuilder();
        source.append("public class ").append(className).append(" {\n");

        // Generate fields (variables)
        for (int i = 1; i <= variableCount; i++) {
            source.append("    public String var").append(i).append(";\n");
        }

        // Constructor
        source.append("    public ").append(className).append("() {}\n");
        source.append("}\n");

        return source.toString();
    }
}
