/**
 */
package org.example.activemq.orbweaver.cc;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.apache.cxf.jaxrs.provider.json.JSONProvider;


/*
 * Class that can be used (instead of XML-based configuration) to inform the JAX-RS
 * runtime about the resources and providers it is supposed to deploy.  See the
 * ApplicationServer class for more information.
 */
@ApplicationPath("/cc")
public class ControlCenterApp extends Application {

	@Override
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>();
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> classes = new HashSet<Object>();

        ControlCenterService personService = new ControlCenterService();
        classes.add(personService);

        // custom providers
        JSONProvider<?> provider = new JSONProvider<Object>();
        provider.setIgnoreNamespaces(true);
        classes.add(provider);

        return classes;
    }
}
