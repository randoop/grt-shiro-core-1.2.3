/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.authz;

import org.checkerframework.dataflow.qual.Impure;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.dataflow.qual.SideEffectFree;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A simple representation of a security role that has a name and a collection of permissions.  This object can be
 * used internally by Realms to maintain authorization state.
 *
 * @since 0.2
 */
public class SimpleRole implements Serializable {

    protected String name = null;
    protected Set<Permission> permissions;

    @SideEffectFree
    public SimpleRole() {
    }

    @Impure
    public SimpleRole(String name) {
        setName(name);
    }

    @Impure
    public SimpleRole(String name, Set<Permission> permissions) {
        setName(name);
        setPermissions(permissions);
    }

    @Pure
    public String getName() {
        return name;
    }

    @Impure
    public void setName(String name) {
        this.name = name;
    }

    @Pure
    public Set<Permission> getPermissions() {
        return permissions;
    }

    @Impure
    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    @Impure
    public void add(Permission permission) {
        Set<Permission> permissions = getPermissions();
        if (permissions == null) {
            permissions = new LinkedHashSet<Permission>();
            setPermissions(permissions);
        }
        permissions.add(permission);
    }

    @Impure
    public void addAll(Collection<Permission> perms) {
        if (perms != null && !perms.isEmpty()) {
            Set<Permission> permissions = getPermissions();
            if (permissions == null) {
                permissions = new LinkedHashSet<Permission>(perms.size());
                setPermissions(permissions);
            }
            permissions.addAll(perms);
        }
    }

    @Pure
    @Impure
    public boolean isPermitted(Permission p) {
        Collection<Permission> perms = getPermissions();
        if (perms != null && !perms.isEmpty()) {
            for (Permission perm : perms) {
                if (perm.implies(p)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Pure
    @Impure
    public int hashCode() {
        return (getName() != null ? getName().hashCode() : 0);
    }

    @Pure
    @Impure
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SimpleRole) {
            SimpleRole sr = (SimpleRole) o;
            //only check name, since role names should be unique across an entire application:
            return (getName() != null ? getName().equals(sr.getName()) : sr.getName() == null);
        }
        return false;
    }

    @Pure
    @Impure
    public String toString() {
        return getName();
    }
}
