package etherlandscore.etherlandscore.fibers;

public class Message {
    private final String command;
    private final Object[] args;

    public Message(String command, Object... args){
        this.command = command;
        this.args = args;
    }

    public String getCommand() {
        return command;
    }


    public Object[] getArgs() {
        return args;
    }
}
