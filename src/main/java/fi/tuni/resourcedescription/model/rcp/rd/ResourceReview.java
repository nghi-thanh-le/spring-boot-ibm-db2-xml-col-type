package fi.tuni.resourcedescription.model.rcp.rd;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name = "RD_Reviews")
@Setter
@Getter
@NoArgsConstructor
public class ResourceReview {
  @Id
  private String id;
  @Column
  private Integer userId;
  @Column
  private String review;
  @Column
  @JsonIgnore
  private Timestamp reviewAt;

  public String getReviewTime() {
    if (Objects.isNull(reviewAt)) {
      return StringUtils.EMPTY;
    }
    Date theDate = new Date(reviewAt.getTime());
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.mmm'Z'");
    return dateFormat.format(theDate);
  }
}
