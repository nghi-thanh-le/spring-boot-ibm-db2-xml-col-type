package fi.tuni.resourcedescription.controller.restapi.rcp.rd;

import fi.tuni.resourcedescription.exception.InternalErrorException;
import fi.tuni.resourcedescription.exception.ResourceExceedsMaxSizeException;
import fi.tuni.resourcedescription.exception.ResourceExistsException;
import fi.tuni.resourcedescription.exception.ResourceNotFoundException;
import fi.tuni.resourcedescription.exception.ResourceStillHasBindingSupplementaryFilesException;
import fi.tuni.resourcedescription.model.XLSTFile;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceDescription;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceReview;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceSupplementaryFile;
import fi.tuni.resourcedescription.model.rcp.rd.ResourceValidationResult;
import fi.tuni.resourcedescription.payload.request.ResourceReviewDTO;
import fi.tuni.resourcedescription.service.resource.ResourceQueryService;
import fi.tuni.resourcedescription.service.resource.ResourceService;
import fi.tuni.resourcedescription.service.resource.ResourceValidationService;
import fi.tuni.resourcedescription.service.security.AuthService;
import fi.tuni.resourcedescription.service.utils.XLSTService;
import fi.tuni.resourcedescription.service.utils.XmlUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/rds")
@SecurityRequirement(name = "jwt-api")
public class RDApiController {

  private final ResourceService resourceService;
  private final ResourceValidationService resourceValidationService;
  private final ResourceQueryService resourceQueryService;
  private final XmlUtils xmlUtil;
  private final AuthService authService;
  private final XLSTService xlstService;

  @Autowired
  public RDApiController(ResourceService resourceService,
                         ResourceValidationService resourceValidationService,
                         ResourceQueryService resourceQueryService,
                         XmlUtils xmlUtil,
                         AuthService authService,
                         XLSTService xlstService) {
    this.resourceService = resourceService;
    this.resourceValidationService = resourceValidationService;
    this.resourceQueryService = resourceQueryService;
    this.xmlUtil = xmlUtil;
    this.authService = authService;
    this.xlstService = xlstService;
  }

  @GetMapping(
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<List<ResourceDescription>> getRDs() {
    return ResponseEntity.ok(resourceService.getRDs());
  }

  @GetMapping(value = "{id}",
    produces = {MediaType.APPLICATION_XML_VALUE}
  )
  @PreAuthorize("isAnonymous() or hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<String> getFullRDAsXmlById(@PathVariable String id)
    throws InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(resourceService.getFullRDById(id));
  }

  @GetMapping(value = "{id}",
    produces = {MediaType.APPLICATION_JSON_VALUE}
  )
  @PreAuthorize("isAnonymous() or hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<String> getFullRDAsJsonById(@PathVariable String id)
    throws Exception {
    return ResponseEntity.ok(xmlUtil.xmlStringToJsonString(resourceService.getFullRDById(id)));
  }

  @GetMapping(value = "{id}/simplified",
    produces =  MediaType.APPLICATION_XML_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<String> getSimplifiedRDByIdAsXML(@PathVariable String id)
    throws InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(resourceQueryService.getSimplifiedRDById(id));
  }

  @GetMapping(value = "{id}/simplified",
    produces =  MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<String> getSimplifiedRDByIdAsJson(@PathVariable String id)
    throws Exception {
    return ResponseEntity.ok(xmlUtil.xmlStringToJsonString(resourceQueryService.getSimplifiedRDById(id)));
  }

  @PostMapping(
    consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider')")
  public ResponseEntity<ResourceDescription> uploadXmlFile(@RequestParam String fileName,
                                                           @RequestParam("file") MultipartFile file,
                                                           Principal principal)
    throws ResourceExistsException, InternalErrorException {
    return new ResponseEntity<>(resourceService.uploadXmlFile(authService.getUserFromPrinciple(principal), fileName, file), HttpStatus.CREATED);
  }

  @PutMapping(
    value = "{id}",
    consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider')")
  public ResponseEntity<ResourceDescription> replaceXmlFileById(@PathVariable String id,
                                                                @RequestParam MultipartFile file,
                                                                Principal principal)
    throws InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(resourceService.replaceXmlFileById(authService.getUserFromPrinciple(principal), id, file));
  }

  @DeleteMapping(value = "{id}")
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider')")
  public ResponseEntity<HttpStatus> deleteXmlFileById(@PathVariable String id)
    throws InternalErrorException, ResourceNotFoundException, ResourceStillHasBindingSupplementaryFilesException {
    resourceService.deleteRdById(id);
    return ResponseEntity.accepted().build();
  }

  @GetMapping(value = "{id}/files",
    produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<List<ResourceSupplementaryFile>> getRDSupplementaryFilesById(@PathVariable String id)
    throws InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(resourceService.getRDSupplementaryFilesById(id));
  }

  @PostMapping(value = "{id}/files",
    consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider')")
  public ResponseEntity<ResourceSupplementaryFile> uploadSupplementaryFileToRDById(@PathVariable String id, @RequestParam MultipartFile file)
    throws InternalErrorException, ResourceExceedsMaxSizeException, ResourceNotFoundException {
    return ResponseEntity.ok(resourceService.uploadSupplementaryFileToRDById(id, file));
  }

  @GetMapping(value = "{id}/files/{fileId}",
    produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
  )
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<Resource> getRDSupplementaryFileById(@PathVariable String id, @PathVariable Integer fileId)
    throws Exception {
    ResourceSupplementaryFile resourceSupplementFile = resourceService.getSupplementaryFileById(id, fileId);
    String headerValue = "attachment; filename=\"" + resourceSupplementFile.getFileName() + "\"";

    return ResponseEntity
      .ok()
      .contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
      .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
      .body(new ByteArrayResource(FileCopyUtils.copyToByteArray(resourceSupplementFile.getContent().getBinaryStream())));
  }

  @PutMapping (value = "{id}/files/{fileId}")
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider')")
  public ResponseEntity<ResourceSupplementaryFile> updateRDSupplementaryFileById(@PathVariable String id, @PathVariable Integer fileId, @RequestParam MultipartFile file)
    throws InternalErrorException, ResourceExceedsMaxSizeException, ResourceNotFoundException {
    return ResponseEntity.ok(resourceService.updateRDSupplementaryFileById(id, fileId, file));
  }

  @DeleteMapping(value = "{id}/files/{fileId}")
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider')")
  public ResponseEntity<ResourceSupplementaryFile> deleteRDSupplementaryFileById(@PathVariable String id, @PathVariable Integer fileId)
    throws InternalErrorException {
    resourceService.deleteRDSupplementaryFileById(id, fileId);
    return ResponseEntity.accepted().build();
  }

  @GetMapping(value = "{id}/interfaces",
    produces = MediaType.APPLICATION_XML_VALUE)
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<String> getInterfacesOfRDById(@PathVariable String id)
    throws InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(resourceQueryService.getRdAllNodesByName(id, "Interfaces").getResult());
  }

  @GetMapping(value = "{id}/capabilities/simplified",
    produces = MediaType.APPLICATION_XML_VALUE)
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<String> getSimplifiedCapabilitiesById(@PathVariable String id)
    throws InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(resourceQueryService.getRdAllNodesByName(id, "Capability").getResult());
  }

  @GetMapping(value = "{id}/capabilities/rawontology",
    produces = MediaType.APPLICATION_XML_VALUE)
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<String> getCapabilityModelAsOWLById(@PathVariable String id)
    throws InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(resourceQueryService.getRdAllNodesByName(id, "PlaceHolder_CapabilityModelAsOWL").getResult());
  }

  @GetMapping(value = "{id}/eclasses",
    produces = MediaType.APPLICATION_XML_VALUE)
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<String> getEClassesOfRDById(@PathVariable String id)
    throws InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(resourceQueryService.getRdAllNodesByName(id, "EClassClassificationClass").getResult());
  }

  @GetMapping(value = "{id}/execCapas",
    produces = MediaType.APPLICATION_XML_VALUE)
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider', 'User')")
  public ResponseEntity<String> getExecCapasOfRDById(@PathVariable String id)
    throws InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(resourceQueryService.getRdAllNodesByName(id, "ExecCapas").getResult());
  }

