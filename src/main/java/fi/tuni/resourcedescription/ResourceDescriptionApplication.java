package fi.tuni.resourcedescription;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@SecurityScheme(
	name = "jwt-api",
	type = SecuritySchemeType.HTTP,
	bearerFormat = "JWT",
	scheme = "bearer",
	in = SecuritySchemeIn.HEADER)
public class ResourceDescriptionApplication {
	@Value("${api-docs.server.localhost}")
	private String localhostUrl;

	@Value("${api-docs.server.stage}")
	private String stageUrl;

	@Value("${api-docs.server.prod}")
	private String prodUrl;

	public static void main(String[] args) {
		SpringApplication.run(ResourceDescriptionApplication.class, args);
	}

	@Bean
	public OpenAPI customOpenAPI() {
		OpenAPI apiDocModel = new OpenAPI()
			.info(
				new Info()
					.title("API Documentation")
					.version("1.0")
					.description("The client-server API provides a API to work with xml resource files.")
			);

		if (StringUtils.isNotBlank(localhostUrl)) {
			apiDocModel.addServersItem(
				new Server()
					.url(localhostUrl)
					.description("Localhost")
			);
		}

		if (StringUtils.isNotBlank(stageUrl)) {
			apiDocModel.addServersItem(
				new Server()
					.url(stageUrl)
					.description("Stage Env")
			);
		}

		if (StringUtils.isNotBlank(prodUrl)) {
			apiDocModel.addServersItem(
				new Server()
					.url(prodUrl)
					.description("Prod Env")
			);
		}
		return apiDocModel;
	}
}
