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
import my.plug.plugWork.starter.Starter;

import java.io.File;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

public class PlugManager {

    private static Set<WiringStation> wiringStations;
    private static Map<String, Object> plugs;

    private static Set<Class> classSet;
    private static Map<String, Map<String, Field>> dependencyMap;

    public static Map<String, Object> powers;

    public static void wire(String prefix) {
        wiringStations = new HashSet<>();
        plugs = new HashMap<>();
        classSet = new HashSet<>();
        dependencyMap = new HashMap<>();
        powers = new HashMap<>();

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
        /* Stolen code belongs to mythbu from StackOverflow
         *      ~ Thanks my dude!
         */
        String path = prefix.replaceAll("\\.", "\\\\");
        String[] classPathEntries = System.getProperty("java.class.path").split(
                System.getProperty("path.separator")
        );

        String name;
        for (String classpathEntry : classPathEntries) {
            if (classpathEntry.endsWith(".jar")) {
                File jar = new File(classpathEntry);
                try {
                    JarInputStream is = new JarInputStream(new FileInputStream(jar));
                    JarEntry entry;
                    while((entry = is.getNextJarEntry()) != null) {
                        name = entry.getName();
                        if (name.endsWith(".class")) {
                            if (name.contains(path) && name.endsWith(".class")) {
                                String classPath = name.substring(0, entry.getName().length() - 6);
                                classPath = classPath.replaceAll("[\\|/]", ".");
                                classSet.add(Class.forName(classPath));
                            }
                        }
                    }
                } catch (Exception ex) {
                    // Silence is gold
                }
            } else {
                try {
                    File base = new File(classpathEntry + File.separatorChar + path);
                    for (File file : base.listFiles()) {
                        name = file.getName();
                        if (name.endsWith(".class")) {
                            name = name.substring(0, name.length() - 6);
                            classSet.add(Class.forName(prefix + "." + name));
                        }
                    }
                } catch (Exception ex) {
                    // Silence is gold
                }
            }
        }
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
            if (!dependencies.isEmpty()) throw new ImpossibleDependencyException("Detected Impossible Dependencies, failed to fill all sockets");
        });
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

        Thread starter = new Thread(new Starter(source, startMethod));

        starter.start();
    }

    private static void cleanUp() {
        wiringStations = null;
        plugs= null;

        classSet = null;
        dependencyMap = null;

        powers = null;
    }
}
