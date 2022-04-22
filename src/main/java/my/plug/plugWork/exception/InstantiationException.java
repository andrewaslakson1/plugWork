package my.plug.plugWork.exception;

public class InstantiationException extends RuntimeException {
    public InstantiationException() {
        super("Failed to instantiate an object, check stack traces");
    }
    public InstantiationException(String msg) {
        super(msg);
    }
}
