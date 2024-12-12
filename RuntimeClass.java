import java.io.StringWriter;
import java.util.Collections;
import javax.tools.*;
import java.lang.reflect.Method;

public class Main {
    public static void main(String[] args) throws Exception {
        // Source code of the class
        String sourceCode = "package com.example;\n" +
            "public class GeneratedClass {\n" +
            "    public static String message = \"Hello, World!\";\n" +
            "    public static void printMessage() {\n" +
            "        System.out.println(\"Hello from GeneratedClass!\");\n" +
            "    }\n" +
            "}";

        // In-memory compilation
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaFileObject file = new SimpleJavaFileObject(
            java.net.URI.create("string:///" + "com/example/GeneratedClass.java"),
            JavaFileObject.Kind.SOURCE
        ) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return sourceCode;
            }
        };

        StringWriter output = new StringWriter();
        JavaCompiler.CompilationTask task = compiler.getTask(
            output,
            null,
            diagnostics,
            null,
            null,
            Collections.singletonList(file)
        );

        boolean success = task.call();
        if (!success) {
            for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
                System.out.println(diagnostic);
            }
            throw new RuntimeException("Compilation failed.");
        }

        // Load the compiled class
        ClassLoader classLoader = ToolProvider.getSystemToolClassLoader();
        Class<?> generatedClass = classLoader.loadClass("com.example.GeneratedClass");

        // Use reflection to access the class and its methods
        Method method = generatedClass.getDeclaredMethod("printMessage");
        method.invoke(null); // Call the static method
    }
}
