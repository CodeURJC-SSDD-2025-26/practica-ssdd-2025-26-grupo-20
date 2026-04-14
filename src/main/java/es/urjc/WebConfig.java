package es.urjc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import es.urjc.security.CsrfHandlerInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private CsrfHandlerInterceptor csrfHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(csrfHandlerInterceptor);
    }
}