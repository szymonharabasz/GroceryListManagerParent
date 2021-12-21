import org.glassfish.embeddable.*;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Application {
    public static void main(String[] args) throws GlassFishException, IOException {
        BootstrapProperties bootstrap = new BootstrapProperties();
        GlassFishRuntime runtime = GlassFishRuntime.bootstrap(bootstrap);
        GlassFishProperties glassfishProperties = new GlassFishProperties();
        // glassfishProperties.setPort("http-listener", 8083);
        //    glassfishProperties.setPort("https-listener", 8184);
        glassfishProperties.setInstanceRoot("./domain1");
        glassfishProperties.setProperty("TEST_VARIABLE", "welcome");
        GlassFish glassfish = runtime.newGlassFish(glassfishProperties);
        if (args.length < 1) {
            throw new GlassFishException("Artifact to deploy has not been provided.");
        }
        File app = new File(args[0]);
        if (!app.exists()) {
            throw new GlassFishException("Could not find artifact to deploy");
        }
        BufferedReader reader = new BufferedReader(new FileReader("./system-properties"));
        String line = reader.readLine();
        reader.close();
        for (String prop : line.split(":")) {
            String[] keyVal = prop.split("=");
            System.setProperty(keyVal[0], keyVal[1]);
            System.err.println("PROPERTY SET " + keyVal[0] + " : " + keyVal[1]);
        }
            /*
            System.setProperty("TEST_VARIABLE","hello");
            System.setProperty("PUBLIC_CAPTCHA_KEY","6LdVDWUdAAAAAGI3BwhWi6lZHGjyWQnTdDEJ044s");
            System.setProperty("PRIVATE_CAPTCHA_KEY","6LdVDWUdAAAAAP60RBJ3xTsm2-h5fa0btmzZk9Ib");
            System.setProperty("EMAIL_PASSWORD","SmP_2005_glm");
*/
        System.setProperty("myprop", "dupa");
        glassfish.start();
        glassfish.getDeployer().deploy(app);

    }
}
