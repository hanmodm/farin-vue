package farin.vue.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class FarinVueApplication {

	public static void main(String[] args) {
    	SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(FarinVueApplication.class); 
    	springApplicationBuilder.properties("spring.config.location=" + "classpath:/application.yml" );
    	SpringApplication springApplication = springApplicationBuilder.build(); 
    	springApplication.run(args);
	}
}
