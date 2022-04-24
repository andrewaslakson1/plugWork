package my.plug.plugWork.testing;

import my.plug.plugWork.annotation.Plug;
import my.plug.plugWork.annotation.WireStation;

@WireStation
public class WireStation2 {
    @Plug
    public Plug3 getPlug3() {
        return new Plug3();
    }

    @Plug
    public Plug4 getPlug4() {
        return new Plug4();
    }

    @Plug
    public Plug5 getPlug5() {
        return new Plug5();
    }
}
