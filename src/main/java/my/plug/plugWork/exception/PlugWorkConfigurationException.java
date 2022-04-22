package my.plug.plugWork.exception;

public class PlugWorkConfigurationException extends RuntimeException {
    public PlugWorkConfigurationException() {
        super("Given current configurations, PlugWork is in an unusable state");
    }

    public PlugWorkConfigurationException(String msg) {
        super(msg);
    }
}
