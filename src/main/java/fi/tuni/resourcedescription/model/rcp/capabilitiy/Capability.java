package fi.tuni.resourcedescription.model.rcp.capabilitiy;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;

public class Capability {

  @Getter
  @Setter
  private int nbrRDs;

  private Capability() {
    nbrRDs = 0;
  }

  public String getName() {
    return StringUtils.EMPTY;
  }

  public String getDescription() {
    return StringUtils.EMPTY;
  }

  public Collection<?> getParameterAssociationsForParameters() {
    return Collections.emptyList();
  }
}
