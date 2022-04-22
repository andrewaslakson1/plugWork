package my.plug.plugWork.testing;

import my.plug.plugWork.annotation.EnablePlugging;
import my.plug.plugWork.annotation.Plug;

@EnablePlugging
public class Test1 {

    @Plug
    public Test1 getTest1() {
        return new Test1();
    }
}
