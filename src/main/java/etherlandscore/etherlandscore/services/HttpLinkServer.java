package etherlandscore.etherlandscore.services;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import etherlandscore.etherlandscore.eth.LinkHandler;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ServerModule;
import org.jetlang.fibers.Fiber;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.concurrent.Executors;

public class HttpLinkServer extends ServerModule  {
  private final Channels channels;

  public HttpLinkServer(Channels channels, Fiber fiber) throws Exception {
    super(fiber);
    this.channels = channels;
  }

  public void launch(int port) throws Exception {
    try {
      // setup the socket address
      InetSocketAddress address = new InetSocketAddress(port);

      // initialise the HTTPS server
      HttpsServer httpsServer = HttpsServer.create(address, 0);
      SSLContext sslContext = SSLContext.getInstance("TLS");

      // initialise the keystore
      char[] password = "password".toCharArray();
      KeyStore ks = KeyStore.getInstance("JKS");
      FileInputStream fis = new FileInputStream("testkey.jks");
      ks.load(fis, password);

      // setup the key manager factory
      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      kmf.init(ks, password);

      // setup the trust manager factory
      TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
      tmf.init(ks);

      // setup the HTTPS context and parameters
      sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
      httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
        public void configure(HttpsParameters params) {
          try {
            // initialise the SSL context
            SSLContext context = getSSLContext();
            SSLEngine engine = context.createSSLEngine();
            params.setNeedClientAuth(false);
            params.setCipherSuites(engine.getEnabledCipherSuites());
            params.setProtocols(engine.getEnabledProtocols());

            // Set the SSL parameters
            SSLParameters sslParameters = context.getSupportedSSLParameters();
            params.setSSLParameters(sslParameters);

          } catch (Exception ex) {
            System.out.println("Failed to create HTTPS port");
          }
        }
      });
      httpsServer.createContext("/link", new LinkHandler(channels));
      httpsServer.setExecutor(Executors.newCachedThreadPool());
      httpsServer.start();

    } catch (Exception exception) {
      System.out.println("Failed to create HTTPS server on port " + 8000 + " of localhost");
      exception.printStackTrace();

    }
  }
}
