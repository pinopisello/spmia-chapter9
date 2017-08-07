import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryClient.DiscoveryClientOptionalArgs;
import com.netflix.discovery.converters.wrappers.CodecWrappers;
import com.netflix.discovery.shared.transport.jersey.EurekaJerseyClient;
import com.netflix.discovery.shared.transport.jersey.EurekaJerseyClientImpl.EurekaJerseyClientBuilder;


@Configuration
public class SpringAppContextConfig {

	
	/*
	 * Server a configurare proxy per EurekaClient cosi che posso monitorare req/resp verso EurekaServer
	 */
	@Bean
	public DiscoveryClientOptionalArgs discoveryClientOptionalArgs(EurekaClientConfigBean clientConfig){	
	    DiscoveryClientOptionalArgs args = new DiscoveryClient.DiscoveryClientOptionalArgs();
	   
	    
	    EurekaJerseyClientBuilder clientBuilder = new EurekaJerseyClientBuilder()
	    		.withProxy("127.0.0.1", "9999", "", "")
                .withClientName("Proxy-DiscoveryClient-HTTPClient")
                .withUserAgent("Pippo")
                .withConnectionTimeout(clientConfig.getEurekaServerConnectTimeoutSeconds() * 1000 )
                .withReadTimeout(clientConfig.getEurekaServerReadTimeoutSeconds() * 1000)
                .withMaxConnectionsPerHost(clientConfig.getEurekaServerTotalConnectionsPerHost())
                .withMaxTotalConnections(clientConfig.getEurekaServerTotalConnections())
                .withConnectionIdleTimeout((int)clientConfig.getEurekaConnectionIdleTimeoutSeconds() * 1000)
                .withEncoderWrapper(CodecWrappers.getEncoder(clientConfig.getEncoderName()))
                .withDecoderWrapper(CodecWrappers.resolveDecoder( clientConfig.getDecoderName(), clientConfig.getClientDataAccept()) );


        EurekaJerseyClient jerseyClient = clientBuilder.build();
	    args.setEurekaJerseyClient(jerseyClient);
	    return args;
	}




	

}
