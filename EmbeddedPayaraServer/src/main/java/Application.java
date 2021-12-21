import org.glassfish.embeddable.*;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Application {
    public static void main(String [] args)
    {
        try {
            BootstrapProperties bootstrap = new BootstrapProperties();
            GlassFishRuntime runtime = GlassFishRuntime.bootstrap(bootstrap);
            GlassFishProperties glassfishProperties = new GlassFishProperties();
            glassfishProperties.setPort("http-listener", 8083);
            glassfishProperties.setPort("https-listener", 8184);
            GlassFish glassfish = runtime.newGlassFish(glassfishProperties);
            glassfish.start();
            glassfish.getDeployer().deploy(
                   new File("GroceryListManager-1.0-SNAPSHOT.war"));
        }
        catch (GlassFishException ex)
        {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }
}
