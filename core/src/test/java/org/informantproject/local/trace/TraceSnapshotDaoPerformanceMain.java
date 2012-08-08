/**
 * Copyright 2011-2012 the original author or authors.
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
package org.informantproject.local.trace;

import org.informantproject.core.util.Clock;
import org.informantproject.core.util.DataSource;
import org.informantproject.core.util.DataSourceTestProvider;
import org.informantproject.core.util.RollingFile;
import org.informantproject.core.util.RollingFileTestProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Trask Stalnaker
 * @since 0.5
 */
public class TraceSnapshotDaoPerformanceMain {

    private static final Logger logger = LoggerFactory
            .getLogger(TraceSnapshotDaoPerformanceMain.class);

    public static class Module extends AbstractModule {
        @Override
        protected void configure() {
            bind(DataSource.class).toProvider(DataSourceTestProvider.class);
            bind(RollingFile.class).toProvider(RollingFileTestProvider.class);
            bind(Clock.class).toInstance(Clock.systemClock());
        }
    }

    public static void main(String... args) {
        Injector injector = Guice.createInjector(new Module());
        TraceSnapshotTestData snapshotTestData = injector.getInstance(TraceSnapshotTestData.class);
        TraceSnapshotDao snapshotDao = injector.getInstance(TraceSnapshotDao.class);

        Stopwatch stopwatch = new Stopwatch().start();
        for (int i = 0; i < 1000; i++) {
            snapshotDao.storeSnapshot(snapshotTestData.createSnapshot());
        }
        logger.info("elapsed time: {}", stopwatch.elapsedMillis());
        logger.info("num traces: {}", snapshotDao.count());
    }
}