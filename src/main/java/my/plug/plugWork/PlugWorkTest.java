package my.plug.plugWork;

import my.plug.plugWork.annotation.EnablePlugging;
import my.plug.plugWork.manager.PlugManager;

@EnablePlugging
public class PlugWorkTest {
    public static void main(String[] args) {
        PlugManager.wire("my.plug.plugWork");
    }
}
