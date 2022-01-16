import org.glassfish.embeddable.*;

import java.io.*;

public class Application {
    public static void main(String[] args) throws GlassFishException, IOException {
        BootstrapProperties bootstrap = new BootstrapProperties();
        GlassFishRuntime runtime = GlassFishRuntime.bootstrap(bootstrap);
        GlassFishProperties glassfishProperties = new GlassFishProperties();
        // glassfishProperties.setPort("http-listener", 8083);
//        glassfishProperties.setInstanceRoot("./domain1");
        System.out.println("Setting http-listener posrt to " + Integer.valueOf(System.getProperty("server.port")));
        glassfishProperties.setPort("http-listener", Integer.valueOf(System.getProperty("server.port")));
        glassfishProperties.setProperty("TEST_VARIABLE", "welcome");
        GlassFish glassfish = runtime.newGlassFish(glassfishProperties);
        if (args.length < 1) {
            throw new GlassFishException("Artifact to deploy has not been provided.");
        }
        File app = new File(args[0]);
        if (!app.exists()) {
            throw new GlassFishException("Could not find artifact to deploy");
        }
        System.setProperty("myprop", "dupa");
        System.setProperty("HOST", System.getenv("HOST"));
        System.setProperty("MONGO_HOST", System.getenv("MONGO_HOST"));
        System.setProperty("TEST_VARIABLE", System.getenv("TEST_VARIABLE"));
        System.setProperty("PUBLIC_CAPTCHA_KEY", System.getenv("PUBLIC_CAPTCHA_KEY"));
        System.setProperty("PRIVATE_CAPTCHA_KEY", System.getenv("PRIVATE_CAPTCHA_KEY"));
        System.setProperty("EMAIL_PASSWORD", System.getenv("EMAIL_PASSWORD"));
        glassfish.start();
        glassfish.getDeployer().deploy(app);

    }
}
