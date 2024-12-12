import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Paths;

public class JavaPoetExample {
    public static void main(String[] args) throws IOException {
        // Define the class name and package
        String className = "DynamicVariables";

        // Create a class builder
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC);

        // Add fields dynamically
        int variableCount = 100;
        for (int i = 1; i <= variableCount; i++) {
            FieldSpec field = FieldSpec.builder(String.class, "var" + i)
                    .addModifiers(Modifier.PUBLIC)
                    .build();
            classBuilder.addField(field);
        }

        // Build the class
        TypeSpec dynamicClass = classBuilder.build();

        // Write to a .java file
        JavaFile javaFile = JavaFile.builder("", dynamicClass).build();
        javaFile.writeTo(Paths.get("./")); // Writes to the current directory
    }
}
