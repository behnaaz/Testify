package testify;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

@SuppressWarnings("rawtypes")
public final class TestSpecGenerator {
 public static final String DEL = "@";
 public static final String LEFT_OP = "left side";
 public static final String RIGHT_OP = "right side";
 public static final String OP = "comprator";
 public List < Constructor > getCallableConstructors(Class klass) {
List < Constructor > callables = new ArrayList < > ();
  Constructor[] makers = klass.getDeclaredConstructors();
  for (int i = 0; i < makers.length; i++) {
   if (Modifier.isPublic(makers[i].getModifiers())) {
    callables.add(makers[i]);
   }
  }
  if (TestMain.debug) {
     System.out.println(callables.size() + " callable constructor(s) found!");
  }
  return callables;
 }

 public Class loadClass(final String path, final String klassName) throws ClassNotFoundException, MalformedURLException {
  URL[] urls = new URL[]{ new URL(path.startsWith("file:")?path:("file:"+path)) };
  @SuppressWarnings("resource")
ClassLoader classLoader = new URLClassLoader(urls);//TODO investigate resource leak
  Class result;
  // URL url = new URL("file://" + klassName);
   //classLoader = new URLClassLoader(new URL[] { url });//, Thread.currentThread().getContextClassLoader());
   //String fileName = getFileName(klassName);
   //System.out.println("Loading " + klassName + " with " + classLoader);
   result = classLoader.loadClass(klassName);
   classLoader = null;
   return result;
 }

 public String getFileName(String filePath) {
  String[] fileNamePieces = filePath.split(findSplitter(filePath));
  String toBeRefinedName = fileNamePieces[fileNamePieces.length - 1];
  int lastIndex = Math.max(toBeRefinedName.lastIndexOf("\\"), toBeRefinedName.lastIndexOf("/")) + 1;
  if (TestMain.debug) {
    System.out.println("File name is : " + toBeRefinedName.substring(lastIndex));
  }
  return toBeRefinedName.substring(lastIndex);
 }

public String findSplitter(final String name) {
String separator = System.getProperty("file.separator");
if ("\\".equals(separator)) {
return "\\\\";
}
return separator;
}

 public List < String > getContext(Method m) {
  if (TestMain.debug) {
    System.out.println("Finding invoker context for " + m);
  }
  String klass = m.getDeclaringClass().getSimpleName();
  List < String > list = new ArrayList < > ();
  if (Modifier.isStatic(m.getModifiers())) {
   list.add(klass);
   return list;
  }

  List < Constructor > makers = getCallableConstructors(m.getDeclaringClass());
  for (Constructor c: makers) {
   String call = "new "+ klass + "(";
   for (int i = 0; i < c.getParameterTypes().length; i++) {
     if (i > 0) {
       call += ',';
     }
     call += c.getParameterTypes()[i].getSimpleName();
   }
   call += ")";
   list.add(call); 
  }
  if (list.isEmpty()) {
     return Collections.singletonList("builder //TODO needs builder");
  }
  return list; //TTODO getConstructors
 }

 public List<String> createTestSpec(Method m) {
  List<String> tests = new ArrayList<>();
  for (String c : getContext(m)) {
    System.out.println("//--------------");
    tests.add(createTestSpec(m, c)); 
  }
  return tests;	
 }

 public String createTestSpec(Method m, String caller) {
  String res = caller + "." + m.getName() + "(";
  for (int i = 0; i < m.getParameterCount(); i++) {
   if (i > 0) {
    res = res + ",";
   }
   res += m.getParameters()[i].getType().getSimpleName();
  }
  res += ")" + DEL + "=" + DEL + m.getReturnType().getSimpleName();
  System.out.println(res);
  System.out.println();
  return res;
 }

 public Map < Method, List<String> > createSkeleton(Class klass) {
  Map < Method, List<String> > tests = new HashMap < > ();
  System.out.println(klass.getName() + " -------");
  for (Method m: klass.getDeclaredMethods()) {
   if (TestMain.debug) {
     System.out.println("Checking method " + m);
   }
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
