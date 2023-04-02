package fi.tuni.resourcedescription.model.rcp.rd;

public enum ResourceStageConstant {
  UPLOAD("UPLOAD"),
  TO_BE_REVIEWED("TO_BE_REVIEWED"),
  IN_AUTOMATIC_VALIDATION("IN_AUTOMATIC_VALIDATION"),
  IN_REVIEW("IN_REVIEW"),
  REVIEW_APPROVED("REVIEW_APPROVED"),
  REVIEW_RETURNED("REVIEW_RETURNED"),
  PUBLISHED("PUBLISHED"),
  WITHDRAW("WITHDRAW");

  private String stageId;
  
  ResourceStageConstant(String stageId) {
    this.stageId = stageId;
  }
  
  public String getIdVal() {
    return stageId;
  }
}
