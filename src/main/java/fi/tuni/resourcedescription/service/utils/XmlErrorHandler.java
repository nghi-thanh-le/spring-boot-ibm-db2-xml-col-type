package fi.tuni.resourcedescription.service.utils;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@Getter
public class XmlErrorHandler implements ErrorHandler {
  private final List<SAXParseException> parseExceptions;

  public XmlErrorHandler() {
    parseExceptions = new ArrayList<>();
  }

  @Override
  public void warning(SAXParseException exception) throws SAXException {
    parseExceptions.add(exception);
  }

  @Override
  public void error(SAXParseException exception) throws SAXException {
    parseExceptions.add(exception);
  }

  @Override
  public void fatalError(SAXParseException exception) throws SAXException {
    parseExceptions.add(exception);
  }
}
