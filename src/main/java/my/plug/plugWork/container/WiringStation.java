package my.plug.plugWork.container;

public class WiringStation {
    public Class clazz;
    public Object station;

    public WiringStation(Class clazz, Object station) {
        this.clazz = clazz;
        this.station = station;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Object getStation() {
        return station;
    }

    public void setStation(Object station) {
        this.station = station;
    }
}
