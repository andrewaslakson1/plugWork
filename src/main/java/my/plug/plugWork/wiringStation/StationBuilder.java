package my.plug.plugWork.wiringStation;

import my.plug.plugWork.annotation.WireStation;
import my.plug.plugWork.exception.InstantiationException;

import java.util.HashSet;
import java.util.Set;

public class StationBuilder {

    public Set<WiringStation> retrieveStations(Set<Class> classSet) {

        Set<WiringStation> wiringStations = new HashSet<>();

        classSet.stream().filter(clazz -> clazz.isAnnotationPresent(WireStation.class)).forEach(clazz -> {
            try {
                Object station = clazz.getDeclaredConstructor().newInstance();
                wiringStations.add(new WiringStation(clazz, station));
            } catch (Exception e) {
                e.printStackTrace();
                throw new InstantiationException("Failed to instantiate WireStation");
            }
        });

        return wiringStations;
    }
}
