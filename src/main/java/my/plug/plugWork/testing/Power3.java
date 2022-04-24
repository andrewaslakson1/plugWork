package my.plug.plugWork.testing;

import my.plug.plugWork.annotation.PowerPlug;
import my.plug.plugWork.annotation.Socket;

@PowerPlug
public class Power3 {

    @Socket
    public Plug4 plug4;

    @Socket
    public Power1 power1;

    @Socket
    public Power2 power2;

    public void doSomething() {
        System.out.println("I am doing something inside of power3, This includes:\n\t" + plug4.doSomething());
        System.out.println("\n");
        power1.doSomething();
        System.out.println("\n");
        power2.doSomething();
    }
}
