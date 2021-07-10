package eu.okaeri.validatortest;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ClassLoaderIClassLoader;
import org.codehaus.janino.Parser;
import org.codehaus.janino.Scanner;
import org.codehaus.janino.UnitCompiler;
import org.codehaus.janino.util.ClassFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Expression {

    private static final Map<Expression, Object> EXPRESSION_CACHE = new HashMap<>();
    private static final ExpressionClassLoader CLASS_LOADER = new ExpressionClassLoader();
    private static final String TEMPLATE =
            "package {package};\n" +
            "\n" +
            "import static java.lang.Math.*;\n" +
            "\n" +
            "public final class {name} implements {interface} {\n" +
            "    public {result_type} eval({input_type} x) {\n" +
            "        return ({expression});\n" +
            "    }\n" +
            "}";

    public static Expression of(String expression) {
        return new Expression(expression);
    }

    private final String expression;
    private Class<?> interfaceType;
    private Class<?> inputType;
    private Class<?> resultType;

    public Expression input(Class<?> inputType) {
        this.inputType = inputType;
        return this;
    }

    public Expression result(Class<?> resultType) {
        this.resultType = resultType;
        return this;
    }

    private String generate(String packageName, String className) {
        String template = TEMPLATE;
        template = template.replace("{package}", packageName);
        template = template.replace("{name}", className);
        template = template.replace("{interface}", this.interfaceType.getCanonicalName());
        template = template.replace("{result_type}", this.resultType.getName());
        template = template.replace("{input_type}", this.inputType.getName());
        template = template.replace("{expression}", this.expression);
        return template;
    }

    @SuppressWarnings("unchecked")
    public <T> T compile(Class<T> asType) throws IOException, CompileException, InstantiationException, IllegalAccessException {

        this.interfaceType = asType;
        Object cached = EXPRESSION_CACHE.get(this);

        if (cached != null) {
            return (T) cached;
        }

        String clazzPackage = this.getClass().getPackage().getName() + ".expr";
        String clazzName = "Expression" + String.valueOf(UUID.randomUUID()).split("-")[4];
        String clazzSource = this.generate(clazzPackage, clazzName);

        Scanner scanner = new Scanner(null, new ByteArrayInputStream(clazzSource.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8.name());
        UnitCompiler unitCompiler = new UnitCompiler(new Parser(scanner).parseAbstractCompilationUnit(), new ClassLoaderIClassLoader(CLASS_LOADER));
        ClassFile[] classFiles = unitCompiler.compileUnit(true, true, true);
        Class<?> clazz = CLASS_LOADER.defineClass(clazzPackage + "." + clazzName, classFiles[0].toByteArray());

        T instance = (T) clazz.newInstance();
        EXPRESSION_CACHE.put(this, instance);

        return instance;
    }

    private final static class ExpressionClassLoader extends SecureClassLoader {
        public Class<?> defineClass(String name, byte[] bytes) {
            ProtectionDomain domain = new ProtectionDomain(null, new Permissions(), this, null);
            return this.defineClass(name, bytes, 0, bytes.length, domain);
        }
    }
}
