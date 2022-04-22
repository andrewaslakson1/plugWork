package my.plug.plugWork.testing;

import my.plug.plugWork.annotation.EnablePlugging;
import my.plug.plugWork.annotation.Socket;

@EnablePlugging
public class Test2 {
    @Socket
    Test1 test1;
}
