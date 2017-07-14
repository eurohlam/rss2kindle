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

    private static final String input_file = "src/test/resources/testrss.xml";
    private static final String output_file = "src/test/resources/test_output.html";
    private static final String xslt = "src/main/resources/rss2html.xsl";

    @Test
    public void rss2htmlTransformationTest()
    {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(new FileInputStream(xslt)));
            transformer.transform(new StreamSource(input_file), new StreamResult(new FileOutputStream(output_file)));
            Assert.assertTrue(new File(output_file).exists());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
