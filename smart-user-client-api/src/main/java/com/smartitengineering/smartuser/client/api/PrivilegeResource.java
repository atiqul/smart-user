/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.smartuser.client.api;

import com.smartitengineering.user.resource.api.WritableResource;

/**
 *
 * @author modhu7
 */
public interface PrivilegeResource extends WritableResource<PrivilegeResource> {

  public Privilege getPrivilege();

  public OrganizationResource getOrganizationResource();
}
