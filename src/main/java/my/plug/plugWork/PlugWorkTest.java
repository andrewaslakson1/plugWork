package my.plug.plugWork;

import my.plug.plugWork.annotation.Socket;
import my.plug.plugWork.annotation.Source;
import my.plug.plugWork.manager.PlugManager;
import my.plug.plugWork.testing.Test1;
import my.plug.plugWork.testing.Test2;

@Source
public class PlugWorkTest {
    @Socket
    public static Test1 test1;

    @Socket
    public static Test2 test2;

    public static void main(String[] args) {
        PlugManager.wire("my.plug.plugWork");

        test1.doSomething();
        test2.doSomething();
    }
}
