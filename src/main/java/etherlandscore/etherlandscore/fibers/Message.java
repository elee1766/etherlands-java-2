package etherlandscore.etherlandscore.fibers;

public class Message<T> {
  private final T command;
  private final Object[] args;
  private Message<ChatTarget> chatResponse = null;

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

  public void setChatResponse(Message<ChatTarget> chatResponse) {
    this.chatResponse = chatResponse;
  }
  public Message<T> setChatResponse(ChatTarget target, Object...args){
    this.chatResponse = new Message<>(target, args);
    return this;
  }
  public boolean hasChatResponse(){
    return chatResponse != null;
  }
  public Message<ChatTarget> getChatResponse(){
    return chatResponse;
  }
}
