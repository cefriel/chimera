package it.cefriel.chimera.reflect;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.topbraid.jenax.util.JenaUtil;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

public class Java2Shacl {
    private static String header=String.join("\n",
            "#baseURI: {0}",
            "#imports: http://datashapes.org/dash",
            "#prefix: c",
            "@prefix dash: <http://datashapes.org/dash#> .",
            "@prefix c: <{0}> .",
            "@prefix owl: <http://www.w3.org/2002/07/owl#> .",
            "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .",
            "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .",
            "@prefix sh: <http://www.w3.org/ns/shacl#> .",
            "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .",
            ""
            );
    private static String attribute_def=String.join("\n",
            "    sh:property [",
            "      sh:path c:{0};",
            "      sh:datatype {1} ;",
            "      sh:name \"{0}\" ;",
            "    ] ;"
            );

    private static String class_def=String.join("\n",
            "c:{0}",
            "    rdf:type rdfs:Class ;",
            "    rdfs:subClassOf rdfs:Resource ;",
            ".",
            "",
            "c:{0}Shape",
            "    a sh:NodeShape ;",
            "    sh:targetClass c:{0} ;",
            "    {1}",
            ".");

    private static Map<String, String> xsd_mappings=null;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("byte", "xsd:byte");
        aMap.put("byte[]", "xsd:byte");
        aMap.put("java.lang.Integer", "xsd:int");
        aMap.put("int", "xsd:int");
        aMap.put("int[]", "xsd:int");
        aMap.put("java.lang.BigInteger", "xsd:integer");
        aMap.put("double[]", "xsd:integer");
        aMap.put("double", "xsd:integer");
        aMap.put("long", "xsd:integer");
        aMap.put("long[]", "xsd:integer");
        aMap.put("char", "xsd:string");
        aMap.put("char[]", "xsd:string");
        aMap.put("java.lang.String", "xsd:string");
        aMap.put("java.lang.Float", "xsd:float");
        aMap.put("java.util.Date", "xsd:date");
        aMap.put("javax.xml.bind.JAXBElement<?>", "rdf:Resource");
        aMap.put("Object[]", "rdf:Resource");
        aMap.put("javax.xml.datatype.XMLGregorianCalendar", "xsd:datetime");
        xsd_mappings = Collections.unmodifiableMap(aMap);
    }

    private static String prefix="c:";

    @SuppressWarnings("rawtypes")
    public static String convert(Collection<Class> classes, String namespace){
        String attribute_name=null;
        String attribute_type=null;
        String res="";

        res=MessageFormat.format(header, namespace);
        for (Class c: classes) {
            if (! c.isEnum()) {
                String attributes_repr="";
                Field[] attributes = c.getDeclaredFields();

                if ((c.getSimpleName()!=null)&&(! c.getSimpleName().equals(""))) {
                    for (Field f: attributes) {
                        attribute_name=f.getName();
                        attribute_type=getSuitableType(f);
                        //System.out.print("Attribute name: " + attribute_name+" | ");

                        attributes_repr=String.join("\n", attributes_repr, MessageFormat.format(attribute_def, attribute_name, attribute_type));
                    }

                    res=String.join("\n", res, MessageFormat.format(class_def, c.getSimpleName(), attributes_repr) );
                }
            }
        }        

        return res;
    }

    private static String getSuitableType(Field f) {
        String attribute_type=null;
        Type type = f.getGenericType();

        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            for (Type t : pt.getActualTypeArguments()) {
                attribute_type=t.getTypeName();

                //System.out.println("Type: " + attribute_type);
                if (xsd_mappings.containsKey(attribute_type)) {
                    return xsd_mappings.get(attribute_type);
                } else {
                    attribute_type=attribute_type.substring(attribute_type.lastIndexOf(".")+1).replaceFirst(">", "");
                    //System.out.println("Parametrized type: " + attribute_type);
                    if (xsd_mappings.containsKey(attribute_type)) {
                        return xsd_mappings.get(attribute_type);
                    }                    
                    else if (attribute_type.contains("$")) {
                        attribute_type=attribute_type.substring(attribute_type.lastIndexOf("$")+1);
                    }
                    return prefix+attribute_type;
                }
            }
        }

        if (attribute_type==null) {
            attribute_type=prefix+f.getType().getSimpleName();
            if (f.getType().isEnum()) {
                attribute_type="xsd:string";
            }
            //System.out.println("Simple type: " + attribute_type);
            if (xsd_mappings.containsKey(f.getType().getCanonicalName())) {
                attribute_type=xsd_mappings.get(f.getType().getCanonicalName());
            }
        }
        return attribute_type;
    }

    public static String create_model(String package_name, String prefix) {
        String result=null;
        List <Class> classes=new ArrayList<Class>();
        List<String> classNames = new FastClasspathScanner(package_name)
                .scan()
                .getNamesOfAllStandardClasses();
        try {
            for (String c:classNames) {
                classes.add(Class.forName(c));
            }
            result= convert( classes, prefix);

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }


    public static List<String> beanProperties(Class beanClass) {
        try {
            List<PropertyDescriptor> descriptors = Arrays.asList(Introspector.getBeanInfo(beanClass, Object.class).getPropertyDescriptors());
            List<String> res=new ArrayList<String>();
            for (PropertyDescriptor p:descriptors) {
                res.add(p.getName());
            }
            return res;
        } catch (IntrospectionException e) {
            // and this, too
            return Collections.emptyList();
        }
    }

    public static Model packageToShacle(String package_name, String prefix) {
        List <Class> classes=new ArrayList<Class>();
        Model model = JenaUtil.createMemoryModel();

        String pkg=null;
        String shacl_output = null;

        pkg=package_name.replaceAll("package:", "").trim();
        
        List<String> classNames = new FastClasspathScanner(pkg)
                .scan()
                .getNamesOfAllClasses();
        try {
            for (String c:classNames) {
                classes.add(Class.forName(c));
            }

            shacl_output=Java2Shacl.convert( classes, prefix);
            model.read(new ByteArrayInputStream(shacl_output.getBytes()), null, "TURTLE");
        }
        catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return model;
    }
    
}
