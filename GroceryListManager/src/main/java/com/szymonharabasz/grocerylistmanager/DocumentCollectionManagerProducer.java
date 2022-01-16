package com.szymonharabasz.grocerylistmanager;

import org.jnosql.diana.api.Settings;
import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentCollectionManagerFactory;
import org.jnosql.diana.api.document.DocumentConfiguration;
import org.jnosql.diana.mongodb.document.MongoDBDocumentConfiguration;

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
        String mongoHost = System.getProperty("MONGO_HOST", "localhost:27017");
        System.err.println("MONGO_HOST " + mongoHost);
        Map<String, Object> settings = Collections.singletonMap("mongodb.url", mongoHost);
        managerFactory = configuration.get(Settings.of(settings));
    }


    @Produces
    public DocumentCollectionManager getManager() {
        return managerFactory.get(COLLECTION);

    }

}
