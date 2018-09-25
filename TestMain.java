import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class TestMain {
    public static void usage() {
        String programName = TestMain.class.getName();
        System.out.println("USAGE:" + programName + " " + "FileName without any suffix" + "-c");
        System.out.println("    creates the skeleton");

        System.out.println("USAGE:" + programName + " " + "FileName without any suffix" + "-l testdata file");
        System.out.println("    load test data");

        System.out.println("USAGE:" + programName + " " + "FileName without any suffix" + "(method(methods,)*)?");
    }

    public static String findSplitter(final String name) {
        String separator = System.getProperty("file.separator");
        if ("\\".equals(separator)) {
            return "\\\\";
        }
        return separator;
    }

    public static Class loadClass(final String klassName) throws ClassNotFoundException, MalformedURLException {
        ClassLoader classLoader = TestMain.class.getClassLoader();
        String refindName = klassName;
        if (klassName.startsWith("classpath:")) {
            refindName = klassName.substring(10);
            return classLoader.loadClass(refindName);
        }

        else {
            URL url = new URL("file://"+refindName );
            classLoader = new URLClassLoader(new URL[] {url} );
            String fileName = getFileName(url.getFile());
        //  System.out.println("Loading " + fileName + " with " + classLoader);
            return classLoader.loadClass(fileName);
        }
    }

    public static String getFileName(String filePath) {
        String[] fileNamePieces = filePath.split(findSplitter(filePath));
        String toBeRefinedName = fileNamePieces[fileNamePieces.length - 1];
        int lastIndex = Math.max(toBeRefinedName.lastIndexOf("\\"), toBeRefinedName.lastIndexOf("/")) + 1;
     //   System.out.println("File name is : " + toBeRefinedName.substring(lastIndex));
        return toBeRefinedName.substring(lastIndex);
    }

    public static boolean isTestable(Method m) {
        if (Modifier.isPublic(m.getModifiers()) && (m.getReturnType() != Void.TYPE)) {//  || m.getExceptionTypes() != null)) {//TODO test
            return true;
        }
        return false;
    }

    public static String createTest(Method m) {
        String res = m.getName()+"(";
        for (int i = 0; i < m.getParameterCount(); i++) {
            if (i > 0) {
                res = res + ",";
            }
            res += m.getParameters()[i].getType().getSimpleName();
        }
        res += ") = " + m.getReturnType().getSimpleName();
        System.out.println(res);
        return res;
    }

    public static Map<Method, String> createSkeleton(Class klass) {
        Map<Method, String> tests = new HashMap<>();
        System.out.println(klass.getSimpleName() + " -------");
        for (Method m : klass.getDeclaredMethods()) {
            if (isTestable(m)) {
                tests.put(m, createTest(m));
            }
        }
        return tests;
    }

    public static Map loadTestData(String testDataFile) {
        System.out.println("Loading " + testDataFile + "------");

        return null;
    }

    public static void main(String[] args) {
        if (args.length == 0 || args[0].contains(".")) {
            usage();
            return;
        }

        Class c = null;
        try {
            c = loadClass(args[0]);
        } catch (ClassNotFoundException|MalformedURLException ex) {
            System.out.println("Could not find " + args[0]);
            ex.printStackTrace();
            return;
        }

        if (args.length > 1 && "-c".equals(args[1])) {
            createSkeleton(c);
            return;
        }

        if (args.length > 2 && "-l".equals(args[1])) {
            loadTestData(args[2]);
            return;
        }

        usage();
    }
}