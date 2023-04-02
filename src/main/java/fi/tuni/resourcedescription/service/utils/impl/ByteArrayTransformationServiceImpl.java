package fi.tuni.resourcedescription.service.utils.impl;

import fi.tuni.resourcedescription.service.utils.ByteArrayTransformationService;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class ByteArrayTransformationServiceImpl implements ByteArrayTransformationService {
  private static final Logger log = LoggerFactory.getLogger(ByteArrayTransformationServiceImpl.class);

  @Override
  public String transformBytesArray(byte[] content, boolean beautifulFormat) {
    if (ArrayUtils.isEmpty(content)) {
      return StringUtils.EMPTY;
    }

    try {
      Source xmlInput = new StreamSource(new StringReader(new String(content, StandardCharsets.UTF_8)));
      StringWriter stringWriter = new StringWriter();
      StreamResult xmlOutput = new StreamResult(stringWriter);

      Transformer transformer = TransformerFactory.newInstance().newTransformer();

      if (beautifulFormat) {
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      }

      transformer.transform(xmlInput, xmlOutput);
      return xmlOutput.getWriter().toString();
    } catch (TransformerException e) {
      log.error(e.getMessage(), e);
      return StringUtils.EMPTY;
    }
  }
}
