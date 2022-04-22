package my.plug.plugWork.exception;

public class ImpossibleDependencyException extends RuntimeException {
    public ImpossibleDependencyException() {
        super("Could not satisfy dependencies");
    }

    public ImpossibleDependencyException(String msg) {
        super(msg);
    }
}
