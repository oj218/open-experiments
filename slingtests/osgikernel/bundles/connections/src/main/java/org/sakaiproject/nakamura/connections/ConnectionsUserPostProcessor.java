/*
 * Licensed to the Sakai Foundation (SF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.sakaiproject.nakamura.connections;

import static javax.jcr.security.Privilege.JCR_ALL;
import static javax.jcr.security.Privilege.JCR_READ;
import static javax.jcr.security.Privilege.JCR_WRITE;
import static org.apache.sling.jcr.base.util.AccessControlUtil.replaceAccessControlEntry;
import static org.sakaiproject.nakamura.api.user.UserConstants.SYSTEM_USER_MANAGER_USER_PATH;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.jcr.base.util.AccessControlUtil;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.apache.sling.servlets.post.Modification;
import org.sakaiproject.nakamura.api.connections.ConnectionConstants;
import org.sakaiproject.nakamura.api.user.UserConstants;
import org.sakaiproject.nakamura.api.user.UserPostProcessor;
import org.sakaiproject.nakamura.util.JcrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

/**
 * This PostProcessor listens to post operations on User objects and creates a connection
 * store. 
 */
@Component(immediate = true, description = "Post Processor for User and Group operations to create a connection store", label = "ConnectionsUserPostProcessor")
@Properties(value = {
    @Property(name = "service.vendor", value = "The Sakai Foundation"),
    @Property(name = "service.description", value = "Post Processes User and Group operations") })
@Service(value = UserPostProcessor.class)
public class ConnectionsUserPostProcessor implements UserPostProcessor {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(ConnectionsUserPostProcessor.class);

  public void process(Authorizable authorizable, Session session,
      SlingHttpServletRequest request, List<Modification> changes) throws Exception {
    String resourcePath = request.getRequestPathInfo().getResourcePath();
    if (resourcePath.equals(SYSTEM_USER_MANAGER_USER_PATH)) {
      PrincipalManager principalManager = AccessControlUtil.getPrincipalManager(session);
      String path = ConnectionUtils.getConnectionPathBase(authorizable);
      LOGGER.debug("Creating connections store: {}", path);

      Node store = JcrUtils.deepGetOrCreateNode(session, path);
      store.setProperty(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY,
          ConnectionConstants.SAKAI_CONTACTSTORE_RT);
      // ACL's are managed by the Personal User Post processor.
      Principal anon = new Principal() {

        public String getName() {
          return UserConstants.ANON_USERID;
        }
      };
      Principal everyone = principalManager.getEveryone();

      replaceAccessControlEntry(session, path, authorizable.getPrincipal(),
          new String[] { JCR_ALL }, null, null);

      // explicitly deny anon and everyone, this is private space.
      String[] deniedPrivs = new String[] { JCR_READ, JCR_WRITE };
      replaceAccessControlEntry(session, path, anon, null, deniedPrivs, null);
      replaceAccessControlEntry(session, path, everyone, null, deniedPrivs, null);
    }
  }

  /**
   * {@inheritDoc}
   * @see org.sakaiproject.nakamura.api.user.UserPostProcessor#getSequence()
   */
  public int getSequence() {
    return 10;
  }


}
