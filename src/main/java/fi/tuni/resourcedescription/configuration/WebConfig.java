package fi.tuni.resourcedescription.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.servlet.view.xslt.XsltView;
import org.springframework.web.servlet.view.xslt.XsltViewResolver;

@EnableWebMvc
@Configuration
@ComponentScan({"fi.tuni.resourcedescription"})
public class WebConfig implements WebMvcConfigurer {
  private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
    "classpath:/static/",
    "classpath:/git-resources/"
  };

  private static final String[] CLASSPATH_FTL_TEMPLATES_LOCATIONS = {
    "classpath:/templates/freemarker/",
    "classpath:/templates/freemarker/intra/",
    "classpath:/templates/freemarker/intra/user",
    "classpath:/templates/freemarker/admin",
    "classpath:/templates/freemarker/admin/company",
    "classpath:/templates/freemarker/admin/role",
    "classpath:/templates/freemarker/admin/user",
  };

  private static final String CLASSPATH_XSLT_TEMPLATES_LOCATIONS = "classpath:/templates/xslt/";

  @Bean
  public FreeMarkerViewResolver freeMarkerViewResolver() {
    FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
    resolver.setCache(true);
    resolver.setSuffix(".ftl");
    return resolver;
  }

  @Bean
  public FreeMarkerConfigurer freeMarkerConfig() {
    FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
    freeMarkerConfigurer.setTemplateLoaderPaths(CLASSPATH_FTL_TEMPLATES_LOCATIONS);
    return freeMarkerConfigurer;
  }

  @Bean
  public XsltViewResolver getXSLTViewResolver() {
    XsltViewResolver xsltViewResolver = new XsltViewResolver();
    xsltViewResolver.setOrder(1);
    xsltViewResolver.setSourceKey("xmlSource");
    xsltViewResolver.setViewClass(XsltView.class);

    xsltViewResolver.setViewNames("RD_FormatterRDDoc", "Repo_HTML_EmplRepo", "Repo_HTML_BPRepo");
    xsltViewResolver.setPrefix(CLASSPATH_XSLT_TEMPLATES_LOCATIONS);
    xsltViewResolver.setSuffix(".xsl");

    return xsltViewResolver;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**")
      .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOrigins("http://localhost:3000", "http://localhost:3001")
      .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
      .allowedHeaders("*")
      .allowCredentials(true);
    WebMvcConfigurer.super.addCorsMappings(registry);
  }
}
