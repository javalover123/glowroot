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

import static org.fest.assertions.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.informantproject.core.util.DataSource;
import org.informantproject.core.util.DataSourceTestProvider;
import org.informantproject.core.util.RollingFile;
import org.informantproject.core.util.RollingFileTestProvider;
import org.informantproject.core.util.UnitTests;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.jukito.TestSingleton;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Trask Stalnaker
 * @since 0.5
 */
@RunWith(JukitoRunner.class)
public class TraceSnapshotDaoTest {

    private Collection<Thread> preExistingThreads;

    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            bind(DataSource.class).toProvider(DataSourceTestProvider.class).in(TestSingleton.class);
            bind(RollingFile.class).toProvider(RollingFileTestProvider.class).in(
                    TestSingleton.class);
        }
    }

    @Before
    public void before(DataSource dataSource) throws SQLException {
        preExistingThreads = UnitTests.currentThreads();
        if (dataSource.tableExists("trace")) {
            dataSource.execute("drop table trace");
        }
    }

    @After
    public void after(DataSource dataSource, RollingFile rollingFile) throws Exception {
        UnitTests.preShutdownCheck(preExistingThreads);
        dataSource.closeAndDeleteFile();
        rollingFile.closeAndDeleteFile();
        UnitTests.postShutdownCheck(preExistingThreads);
    }

    @Test
    public void shouldReadSnapshot(TraceSnapshotDao snapshotDao,
            TraceSnapshotTestData snapshotTestData) {

        // given
        TraceSnapshot snapshot = snapshotTestData.createSnapshot();
        snapshotDao.storeSnapshot(snapshot);
        // when
        List<TraceSnapshotSummary> summaries = snapshotDao.readSummaries(0, 0, 0,
                Long.MAX_VALUE, null, null);
        TraceSnapshot snapshot2 = snapshotDao.readSnapshot(summaries.get(0).getId());
        // then
        assertThat(snapshot2.getStartAt()).isEqualTo(snapshot.getStartAt());
        assertThat(snapshot2.isStuck()).isEqualTo(snapshot.isStuck());
        assertThat(snapshot2.getId()).isEqualTo(snapshot.getId());
        assertThat(snapshot2.getDuration()).isEqualTo(snapshot.getDuration());
        assertThat(snapshot2.isCompleted()).isEqualTo(snapshot.isCompleted());
        assertThat(snapshot2.getDescription()).isEqualTo("test description");
        assertThat(snapshot2.getUsername()).isEqualTo(snapshot.getUsername());
        // TODO verify metricData, trace and mergedStackTree
    }

    @Test
    public void shouldReadSnapshotWithDurationQualifier(TraceSnapshotDao snapshotDao,
            TraceSnapshotTestData snapshotTestData) {

        // given
        TraceSnapshot snapshot = snapshotTestData.createSnapshot();
        snapshotDao.storeSnapshot(snapshot);
        // when
        List<TraceSnapshotSummary> summaries = snapshotDao.readSummaries(0, 0,
                snapshot.getDuration(), snapshot.getDuration(), null, null);
        // then
        assertThat(summaries).hasSize(1);
    }

    @Test
    public void shouldNotReadSnapshotWithHighDurationQualifier(TraceSnapshotDao snapshotDao,
            TraceSnapshotTestData snapshotTestData) {

        // given
        TraceSnapshot snapshot = snapshotTestData.createSnapshot();
        snapshotDao.storeSnapshot(snapshot);
        // when
        List<TraceSnapshotSummary> summaries = snapshotDao.readSummaries(0, 0,
                snapshot.getDuration() + 1, snapshot.getDuration() + 2, null, null);
        // then
        assertThat(summaries).isEmpty();
    }

    @Test
    public void shouldNotReadSnapshotWithLowDurationQualifier(TraceSnapshotDao snapshotDao,
            TraceSnapshotTestData snapshotTestData) {

        // given
        TraceSnapshot snapshot = snapshotTestData.createSnapshot();
        snapshotDao.storeSnapshot(snapshot);
        // when
        List<TraceSnapshotSummary> summaries = snapshotDao.readSummaries(0, 0,
                snapshot.getDuration() - 2, snapshot.getDuration() - 1, null, null);
        // then
        assertThat(summaries).isEmpty();
    }

    @Test
    public void shouldDeletedTrace(TraceSnapshotDao snapshotDao,
            TraceSnapshotTestData snapshotTestData) {

        // given
        snapshotDao.storeSnapshot(snapshotTestData.createSnapshot());
        // when
        snapshotDao.deleteSnapshots(0, 0);
        // then
        assertThat(snapshotDao.count()).isEqualTo(0);
    }
}