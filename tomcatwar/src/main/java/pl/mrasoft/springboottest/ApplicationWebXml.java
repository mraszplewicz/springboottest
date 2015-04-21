package pl.mrasoft.springboottest;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.context.web.SpringBootServletInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class ApplicationWebXml extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        super.onStartup(container);

        ServletRegistration.Dynamic registration = (ServletRegistration.Dynamic) container.getServletRegistration(EmbeddedWebApplicationContext.DISPATCHER_SERVLET_NAME);
        registration.setLoadOnStartup(1);
        registration.addMapping("/*");
    }

}
