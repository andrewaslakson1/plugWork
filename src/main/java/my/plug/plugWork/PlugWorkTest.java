package my.plug.plugWork;

import my.plug.plugWork.annotation.Source;
import my.plug.plugWork.manager.PlugManager;

@Source
public class PlugWorkTest {
    public static void main(String[] args) {
        PlugManager.wire("my.plug.plugWork");
    }
}
