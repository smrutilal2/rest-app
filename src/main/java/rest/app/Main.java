package rest.app;

import javax.servlet.ServletException;

public class Main {

    public static void main(String[] args)
    {
        String host = "0.0.0.0";
        int port = 8080;

        UndertowServer server = new UndertowServer(host, port);
        try {
            server.start();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }
}
