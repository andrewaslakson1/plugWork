package my.plug.plugWork.exception;

public class DuplicatePlugException extends RuntimeException {
    public DuplicatePlugException() {
        super("Encountered two plugs with the same name.");
    }

    public DuplicatePlugException(String msg) {
        super(msg);
    }
}
