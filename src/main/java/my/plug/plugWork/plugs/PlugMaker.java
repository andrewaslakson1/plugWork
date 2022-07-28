package my.plug.plugWork.plugs;

import my.plug.plugWork.annotation.Plug;
import my.plug.plugWork.annotation.PowerPlug;
import my.plug.plugWork.exception.ImpossibleDependencyException;
import my.plug.plugWork.exception.InstantiationException;
import my.plug.plugWork.wiringStation.WiringStation;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class PlugMaker {

    public Map<String, Object> retrieveWiringStationPlugs(Set<WiringStation> wiringStations) {
        Map<String,Object> plugs = new HashMap<>();

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

        return plugs;
    }

    public Map<String, Object> retrievePowerPlugs(
            Set<Class> classSet,
            Map<String, Map<String, Field>> dependencyMap,
            Map<String, Object> plugs
    ) {
        Map<String, Object> powerPlugs = new HashMap<>();

        classSet.stream().filter(clazz -> clazz.isAnnotationPresent(PowerPlug.class)).forEach(clazz -> {
            try {
                String name = ((PowerPlug)clazz.getAnnotation(PowerPlug.class)).name();
                if (name.equals("")) name = clazz.getName();
                Object power = clazz.getDeclaredConstructor().newInstance();
                powerPlugs.put(name, power);
            } catch (Exception e) {
                e.printStackTrace();
                throw new InstantiationException("Failed to instantiate Power Plugs.");
            }
        });

        AtomicReference<Map<String, Field>> foundDependencies = new AtomicReference<>();
        foundDependencies.set(new HashMap<>());

        powerPlugs.forEach((name, power) -> {
            Map<String, Field> dependencies = dependencyMap.get(name);

            dependencies.forEach((socketName, socket) -> {
                try {
                    if (plugs.containsKey(socketName)) {
                        socket.set(power, plugs.get(socketName));
                        foundDependencies.get().put(socketName, socket);
                    } else if (powerPlugs.containsKey(socketName)) {
                        socket.set(power, powerPlugs.get(socketName));
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

        return powerPlugs;
    }
}
