package com.szymonharabasz.grocerylistmanager;

import jakarta.nosql.Settings;
import jakarta.nosql.document.DocumentCollectionManager;
import jakarta.nosql.document.DocumentCollectionManagerFactory;
import jakarta.nosql.document.DocumentConfiguration;
import org.eclipse.jnosql.communication.mongodb.document.MongoDBDocumentConfiguration;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Collections;
import java.util.Map;

@ApplicationScoped
public class DocumentCollectionManagerProducer {

    private static final String COLLECTION = "groceries";

    private DocumentConfiguration configuration;

    private DocumentCollectionManagerFactory managerFactory;

    @PostConstruct
    public void init() {
        configuration = new MongoDBDocumentConfiguration();
        String mongoHost = System.getProperty("MONGO_HOST", "localhost");
        Map<String, Object> settings = Collections.singletonMap("mongodb-server-host-1", mongoHost + ":27017");
        managerFactory = configuration.get(Settings.of(settings));
    }


    @Produces
    public DocumentCollectionManager getManager() {
        return managerFactory.get(COLLECTION);

    }

}
