package com.pivotallabs.web;

import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class WebAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        XmlWebApplicationContext appContext = new XmlWebApplicationContext();
        String[] locations = {"classpath*:application-config.xml"};
        appContext.setConfigLocations(locations);

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(appContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");

        ServletRegistration.Dynamic jersey = servletContext.addServlet("java-starter", SpringServlet.class);
        jersey.setInitParameter("com.sun.jersey.config.property.packages", "com.pivotallabs");
        jersey.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
        jersey.setLoadOnStartup(2);
        jersey.addMapping("/rest/*");

        FilterRegistration.Dynamic filter = servletContext.addFilter("openSessionInViewFilter", OpenSessionInViewFilter.class);
        filter.setInitParameter("singleSession", "true");
        filter.addMappingForServletNames(null, true, "dispatcher");
        filter.addMappingForServletNames(null, true, "java-starter");

        servletContext.addListener(new ContextLoaderListener(appContext));
    }
}
