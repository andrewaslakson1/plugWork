package my.plug.plugWork.testing;

import my.plug.plugWork.annotation.PowerSource;
import my.plug.plugWork.annotation.Socket;

@PowerSource
public class Test2 {
    @Socket
    private Test1 test1;
}
