package com.szymonharabasz.grocerylistmanager.functional;

import com.szymonharabasz.grocerylistmanager.ListsController;
import com.szymonharabasz.grocerylistmanager.LoginBacking;
import com.szymonharabasz.grocerylistmanager.RegisterBacking;
import com.szymonharabasz.grocerylistmanager.domain.SaltRepository;
import com.szymonharabasz.grocerylistmanager.domain.UserRepository;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.io.File;
import java.lang.Package;
import java.net.URL;

import javax.inject.Inject;

import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.delete;
import static org.jboss.arquillian.graphene.Graphene.waitModel;

@RunWith(Arquillian.class)
public class ListsControllerTest {
    private static final String WEBAPP_SRC = "src/main/webapp";

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

    @FindBy(id = "loginForm:textLogin")
    private WebElement textLogin;

    @FindBy(id = "loginForm:textPassword")
    private WebElement textPassword;

    @FindBy(id = "loginForm:btnSignin")
    private WebElement btnSignin;

    @FindBy(id = "loginForm:messages")
    private WebElement messages;

    @FindBy(id = "labelGreeting")
    private WebElement labelGreeting;
    
    @FindBy(id = "formAddNewList:link")
    private WebElement linkAddNewList;

    @FindBy(id = "dataViewLists:0:formLists:inName")
    private WebElement inputName;

    @FindBy(id = "dataViewLists:0:formLists:inDesc")
    private WebElement inputDesc;

    @FindBy(id = "dataViewLists:0:formLists:outName")
    private WebElement outputName;

    @FindBy(id = "dataViewLists:0:formLists:outDesc")
    private WebElement outputDesc;

    @FindBy(id = "dataViewLists:0:formLists:linkSave")
    private WebElement linkSave;

    @FindBy(id = "dataViewLists:0:formLists:linkDelete")
    private WebElement linkDelete;

    @FindBy(id = "dataViewLists:0:formLists:buttonConfirmDeleteList")
    private WebElement buttonConfirmDeleteList;

    public ListsControllerTest() {
        PageFactory.initElements(browser, this);
    }

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
        archive.addAsWebResource(new File(WEBAPP_SRC + "/index.xhtml"));
        archive.addAsWebResource(new File(WEBAPP_SRC + "/login.xhtml"));
        final String finalPageCSS = "/resources/css/form-page.css";
        final String indexCSS = "/resources/css/index.css";
        archive.addAsWebResource(new File(WEBAPP_SRC, finalPageCSS), finalPageCSS);
        archive.addAsWebResource(new File(WEBAPP_SRC, indexCSS), indexCSS);
        archive.addAsWebInfResource(new File(WEBAPP_SRC + "/WEB-INF/web.xml"));
        archive.addAsWebInfResource(new File(WEBAPP_SRC + "/WEB-INF/glassfish-web.xml"));
        archive.addAsWebInfResource(new File(WEBAPP_SRC + "/WEB-INF/faces-config.xml"));
        System.err.println("Deployed archove " + archive.toString(true));

        return archive;
    }

    void clearTestRepositories() {
        DocumentDeleteQuery deleteUsersQuery = delete().from("User").where("_id").not().eq("").build();
        DocumentDeleteQuery deleteSaltsQuery = delete().from("Salt").where("_id").not().eq("").build();
        collectionManager.delete(deleteUsersQuery);
        collectionManager.delete(deleteSaltsQuery);
    }

   // @Before
    public void registerAndLoginUser() {
        clearTestRepositories();

        registerBacking.setUsername("User");
        registerBacking.setPassword("pwd");
        registerBacking.setRepeatPassword("pwd");
        registerBacking.setEmail("user@example.com");
        registerBacking.register();
    }

    @Test @RunAsClient @InSequence(2)
    public void showsTheHomePageIfCorrectCredentials() {
        browser.get(this.deploymentURL.toString() + "/index.xhtml");
        textLogin.sendKeys("NewUser");
        textPassword.sendKeys("N3wUserPas$");
        btnSignin.click();
        waitModel().until().element(labelGreeting).text().contains("Hello, NewUser");
    }

    @Test @RunAsClient @InSequence(3)
    public void creatingNewList() throws InterruptedException {
        linkAddNewList.click();
        waitModel().until().element(inputName).is().visible();
        inputName.sendKeys("List 1");
        inputDesc.sendKeys("First shopping list");
        linkSave.click();
        waitModel().until().element(outputName).text().contains("List 1");
        waitModel().until().element(outputDesc).text().contains("First shopping list");
        linkDelete.click();
        waitModel().until().element(buttonConfirmDeleteList).is().clickable();
        buttonConfirmDeleteList.click();
        waitModel().until().element(outputName).is().not().visible();
    }

}