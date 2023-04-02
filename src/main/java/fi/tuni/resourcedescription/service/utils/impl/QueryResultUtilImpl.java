package fi.tuni.resourcedescription.service.utils.impl;

import fi.tuni.resourcedescription.payload.QueryResult;
import fi.tuni.resourcedescription.service.utils.QueryResultUtil;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Service
public class QueryResultUtilImpl implements QueryResultUtil {

  @Override
  public QueryResult build(String result)
    throws ParserConfigurationException, IOException, TransformerException, SAXException, JAXBException {
    if (StringUtils.isBlank(result)) {
      return new QueryResult();
    }

    StringWriter stringWriter = transformXmlStringResult(result);
    return new QueryResult(stringWriter.toString());
  }

  @Override
  public QueryResult build(List<String> results)
    throws ParserConfigurationException, IOException, TransformerException, SAXException {
    if (Objects.isNull(results) || results.isEmpty()) {
      return new QueryResult();
    }

    StringBuilder combinedResult = new StringBuilder();
    results.forEach(combinedResult::append);
    StringWriter stringWriter = transformXmlStringResult(combinedResult.toString());
    return new QueryResult(stringWriter.toString());
  }

  private StringWriter transformXmlStringResult(String result)
    throws ParserConfigurationException, IOException, SAXException, TransformerException {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
      .append("<?xml version=\"1.0\"?>")
      .append("<result>");

    if (StringUtils.isNotBlank(result)) {
      stringBuilder.append(result);
    }

    stringBuilder.append("</result>");

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(new InputSource(new StringReader(stringBuilder.toString())));
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    doc.getDocumentElement().normalize();
    StringWriter stringWriter = new StringWriter();
    transformer.transform(new DOMSource(doc), new StreamResult(stringWriter));
    return stringWriter;
  }
}
