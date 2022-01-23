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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;
import java.lang.Package;
import java.net.URL;

import javax.inject.Inject;

import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.delete;
import static org.jboss.arquillian.graphene.Graphene.waitModel;
import static org.jboss.arquillian.graphene.Graphene.waitAjax;
import static org.jboss.arquillian.graphene.Graphene.guardAjax;

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
    public void createAndDeleteList() throws InterruptedException {
        addListAndCheck("List 1", "First shopping list", 0);
        deleteList(0);
    }

    @Test @RunAsClient @InSequence(4)
    public void createThreeListsAndMoveThem() throws InterruptedException {
        addListAndCheck("List 1", "First shopping list", 0);
        addListAndCheck("List 2", "Second shopping list", 1);
        addListAndCheck("List 3", "Third shopping list", 2);
        moveListUpAndCheck(2);
        moveListUpAndCheck(0);
        moveListDownAndCheck(0, 3);
        moveListDownAndCheck(2, 3);
       // Thread.sleep(3000);
        String outputNameId = "dataViewLists:" + 0 + ":formLists:outName";
        WebElement outputName = browser.findElement(By.id(outputNameId));
        deleteList(0);
        deleteList(0);
        deleteList(0);
        waitModel().until().element(outputName).is().not().visible();
    }

    private void addListAndCheck(String name, String desc, int position) throws InterruptedException {
        linkAddNewList.click();
        String inputNameId = "dataViewLists:" + position + ":formLists:inName";
        String inputDescId = "dataViewLists:" + position + ":formLists:inDesc";
        waitModel().until(ExpectedConditions.visibilityOfElementLocated(By.id(inputNameId)));
        WebElement inputName = browser.findElement(By.id(inputNameId));
        WebElement inputDesc = browser.findElement(By.id(inputDescId));
        inputName.sendKeys(name);
        inputDesc.sendKeys(desc);
        
        String linkSaveId = "dataViewLists:" + position + ":formLists:linkSave";
        WebElement linkSave = browser.findElement(By.id(linkSaveId));
        linkSave.click();

        String outputNameId = "dataViewLists:" + position + ":formLists:outName";
        String outputDescId = "dataViewLists:" + position + ":formLists:outDesc";
        waitModel().until(ExpectedConditions.visibilityOfElementLocated(By.id(outputNameId)));
        WebElement outputName = browser.findElement(By.id(outputNameId));
        WebElement outputDesc = browser.findElement(By.id(outputDescId));
        waitModel().until().element(outputName).text().contains(name);
        waitModel().until().element(outputDesc).text().contains(desc);
       
    }

    private void deleteList(int position) throws InterruptedException {
        String linkDeleteId = "dataViewLists:" + position + ":formLists:linkDelete";
        WebElement linkDelete = browser.findElement(By.id(linkDeleteId));
        linkDelete.click();

        String buttonConfirmDeleteId = "dataViewLists:" + position + ":formLists:buttonConfirmDeleteList";
       // Thread.sleep(2000);
        System.err.println(browser.getPageSource());
        waitModel().until(ExpectedConditions.visibilityOfElementLocated(By.id(buttonConfirmDeleteId)));
        WebElement buttonConfirmDeleteList = browser.findElement(By.id(buttonConfirmDeleteId));

        waitModel().until().element(buttonConfirmDeleteList).is().clickable();
        guardAjax(buttonConfirmDeleteList).click();
    }

    private void moveListUpAndCheck(int initialPosition) {
        String moveListUpLinkId = "dataViewLists:" + initialPosition + ":formLists:linkUp";
        String outputNameId = "dataViewLists:" + initialPosition + ":formLists:outName";
        WebElement outputName = browser.findElement(By.id(outputNameId));
        String listName = outputName.getText();

        WebElement linkMoveListUp = browser.findElement(By.id(moveListUpLinkId));
        guardAjax(linkMoveListUp).click();

        String newOutputNameId = (initialPosition > 0) ? 
            "dataViewLists:" + (initialPosition - 1) + ":formLists:outName" :
            "dataViewLists:" + initialPosition + ":formLists:outName";
        
        WebElement newOutputName = browser.findElement(By.id(newOutputNameId));
        waitAjax().until().element(newOutputName).text().contains(listName);
    }

    private void moveListDownAndCheck(int initialPosition, int size) {
        String moveListUpLinkId = "dataViewLists:" + initialPosition + ":formLists:linkDown";
        String outputNameId = "dataViewLists:" + initialPosition + ":formLists:outName";
        WebElement outputName = browser.findElement(By.id(outputNameId));
        String listName = outputName.getText();

        WebElement linkMoveListUp = browser.findElement(By.id(moveListUpLinkId));
        guardAjax(linkMoveListUp).click();

        String newOutputNameId = (initialPosition < size - 1) ? 
            "dataViewLists:" + (initialPosition + 1) + ":formLists:outName" :
            "dataViewLists:" + initialPosition + ":formLists:outName";
        
        WebElement newOutputName = browser.findElement(By.id(newOutputNameId));
        waitAjax().until().element(newOutputName).text().contains(listName);
    }
}