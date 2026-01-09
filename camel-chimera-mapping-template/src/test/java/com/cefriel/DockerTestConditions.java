package com.cefriel;

import org.testcontainers.DockerClientFactory;

public class DockerTestConditions {

    public static boolean isDockerAvailable() {
        try {
            DockerClientFactory.instance().client();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

