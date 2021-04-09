/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atlantis.analytics.util;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.plugins.interceptors.CorsFilter;

@ApplicationPath("/analytics")
public class AnalyticsRestApplication extends Application {
    private Set<Object> singletons = new HashSet<>();
    private Set<Class<?>> empty = new HashSet<>();

    public AnalyticsRestApplication() {
        CorsFilter filter = new CorsFilter();
        filter.getAllowedOrigins().add("*");
        singletons.add(filter);
        singletons.add(new AnalyticsRestService());
    }

    @Override
    public Set<Class<?>> getClasses() {
        return empty;
    }

    @Override
    public Set<Object> getSingletons() {
        if (singletons == null) {
            CorsFilter corsFilter = new CorsFilter();
            corsFilter.getAllowedOrigins().add("*");
            corsFilter.setAllowedMethods("GET, POST, PUT, DELETE, OPTIONS");
            singletons = new LinkedHashSet<>();
            singletons.add(corsFilter);
        }

        return singletons;
    }
}