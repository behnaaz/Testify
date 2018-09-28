package testify.testify;

import org.junit.Test;

import java.net.MalformedURLException;

import org.junit.Assert;
import testify.TestMain;
public final class TestMainTest {

/***
comments
 * @throws MalformedURLException 
 * @throws ClassNotFoundException 
**/
@Test
    public void test1() throws ClassNotFoundException, MalformedURLException {
        Assert.assertEquals(TestMain.class, TestMain.loadClass("classpath:testify.TestMain"));
    }

/***
comments
**/
@Test
    public void test2() {
        Assert.assertEquals("d.txt", TestMain.getFileName("c://a/b/c/d.txt"));
    }

/***
comments
**/
@Test
    public void test3() {
        Assert.assertEquals("\\\\", TestMain.findSplitter("C:\\abc\\d.txt"));
    }
}