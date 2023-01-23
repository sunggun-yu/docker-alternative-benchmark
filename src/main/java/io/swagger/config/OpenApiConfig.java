package io.swagger.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {

		Contact defaultContact = new Contact();
		defaultContact.setName("Sample Swagger");
		return new OpenAPI()
				.components(new Components())
				.info(new Info().title("Sample Swagger API")
						.description("Sample Swagger")
						.contact(defaultContact)
						.version("1.0.0"));
	}
}
