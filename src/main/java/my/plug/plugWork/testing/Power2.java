package my.plug.plugWork.testing;

import my.plug.plugWork.annotation.PowerPlug;
import my.plug.plugWork.annotation.Socket;

@PowerPlug
public class Power2 {

    @Socket
    public Plug1 plug1;

    @Socket
    public Plug2 plug2;

    @Socket
    public Plug3 plug3;

    @Socket
    public Power1 power1;

    public void doSomething() {
        System.out.println("I am doing something inside of Power2, This includes:\n\t"
                + plug1.doSomething() + "\n\t"
                + plug2.doSomething() + "\n\t"
                + plug3.doSomething());
        System.out.println("This also includes: ");
        power1.doSomething();
    }
}
