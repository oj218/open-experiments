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
package org.sakaiproject.kernel.jaxrs;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * 
 */
public class ServletConfigWrapper implements ServletConfig {
  /**
   *
   */
  private ServletConfig delegate;
  /**
   *
   */
  private ServletContext servletContext;

  /**
   * {@inheritDoc}
   * @see javax.servlet.ServletConfig#getInitParameter(java.lang.String)
   */
  public String getInitParameter(String arg0) {
    return delegate.getInitParameter(arg0);
  }

  /**
   * {@inheritDoc}
   * @see javax.servlet.ServletConfig#getInitParameterNames()
   */
  @SuppressWarnings("unchecked")
  @Deprecated
  public Enumeration getInitParameterNames() {
    return delegate.getInitParameterNames();
  }

  /**
   * {@inheritDoc}
   * @see javax.servlet.ServletConfig#getServletContext()
   */
  public ServletContext getServletContext() {
    return servletContext;
  }

  /**
   * {@inheritDoc}
   * @see javax.servlet.ServletConfig#getServletName()
   */
  public String getServletName() {
    return delegate.getServletName();
  }

  /**
   * @param delegate
   */
  ServletConfigWrapper(ServletConfig delegate) {
    this.delegate = delegate;
    servletContext = new ServletContextWrapper(delegate.getServletContext());
  }
}