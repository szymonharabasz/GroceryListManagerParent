package com.szymonharabasz.grocerylistmanager.functional;

import com.szymonharabasz.grocerylistmanager.DocumentCollectionManagerProducer;
import com.szymonharabasz.grocerylistmanager.domain.User;
import com.szymonharabasz.grocerylistmanager.domain.UserRepository;
import com.szymonharabasz.grocerylistmanager.service.UserService;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class UserServiceTest {

   @Inject
   UserService userService;

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class);

        File pom = new File("pom.xml");
        System.err.println("pom.exists = " + pom.exists());
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeDependencies()
                .resolve().withTransitivity().asFile();

        archive.addAsLibraries(libs);
        Package domain = UserRepository.class.getPackage();
        String domainName = domain.getName();
        String parentName = domainName.substring(0, domainName.lastIndexOf('.'));
        Package parant = Package.getPackage(parentName);
        archive.addPackages(true, Filters.exclude(".*Test.*"), parant);
        archive.addClass(DocumentCollectionManagerProducer.class);
        archive.addAsResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));


        System.err.println("Deployed archove " + archive.toString(true));

        return archive;
    }

    @Test
    public void userServiceCreated() {
        assertNotNull(userService);
    }

    @Test
    public void canSaveToRepository() {
        userService.save(new User("1", "User", "pwd", "user@example.com"));
    }

    @Test
    public void twoTimesTwoEqualsFour() {
        assertEquals(4, 2*2);
    }
}
