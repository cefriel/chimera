import org.apache.camel.CamelContext;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConverterApplication {

	public static void main(String[] args) throws Exception { 
		ApplicationContext appContext = new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
		CamelContext camelContext = new SpringCamelContext(appContext);
//		try {            
			camelContext.start();
/*
		} finally {
			camelContext.stop();
		}
*/
	}

}