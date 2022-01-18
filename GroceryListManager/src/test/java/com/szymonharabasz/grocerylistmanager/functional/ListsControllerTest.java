package com.szymonharabasz.grocerylistmanager.functional;

import com.szymonharabasz.grocerylistmanager.ListsController;
import com.szymonharabasz.grocerylistmanager.LoginBacking;
import com.szymonharabasz.grocerylistmanager.RegisterBacking;
import com.szymonharabasz.grocerylistmanager.domain.SaltRepository;
import com.szymonharabasz.grocerylistmanager.domain.UserRepository;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jnosql.artemis.Database;
import org.jnosql.artemis.DatabaseType;
import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.lang.Package;
import java.net.URL;

import javax.inject.Inject;

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
    DocumentCollectionManager collectionManager;

    @Inject
    ListsController listsController;

    @Inject
    RegisterBacking registerBacking;

    @Inject
    LoginBacking loginBacking;

    @Drone
    WebDriver browser;

    @ArquillianResource
    URL deploymentURL;

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
        DocumentDeleteQuery deleteUsersQuery = delete().from("User").where("_id").not().eq("").build();
        DocumentDeleteQuery deleteSaltsQuery = delete().from("Salt").where("_id").not().eq("").build();
        collectionManager.delete(deleteUsersQuery);
        collectionManager.delete(deleteSaltsQuery);
    }

    @Before
    public void registerAndLoginUser() {
        clearTestRepositories();

        registerBacking.setUsername("User");
        registerBacking.setPassword("pwd");
        registerBacking.setRepeatPassword("pwd");
        registerBacking.setEmail("user@example.com");
        registerBacking.register();
    }

    @Test
    public void loginPage() {
        System.err.println("Deployment URL: " + this.deploymentURL.toString());
    }

    @Test
    public void twoTimesTwoEqualsFour() {
        assertEquals(4, 2*2);
    }

}