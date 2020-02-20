package rest.app;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UndertowServerTest {

  private String host = "0.0.0.0";
  private int port = 8080;

  @Test
  void testServer() throws ServletException {

    UndertowServer server = new UndertowServer(host, port);
    server.start();

    assertTrue(server.isRunning());
    assertTrue(pingable("localhost", port));

    server.stop();
    assertFalse(server.isRunning());
  }

  @Test
  void testStatus() throws IOException, ServletException {

    UndertowServer server = new UndertowServer(host, port);
    server.start();

    try (CloseableHttpClient client =
        HttpClientBuilder.create().setMaxConnTotal(1).setMaxConnPerRoute(1).build()) {
      String statusUri = "http://localhost:" + port + "api/v1/status";
      HttpGet get = new HttpGet(statusUri);
      HttpResponse response = client.execute(get);
      StatusLine statusLine = response.getStatusLine();
      assertEquals(200, statusLine.getStatusCode());
      assertEquals("OK", EntityUtils.toString(response.getEntity()));
    }
  }

  private boolean pingable(String host, int port) {
    Socket socket = new Socket();
    try {
      socket.connect(new InetSocketAddress(host, port), 1000);
      return true;
    } catch (IOException e) {
    }
    return false;
  }
}
