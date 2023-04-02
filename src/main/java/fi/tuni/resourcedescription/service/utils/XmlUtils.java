package fi.tuni.resourcedescription.service.utils;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import org.springframework.core.io.Resource;

public interface XmlUtils {
  String xmlStringToJsonString(String xmlString) throws InternalErrorException;
  String xmlStringViaXsl(String xmlString, String xlsFilePath) throws InternalErrorException;
  String xmlStringViaXsl(byte[] xmlContent, String xlsFilePath) throws InternalErrorException;
  String xmlStringViaXsl(byte[] xmlContent, Resource xlsResource) throws InternalErrorException;

  XmlErrorHandler validateXmlContent(String xmlContent) throws InternalErrorException;
}
