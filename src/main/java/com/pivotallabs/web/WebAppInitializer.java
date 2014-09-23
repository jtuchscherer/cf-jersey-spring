package com.pivotallabs.web;

import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        // Listeners
        servletContext.addListener(ContextLoaderListener.class);

        servletContext.setInitParameter(ContextLoader.CONTEXT_CLASS_PARAM,
                AnnotationConfigWebApplicationContext.class.getName());
        servletContext.setInitParameter(ContextLoader.CONFIG_LOCATION_PARAM, SpringConfig.class.getName());

        // Register Jersey 2.0 servlet
        ServletRegistration.Dynamic servletRegistration = servletContext.addServlet("java-starter",
                ServletContainer.class.getName());

        servletRegistration.addMapping("/rest/*");
        servletRegistration.setLoadOnStartup(1);
        servletRegistration.setInitParameter("javax.ws.rs.Application", UserApplication.class.getName());

        FilterRegistration.Dynamic filter = servletContext.addFilter("openSessionInViewFilter", OpenSessionInViewFilter.class);
        filter.setInitParameter("singleSession", "true");
        filter.addMappingForServletNames(null, true, "java-starter");
    }
}
