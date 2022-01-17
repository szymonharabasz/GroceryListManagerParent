package com.szymonharabasz.grocerylistmanager.functional;

import com.szymonharabasz.grocerylistmanager.ListsController;
import com.szymonharabasz.grocerylistmanager.LoginBacking;
import com.szymonharabasz.grocerylistmanager.RegisterBacking;
import com.szymonharabasz.grocerylistmanager.domain.Salt;
import com.szymonharabasz.grocerylistmanager.domain.SaltRepository;
import com.szymonharabasz.grocerylistmanager.domain.User;
import com.szymonharabasz.grocerylistmanager.domain.UserRepository;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jnosql.artemis.Database;
import org.jnosql.artemis.DatabaseType;
import org.jnosql.diana.api.Settings;
import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentCollectionManagerFactory;
import org.jnosql.diana.api.document.DocumentConfiguration;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.mongodb.document.MongoDBDocumentConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.Package;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;

import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.select;
import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.delete;
@RunWith(Arquillian.class)
public class ListsControllerTest {

    @Inject
    @Database(DatabaseType.DOCUMENT)
    UserRepository userRepository;

    @Inject
    @Database(DatabaseType.DOCUMENT)
    SaltRepository saltRepository;

    @Inject
    ListsController listsController;

    @Inject
    RegisterBacking registerBacking;

    @Inject
    LoginBacking loginBacking;

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class);

        File pom = new File("pom.xml");
        System.err.println("pom.exists = " + pom.exists());
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeDependencies()
                .resolve().withTransitivity().asFile();

        archive.addAsLibraries(libs);
        Package mainPackage = ListsController.class.getPackage();
        archive.addPackages(true, Filters.exclude(".*Test.*"), mainPackage);
        archive.addAsResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
        return archive;
    }

    void clearTestRepositories() {
        Map<String, Object> map = new HashMap<>();
        map.put("mongodb-server-host-1", "localhost:27017");
        DocumentConfiguration configuration = new MongoDBDocumentConfiguration();
        DocumentCollectionManagerFactory managerFactory = configuration.get(Settings.of(map));
        DocumentCollectionManager manager = managerFactory.get("groceries");
        DocumentDeleteQuery deleteUsersQuery = delete().from("User").where("_id").not().eq("").build();
        DocumentDeleteQuery deleteSaltsQuery = delete().from("Salt").where("_id").not().eq("").build();
        manager.delete(deleteUsersQuery);
        manager.delete(deleteSaltsQuery);
    }

    @Before
    public void registerAndLoginUser() {
        clearTestRepositories();

        registerBacking.setUsername("User");
        registerBacking.setPassword("pwd");
        registerBacking.setRepeatPassword("pwd");
        registerBacking.setEmail("user@example.com");
        registerBacking.register();

        loginBacking.setUsername("User");
        loginBacking.setPassword("pwd");
        loginBacking.handleLogin();
    }

    @Test
    public void twoTimesTwoEqualsFour() {
        assertEquals(4, 2*2);
    }

    @Test
    public void listCanBeAddedForUser() {
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return "User";
            }
        };
    }

}