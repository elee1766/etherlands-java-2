package etherlandscore.etherlandscore.fibers;

import org.jetlang.fibers.Fiber;

public class ServerModule {
  final Fiber fiber;

  protected ServerModule(Fiber fiber) {
    this.fiber = fiber;
  }

  public void start() {
    fiber.start();
  }

  public void stop() {
    fiber.dispose();
  }
}
