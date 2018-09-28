package testify;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

@SuppressWarnings("rawtypes")
public class TestMain {
 public static String findSplitter(final String name) {
  String separator = System.getProperty("file.separator");
  if ("\\".equals(separator)) {
   return "\\\\";
  }
  return separator;
 }

 public static Class loadClass(final String path, final String klassName) throws ClassNotFoundException, MalformedURLException {
  URL[] urls = new URL[]{ new URL(path.startsWith("file:")?path:("file:"+path)) };
  ClassLoader classLoader = new URLClassLoader(urls);
  Class result;
  // URL url = new URL("file://" + klassName);
   //classLoader = new URLClassLoader(new URL[] { url });//, Thread.currentThread().getContextClassLoader());
   //String fileName = getFileName(klassName);
   System.out.println("Loading " + klassName + " with " + classLoader);
   result = classLoader.loadClass(klassName);
   return result;
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

  if (args.length > 1 && "-l".equals(args[0])) {
      try {
       new TestCodeGenerator().loadTestData(args[1]);
      } catch (IOException ex) {
       ex.printStackTrace();
      }
      return;
     }
  
  Class c = null;
  try {
    //  URL url = new URL("file://" + klassName);
    //  classLoader = new URLClassLoader(new URL[] { url });//, Thread.currentThread().getContextClassLoader());
      c = loadClass(args[0], args[1]);
  } catch (ClassNotFoundException | MalformedURLException ex) {
   System.out.println("Could not find " + args[0]);
   ex.printStackTrace();
   return;
  }

  if (args.length > 1 && "-c".equals(args[1])) {
   new TestSpecGenerator().createSkeleton(c);
   return;
  }

  usage();
 }
}