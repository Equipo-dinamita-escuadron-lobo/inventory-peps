package kardex.PEPS.InventoryPEPS.infrastructure.adapters.config.i18n;

import java.util.Locale;


import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * @brief Configuration for application internationalization (i18n)
 * 
 * Sets up message sources, locale resolution strategies, and interceptors
 * to handle multiple languages.
 */
@Configuration
public class InternationalizationConfig implements WebMvcConfigurer {
    /**
     * @brief Configures the MessageSource for loading message files
     * 
     * Sets the base name to "messages", encoding to UTF-8, and cache duration.
     * @return The configured MessageSource
    */
    @Bean
     MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600); // Cache por 1 hora
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setDefaultLocale(Locale.ENGLISH); // Idioma por defecto
        return messageSource;
    }

    /**
     * @brief Configures the LocaleResolver to determine the current locale
     * 
     * Uses SessionLocaleResolver to store the locale in the user's session.
     * Defaults to English.
     * @return The configured LocaleResolver
     */
    @Bean
    LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        return localeResolver;
    }

    /**
     * @brief Creates an interceptor to change the locale based on a request parameter
     * 
     * Allows changing the language using the "lang" parameter (e.g., ?lang=es).
     * @return The configured LocaleChangeInterceptor
    */
    @Bean
    LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang"); // ?lang=es para cambiar a español
        return interceptor;
    }
     /**
     * @brief Registers the locale change interceptor
     * 
     * Adds the interceptor to the registry so it can process requests.
     * @param registry The InterceptorRegistry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }


    
}
