/**
 */
package org.example.activemq.orbweaver.cc;

import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;


/**
 * JAX-RS ControlCenter root resource
 */
public class ControlCenterServer {
    private static Server server;

    protected ControlCenterServer() throws Exception {
        ControlCenterApp application = new ControlCenterApp();
        RuntimeDelegate delegate = RuntimeDelegate.getInstance();

        JAXRSServerFactoryBean bean = delegate.createEndpoint(application, JAXRSServerFactoryBean.class);
        bean.setAddress("http://localhost:9000/services" + bean.getAddress());
        server = bean.create();
        server.start();
    }

    public static void main(String args[]) throws Exception {
        new ControlCenterServer();
        System.out.println("Server ready...");

        Thread.sleep(125 * 60 * 1000);
        System.out.println("Server exiting");
        server.stop();
        server.destroy();
        System.exit(0);
    }
}
