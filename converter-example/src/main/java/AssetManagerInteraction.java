import org.apache.camel.CamelContext;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AssetManagerInteraction {

	public static void main(String[] args) throws Exception { 
		ApplicationContext appContext = new ClassPathXmlApplicationContext("asset_manager.xml");
		CamelContext camelContext = SpringCamelContext.springCamelContext(appContext, false);
//		try {            
			camelContext.start();
	/*	} finally {
			camelContext.stop();
		}
*/
	}

}