package my.plug.plugWork;

import my.plug.plugWork.classRetrieval.ClassRetriever;
import my.plug.plugWork.dependency.DependencyMapper;
import my.plug.plugWork.plugs.PlugMaker;
import my.plug.plugWork.starter.Solenoid;
import my.plug.plugWork.wiringStation.StationBuilder;
import my.plug.plugWork.wiringStation.WiringStation;
import my.plug.plugWork.exception.PlugWorkConfigurationException;

import java.lang.reflect.Field;

import java.util.Set;
import java.util.Map;

public class PlugMachine {

    public void wire(String prefix) {

        ClassRetriever classRetriever = new ClassRetriever();
        StationBuilder stationBuilder = new StationBuilder();
        DependencyMapper dependencyMapper = new DependencyMapper();
        PlugMaker plugMaker = new PlugMaker();
        Solenoid solenoid = new Solenoid();

        Set<Class> classSet = classRetriever.retrieveClassSet(prefix);
        if (classSet.isEmpty()) throw new PlugWorkConfigurationException("Detected 0 classes, but how?");

        Set<WiringStation> wiringStations = stationBuilder.retrieveStations(classSet);
        if (wiringStations.isEmpty()) throw new PlugWorkConfigurationException("Could not locate any WiringStations.");

        Map<String, Map<String, Field>> dependencyMap = dependencyMapper.retrieveDependencyMap(classSet);
        if (dependencyMap.isEmpty()) throw new PlugWorkConfigurationException("Could not locate any PowerSources.");

        Map<String, Object> plugs = plugMaker.retrieveWiringStationPlugs(wiringStations);

        Map<String, Object> powers = plugMaker.retrievePowerPlugs(classSet, dependencyMap, plugs);

        solenoid.contact(classSet, powers);
    }
}
