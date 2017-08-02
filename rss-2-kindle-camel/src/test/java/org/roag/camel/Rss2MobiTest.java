package org.roag.camel;

import org.junit.Assert;
import org.junit.Test;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by RomanA on 13/07/2017.
 */
public class Rss2MobiTest {

    private static final String rss_file = "src/test/resources/testrss.xml";
    private static final String text_file = "src/test/resources/test_output.html";
    private static final String text_xslt = "src/main/resources/xslt/rss2html.xsl";
    private static final String toc_file = "src/test/resources/toc.html";
    private static final String toc_xslt = "src/main/resources/xslt/toc.xsl";
    private static final String ncx_file = "src/test/resources/toc.ncx";
    private static final String ncx_xslt = "src/main/resources/xslt/ncx.xsl";
    private static final String opf_file = "src/test/resources/test_output.opf";
    private static final String opf_xslt = "src/main/resources/xslt/opf.xsl";

    @Test
    public void rss2htmlTransformationTest()
    {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(new FileInputStream(text_xslt)));
            transformer.transform(new StreamSource(rss_file), new StreamResult(new FileOutputStream(text_file)));
            Assert.assertTrue("Transformation for text file does not work", new File(text_file).exists());

            transformer = factory.newTransformer(new StreamSource(new FileInputStream(toc_xslt)));
            transformer.transform(new StreamSource(rss_file), new StreamResult(new FileOutputStream(toc_file)));
            Assert.assertTrue("Transformation for toc file does not work", new File(toc_file).exists());

            transformer = factory.newTransformer(new StreamSource(new FileInputStream(ncx_xslt)));
            transformer.transform(new StreamSource(rss_file), new StreamResult(new FileOutputStream(ncx_file)));
            Assert.assertTrue("Transformation for ncx file does not work", new File(ncx_file).exists());

            transformer = factory.newTransformer(new StreamSource(new FileInputStream(opf_xslt)));
            transformer.setParameter("output_file", "test_output.html");
            transformer.transform(new StreamSource(rss_file), new StreamResult(new FileOutputStream(opf_file)));
            Assert.assertTrue("Transformation for opf file does not work", new File(opf_file).exists());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
