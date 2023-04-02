package fi.tuni.resourcedescription.service.utils.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.service.utils.XmlErrorHandler;
import fi.tuni.resourcedescription.service.utils.XmlUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@Service
@Scope("singleton")
public class XmlUtilsImpl implements XmlUtils {
  private static final Logger log = LoggerFactory.getLogger(XmlUtilsImpl.class);

  private final ResourceLoader resourceLoader;

  @Autowired
  public XmlUtilsImpl(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  @Override
  public String xmlStringToJsonString(String xmlString) throws InternalErrorException {
    if (StringUtils.isEmpty(xmlString)) {
      throw new InternalErrorException("Invalid xml value to parse.");
    }

    try {
      XmlMapper xmlMapper = new XmlMapper();
      JsonNode jsonNode = xmlMapper.readTree(xmlString.getBytes());
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(jsonNode);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public String xmlStringViaXsl(String xmlString, String xlsFilePath) throws InternalErrorException {
    if (StringUtils.isBlank(xmlString) || StringUtils.isBlank(xlsFilePath)) {
      throw new InternalErrorException("XML source is empty or incorrect xls file path!");
    }

    return xmlStringViaXsl(xmlString.getBytes(StandardCharsets.UTF_8), xlsFilePath);
  }

  @Override
  public String xmlStringViaXsl(byte[] xmlContent, String xlsFilePath) throws InternalErrorException {
    if (ArrayUtils.isEmpty(xmlContent) || StringUtils.isBlank(xlsFilePath)) {
      throw new InternalErrorException("XML source is empty or incorrect xls file path!");
    }

    try {
      Resource xlstFile = resourceLoader.getResource(xlsFilePath);
      return xmlStringViaXsl(xmlContent, xlstFile);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public String xmlStringViaXsl(byte[] xmlContent, Resource xlstResource) throws InternalErrorException {
    if (ArrayUtils.isEmpty(xmlContent) || Objects.isNull(xlstResource)) {
      throw new InternalErrorException("XML source is empty or incorrect xls file path!");
    }

    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();

      // kinda hard to write unit test in this approach
      Transformer transformer = transformerFactory.newTransformer(new StreamSource(xlstResource.getInputStream()));

      StreamSource xmlSource = new StreamSource(new ByteArrayInputStream(xmlContent));
      StringWriter writer = new StringWriter();
      StreamResult result = new StreamResult(writer);
      transformer.transform(xmlSource, result);
      return writer.toString();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public XmlErrorHandler validateXmlContent(String xmlContent) throws InternalErrorException {
    if (StringUtils.isBlank(xmlContent)) {
      throw new InternalErrorException("Cannot validate a blank xml content.");
    }

    try {
      DocumentBuilderFactory factory = initFactory();
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

      // kinda hard to write unit test in this approach
      File resource = ResourceUtils.getFile("classpath:/templates/xsd/ResourceDesc_v2-0-1.xsd");

      Schema schema = schemaFactory.newSchema(resource);
      factory.setSchema(schema);
      Validator validator = schema.newValidator();
      XmlErrorHandler xmlErrorHandler = new XmlErrorHandler();
      validator.setErrorHandler(xmlErrorHandler);

      validator.validate(new StreamSource(new ByteArrayInputStream(xmlContent.getBytes())));

      return xmlErrorHandler;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  private DocumentBuilderFactory initFactory() {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(true);
    factory.setNamespaceAware(true);
    return factory;
  }
}
