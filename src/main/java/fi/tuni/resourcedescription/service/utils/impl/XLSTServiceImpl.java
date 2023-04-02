package fi.tuni.resourcedescription.service.utils.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import fi.tuni.resourcedescription.model.XLSTFile;
import fi.tuni.resourcedescription.service.utils.XLSTService;

@Service
public class XLSTServiceImpl implements XLSTService {
  // @Value("classpath:/git-resources/stylesheets/*.xsl")
  private final Resource[] stylesheetFiles_1 = new Resource[] {};
  // @Value("classpath:/templates/xslt/*.xsl")
  private final Resource[] stylesheetFiles_2 = new Resource[] {};
  
  @Override
  public List<XLSTFile> getAllFiles() {
    return Arrays.stream(ArrayUtils.addAll(stylesheetFiles_1, stylesheetFiles_2))
      .map(resourceFile -> {
        try {
          XLSTFile xlstFile = new XLSTFile();
          xlstFile.setName(resourceFile.getFilename());
          xlstFile.setResource(resourceFile);
          return xlstFile;
        } catch(Exception ex) {
          ex.printStackTrace();
          return null;
        }
      })
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }
}
