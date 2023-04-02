package fi.tuni.resourcedescription.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CachingConfig {
  // Leave it empty to use the default CacheManager
  // Implement it if devs want to try RedisCache for example.
}
