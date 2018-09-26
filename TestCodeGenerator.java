import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
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
public final class TestCodeGenerator {
 public static final String DEL = "@";
 public static final String LEFT_OP = "left side";
 public static final String RIGHT_OP = "right side";
 public static final String OP = "comprator";
 public List < String > loadTestDataCSV(String testDataFile) throws IOException {
  System.out.println("Loading " + testDataFile + "------");

  List < String > tests = new ArrayList < > ();
  Reader in = new FileReader(testDataFile);

  Iterable < CSVRecord > records = CSVFormat.EXCEL /*.withDelimiter(DEL)*/ .parse( in );
  for (CSVRecord record: records) {
   String leftSide = record.get(LEFT_OP);
   String relation = record.get(OP);
   String rightSide = record.get(RIGHT_OP);
   String name = record.get("name");
   String comments = record.get("comments");
   String test = createTestCode(leftSide, relation, rightSide);
   System.out.println(test);
   tests.add(System.lineSeparator() + "@Test" + System.lineSeparator() + "public void " + name + "() {" + System.lineSeparator() + test + System.lineSeparator() + "}");
  }
  return tests;
 }
 public String mapOperator(String operator) {
  switch (operator) {
   case "=":
    return "Assert.assertEquals";
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

 public String createTestCode(String actual, String operator, String expected) {
  return mapOperator(operator) + "(" + actual + "," + expected + ");";
 }

 public List < String > loadTestData(String testDataFile) throws IOException {
  System.out.println("Loading " + testDataFile + "------");

  List < String > tests = new ArrayList < > ();
  Reader in = new FileReader(testDataFile);
  BufferedReader bf = new BufferedReader( in );

  String name = "test";
  int counter = 1;
  String line = bf.readLine();
  String pieces[] = line.split(" "); //TODO 
  String preTest = "import org.junit.Test;" + System.lineSeparator() + "import org.junit.Assert;" + System.lineSeparator() + "import " + pieces[0] + ";";
  System.out.println(preTest);
  while ((line = bf.readLine()) != null) {
   if (line.startsWith("//")) {
    continue;
   }
   String[] parts = line.split(DEL);
   String leftSide = parts[0];
   String relation = parts[1];
   String rightSide = parts[2];
   String comments = "comments"; //parts[4];
   String test = createTestCode(leftSide, relation, rightSide);
   String newTest = System.lineSeparator() + "    @Test" + System.lineSeparator() + "    public void " + name + counter++ + "() {" + System.lineSeparator() + "        " + test + System.lineSeparator() + "    }";
   System.out.println(newTest);
   tests.add(newTest);
  }
  return tests;
 }

}