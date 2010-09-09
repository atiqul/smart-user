/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.user.security.impl;

import com.smartitengineering.dao.common.CommonReadDao;
import com.smartitengineering.dao.common.CommonWriteDao;
import com.smartitengineering.dao.common.queryparam.FetchMode;
import com.smartitengineering.dao.common.queryparam.MatchMode;
import com.smartitengineering.dao.common.queryparam.QueryParameter;
import com.smartitengineering.dao.common.queryparam.QueryParameterFactory;
import com.smartitengineering.user.domain.UniqueConstrainedField;
import com.smartitengineering.user.filter.SmartAceFilter;
import com.smartitengineering.user.filter.SmartAclFilter;
import com.smartitengineering.user.security.domain.SmartAce;
import com.smartitengineering.user.security.domain.SmartAcl;
import com.smartitengineering.user.security.service.SmartAceService;
import com.smartitengineering.user.security.service.SmartAclService;
import com.smartitengineering.user.service.ExceptionMessage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.hibernate.StaleStateException;
import org.hibernate.exception.ConstraintViolationException;

/**
 *
 * @author modhu7
 */
public class SmartAclServiceImpl implements SmartAclService {

    private CommonReadDao<SmartAcl, Integer> smartAclReadDao;
    private CommonWriteDao<SmartAcl> smartAclWriteDao;
    private SmartAceService smartAceService;

    public SmartAceService getSmartAceService() {
        return smartAceService;
    }

    public void setSmartAceService(SmartAceService smartAceService) {
        this.smartAceService = smartAceService;
    }



    public CommonReadDao<SmartAcl, Integer> getSmartAclReadDao() {
        return smartAclReadDao;
    }

    public void setSmartAclReadDao(CommonReadDao<SmartAcl, Integer> smartAclReadDao) {
        this.smartAclReadDao = smartAclReadDao;
    }

    public CommonWriteDao<SmartAcl> getSmartAclWriteDao() {
        return smartAclWriteDao;
    }

    public void setSmartAclWriteDao(CommonWriteDao<SmartAcl> smartAclWriteDao) {
        this.smartAclWriteDao = smartAclWriteDao;
    }

    public void create(SmartAcl ace) {
        validate(ace);
        try {
            getSmartAclWriteDao().save(ace);
        } catch (ConstraintViolationException e) {
            String message = ExceptionMessage.CONSTRAINT_VIOLATION_EXCEPTION.name() + "-" + UniqueConstrainedField.OTHER;
            throw new RuntimeException(message, e);
        } catch (StaleStateException e) {
            String message =
                    ExceptionMessage.STALE_OBJECT_STATE_EXCEPTION.name() + "-" +
                    UniqueConstrainedField.OTHER;
            throw new RuntimeException(message, e);
        }
    }

    public void update(SmartAcl acl) {
        validate(acl);
        try {
            getSmartAclWriteDao().update(acl);
        } catch (ConstraintViolationException e) {
            String message = ExceptionMessage.CONSTRAINT_VIOLATION_EXCEPTION.name() + "-" + UniqueConstrainedField.OTHER;
            throw new RuntimeException(message, e);
        } catch (StaleStateException e) {
            String message =
                    ExceptionMessage.STALE_OBJECT_STATE_EXCEPTION.name() + "-" +
                    UniqueConstrainedField.OTHER;
            throw new RuntimeException(message, e);
        }
    }

    public void delete(SmartAcl acl) {
        try {
            getSmartAclWriteDao().delete(acl);
        } catch (RuntimeException e) {
            String message = ExceptionMessage.CONSTRAINT_VIOLATION_EXCEPTION.name() + "-" + UniqueConstrainedField.ACL;
            throw new RuntimeException(message, e);
        }
    }

    public Collection<SmartAcl> search(SmartAclFilter filter) {
        QueryParameter qp = null;
        System.out.println("At smart acl service impl");
        List<QueryParameter> queryParameters = new ArrayList<QueryParameter>();
        if (!StringUtils.isEmpty(filter.getOid())) {            
            qp = QueryParameterFactory.getStringLikePropertyParam("objectIdentity.oid",
                    filter.getOid(), MatchMode.ANYWHERE);
            queryParameters.add(qp);
        }
        if (!StringUtils.isEmpty(filter.getOwnerUsername())) {
            qp = QueryParameterFactory.getNestedParametersParam("owner", FetchMode.DEFAULT,
                    QueryParameterFactory.getEqualPropertyParam("username",
                    filter.getOwnerUsername()));
            queryParameters.add(qp);
        }
        System.out.println(queryParameters.size());

        Collection<SmartAcl> acls = new HashSet<SmartAcl>();
        if (queryParameters.size() == 0) {
            try {
                acls = getSmartAclReadDao().getAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                if(getSmartAclReadDao()==null)
                    System.out.println("Read dao is null");
                else
                    System.out.println("Read dao is not null");
                acls = getSmartAclReadDao().getList(queryParameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return acls;
    }

    public Collection<SmartAce> getAceEntries(SmartAcl acl) {
        SmartAceFilter filter = new SmartAceFilter();
        filter.setOid(acl.getObjectIdentity().getOid());
        return smartAceService.search(filter);
    }

    public void validate(SmartAcl acl) {
        if (acl.getId() != null) {
            Integer count = (Integer) getSmartAclReadDao().getOther(QueryParameterFactory.getElementCountParam(
                    "objectIdentity.oid"), QueryParameterFactory.getConjunctionParam(
                    QueryParameterFactory.getNotEqualPropertyParam("id",
                    acl.getId()), QueryParameterFactory.getStringLikePropertyParam(
                    "objectIdentity.oid", acl.getObjectIdentity().getOid(), MatchMode.EXACT)));
            if (count.intValue() > 0) {
                throw new RuntimeException(ExceptionMessage.CONSTRAINT_VIOLATION_EXCEPTION.name() + "-" +
                        UniqueConstrainedField.OBJECT_IDENTITY.name());
            }
        } else {
            Integer count = (Integer) getSmartAclReadDao().getOther(QueryParameterFactory.getElementCountParam(
                    "objectIdentity.oid"), QueryParameterFactory.getStringLikePropertyParam(
                    "objectIdentity.oid", acl.getObjectIdentity().getOid(), MatchMode.EXACT));
            if (count.intValue() > 0) {
                throw new RuntimeException(ExceptionMessage.CONSTRAINT_VIOLATION_EXCEPTION.name() + "-" +
                        UniqueConstrainedField.OBJECT_IDENTITY.name());
            }
        }
    }
}
