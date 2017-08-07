import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

   @Order(0)
   @Configuration
   public class SpringBootContextConfig  {

	   /**
	    * 
	    * Setta proxy 127.0.0.1 9999 per le request verso configserver!
	    * 
	    */
			   
	   
       @Autowired
       private Environment environment;

       @Autowired
       ConfigClientProperties configClientProperties;

       
       
       @Primary
       @Bean
       public ConfigServicePropertySourceLocator configServicePropertySourceLocator() {
           ConfigClientProperties clientProperties = configClientProperties;
           ConfigServicePropertySourceLocator sourceLocator = new ConfigServicePropertySourceLocator(clientProperties);
           sourceLocator.setRestTemplate(getSecureRestTemplate(clientProperties));
           return sourceLocator;
       }
       
       private RestTemplate getSecureRestTemplate(ConfigClientProperties client) {
           SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
           requestFactory.setReadTimeout((60 * 1000 * 3) + 5000); // TODO 3m5s, make configurable?
           Proxy proxy= new Proxy(Type.HTTP, new InetSocketAddress("127.0.0.1", 9999));
           requestFactory.setProxy(proxy);
           RestTemplate template = new RestTemplate(requestFactory);
           return template;
       }
   }
