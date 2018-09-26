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
public final class TestSpecGenerator {
 public static final String DEL = "@";
 public static final String LEFT_OP = "left side";
 public static final String RIGHT_OP = "right side";
 public static final String OP = "comprator";
 public List < Constructor > getCallableConstructors(Class klass) {
  List < Constructor > callables = new ArrayList < > ();
  Constructor[] makers = klass.getConstructors();
  for (int i = 0; i < makers.length; i++) {
   if (Modifier.isPublic(makers[i].getModifiers())) {
    callables.add(makers[i]);
   }
  }
  return callables;
 }
 public List < String > getContext(Method m) {
  String klass = m.getDeclaringClass().getSimpleName();
  List < String > list = new ArrayList < > ();
  if (Modifier.isStatic(m.getModifiers())) {
   list.add(klass);
   return list;
  }

  List < Constructor > makers = getCallableConstructors(m.getDeclaringClass());
  for (Constructor c: makers) {
   list.add(c.getName());
   System.out.println("KKKKKKK" + c.getParameters().length);
  }
  return list; //TTODO getConstructors
 }

 public String createTestSpec(Method m) {
  String res = getContext(m) + "." + m.getName() + "(";
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

 public Map < Method, String > createSkeleton(Class klass) {
  Map < Method, String > tests = new HashMap < > ();
  System.out.println(klass.getName() + " -------");
  for (Method m: klass.getDeclaredMethods()) {
   if (isTestable(m)) {
    tests.put(m, createTestSpec(m));
   }
  }
  return tests;
 }
 public boolean isTestable(Method m) {
  if (Modifier.isPublic(m.getModifiers()) &&
   !Modifier.isAbstract(m.getModifiers()) &&
   (m.getReturnType() != Void.TYPE)) { //  || m.getExceptionTypes() != null)) {//TODO test
   return true;
  }
  return false;
 }
}