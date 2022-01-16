package com.szymonharabasz.grocerylistmanager.functional;

import com.szymonharabasz.grocerylistmanager.domain.UserRepository;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import jakarta.nosql.mapping.Database;
import jakarta.nosql.mapping.DatabaseType;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

//@RunWith(Arquillian.class)
public class UserServiceTest {
    @Inject
    @Database(DatabaseType.DOCUMENT)
    protected UserRepository repo;

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class);

        File pom = new File("pom.xml");
        System.err.println("pom.exists = " + pom.exists());
       // File[] libs = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeDependencies()
       //         .resolve().withTransitivity().asFile();

        // archive.addAsLibraries(libs);
        archive.addPackages(false, Filters.exclude(".*Test.*"), UserRepository.class.getPackage());

        return archive;
    }

   // @Test
    public void repositoryCreated() {
        System.err.println("All users: " + repo.findAll());
        assertNull(repo);
    }

   // @Test
    public void twoTimesTwoEqualsFive() {
        assertEquals(5, 2*2);
    }
}
