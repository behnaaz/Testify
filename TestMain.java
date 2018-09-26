import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.IOException;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVFormat;

public class TestMain {
    public static final String DEL = "@";
    public static final String LEFT_OP = "left side";
    public static final String RIGHT_OP = "right side";
    public static final String OP = "comprator";

    public int nonStatic() { return 1; }

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
        if (Modifier.isPublic(m.getModifiers()) &&
                ! Modifier.isAbstract(m.getModifiers()) &&
                (m.getReturnType() != Void.TYPE)) {//  || m.getExceptionTypes() != null)) {//TODO test
            return true;
        }
        return false;
    }

    public static String getContext(Method m) {
        String klass = m.getDeclaringClass().getSimpleName();
        if (Modifier.isStatic(m.getModifiers())) {
            return klass; 
        }
        return "new "+klass+"()"; //TTODO getConstructors
    }

    public static String createTestSpec(Method m) {
        String res = getContext(m)+"."+m.getName()+"(";
        for (int i = 0; i < m.getParameterCount(); i++) {
            if (i > 0) {
                res = res + ",";
            }
            res += m.getParameters()[i].getType().getSimpleName();
        }
        res += ")" + DEL + "=" + DEL + m.getReturnType().getSimpleName();
        System.out.println(res);
        return res;
    }

    public static Map<Method, String> createSkeleton(Class klass) {
        Map<Method, String> tests = new HashMap<>();
        System.out.println(klass.getSimpleName() + " -------");
        for (Method m : klass.getDeclaredMethods()) {
            if (isTestable(m)) {
                tests.put(m, createTestSpec(m));
            }
        }
        return tests;
    }

    public static String mapOperator(String operator) {
        switch (operator) {
            case "=": 
                         return "Asssert.AssertEqulas";
            case ">": 
                         break;
            case "<": 
                         break;
            case ">=": 
                         break;
            case "<=": 
                         break;
            case "is": 
                         break;
            case "not is": 
                         break;
            case "exception": 
                         break;
        }
	return null;
    }

     public static List<String> loadTestData(String testDataFile) throws IOException {
        System.out.println("Loading " + testDataFile + "------" );

	List<String> tests = new ArrayList<>();
	Reader in = new FileReader(testDataFile);
        BufferedReader bf = new BufferedReader(in);

        String name = "test";
        int counter = 1;
        String line = bf.readLine();//Title 
	while ((line = bf.readLine()) != null) {
		String[] parts = line.split(DEL);
    		String leftSide = parts[0];
                String relation= parts[1];
    		String rightSide = parts[2];
                String comments = "comments"; //parts[4];
		String test = createTestCode(leftSide, relation, rightSide);
		String newTest = System.lineSeparator() + "    @Test" + System.lineSeparator() + "    public void " + name + counter++ + "() {" + System.lineSeparator() + "        " + test + System.lineSeparator() + "    }";
		System.out.println(newTest);
		tests.add(newTest);
	}
        return tests;
    }

    public static String createTestCode(String actual, String operator, String expected) {
        return mapOperator(operator) + "(" + actual + "," + expected + ");";
    }

    public static List<String> loadTestDataCSV(String testDataFile) throws IOException {
        System.out.println("Loading " + testDataFile + "------" );

	List<String> tests = new ArrayList<>();
	Reader in = new FileReader(testDataFile);

	Iterable<CSVRecord> records = CSVFormat.EXCEL/*.withDelimiter(DEL)*/.parse(in);
	for (CSVRecord record : records) {
    		String leftSide = record.get(LEFT_OP);
                String relation= record.get(OP);
    		String rightSide = record.get(RIGHT_OP);
                String name = record.get("name");
                String comments = record.get("comments");
		String test = createTestCode(leftSide, relation, rightSide);
		System.out.println(test);
		tests.add(System.lineSeparator() + "@Test" + System.lineSeparator() + "public void " + name + "() {" + System.lineSeparator() + test + System.lineSeparator() + "}");
	}
        return tests;
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
            try {
                loadTestData(args[2]);
            } catch (IOException ex) {
                 ex.printStackTrace();
            }
            return;
        }

        usage();
    }
}
