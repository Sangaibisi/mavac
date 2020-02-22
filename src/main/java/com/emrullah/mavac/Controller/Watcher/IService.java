package com.emrullah.mavac.Controller.Watcher;

public interface IService {

    /**
     * Starts the service. This method blocks until the service has completely started.
     */
    void start() throws Exception;

    /**
     * Stops the service. This method blocks until the service has completely shut down.
     */
    void stop();

}
