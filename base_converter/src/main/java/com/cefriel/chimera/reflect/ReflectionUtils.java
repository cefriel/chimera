package it.cefriel.chimera.reflect;

import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {
    
    public static List<Class> getSuperClasses(Class cl) {
        List<Class> classList = new ArrayList<Class>();
        Class superclass = cl.getSuperclass();
        if (superclass!=null) {
            classList.add(superclass);
            while (superclass != null) {   
                cl = superclass;
                superclass = cl.getSuperclass();
                classList.add(superclass);
            }
        }
        return classList;
    }
}
