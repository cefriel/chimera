package it.cefriel.chimera.parser;

import java.lang.annotation.Annotation;
import java.util.List;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

public class St4rtParser {

    public String toShaclMapping(String package_name) {
        List<String> classNames = new FastClasspathScanner(package_name)
                .scan()
                .getNamesOfAllClasses();

        try {
            for (String cls_name: classNames) {
                Class cls=Class.forName(cls_name);
                Annotation[] annotations = cls.getAnnotations();
                /*
                for (Annotation a: annotations) {
                    if (a instanceof Link) {

                    }
                    else if (a instanceof NamedGraph) {

                    }
                    else if (a instanceof Queries) {

                    }
                    else if (a instanceof RdfsClass) {

                    }
                    else if (a instanceof RdfProperty) {

                    }
                    else if (a instanceof NamedGraph) {

                    }
                }*/
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return package_name;

    }
}
