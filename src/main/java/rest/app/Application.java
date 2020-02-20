package rest.app;

import rest.app.resource.HealthResource;

import java.util.HashSet;
import java.util.Set;

public class Application extends javax.ws.rs.core.Application {
  private final Set<Object> singletons;

  public Application() {
    this.singletons = new HashSet<>();
    this.singletons.add(new HealthResource());
  }

  @Override
  public Set<Object> getSingletons() {
    return singletons;
  }
}
