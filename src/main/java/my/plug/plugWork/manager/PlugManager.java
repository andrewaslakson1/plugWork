package my.plug.plugWork.manager;

import my.plug.plugWork.annotation.EnablePlugging;
import my.plug.plugWork.annotation.Plug;
import my.plug.plugWork.annotation.Socket;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Method;
import java.util.*;

public class PlugManager {

    public static Map<String, Object> plugs;

    public static void wire(String prefix) {
        plugs = new HashMap<>();

        Map<Class, List<Class>> dependencyMap = new HashMap<>();
        Map<Class, Method> plugBuilderMap = new HashMap<>();

        getDependencies(prefix, dependencyMap, plugBuilderMap);
    }

    private static void getDependencies(String prefix, Map<Class, List<Class>> dependencyMap, Map<Class, Method> plugBuilderMap) {
        Set<Class> classSet = new HashSet<>(
                new Reflections(prefix, new SubTypesScanner(false))
                        .getSubTypesOf(Object.class));

        classSet.stream().filter(clazz -> clazz.isAnnotationPresent(EnablePlugging.class)).forEach(clazz -> {
            List<Class> dependencies = new ArrayList<>();
            Arrays.stream(clazz.getFields()).forEach(field -> {
                if (field.isAnnotationPresent(Socket.class)) {
                    dependencies.add(field.getType());
                }
            });
            dependencyMap.put(clazz, dependencies);

            //TODO: move outside and use @PlugConfig to look for these
            Arrays.stream(clazz.getMethods()).forEach(method -> {
                if (method.isAnnotationPresent(Plug.class)) plugBuilderMap.put(method.getReturnType(), method);
            });
        });
        String yum = "yum";
    }


}