  @GetMapping(value = "{id}/validate",
    produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAuthority('Manager')")
  public ResponseEntity<ResourceValidationResult> validateRd(@PathVariable String id)
    throws IOException, InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(resourceValidationService.validateRdById(id));
  }

  @PutMapping(value = "{id}/changestage")
  @PreAuthorize("hasAuthority('Manager')")
  public ResponseEntity<HttpStatus> publishRdById(@PathVariable String id, @RequestParam String stageId)
    throws InternalErrorException, ResourceNotFoundException {
    resourceService.changeRdStageById(id, stageId);
    return ResponseEntity.ok(HttpStatus.NO_CONTENT);
  }

  @GetMapping(value = "{id}/validationReport")
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider')")
  public ResponseEntity<ResourceValidationResult> getRdValidationReportById(@PathVariable String id) throws InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(resourceService.getRDValidationReportById(id));
  }

  @GetMapping(value = "{id}/reviewReport")
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ResourceProvider')")
  public ResponseEntity<ResourceReview> getRdReviewById(@PathVariable String id) throws InternalErrorException, ResourceNotFoundException {
    return ResponseEntity.ok(resourceService.getRDReviewById(id));
  }

  @PostMapping(value = "{id}/reviewReport")
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<HttpStatus> reviewRdById(@PathVariable String id, @RequestBody ResourceReviewDTO rdReviewRequest)
    throws InternalErrorException {
    resourceService.reviewRdById(id, rdReviewRequest);
    return ResponseEntity.ok().build();
  }

  @PutMapping(value = "{id}/reviewReport")
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<HttpStatus> updateReviewRdById(@PathVariable String id, @RequestBody ResourceReviewDTO rdReviewRequest)
    throws InternalErrorException, ResourceNotFoundException {
    resourceService.updateRDReviewById(id, rdReviewRequest);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping(value = "{id}/reviewReport")
  @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
  public ResponseEntity<HttpStatus> deleteReviewRdById(@PathVariable String id)
    throws InternalErrorException {
    resourceService.deleteRDReviewById(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping(value = "{id}/xlsts")
  public ResponseEntity<List<XLSTFile>> getTransformFilesOfRDById(@PathVariable String id) {
    return ResponseEntity.ok(xlstService.getAllFiles());
  }

  @GetMapping(value = "{id}/xlsts/{rdId}/transform")
  public ResponseEntity<String> getTransformedRDById(@PathVariable String id, @PathVariable int rdId) throws InternalErrorException, ResourceNotFoundException {
    ResourceDescription rdFile = resourceService.getRDById(id);
    XLSTFile xlstFile = xlstService.getAllFiles().get(rdId);

    return ResponseEntity.ok(xmlUtil.xmlStringViaXsl(rdFile.getContent(), xlstFile.getResource()));
  }
}
