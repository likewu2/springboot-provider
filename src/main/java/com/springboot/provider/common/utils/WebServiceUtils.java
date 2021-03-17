package com.springboot.provider.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.client.Client;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class WebServiceUtils {

    private Log logger = LogFactory.getLog(WebServiceUtils.class);

    private static String url = "http://10.80.5.34:9528/hai/WebServiceEntry?wsdl";

    public String getPlatform(String xml, String service) {
        logger.info(" >>> 调用 WebService 接口：service: " + service + "; 入参: " + xml);
        String reStr = null;
        try {
            Client client = new Client(new URL(url));
            client.setUrl(url);
            Object[] result = client.invoke("invoke", new Object[]{service, "HOL", "HOL", xml});

//            Map map;
//            map = getReXml((String) result[0]);
//            if (map != null && map.size() > 0) {
//                reStr = map.get("Detail");
//            }
            reStr = (String) result[0];
        } catch (Exception e) {
            logger.error(" >>> 调用 WebService 接口：service: " + service + ", 异常" + e.getMessage());
        }
        logger.info(" >>> 调用 WebService 接口：service: " + service + "; 出参: " + reStr);
        return reStr;
    }

    private Map<String, String> getReXml(String xml) {
        Map<String, String> map = new HashMap<>();
        try {
            if (!("".equals(xml))) {
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                InputSource is = new InputSource(new StringReader(xml));
                XPath xPath = XPathFactory.newInstance().newXPath();
                Document document = builder.parse(is);
                map.put("Detail", (String) xPath.evaluate("/BSXml/MsgBody/Detail/text()", document, XPathConstants.STRING));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return map;
    }

}
