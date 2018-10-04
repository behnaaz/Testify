package testify;

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
    /* TODO include as resource  
    Assert.assertEquals(TestMain.class, TestMain.loadClass("C:/Users/Max/Desktop/Testify", "testify.TestMain"));
        Assert.assertNotEquals(TestMain.class, TestMain.loadClass("C:/Users/Max/Desktop/A/A.jar", "A"));
        Assert.assertNotEquals(TestMain.class, TestMain.loadClass("C:/Projects/rabo/payment-data-service/app/target/app.jar", "org.springframework.boot.loader.Launcher"));
*/
        Assert.assertNotEquals(TestMain.class, new TestSpecGenerator().loadClass("C:/Projects/rabo/payment-data-service/app/target/classes/", "nl.rabobank.online.paymentssavings.data.service.mapper.CurrencyMapper"));

}

/***
comments
**/
@Test
    public void test2() {
        Assert.assertEquals("d.txt", new TestSpecGenerator().getFileName("c://a/b/c/d.txt"));
    }

/***
comments
**/
@Test
    public void test3() {
        Assert.assertEquals("\\\\", new TestSpecGenerator().findSplitter("C:\\abc\\d.txt"));
    }
}