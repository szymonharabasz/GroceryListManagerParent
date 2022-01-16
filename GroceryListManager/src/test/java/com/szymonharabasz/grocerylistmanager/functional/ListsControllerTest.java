package com.szymonharabasz.grocerylistmanager.functional;

import com.szymonharabasz.grocerylistmanager.ListsController;

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
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.Package;
import java.security.Principal;

import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;

@RunWith(Arquillian.class)
public class ListsControllerTest {

    @Mock
    SecurityContext securityContext;

    @Inject
    ListsController listsController;

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


        System.err.println("Deployed archove " + archive.toString(true));

        return archive;
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
        assertNull(securityContext);
       // when(securityContext.getCallerPrincipal()).thenReturn(principal);
       // listsController.addList();
    }

}