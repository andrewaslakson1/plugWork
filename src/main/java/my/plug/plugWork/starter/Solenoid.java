package my.plug.plugWork.starter;

import my.plug.plugWork.annotation.PowerPlug;
import my.plug.plugWork.annotation.Source;
import my.plug.plugWork.annotation.Start;
import my.plug.plugWork.exception.PlugWorkConfigurationException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Solenoid {

    public void contact(Set<Class> classSet, Map<String, Object> powerPlugs) {
        List<Class> sourceClasses = classSet.stream().filter(clazz -> clazz.isAnnotationPresent(Source.class)).collect(Collectors.toList());
        if (sourceClasses.size() == 0 || sourceClasses.size() > 1)
            throw new PlugWorkConfigurationException("1 and only 1 class may be annotated with source, found: " + sourceClasses.size());

        String name = ((PowerPlug)sourceClasses.get(0).getAnnotation(PowerPlug.class)).name();
        if (name.equals("")) name = sourceClasses.get(0).getName();
        Object source = powerPlugs.get(name);

        List<Method> startMethods = Arrays.stream(sourceClasses.get(0).getDeclaredMethods()).filter(method -> method.isAnnotationPresent(Start.class)).collect(Collectors.toList());
        if (startMethods.size() != 1)
            throw new PlugWorkConfigurationException("1 and only 1 method may be marked as start method in source class, found: " + startMethods.size());

        Method startMethod = startMethods.get(0);

        Thread starter = new Thread(new Starter(source, startMethod));

        starter.start();
    }
}
