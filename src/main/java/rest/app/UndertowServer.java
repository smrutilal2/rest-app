package rest.app;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.xnio.Options;

import javax.servlet.ServletException;

public class UndertowServer {

  private String host;
  private int port;
  private Undertow undertow;

  public UndertowServer(final String host, final int port) {
    this.host = host;
    this.port = port;
  }

  public void start() throws ServletException {
    final HttpHandler restHandler = createRestHandler();

    final Undertow.Builder builder =
        Undertow.builder()
            .addHttpListener(port, host)
            .setBufferSize(1024 * 16)
            .setIoThreads(Math.max(1, Runtime.getRuntime().availableProcessors() - 1))
            .setSocketOption(Options.BACKLOG, 20)
            .setServerOption(UndertowOptions.ALWAYS_SET_DATE, true)
            .setWorkerOption(Options.CONNECTION_HIGH_WATER, 7000)
            .setWorkerOption(Options.CONNECTION_LOW_WATER, 5000)
            .setWorkerThreads(100)
            .setHandler(restHandler)
            .setServerOption(UndertowOptions.ENABLE_STATISTICS, true)
            .setServerOption(UndertowOptions.RECORD_REQUEST_START_TIME, true);

    undertow = builder.build();
    undertow.start();

    System.out.println("===============================================");
    System.out.println("Listening on host: " + host + "  port: " + port);
    System.out.println("===============================================");
  }

  public void stop() {
    if (undertow != null) {
      undertow.stop();
      undertow = null;
    }
  }

  public boolean isRunning() {
    return undertow != null;
  }

  private HttpHandler createRestHandler() throws ServletException {
    ResteasyDeployment deployment = new ResteasyDeploymentImpl();
    deployment.setApplication(new Application());

    UndertowJaxrsServer server = new UndertowJaxrsServer();
    DeploymentInfo deploymentInfo =
        server
            .undertowDeployment(deployment, "/")
            .setClassLoader(UndertowServer.class.getClassLoader())
            .setContextPath("/api")
            .setDeploymentName("API");
    DeploymentManager deploymentManager = Servlets.defaultContainer().addDeployment(deploymentInfo);
    deploymentManager.deploy();
    return deploymentManager.start();
  }
}
