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

public class TestMain {
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
  } else {
   URL url = new URL("file://" + refindName);
   classLoader = new URLClassLoader(new URL[] {
    url
   });
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

 public static void usage() {
  String programName = TestMain.class.getName();
  System.out.println("USAGE:" + programName + " " + "FileName without any suffix" + "-c");
  System.out.println("    creates the skeleton");

  System.out.println("USAGE:" + programName + " " + "FileName without any suffix" + "-l testdata file");
  System.out.println("    load test data");

  System.out.println("Comment a line in test spec file with //");
 }



 public static void main(String[] args) {
  if (args.length == 0) {
   usage();
   return;
  }

  Class c = null;
  try {
   c = loadClass(args[0]);
  } catch (ClassNotFoundException | MalformedURLException ex) {
   System.out.println("Could not find " + args[0]);
   ex.printStackTrace();
   return;
  }

  if (args.length > 1 && "-c".equals(args[1])) {
   new TestSpecGenerator().createSkeleton(c);
   return;
  }

  if (args.length > 2 && "-l".equals(args[1])) {
   try {
    new TestCodeGenerator().loadTestData(args[2]);
   } catch (IOException ex) {
    ex.printStackTrace();
   }
   return;
  }

  usage();
 }
}