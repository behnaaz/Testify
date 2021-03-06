package testify;
import java.io.IOException;
import java.net.MalformedURLException;

@SuppressWarnings("rawtypes")
public class TestMain {
 static boolean debug = false;
 public static void usage() {
  String programName = TestMain.class.getName();
  System.out.println("USAGE:" + programName + " -c path class");
  System.out.println("    creates the skeleton");

  System.out.println("USAGE:" + programName + " -l test_data_file");
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
  debug = ("-d".equals(args[args.length - 1]));
  if (args.length > 2 && "-c".equals(args[0])) {
     TestSpecGenerator generator = new TestSpecGenerator();
     Class c = null;
     try {
           c = generator.loadClass(args[1], args[2]);
     } catch (ClassNotFoundException | MalformedURLException ex) {
           System.out.println("Make sure the path to jar file containing the class is correct and the class has the full namespace in its name");
           ex.printStackTrace();
     return;
    }
    generator.createSkeleton(c);
    return;
  }

  usage();
 }
}
