package fi.tuni.resourcedescription.payload;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FileModel {
  private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  private String name;
  private long size;
  private Date lastModified;

  public FileModel(String name, long size, Date lastModified) {
    this.name = name;
    this.size = size;
    this.lastModified = lastModified;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public String getFormattedLastModified() {
    return formatter.format(lastModified);
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }
}
