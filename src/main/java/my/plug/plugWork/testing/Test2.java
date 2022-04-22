package my.plug.plugWork.testing;

import my.plug.plugWork.annotation.Power;
import my.plug.plugWork.annotation.Socket;

@Power
public class Test2 {
    @Socket
    private Test1 test1;
}
