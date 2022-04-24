package my.plug.plugWork.manager;

import my.plug.plugWork.annotation.Plug;
import my.plug.plugWork.annotation.PowerPlug;
import my.plug.plugWork.annotation.Socket;
import my.plug.plugWork.annotation.Source;
import my.plug.plugWork.annotation.Start;
import my.plug.plugWork.annotation.WireStation;

import my.plug.plugWork.container.WiringStation;
import my.plug.plugWork.exception.ImpossibleDependencyException;
import my.plug.plugWork.exception.InstantiationException;
import my.plug.plugWork.exception.PlugWorkConfigurationException;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class PlugManager {

    private static Set<WiringStation> wiringStations = new HashSet<>();
    private static Map<String, Object> plugs= new HashMap<>();

    private static Set<Class> classSet = new HashSet<>();
    private static Map<String, Map<String, Field>> dependencyMap = new HashMap<>();

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

        turnOn();
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
        classSet.stream().filter(clazz -> clazz.isAnnotationPresent(PowerPlug.class)).forEach(clazz -> {
            Map<String, Field> dependencies = new HashMap<>();
            String powerName = ((PowerPlug)clazz.getAnnotation(PowerPlug.class)).name();
            if (powerName.equals("")) powerName = clazz.getName();
            Arrays.stream(clazz.getDeclaredFields()).forEach(socket -> {
                if (socket.isAnnotationPresent(Socket.class)) {
                    String socketName = socket.getAnnotation(Socket.class).name();
                    if (socketName.equals("")) socketName = socket.getType().getName();
                    dependencies.put(socketName, socket);
                }
            });
            dependencyMap.put(powerName, dependencies);
        });
    }

    private static void createWiringStationPlugs() {
        wiringStations.stream().forEach(station -> {
            Arrays.asList(station.getClazz().getDeclaredMethods()).stream()
                    .filter(method -> method.isAnnotationPresent(Plug.class))
                    .forEach(plugMethod -> {
                        try {
                            String name = plugMethod.getAnnotation(Plug.class).name();
                            if (name.equals("")) name = plugMethod.getReturnType().getName();
                            Object plug = plugMethod.invoke(station.getStation());
                            plugs.put(name, plug);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new InstantiationException("Failed to instantiate level 1 plugs.");
                        }
                    });
        });
    }

    private static void createPowerPlugs() {
        classSet.stream().filter(clazz -> clazz.isAnnotationPresent(PowerPlug.class)).forEach(clazz -> {
            try {
                String name = ((PowerPlug)clazz.getAnnotation(PowerPlug.class)).name();
                if (name.equals("")) name = clazz.getName();
                Object power = clazz.getDeclaredConstructor().newInstance();
                powers.put(name, power);
            } catch (Exception e) {
                e.printStackTrace();
                throw new InstantiationException("Failed to instantiate Power Plugs.");
            }
        });

        do {
            int previousSize = dependencyMap.size();

            AtomicReference<Map<String, Field>> foundDependencies = new AtomicReference<>();
            foundDependencies.set(new HashMap<>());

            powers.forEach((name, power) -> {
                Map<String, Field> dependencies = dependencyMap.get(name);

                dependencies.forEach((socketName, socket) -> {
                    try {
                        if (plugs.containsKey(socketName)) {
                            socket.set(power, plugs.get(socketName));
                            foundDependencies.get().put(socketName, socket);
                        } else if (powers.containsKey(socketName)) {
                            socket.set(power, powers.get(socketName));
                            foundDependencies.get().put(socketName, socket);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new InstantiationException("Failed to inject Socket into Power");
                    }
                });

                foundDependencies.get().forEach((powerName, dependency) -> {
                    dependencies.remove(powerName);
                });
                if (dependencies.isEmpty()) dependencyMap.remove(name);
            });

            if (previousSize == dependencyMap.size()) throw new ImpossibleDependencyException("Detected Impossible Dependencies, failed to fill all sockets");
        } while (dependencyMap.size() > 0);
    }

    private static void turnOn() {
        List<Class> sourceClasses = classSet.stream().filter(clazz -> clazz.isAnnotationPresent(Source.class)).collect(Collectors.toList());
        if (sourceClasses.size() == 0 || sourceClasses.size() > 1)
            throw new PlugWorkConfigurationException("1 and only 1 class may be annotated with source, found: " + sourceClasses.size());

        String name = ((PowerPlug)sourceClasses.get(0).getAnnotation(PowerPlug.class)).name();
        if (name.equals("")) name = sourceClasses.get(0).getName();
        Object source = powers.get(name);

        List<Method> startMethods = Arrays.stream(sourceClasses.get(0).getDeclaredMethods()).filter(method -> method.isAnnotationPresent(Start.class)).collect(Collectors.toList());
        if (startMethods.size() == 0 || startMethods.size() > 1)
            throw new PlugWorkConfigurationException("1 and only 1 method may be marked as start method in source class, found: " + startMethods.size());

        Method startMethod = startMethods.get(0);

        sourceClasses = null;
        startMethods = null;

        cleanUp();

        try {
            startMethod.invoke(source);
        } catch (Exception e) {
            e.printStackTrace();
            throw new PlugWorkConfigurationException("Failed to start application");
        }
    }

    private static void cleanUp() {
        wiringStations = null;
        plugs= null;

        classSet = null;
        dependencyMap = null;

        powers = null;
    }
}
