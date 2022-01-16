package com.szymonharabasz.grocerylistmanager.functional;

import com.szymonharabasz.grocerylistmanager.domain.UserRepository;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.jnosql.artemis.Database;
import org.jnosql.artemis.DatabaseType;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class UserServiceTest {
    @Inject
    @Database(DatabaseType.DOCUMENT)
    public UserRepository repo;

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class);

        File pom = new File("pom.xml");
        System.err.println("pom.exists = " + pom.exists());
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeDependencies()
                .resolve().withTransitivity().asFile();

        archive.addAsLibraries(libs);
        archive.addPackages(false, Filters.exclude(".*Test.*"), UserRepository.class.getPackage());
        archive.addAsResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));


        System.err.println("Deployed archove " + archive.toString(true));

        return archive;
    }

    @Test
    public void repositoryCreated() {
        assertNotNull(repo);
    }

    @Test
    public void twoTimesTwoEqualsFour() {
        assertEquals(4, 2*2);
    }
}
