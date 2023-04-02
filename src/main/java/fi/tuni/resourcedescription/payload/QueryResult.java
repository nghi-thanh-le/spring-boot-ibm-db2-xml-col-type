package fi.tuni.resourcedescription.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class QueryResult {
  private String result = StringUtils.EMPTY;
}
