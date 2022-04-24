package my.plug.plugWork.testing;

import my.plug.plugWork.annotation.PowerPlug;
import my.plug.plugWork.annotation.Socket;
import my.plug.plugWork.annotation.Source;
import my.plug.plugWork.annotation.Start;

@PowerPlug
@Source
public class Source1 {

    @Socket
    public Power3 power3;

    @Start
    public void start() {
        System.out.println("Inside Source, Executing actions:" + "\n");
        power3.doSomething();
    }
}
