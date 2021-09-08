package com.jasonernst.awm.loggers;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.jasonernst.awm.ObservingDevice;
import com.jasonernst.awm.stats.NetworkStat;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseLoggerTest {

    private DatabaseObservationDao dao;
    private ObservationDatabase db;
    private NetworkLogger networkLogger;

    @BeforeEach public void init() {
        dao = Mockito.mock(DatabaseObservationDao.class);
        db = Mockito.mock(ObservationDatabase.class);
        doReturn(dao).when(db).databaseObservationDao();
        networkLogger = Mockito.mock(NetworkLogger.class);
    }

    @Test public void DatabaseLoggerTest() throws Exception {
        DatabaseLogger logger = Mockito.spy(new DatabaseLogger(networkLogger, db,false, false));
        logger.start();
        logger.stop();

        logger = Mockito.spy(new DatabaseLogger(networkLogger, db,true, false));
        logger.start();
        logger.stop();

        assertEquals(logger.getCountUploaded(), 0);
        assertEquals(logger.getCountNonUploaded(), 0);
        assertEquals(logger.getTotalCount(), 0);
    }

    @Test public void LogTest() throws JSONException {
        // good path
        DatabaseLogger logger = Mockito.spy(new DatabaseLogger(networkLogger, db,false, false));
        NetworkStat networkStat = Mockito.mock(NetworkStat.class);
        ObservingDevice device = Mockito.mock(ObservingDevice.class);
        logger.log(networkStat, device);

        // json ex on toJSON
        doThrow(JSONException.class).when(networkStat).toJSON(device);
        logger.log(networkStat, device);

        // record saved correctly
        doReturn(1L).when(dao).insert(any());
        logger.log(networkStat, device);
    }

    @Test public void uploadTest() throws IOException, InterruptedException {
        DatabaseLogger logger = Mockito.spy(new DatabaseLogger(networkLogger, db,false, false));
        Thread networkThread = Mockito.mock(Thread.class);
        logger.setNetworkThread(networkThread);
        // !running && !interrupted
        logger.setRunning(false);
        doReturn(false).when(networkThread).isInterrupted();
        doNothing().when(logger).wait(anyInt());
        logger.uploadLogs();

        // running && !interrupted, interrupted
        logger.setRunning(true);
        doReturn(false).doReturn(true).when(networkThread).isInterrupted();
        // !online, nonuploaded = 0
        doReturn(false).when(networkLogger).isOnline();
        doReturn(0).when(dao).getCountNonUploaded();
        doNothing().when(logger).wait(anyInt());
        logger.uploadLogs();

        // running && !interrupted, interrupted
        logger.setRunning(true);
        doReturn(false).doReturn(true).when(networkThread).isInterrupted();
        // online, nonuploaded = 0
        doReturn(true).when(networkLogger).isOnline();
        doReturn(0).when(dao).getCountNonUploaded();
        doNothing().when(logger).wait(anyInt());
        logger.uploadLogs();

        // running && !interrupted, interrupted
        logger.setRunning(true);
        doReturn(false).doReturn(true).when(networkThread).isInterrupted();
        // online, nonuploaded = 1
        doReturn(true).when(networkLogger).isOnline();
        doReturn(1).when(dao).getCountNonUploaded();
        doNothing().when(logger).wait(anyInt());
        logger.uploadLogs();

        // running && !interrupted, interrupted
        logger.setRunning(true);
        doReturn(false).doReturn(true).when(networkThread).isInterrupted();
        // online, nonuploaded = 1
        doReturn(true).when(networkLogger).isOnline();
        doReturn(1).when(dao).getCountNonUploaded();
        // observations not empty, clearupload = false
        List<DatabaseObservation> observations = new ArrayList<>();
        DatabaseObservation observation = Mockito.mock(DatabaseObservation.class);
        observations.add(observation);
        doReturn(observations).when(dao).getNonUploaded();
        doNothing().when(logger).wait(anyInt());
        logger.uploadLogs();

        // running && !interrupted, !interrupted, interrupted
        logger.setRunning(true);
        doReturn(false).doReturn(false).doReturn(true).when(networkThread).isInterrupted();
        // online, nonuploaded = 1
        doReturn(true).when(networkLogger).isOnline();
        doReturn(1).when(dao).getCountNonUploaded();
        // observations not empty, clearupload = false
        observations = new ArrayList<>();
        observation = Mockito.mock(DatabaseObservation.class);
        observations.add(observation);
        doReturn(observations).when(dao).getNonUploaded();
        doNothing().when(logger).wait(anyInt());
        logger.uploadLogs();

        // running && !interrupted, !interrupted, interrupted
        logger.setClearUpload(true);
        logger.setRunning(true);
        doReturn(false).doReturn(false).doReturn(true).when(networkThread).isInterrupted();
        // online, nonuploaded = 1
        doReturn(true).when(networkLogger).isOnline();
        doReturn(1).when(dao).getCountNonUploaded();
        // observations not empty, clearupload = true
        observations = new ArrayList<>();
        observation = Mockito.mock(DatabaseObservation.class);
        observations.add(observation);
        doReturn(observations).when(dao).getNonUploaded();
        doNothing().when(logger).wait(anyInt());
        logger.uploadLogs();

        // running && !interrupted, !interrupted, interrupted, invalid param exception
        logger.setClearUpload(true);
        logger.setRunning(true);
        doReturn(false).doReturn(false).doReturn(true).when(networkThread).isInterrupted();
        // online, nonuploaded = 1
        doReturn(true).when(networkLogger).isOnline();
        doReturn(1).when(dao).getCountNonUploaded();
        // observations not empty, clearupload = true
        observations = new ArrayList<>();
        observation = Mockito.mock(DatabaseObservation.class);
        observations.add(observation);
        doReturn(observations).when(dao).getNonUploaded();
        doThrow(InvalidParameterException.class).when(networkLogger).uploadJsonEntry(any());
        doNothing().when(logger).wait(anyInt());
        logger.uploadLogs();

        // running && !interrupted, !interrupted, interrupted, ioex
        logger.setClearUpload(true);
        logger.setRunning(true);
        doReturn(false).doReturn(false).doReturn(true).when(networkThread).isInterrupted();
        // online, nonuploaded = 1
        doReturn(true).when(networkLogger).isOnline();
        doReturn(1).when(dao).getCountNonUploaded();
        // observations not empty, clearupload = true
        observations = new ArrayList<>();
        observation = Mockito.mock(DatabaseObservation.class);
        observations.add(observation);
        doReturn(observations).when(dao).getNonUploaded();
        doThrow(IOException.class).when(networkLogger).uploadJsonEntry(any());
        doNothing().when(logger).wait(anyInt());
        logger.uploadLogs();

        // running && !interrupted, !interrupted, interrupted, interrupted exception
        logger.setClearUpload(true);
        logger.setRunning(true);
        doReturn(false).doReturn(false).doReturn(true).when(networkThread).isInterrupted();
        doThrow(InterruptedException.class).when(logger).wait(anyInt());
        // online, nonuploaded = 1
        doReturn(true).when(networkLogger).isOnline();
        doReturn(1).when(dao).getCountNonUploaded();
        // observations not empty, clearupload = true
        observations = new ArrayList<>();
        observation = Mockito.mock(DatabaseObservation.class);
        observations.add(observation);
        doReturn(observations).when(dao).getNonUploaded();
        logger.uploadLogs();
    }

    @Test public void wait_test() throws InterruptedException {
        DatabaseLogger logger = Mockito.spy(new DatabaseLogger(networkLogger, db,false, false));
        logger.wait(10);
    }
}
