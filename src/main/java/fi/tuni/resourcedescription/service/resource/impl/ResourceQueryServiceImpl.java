package fi.tuni.resourcedescription.service.resource.impl;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceDescription;
import fi.tuni.resourcedescription.payload.QueryResult;
import fi.tuni.resourcedescription.rowmapper.ResourceDescriptionRowMapper;
import fi.tuni.resourcedescription.rowmapper.StringRowMapper;
import fi.tuni.resourcedescription.service.resource.ResourceQueryService;
import fi.tuni.resourcedescription.service.resource.ResourceService;
import fi.tuni.resourcedescription.service.utils.QueryResultUtil;
import fi.tuni.resourcedescription.service.utils.XmlUtils;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ResourceQueryServiceImpl implements ResourceQueryService {
  private static final Logger log = LoggerFactory.getLogger(ResourceQueryServiceImpl.class);

  private final ResourceService resourceService;
  private final QueryResultUtil queryResultUtil;
  private final JdbcTemplate jdbcTemplate;
  private final XmlUtils xmlUtils;

  protected static final String SIMPLIFIED_XSL_PATH = "classpath:/templates/xslt/RD_simplified_content_v1-0.xsl";

  private static final String QUERY_GET_RD_ALL_NODES_BY_NAME = "SELECT XMLQUERY ('$d//*:%s' passing content as \"d\") FROM RDs WHERE id = '%s'";

  // Capabilities
  private static final String QUERY_GET_RDS_BY_CAPABILITY_NAME = "SELECT id, fileName FROM RDs WHERE xmlexists('$d//*:Capability[contains(@id, \"%s\")]' passing content as \"d\");";

  // Vendor
  private static final String QUERY_GET_RDS_BY_VENDOR_NAME = "SELECT id, fileName FROM RDs WHERE xmlexists ('$d//*:Vendor/*:NameLoc[contains(lower-case(text()), \"%s\")]' passing content as \"d\");";

  // Standards
  private static final String QUERY_GET_RDS_BY_STD_ID = "SELECT id, fileName FROM RDs WHERE xmlexists('$d//*[contains(@id, \"%s\")]' passing content as \"d\");";
  private static final String QUERY_GET_STDS_OF_RD_BY_STD_ID = "SELECT XMLQUERY ('$d//*[contains(@id, \"%s\")]' passing content as \"d\") FROM RDs WHERE id = '%s';";
  private static final String QUERY_GET_STDS_OF_RD = "SELECT XMLQUERY ('$d//*[contains(@id, \"std\")]' passing content as \"d\") FROM RDs WHERE id = '%s';";

  // EClasses
  private static final String QUERY_GET_ALL_ECLASSES = "SELECT XMLQUERY ('$d//*:EClassClassificationClass' passing content as \"d\") FROM RDs;";
  private static final String QUERY_GET_ECLASSES_BY_ID = "SELECT XMLQUERY ('$d//*:EClassClassificationClass[@codedName=\"%s\"]' passing content as \"d\") FROM RDs;";
  private static final String QUERY_GET_RDS_BY_ECLASS_ID = "SELECT id, fileName FROM RDs WHERE xmlexists('$d//*:EClassClassificationClass[@codedName=\"%s\"]' passing content as \"d\");";

  // Categories
  private static final String QUERY_GET_ALL_CATEGORIES = "SELECT xmlquery('for $d in $CONTENT//*[@ifCategory]\n" +
    "return $d/@ifCategory/string()\n" +
    "')\n" +
    "FROM RDs;";

  private static final String QUERY_GET_RDS_BY_CATEGORY_NAME = "SELECT id, fileName FROM RDs WHERE XMLEXISTS ('$d//*[contains(@ifCategory, \"%s\")]' passing content as \"d\");";

  @Autowired
  public ResourceQueryServiceImpl(ResourceService resourceService,
                                  QueryResultUtil queryResultUtil,
                                  JdbcTemplate jdbcTemplate,
                                  XmlUtils xmlUtils) {
    this.resourceService = resourceService;
    this.queryResultUtil = queryResultUtil;
    this.jdbcTemplate = jdbcTemplate;
    this.xmlUtils = xmlUtils;
  }

  @Override
  public String getSimplifiedRDById(String id) throws InternalErrorException, ResourceNotFoundException {
    if (StringUtils.isBlank(id)) {
      throw new InternalErrorException("Invalid id " + id);
    }

    ResourceDescription rdFile = resourceService.getRDById(id);
    return xmlUtils.xmlStringViaXsl(rdFile.getContent(), SIMPLIFIED_XSL_PATH);
  }

  @Override
  public List<ResourceDescription> getRDsImplementingStdByStdId(String stdId) throws InternalErrorException {
    if (StringUtils.isBlank(stdId)) {
      throw new InternalErrorException("Invalid stdId");
    }
    return retrieveRDsByQuery(String.format(QUERY_GET_RDS_BY_STD_ID, stdId));
  }

  @Override
  public List<ResourceDescription> getRDsImplementingCapabilityByName(String capaName) throws InternalErrorException {
    if (StringUtils.isBlank(capaName)) {
      throw new InternalErrorException("Invalid name");
    }
    return retrieveRDsByQuery(String.format(QUERY_GET_RDS_BY_CAPABILITY_NAME, capaName));
  }
  
  @Override
  public QueryResult getRdAllNodesByName(String resourceId, String name)
    throws InternalErrorException, ResourceNotFoundException {
    if (StringUtils.isBlank(resourceId) || StringUtils.isBlank(name)) {
      throw new InternalErrorException("Invalid id or node name");
    }

    ResourceDescription resourceDescription = resourceService.getRDById(resourceId);
    String query = String.format(QUERY_GET_RD_ALL_NODES_BY_NAME, name, resourceDescription.getId());
    String result = jdbcTemplate.queryForObject(query, new StringRowMapper());
    try {
      return queryResultUtil.build(result);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public QueryResult getAllEClasses()
    throws InternalErrorException {
    try {
      List<String> results = jdbcTemplate.query(QUERY_GET_ALL_ECLASSES, new StringRowMapper())
        .stream()
        .distinct()
        .collect(Collectors.toList());
      return queryResultUtil.build(results);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public QueryResult getEClassesById(String id) throws InternalErrorException {
    if (StringUtils.isBlank(id)) {
      throw new InternalErrorException("Invalid id");
    }
    try {
      String query = String.format(QUERY_GET_ECLASSES_BY_ID, id);
      List<String> results = jdbcTemplate.query(query, new StringRowMapper())
        .stream()
        .distinct()
        .collect(Collectors.toList());
      return queryResultUtil.build(results);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public List<ResourceDescription> getRDsImplementingEClassesById(String id) throws InternalErrorException {
    if (StringUtils.isBlank(id)) {
      throw new InternalErrorException("Invalid id or node name");
    }
    return retrieveRDsByQuery(String.format(QUERY_GET_RDS_BY_ECLASS_ID, id));
  }

  @Override
  public QueryResult getStdsOfRDByIdAndStdVal(String id, String stdVal)
    throws InternalErrorException, ResourceNotFoundException {
    if (StringUtils.isBlank(id) || StringUtils.isBlank(stdVal)) {
      throw new InternalErrorException("Invalid id or blank standard id value");
    }

    ResourceDescription resourceDescription = resourceService.getRDById(id);
    try {
      String query = String.format(QUERY_GET_STDS_OF_RD_BY_STD_ID, stdVal, resourceDescription.getFileName());
      List<String> results = jdbcTemplate.query(query, new StringRowMapper())
        .stream()
        .distinct()
        .collect(Collectors.toList());
      return queryResultUtil.build(results);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public QueryResult getStdsOfRDById(String id) throws InternalErrorException, ResourceNotFoundException {
    if (StringUtils.isEmpty(id)) {
      throw new InternalErrorException("Invalid id");
    }

    ResourceDescription resourceDescription = resourceService.getRDById(id);
    try {
      String query = String.format(QUERY_GET_STDS_OF_RD, resourceDescription.getFileName());
      List<String> results = jdbcTemplate.query(query, new StringRowMapper())
        .stream()
        .distinct()
        .collect(Collectors.toList());
      return queryResultUtil.build(results);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public List<String> getAllCategories() throws InternalErrorException {
    try {
      return jdbcTemplate.query(QUERY_GET_ALL_CATEGORIES, new StringRowMapper())
        .stream()
        .map(String::trim)
        .map(value -> value.split(" "))
        .flatMap(Stream::of)
        .distinct()
        .collect(Collectors.toList());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public List<ResourceDescription> getRdsByCategoryName(String name) throws InternalErrorException {
    if (StringUtils.isBlank(name)) {
      throw new InternalErrorException("Empty name to query");
    }

    try {
      String query = String.format(QUERY_GET_RDS_BY_CATEGORY_NAME, name.toUpperCase());
      return retrieveRDsByQuery(query);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  @Override
  public List<ResourceDescription> getRdsByVendorName(String name) throws InternalErrorException {
    if (StringUtils.isBlank(name)) {
      throw new InternalErrorException("Cannot find RD by empty name");
    }

    try {
      String query = String.format(QUERY_GET_RDS_BY_VENDOR_NAME, name.toLowerCase());
      return retrieveRDsByQuery(query);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new InternalErrorException(e.getMessage());
    }
  }

  private List<ResourceDescription> retrieveRDsByQuery(String query) {
    List<ResourceDescription> results = jdbcTemplate.query(query, new ResourceDescriptionRowMapper());

    return results
      .stream()
      .map(rdXml -> {
        try {
          return resourceService.getRDById(rdXml.getId());
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          return null;
        }
      })
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }
}
