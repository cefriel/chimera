package org.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import it.cefriel.chimera.reflect.Java2Shacl;

public class Class2ShaclTest {

    @SuppressWarnings("rawtypes")
    @Test
    public void createShaclFsm() {
        //Class [] input={Polygon.class, Rectangle.class};
        
        List <Class> classes=new ArrayList<Class>();
        List<String> classNames = new FastClasspathScanner("eu.st4rt.standards.fsm")
                .scan()
                .getNamesOfAllClasses();
        try {
            for (String c:classNames) {
                classes.add(Class.forName(c));
            }
        
            FileOutputStream output=new FileOutputStream("/tmp/fsm.shacl");
            output.write(Java2Shacl.convert( classes, "http://st4rt.eu/ontologies/fsm#").getBytes());
            output.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    
    @SuppressWarnings("rawtypes")
    @Test
    public void createShaclGtfs() {
        //Class [] input={Polygon.class, Rectangle.class};
        
        List <Class> classes=new ArrayList<Class>();
        List<String> classNames = new FastClasspathScanner("it.cefriel.gtfs.model")
                .scan()
                .getNamesOfAllClasses();
        try {
            for (String c:classNames) {
                classes.add(Class.forName(c));
            }
        
            FileOutputStream output=new FileOutputStream("/tmp/gtfs.shacl");
            output.write(Java2Shacl.convert( classes, "http://st4rt.eu/ontologies/gtfs#").getBytes());
            output.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("rawtypes")
    @Test
    public void createShaclNetex() {
        //Class [] input={Polygon.class, Rectangle.class};
        
        List <Class> classes=new ArrayList<Class>();
        List<String> classNames = new FastClasspathScanner("org.rutebanken.netex.model")
                .scan()
                .getNamesOfAllClasses();
        try {
            for (String c:classNames) {
                classes.add(Class.forName(c));
            }
        
            FileOutputStream output=new FileOutputStream("/tmp/netex.shacl");
            output.write(Java2Shacl.convert( classes, "http://st4rt.eu/ontologies/netex#").getBytes());
            output.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
