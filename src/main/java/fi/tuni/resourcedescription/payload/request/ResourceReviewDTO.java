package fi.tuni.resourcedescription.payload.request;


import lombok.Data;

@Data
public class ResourceReviewDTO {
  private Integer userId;
  private String review;
  private String stageId;
}
