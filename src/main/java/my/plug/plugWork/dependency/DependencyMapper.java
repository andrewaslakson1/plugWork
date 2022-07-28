package my.plug.plugWork.dependency;

import my.plug.plugWork.annotation.PowerPlug;
import my.plug.plugWork.annotation.Socket;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DependencyMapper {

    public Map<String, Map<String, Field>> retrieveDependencyMap(Set<Class> classSet) {
        Map<String, Map<String, Field>> dependencyMap = new HashMap<>();

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

        return dependencyMap;
    }
}
