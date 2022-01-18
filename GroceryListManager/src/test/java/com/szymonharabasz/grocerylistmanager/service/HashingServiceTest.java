package com.szymonharabasz.grocerylistmanager.service;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;

import com.szymonharabasz.grocerylistmanager.DocumentCollectionManagerProducer;
import com.szymonharabasz.grocerylistmanager.domain.Salt;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.runner.RunWith;
import org.junit.Test;

import javax.inject.Inject;

@RunWith(Arquillian.class)
public class HashingServiceTest {
    @Inject
    public HashingService hashingService;

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class);

        File pom = new File("pom.xml");
        System.err.println("pom.exists = " + pom.exists());
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeDependencies()
                .resolve().withTransitivity().asFile();

        archive.addAsLibraries(libs);
        Package service = HashingService.class.getPackage();
        String serviceName = service.getName();
        String parentName = serviceName.substring(0, serviceName.lastIndexOf('.'));
        Package parent = Package.getPackage(parentName);
        archive.addPackages(true, Filters.exclude(".*Test.*"), parent);
        archive.addClass(DocumentCollectionManagerProducer.class);
        archive.addAsResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
        return archive;
    }

    @Test
    public void saltIsSavedAndRetrieved() {
        byte[] saltContent = "saltContent".getBytes();
        hashingService.save(new Salt("id", saltContent));
        Salt salt = hashingService.findSaltByUserId("id").get();
        assertArrayEquals(saltContent, salt.getSalt());
    }
}
