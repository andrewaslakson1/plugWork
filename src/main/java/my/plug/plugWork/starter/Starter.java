package my.plug.plugWork.starter;

import my.plug.plugWork.exception.PlugWorkConfigurationException;

import java.lang.reflect.Method;

public class Starter implements Runnable {

    private Object source;
    private Method start;

    public Starter(Object source, Method start) {
        super();
        this.source = source;
        this.start = start;
    }

    @Override
    public void run() {
        try {
            start.invoke(source);
        } catch (Exception e) {
            e.printStackTrace();
            throw new PlugWorkConfigurationException("Failed to start application");
        }
    }
}
