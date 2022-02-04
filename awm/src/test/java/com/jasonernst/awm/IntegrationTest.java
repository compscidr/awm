package com.jasonernst.awm;

import static org.mockito.Mockito.mock;

import android.content.Context;

import com.jasonernst.awm.loggers.NetworkLogger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.FileSystems;

@Testcontainers
public class IntegrationTest {
    private NetworkLogger networkLogger;

    // todo: convert to docker-compose to get the mysql service up and running too
    @Container
    public GenericContainer awm_server = new GenericContainer(new ImageFromDockerfile()
            .withDockerfile(FileSystems.getDefault().getPath("../awm-server/Dockerfile")))
            .withExposedPorts(80)
            .waitingFor(Wait.forHttp("/"));

    @BeforeEach
    public void setUp() throws InterruptedException {
        Context context = mock(Context.class);
        networkLogger = new NetworkLogger(context, false, true, "localhost/index.php");
        Thread.sleep(1000);
    }

    @Test
    public void test() {
        assert(true);
    }
}
