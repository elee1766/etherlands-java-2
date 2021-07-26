package etherlandscore.etherlandscore.fibers;

public class Message<T> {
  private final T command;
  private final Object[] args;

  public Message(T command, Object... args) {
    this.command = command;
    this.args = args;
  }

  public Object[] getArgs() {
    return args;
  }

  public T getCommand() {
    return command;
  }
}
