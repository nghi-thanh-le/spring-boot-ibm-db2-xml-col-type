package fi.tuni.resourcedescription.service.utils;


import fi.tuni.resourcedescription.payload.QueryResult;
import java.io.IOException;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

public interface QueryResultUtil {
  QueryResult build(String result)
    throws ParserConfigurationException, IOException, TransformerException, SAXException, JAXBException;
  QueryResult build(List<String> results)
    throws ParserConfigurationException, IOException, TransformerException, SAXException;
}
