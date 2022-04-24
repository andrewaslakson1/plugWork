package my.plug.plugWork.testing;

import my.plug.plugWork.annotation.PowerPlug;
import my.plug.plugWork.annotation.Socket;

@PowerPlug
public class Power1 {
    @Socket
    public Plug1 plug1;

    @Socket
    public Plug3 plug3;

    @Socket
    public Plug5 plug5;

    public void doSomething() {
        System.out.println("I am doing something inside of Power1, This includes:\n\t"
                + plug1.doSomething() + "\n\t"
                + plug3.doSomething() + "\n\t"
                + plug5.doSomething());
    }
}
