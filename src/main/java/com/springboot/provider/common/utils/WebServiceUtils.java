package com.springboot.provider.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.client.Client;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.net.URL;

public class WebServiceUtils {

    private Log logger = LogFactory.getLog(WebServiceUtils.class);

    private static XPath xPath = null;
    private static DocumentBuilder builder = null;

    static {
        try {
            xPath = XPathFactory.newInstance().newXPath();
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private static String url = "http://10.80.5.34:9528/hai/WebServiceEntry?wsdl";

    public String getPlatform(String xml, String service) {
        logger.info(" >>> 调用 WebService 接口：service: " + service + "; 入参: " + xml);
        String reStr = null;
        try {
            Client client = new Client(new URL(url));
            client.setUrl(url);
            Object[] result = client.invoke("invoke", new Object[]{service, "HOL", "HOL", xml});

//            String expression = "/BSXml/MsgBody/Detail/text()";
//            String content = getReXml((String) result[0], expression);

            reStr = (String) result[0];
        } catch (Exception e) {
            logger.error(" >>> 调用 WebService 接口：service: " + service + ", 异常" + e.getMessage());
        }
        logger.info(" >>> 调用 WebService 接口：service: " + service + "; 出参: " + reStr);
        return reStr;
    }

    private String getReXml(String xml, String expression) {
        String content = "";
        if (StringUtils.isNotBlank(xml)) {
            try {
                InputSource is = new InputSource(new StringReader(xml));
                Document document = builder.parse(is);
                content = (String) xPath.evaluate(expression, document, XPathConstants.STRING);

            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return content;
    }

}
