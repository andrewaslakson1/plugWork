package my.plug.plugWork.testing;

import my.plug.plugWork.annotation.Plug;
import my.plug.plugWork.annotation.WireStation;

@WireStation
public class Test1 {

    @Plug
    public Test1 getTest1() {
        return new Test1();
    }
}
