/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glowroot.agent.weaving;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// this is needed because Instrumentation.appendToBootstrapClassLoaderSearch() doesn't add resources
// to the search path
public class ExtraBootResourceFinder {

    private static final Logger logger = LoggerFactory.getLogger(ExtraBootResourceFinder.class);

    private final ImmutableList<File> jarFiles;

    public ExtraBootResourceFinder(List<File> jarFiles) {
        this.jarFiles = ImmutableList.copyOf(jarFiles);
    }

    @Nullable
    URL findResource(String name) {
        for (File jarFile : jarFiles) {
            try {
                URL url = new URL("jar:" + jarFile.toURI() + "!/" + name);
                // call openStream()/close() to test if this exists
                url.openStream().close();
                return url;
            } catch (IOException e) {
                // log exception at trace level
                logger.trace(e.getMessage(), e);
            }
        }
        return null;
    }
}
