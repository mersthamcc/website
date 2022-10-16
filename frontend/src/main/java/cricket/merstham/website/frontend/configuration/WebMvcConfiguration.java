package cricket.merstham.website.frontend.configuration;

import cricket.merstham.website.frontend.formatters.InstantFormatFactory;
import cricket.merstham.website.frontend.formatters.LocalDateTimeDateOnlyFormatFactory;
import cricket.merstham.website.frontend.formatters.LocalDateTimeFormatFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.method.annotation.OAuth2AuthorizedClientArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    private final ViewConfiguration viewConfiguration;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizedClientRepository authorizedClientRepository;

    @Autowired
    public WebMvcConfiguration(
            ViewConfiguration viewConfiguration,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {
        this.viewConfiguration = viewConfiguration;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizedClientRepository = authorizedClientRepository;
    }

    @Bean
    @Override
    public LocaleResolver localeResolver() {
        var slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.UK);
        return slr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        var lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Bean
    public OAuth2AuthorizedClientArgumentResolver auth2AuthorizedClientArgumentResolver() {
        return new OAuth2AuthorizedClientArgumentResolver(
                clientRegistrationRepository, authorizedClientRepository);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(viewConfiguration);
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        super.addFormatters(registry);
        registry.addFormatterForFieldAnnotation(new LocalDateTimeFormatFactory());
        registry.addFormatterForFieldAnnotation(new LocalDateTimeDateOnlyFormatFactory());
        registry.addFormatterForFieldAnnotation(new InstantFormatFactory());
    }

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
        argumentResolvers.add(auth2AuthorizedClientArgumentResolver());
    }
}
