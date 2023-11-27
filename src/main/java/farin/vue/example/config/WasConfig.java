package farin.vue.example.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ajp.AjpNioProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class WasConfig {

	private static final Logger LOG = LoggerFactory.getLogger(WasConfig.class);
	
	@Value("${server.tomcat.ajp.protocol}")
    String ajpProtocol;
	
	@Value("${server.tomcat.ajp.port}")
    int ajpPort;
	
	@Value("${server.tomcat.ajp.enabled}")
    boolean tomcatAjpEnabled;
	
	@Bean
	public ServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
		if (tomcatAjpEnabled) {
			tomcat.addAdditionalTomcatConnectors(createAjpConnector());
		}
		return tomcat;
	} 
	
	@Bean
	public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
	   return beanFactory -> {
	      BeanDefinition bean =
	         beanFactory.getBeanDefinition(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME);
	      bean.getPropertyValues().add("loadOnStartup", 1);
	   };
	}

	private Connector createAjpConnector() {
		Connector 		ajpConnector 	= new Connector(ajpProtocol);
		AjpNioProtocol 	protocol 		= (AjpNioProtocol) 	ajpConnector.getProtocolHandler();
		
		ajpConnector.setPort(ajpPort);
		ajpConnector.setSecure(false);
		ajpConnector.setAllowTrace(false);

		protocol.setSecretRequired(false); 

		ajpConnector.setScheme("http");

        try{
            protocol.setAddress( InetAddress.getByName( "0.0.0.0" ) );
        }catch( UnknownHostException e ){
        	LOG.info("TomcatConfiguration.createAjpConnector UnknownHostException ::: " + e.getMessage());
        }catch (Exception e) {
        	throw new RuntimeException(e); 
        }
        
		return ajpConnector;
	}
	
	@Bean
	public SessionLocaleResolver localeResolver() {
		return new SessionLocaleResolver();
	}
	
	/*
	 * @Bean public ExceptionManager exceptionManager() { return new
	 * ExceptionManager(); }
	 */
	
	@Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(5000);
        restTemplate.setRequestFactory(requestFactory);
        
        List<HttpMessageConverter<?>> converterList = new ArrayList<HttpMessageConverter<?>>();
        HttpMessageConverter<?> jacksonConverter = new MappingJackson2HttpMessageConverter();
        converterList.add(jacksonConverter);
        restTemplate.setMessageConverters(converterList);
        
        return restTemplate;
    }
}
