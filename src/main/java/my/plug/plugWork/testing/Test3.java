package my.plug.plugWork.testing;

import my.plug.plugWork.annotation.PowerPlug;
import my.plug.plugWork.annotation.Socket;
import my.plug.plugWork.annotation.Source;
import my.plug.plugWork.annotation.Start;

@PowerPlug
@Source
public class Test3 {

    @Socket
    public Test1 test1;

    @Socket
    public Test2 test2;

    @Start
    public void start() {
        test1.doSomething();
        test2.doSomething();
    }
}
