/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.user.client.impl.domain;

/**
 *
 * @author russel
 */
import java.util.Date;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

public class Organization extends AbstractClientDomain implements
    com.smartitengineering.user.client.api.Organization {

  public static final String ADDRESS_TYPE = Address.class.getName();
  private String name;
  private String uniqueShortName;
  private com.smartitengineering.user.client.api.Address address;
  private Date lastModifiedDate;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    if (name == null) {
      return;
    }
    this.name = name;
  }

  @Override
  public void setUniqueShortName(String uniqueShortName) {
    if (uniqueShortName == null) {
      return;
    }
    this.uniqueShortName = uniqueShortName;
  }

  @Override
  public String getUniqueShortName() {
    return uniqueShortName;
  }

  @Override
  public com.smartitengineering.user.client.api.Address getAddress() {
    return address;
  }

  @Override
  public Date getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(Date lastModifiedDate) {
    if (lastModifiedDate == null) {
      return;
    }
    this.lastModifiedDate = lastModifiedDate;
  }

  @Override
  @JsonDeserialize(as = Address.class)
  public void setAddress(com.smartitengineering.user.client.api.Address address) {
    this.address = address;
  }
}
