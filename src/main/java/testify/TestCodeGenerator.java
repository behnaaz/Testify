package testify;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public final class TestCodeGenerator {
 public static final String DEL = "@";
 public static final String LEFT_OP = "left side";
 public static final String RIGHT_OP = "right side";
 public static final String OP = "comprator";
 public List < String > loadTestDataCSV(String testDataFile) throws IOException {
  //System.out.println("Loading " + testDataFile + "------");

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
   tests.add(createComments(comments) + "@Test" + System.lineSeparator() + "public void " + name + "() {" + System.lineSeparator() + test + System.lineSeparator() + "}");
  }
  return tests;
 }
 
 public String createComments(final String comments) {
     return System.lineSeparator() + "/***" + System.lineSeparator() + comments + System.lineSeparator() + "**/" + System.lineSeparator();
 }
 
 public String mapOperator(String operator) {
  switch (operator) {
   case "=":
    return "Assert.assertEquals";
   case "!=":
    return "Assert.assertNotEquals";
   case ">":
    break;
   case "<":
    break;
   case ">=":
    break;
   case "<=":
    break;
   case "null":
    return "Assert.assertNull";
   case "!null":
    return "Assert.assertNotNull";
   case "exception":
    break;
  }
  return null;
 }

 public String createTestCode(String actual, String operator, String expected) {
  String res = mapOperator(operator) + "(" + actual;
  if (! "null".equals(operator) && ! "!null".equals(operator)) {
   res += "," + expected;
  }
  return res + ");";
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
  String[] classNameParts = pieces[0].split("\\.");
  preTest += System.lineSeparator() + "public final class " + classNameParts[classNameParts.length - 1] + "Test {";
  System.out.println(preTest);
  while ((line = bf.readLine()) != null) {
   if (line.startsWith("//") || line.trim().length() == 0) {
    continue;
   }
   bf.close();
   String[] parts = line.split(DEL);
   String leftSide = parts[0];
   String relation = parts[1];
   String rightSide = parts[2];
   String comments = "comments"; //parts[4];
   String test = createTestCode(leftSide, relation, rightSide);
   String newTest = createComments(comments) + "@Test" + System.lineSeparator() + "    public void " + name + counter++ + "() {" + System.lineSeparator() + "        " + test + System.lineSeparator() + "    }";
   System.out.println(newTest);
   tests.add(newTest);
  }
  System.out.println("}"); //post test
  return tests;
 }

}
