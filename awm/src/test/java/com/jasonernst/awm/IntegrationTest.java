package com.jasonernst.awm;

import static org.mockito.Mockito.mock;

import android.content.Context;

import com.jasonernst.awm.loggers.NetworkLogger;
import com.jasonernst.awm.stats.NetworkStat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.time.Duration;

@Testcontainers
public class IntegrationTest {
    private static final int AWM_SERVER_PORT = 80;

    @Container
    public GenericContainer awm_server = new GenericContainer(new ImageFromDockerfile()
            .withDockerfile(FileSystems.getDefault().getPath("../awm-server/Dockerfile")))
            .withExposedPorts(AWM_SERVER_PORT)
            .waitingFor(Wait.forListeningPort());

    @Test
    public void smokeTest() throws IOException {
        URL url = new URL("http://" + awm_server.getHost() + ":" + awm_server.getMappedPort(AWM_SERVER_PORT) + "/index.php");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        con.setInstanceFollowRedirects(true);
        int status = con.getResponseCode();
        assert(status == 200);
    }

    @Test
    public void uploadTest() {
        // step 1: assert there is nothing in the downloads

        // step 2: upload a stat, assert status nominal

        // step 3: assert there is something in the downloads


//        Context context = mock(Context.class);
//        NetworkLogger networkLogger = new NetworkLogger(context, true, false, "http://" + awm_server.getHost() + ":" + awm_server.getMappedPort(AWM_SERVER_PORT) + "/upload");
//        NetworkStat networkStat = new NetworkStat(NetworkStat.DeviceType.BLUETOOTH, );
//        networkLogger.log();
    }
}
