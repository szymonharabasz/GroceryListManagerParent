package com.szymonharabasz.grocerylistmanager.functional;

import com.szymonharabasz.grocerylistmanager.DocumentCollectionManagerProducer;
import com.szymonharabasz.grocerylistmanager.domain.ExpirablePayload;
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
import org.jnosql.artemis.Database;
import org.jnosql.artemis.DatabaseType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class UserServiceTest {

   @Inject
   UserService userService;

   @Inject
   @Database(DatabaseType.DOCUMENT)
   UserRepository userRepository;

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
        return archive;
    }

    @Before
    public void clearTestRepository() {
        userRepository.deleteById(userRepository.findAll().stream().map(User::getId).collect(Collectors.toList()));
    }

    @Test
    public void userServiceCreated() {
        assertNotNull(userService);
    }

    @Test
    public void canSaveToRepository() {
        userService.save(new User("1", "User", "pwd", "user@example.com"));
        assertEquals(1, userService.findAll().size());
    }

    @Test
    public void savedUserCanBeFoundByName() {
        userService.save(new User("2", "User2", "pwd", "user@example.com"));
        Optional<User> user = userService.findByName("User2");
        assertTrue(user.isPresent());
        assertEquals("2", user.get().getId());
    }

    @Test
    public void emptyOptionIsReturndForNonexistingName() {
        assertFalse(userService.findByName("User4").isPresent());
    }

    @Test
    public void savedUserCanBeFoundByEmail() {
        userService.save(new User("3", "User3", "pwd", "user3@example.com"));
        Optional<User> user = userService.findByEmail("user3@example.com");
        assertTrue(user.isPresent());
        assertEquals("3", user.get().getId());
    }

    @Test
    public void emptyOptionIsReturndForNonexistingEmail() {
        assertFalse(userService.findByName("user5@example.com").isPresent());
    }

    @Test
    public void savedUserCanBeFoundByConfirmationToken() {
        User user = new User("6", "User6", "pwd", "user6@example.com");
        user.setConfirmationToken(new ExpirablePayload("token6", new Date()));
        userService.save(user);
        Optional<User> foundUser = userService.findByConfirmationToken("token6");
        assertTrue(foundUser.isPresent());
        assertEquals("6", foundUser.get().getId());
    }

}
