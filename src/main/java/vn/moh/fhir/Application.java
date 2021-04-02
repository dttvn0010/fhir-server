package vn.moh.fhir;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    
	@Autowired private FhirRestfulServlet fhirRestfulServlet;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
    public ServletRegistrationBean<?> servletRegistrationBean() {
        var registration = new ServletRegistrationBean<>(fhirRestfulServlet, "/R4/*");
        registration.setName("FhirServlet");
        return registration;
    }
}
