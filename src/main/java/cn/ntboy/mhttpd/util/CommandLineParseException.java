package cn.ntboy.mhttpd.util;

public class CommandLineParseException extends Exception{
    public CommandLineParseException() {
        super();
    }

    public CommandLineParseException(String message) {
        super(message);
    }

    public CommandLineParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandLineParseException(Throwable cause) {
        super(cause);
    }
}
