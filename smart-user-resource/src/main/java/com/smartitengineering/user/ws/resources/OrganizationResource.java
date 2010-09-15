/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.user.ws.resources;

import com.smartitengineering.user.domain.Address;
import com.smartitengineering.user.domain.Organization;
import com.sun.jersey.api.view.Viewable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author russel
 */
@Path("/orgs/sn/{uniqueShortName}")
public class OrganizationResource extends AbstractResource {

  static final UriBuilder ORGANIZATION_URI_BUILDER = UriBuilder.fromResource(OrganizationResource.class);
  static final UriBuilder ORGANIZATION_CONTENT_URI_BUILDER;

  private final String REL_USERS = "users";
  private final String REL_PRIVILEGES = "privileges";
  private final String REL_SECUREDOBJECTS = "securedobjects";

  @Context
  private HttpServletRequest servletRequest;

  static {
    ORGANIZATION_CONTENT_URI_BUILDER = ORGANIZATION_URI_BUILDER.clone();
    try {
      ORGANIZATION_CONTENT_URI_BUILDER.path(OrganizationResource.class.getMethod("getOrganization"));

    }
    catch (Exception ex) {
      ex.printStackTrace();
      throw new InstantiationError();
    }
  }
  private Organization organization;
  @PathParam("uniqueShortName")
  private String uniqueShortName;

  public OrganizationResource(@PathParam("uniqueShortName") String uniqueShortName) {
    this.uniqueShortName = uniqueShortName;
    organization = Services.getInstance().getOrganizationService().getOrganizationByUniqueShortName(uniqueShortName);

  }

