package my.plug.plugWork.testing;

import my.plug.plugWork.annotation.Plug;
import my.plug.plugWork.annotation.WireStation;

@WireStation
public class WireStation1 {

    @Plug
    public Plug1 getPlug1() {
        return new Plug1();
    }

    @Plug
    public Plug2 getPlug2() {
        return new Plug2();
    }
}
