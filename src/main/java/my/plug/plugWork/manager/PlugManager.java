package my.plug.plugWork.manager;

import my.plug.plugWork.annotation.Plug;
import my.plug.plugWork.annotation.Power;
import my.plug.plugWork.annotation.Socket;
import my.plug.plugWork.annotation.WireStation;

import my.plug.plugWork.container.WiringStation;
import my.plug.plugWork.exception.InstantiationException;
import my.plug.plugWork.exception.PlugWorkConfigurationException;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.*;

public class PlugManager {

    private static Set<WiringStation> wiringStations = new HashSet<>();
    private static Map<String, Object> plugs= new HashMap<>();

    private static Set<Class> classSet = new HashSet<>();
    private static Map<Class, Set<Class>> dependencyMap = new HashMap<>();

    public static Map<String, Object> powers = new HashMap<>();

    public static void wire(String prefix) {
        setClassSet(prefix);
        if (classSet.isEmpty()) throw new PlugWorkConfigurationException("Detected 0 classes, but how?");

        buildWiringStations();
        if (wiringStations.isEmpty()) throw new PlugWorkConfigurationException("Could not locate any WiringStations.");

        buildDependencyMap();
        if (dependencyMap.isEmpty()) throw new PlugWorkConfigurationException("Could not locate any PowerSources.");

        createWiringStationPlugs();

        createPowerPlugs();

        injectPlugsInSources();
    }

    private static void setClassSet(String prefix) {
        classSet.addAll(new Reflections(prefix, new SubTypesScanner(false))
                .getSubTypesOf(Object.class));
    }

    private static void buildWiringStations() {
        classSet.stream().filter(clazz -> clazz.isAnnotationPresent(WireStation.class)).forEach(clazz -> {
            try {
                Object station = clazz.getDeclaredConstructor().newInstance();
                wiringStations.add(new WiringStation(clazz, station));
            } catch (Exception e) {
                e.printStackTrace();
                throw new InstantiationException("Failed to instantiate WireStation");
            }
        });
    }

    private static void buildDependencyMap() {
        classSet.stream().filter(clazz -> clazz.isAnnotationPresent(Power.class)).forEach(clazz -> {
            Set<Class> dependencies = new HashSet<>();
            Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
                if (field.isAnnotationPresent(Socket.class)) {
                    dependencies.add(field.getType());
                }
            });
            dependencyMap.put(clazz, dependencies);
        });
        String yum = "yum";
    }

    private static void createWiringStationPlugs() {
        wiringStations.stream().forEach(station -> {
            Arrays.asList(station.getClazz().getDeclaredMethods()).stream()
                    .filter(method -> method.isAnnotationPresent(Plug.class))
                    .forEach(plugMethod -> {
                        String name = plugMethod.getAnnotation(Plug.class).name();
                        if (name.equals("")) name = plugMethod.getReturnType().getName();
                        Object plug = plugMethod.invoke(station.getStation());
                        plugs.put(name, plug);
                    });
        });
    }

    private static void createPowerPlugs() {
    }

    private static void injectPlugsInSources(){
    }
}