  @GET
  @Produces(MediaType.APPLICATION_ATOM_XML)
  public Response get() {
    Feed organizationFeed = getOrganizationFeed();
    ResponseBuilder responseBuilder = Response.ok(organizationFeed);
    return responseBuilder.build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/content")
  public Response getOrganization() {
    ResponseBuilder responseBuilder = Response.ok(organization);
    return responseBuilder.build();
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response getHtml() {
    ResponseBuilder responseBuilder = Response.ok();




    

    servletRequest.setAttribute("templateContent",
                                "/com/smartitengineering/user/ws/resources/OrganizationResource/OrganizationDetails.jsp");
    Viewable view = new Viewable("/template/template.jsp", organization);

    //Viewable view = new Viewable("OrganizationDetails", organization, OrganizationResource.class);

    responseBuilder.entity(view);
    return responseBuilder.build();
  }

  @PUT
  @Produces(MediaType.APPLICATION_ATOM_XML)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response update(Organization newOrganization) {
    ResponseBuilder responseBuilder = Response.status(Status.SERVICE_UNAVAILABLE);
    try {

      organization.getAddress().setCity(newOrganization.getAddress().getCity());
      organization.getAddress().setCountry(newOrganization.getAddress().getCountry());
      organization.getAddress().setState(newOrganization.getAddress().getState());
      organization.getAddress().setStreetAddress(newOrganization.getAddress().getStreetAddress());
      organization.getAddress().setZip(newOrganization.getAddress().getZip());

      organization.setName(newOrganization.getName());
      organization.setLastModifiedDate(new Date());

      Services.getInstance().getOrganizationService().update(organization);
      organization = Services.getInstance().getOrganizationService().getOrganizationByUniqueShortName(newOrganization.
          getUniqueShortName());
      responseBuilder = Response.ok(getOrganizationFeed());

    }
    catch (Exception ex) {
      responseBuilder = Response.status(Status.INTERNAL_SERVER_ERROR);
    }


    return responseBuilder.build();
  }

  @POST
  @Path("/update")
  //@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response updatePost(@HeaderParam("Content-type") String contentType, String message) {
    ResponseBuilder responseBuilder = Response.status(Status.SERVICE_UNAVAILABLE);


    if (StringUtils.isBlank(message)) {
      responseBuilder = Response.status(Status.BAD_REQUEST);
      responseBuilder.build();

    }

    final boolean isHtmlPost;
    if (StringUtils.isBlank(contentType)) {
      contentType = MediaType.APPLICATION_OCTET_STREAM;
      isHtmlPost = false;
    }
    else if (contentType.equals(MediaType.APPLICATION_FORM_URLENCODED)) {
      contentType = MediaType.APPLICATION_OCTET_STREAM;
      isHtmlPost = true;
      try {
        //Will search for the first '=' if not found will take the whole string
        final int startIndex = 0;//message.indexOf("=") + 1;
        //Consider the first '=' as the start of a value point and take rest as value
        final String realMsg = message.substring(startIndex);
        //Decode the message to ignore the form encodings and make them human readable
        message = URLDecoder.decode(realMsg, "UTF-8");
      }
      catch (UnsupportedEncodingException ex) {
        ex.printStackTrace();
      }
    }
    else {
      contentType = contentType;
      isHtmlPost = false;
    }

    if (isHtmlPost) {
      Organization newOrganization = getObjectFromContent(message);
      try {
        Services.getInstance().getOrganizationService().update(newOrganization);
        //organization = Services.getInstance().getOrganizationService().getOrganizationByUniqueShortName(organization.getUniqueShortName());
        responseBuilder = Response.ok(getOrganizationFeed());
      }
      catch (Exception ex) {
        responseBuilder = Response.status(Status.INTERNAL_SERVER_ERROR);
      }
    }
    return responseBuilder.build();
  }

  private Organization getObjectFromContent(String message) {

    Map<String, String> keyValueMap = new HashMap<String, String>();

    String[] keyValuePairs = message.split("&");

    for (int i = 0; i < keyValuePairs.length; i++) {

      String[] keyValuePair = keyValuePairs[i].split("=");
      keyValueMap.put(keyValuePair[0], keyValuePair[1]);
    }

    Organization newOrganization = new Organization();




    if (keyValueMap.get("id") != null) {
      newOrganization.setId(Integer.valueOf(keyValueMap.get("id")));
    }

    if (keyValueMap.get("version") != null) {
      newOrganization.setVersion(Integer.valueOf(keyValueMap.get("version")));
    }


    if (keyValueMap.get("name") != null) {
      newOrganization.setName(keyValueMap.get("name"));
    }
    if (keyValueMap.get("uniqueShortName") != null) {
      newOrganization.setUniqueShortName(keyValueMap.get("uniqueShortName"));

    }

    Address address = new Address();

    if (keyValueMap.get("city") != null) {
      address.setCity(keyValueMap.get("city"));
    }

    if (keyValueMap.get("streetAddress") != null) {
      address.setStreetAddress(keyValueMap.get("streetAddress"));
    }

    if (keyValueMap.get("country") != null) {
      address.setCountry(keyValueMap.get("country"));
    }

    if (keyValueMap.get("state") != null) {
      address.setState(keyValueMap.get("state"));
    }
    if (keyValueMap.get("zip") != null) {
      address.setZip(keyValueMap.get("zip"));
    }

    newOrganization.setAddress(address);

    return newOrganization;
  }

  @POST
  @Path("/delete")
  //@Produces(MediaType.APPLICATION_ATOM_XML)
  public Response deletePost() {
    Services.getInstance().getOrganizationService().delete(organization);
    ResponseBuilder responseBuilder = Response.ok();
    return responseBuilder.build();
  }

  @DELETE
  public Response delete() {
    ResponseBuilder responseBuilder = Response.ok();

    try {
      Services.getInstance().getOrganizationService().delete(organization);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      responseBuilder = Response.status(Status.INTERNAL_SERVER_ERROR);
    }

    return responseBuilder.build();
  }

  private Feed getOrganizationFeed() throws UriBuilderException, IllegalArgumentException {

    Feed organizationFeed = getFeed(organization.getName(), organization.getLastModifiedDate());
    organizationFeed.setTitle(organization.getName());

    // add a self link
    organizationFeed.addLink(getSelfLink());


    // add a edit link
    Link editLink = abderaFactory.newLink();
    editLink.setHref(uriInfo.getRequestUri().toString());
    editLink.setRel(Link.REL_EDIT);
    editLink.setMimeType(MediaType.APPLICATION_JSON);

    // add a alternate link
    Link altLink = abderaFactory.newLink();
    altLink.setHref(ORGANIZATION_CONTENT_URI_BUILDER.clone().build(organization.getUniqueShortName()).toString());
    altLink.setRel(Link.REL_ALTERNATE);
    altLink.setMimeType(MediaType.APPLICATION_JSON);
    organizationFeed.addLink(altLink);

    Link usersLink = abderaFactory.newLink();
    usersLink.setHref(OrganizationUsersResource.ORGANIZATION_USERS_URI_BUILDER.clone().build(organization.getUniqueShortName()).toString());
    usersLink.setRel(REL_USERS);
    usersLink.setMimeType(MediaType.APPLICATION_JSON);
    organizationFeed.addLink(usersLink);

    Link privilegesLink = abderaFactory.newLink();
    privilegesLink.setHref(OrganizationUsersResource.ORGANIZATION_USERS_URI_BUILDER.clone().build(organization.getUniqueShortName()).toString());
    privilegesLink.setRel(REL_PRIVILEGES);
    privilegesLink.setMimeType(MediaType.APPLICATION_JSON);
    organizationFeed.addLink(privilegesLink);

    Link securedObjectsLink = abderaFactory.newLink();
    securedObjectsLink.setHref(OrganizationUsersResource.ORGANIZATION_USERS_URI_BUILDER.clone().build(organization.getUniqueShortName()).toString());
    securedObjectsLink.setRel(REL_SECUREDOBJECTS);
    securedObjectsLink.setMimeType(MediaType.APPLICATION_JSON);
    organizationFeed.addLink(securedObjectsLink);

    return organizationFeed;
  }
}
