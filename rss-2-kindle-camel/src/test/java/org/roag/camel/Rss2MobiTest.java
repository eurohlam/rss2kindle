package org.roag.camel;

import org.apache.camel.PropertyInject;
import org.apache.camel.testng.CamelSpringTestSupport;
import org.junit.Assert;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.FileSystemUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by eurohlam on 13/07/2017.
 */
public class Rss2MobiTest extends CamelSpringTestSupport {


    private static final String rss_file = "src/test/resources/testrss.xml";
    private static final String output_file = "test_output";

    private static final String text_xslt = "src/main/resources/xslt/rss2html.xsl";
    private static final String toc_xslt = "src/main/resources/xslt/toc.xsl";
    private static final String ncx_xslt = "src/main/resources/xslt/ncx.xsl";
    private static final String opf_xslt = "src/main/resources/xslt/opf.xsl";

    @PropertyInject("storage.path.mobi")
    private String pathMobi;


    @Override
    protected AbstractApplicationContext createApplicationContext() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/test-spring-context.xml");
        return context;
    }

    @Override
    public boolean isCreateCamelContextPerClass() {
        return true;
    }

    @Override
    public boolean isUseAdviceWith() {
        return false;
    }

    public void startCamelContext() throws Exception {
        context.start();
    }


    public void stopCamelContext() throws Exception {
        context.stop();
    }

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        File file = new File("test/data");
        if (!FileSystemUtils.deleteRecursively(file)) {
            System.out.println("Problem occurs when deleting the directory : test/data");
        }
        super.setUp();
    }


    @Test(groups = {"transformation"})
    public void rss2htmlTransformationTest() {
        try {
            File dir = new File(pathMobi);
            if (!dir.exists())
                dir.mkdirs();
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(new FileInputStream(text_xslt)));
            transformer.transform(new StreamSource(rss_file), new StreamResult(new FileOutputStream(pathMobi + output_file + ".htm")));
            Assert.assertTrue("Transformation for text file does not work", new File(pathMobi + output_file + ".htm").exists());

            transformer = factory.newTransformer(new StreamSource(new FileInputStream(toc_xslt)));
            transformer.setParameter("output_file", "test_output");
            transformer.transform(new StreamSource(rss_file), new StreamResult(new FileOutputStream(pathMobi + output_file + "_toc.htm")));
            Assert.assertTrue("Transformation for toc file does not work", new File(pathMobi + output_file + "_toc.htm").exists());

            transformer = factory.newTransformer(new StreamSource(new FileInputStream(ncx_xslt)));
            transformer.setParameter("output_file", "test_output");
            transformer.transform(new StreamSource(rss_file), new StreamResult(new FileOutputStream(pathMobi + output_file + ".ncx")));
            Assert.assertTrue("Transformation for ncx file does not work", new File(pathMobi + output_file + ".ncx").exists());

            transformer = factory.newTransformer(new StreamSource(new FileInputStream(opf_xslt)));
            transformer.setParameter("output_file", "test_output");
            transformer.transform(new StreamSource(rss_file), new StreamResult(new FileOutputStream(pathMobi + output_file + ".opf")));
            Assert.assertTrue("Transformation for opf file does not work", new File(pathMobi + output_file + ".opf").exists());

            template.sendBodyAndHeader("seda:kindlegen", null, "CamelFileName", output_file + ".opf");
            Thread.sleep(10000);
            Assert.assertTrue("Transformation for mobi file does not work", new File(pathMobi + output_file + ".mobi").exists());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
