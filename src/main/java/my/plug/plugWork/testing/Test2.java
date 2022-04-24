package my.plug.plugWork.testing;

import my.plug.plugWork.annotation.PowerPlug;
import my.plug.plugWork.annotation.Socket;

@PowerPlug
public class Test2 {
    @Socket
    public Test1 test1;

    public void doSomething() {
        System.out.println("I am doing something inside of Test2");
    }
}
