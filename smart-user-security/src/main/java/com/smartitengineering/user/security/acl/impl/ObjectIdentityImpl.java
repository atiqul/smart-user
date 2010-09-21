/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.user.security.acl.impl;

import com.smartitengineering.domain.PersistentDTO;
import com.smartitengineering.user.security.domain.SmartObjectIdentity;
import java.io.Serializable;
import org.springframework.security.acls.objectidentity.ObjectIdentity;


/**
 *
 * @author modhu7
 */
public class ObjectIdentityImpl implements ObjectIdentity {

    SmartObjectIdentity objectIdentity = new SmartObjectIdentity();

    public ObjectIdentityImpl(Object object) {
        if(object instanceof PersistentDTO)  {
            objectIdentity.setClassType(object.getClass());
            objectIdentity.setObjectIdentityId(((PersistentDTO<PersistentDTO, Integer, Integer>)object).getId());
        }else if(object instanceof SmartObjectIdentity){
            this.objectIdentity = (SmartObjectIdentity) object;
        }
    }

    public SmartObjectIdentity getObjectIdentity() {
        return objectIdentity;
    }
    

    public Serializable getIdentifier() {
        return objectIdentity.getObjectIdentityId();
    }

    public Class getJavaType() {
        return objectIdentity.getClassType();
    }
}
