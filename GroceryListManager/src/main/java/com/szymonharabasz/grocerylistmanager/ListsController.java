package com.szymonharabasz.grocerylistmanager;

import com.codepoetics.protonpack.StreamUtils;
import com.szymonharabasz.grocerylistmanager.domain.GroceryList;
import com.szymonharabasz.grocerylistmanager.domain.SharedBundle;
import com.szymonharabasz.grocerylistmanager.domain.User;
import com.szymonharabasz.grocerylistmanager.service.ListsService;
import com.szymonharabasz.grocerylistmanager.service.SharedBundleService;
import com.szymonharabasz.grocerylistmanager.service.UserService;
import com.szymonharabasz.grocerylistmanager.view.GroceryItemView;
import com.szymonharabasz.grocerylistmanager.view.GroceryListView;
import org.primefaces.PrimeFaces;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.SecurityContext;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Named
@SessionScoped
public class ListsController implements Serializable {
    private final ListsService listsService;
    private final UserService userService;
    private final SharedBundleService sharedBundleService;
    private final SecurityContext securityContext;
    private final ExternalContext externalContext;
    private final FacesContext facesContext;
    private final ServletContext servletContext;
    private final Date creationDate = new Date();
    private List<GroceryListView> lists = new ArrayList<>();
    private List<GroceryListView> listsToShare = new ArrayList<>();
    private List<GroceryListView> listsNotToShare = new ArrayList<>();
    private String greeting;
    private String shareNickname;
    private String toBecomeShared;
    private String linkToShare;
    private final Logger logger = Logger.getLogger(ListsController.class.getName());

    @Inject
    public ListsController(
            ListsService listsService,
            UserService userService,
            SharedBundleService sharedBundleService,
            FacesContext facesContext,
            ServletContext servletContext,
            SecurityContext securityContext
    ) {
        this.listsService = listsService;
        this.userService = userService;
        this.sharedBundleService = sharedBundleService;
        this.facesContext = facesContext;
        this.externalContext = facesContext.getExternalContext();
        this.servletContext = servletContext;
        this.securityContext = securityContext;
        this.greeting = "Yellow";
    }

    public List<GroceryListView> getLists() {
        return lists;
    }

    public void setLists(List<GroceryListView> lists) {
        this.lists = lists;
    }

    public List<GroceryListView> getListsToShare() {
        return listsToShare;
    }

    public void setListsToShare(List<GroceryListView> listsToShare) {
        this.listsToShare = listsToShare;
    }

    public List<GroceryListView> getListsNotToShare() {
        System.err.println("Lists not to share " + listsNotToShare);
        return listsNotToShare;
    }

    public void setListsNotToShare(List<GroceryListView> listsNotToShare) {
        this.listsNotToShare = listsNotToShare;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) { this.greeting = greeting; }

    public String getShareNickname() {
        return shareNickname;
    }

    public void setShareNickname(String shareNickname) {
        this.shareNickname = shareNickname;
    }

    public String getToBecomeShared() {
        return toBecomeShared;
    }

    public void setToBecomeShared(String toBecomeShared) {
        this.toBecomeShared = toBecomeShared;
    }

    public String getLinkToShare() {
        return linkToShare;
    }

    public boolean isLinkToShareVisible() {
        return linkToShare != null && !linkToShare.isEmpty();
    }

    public void openDlgShareList(String listId) {
        listsToShare = lists.stream().filter(list -> Objects.equals(list.getId(), listId)).collect(Collectors.toList());
        listsNotToShare = lists.stream().filter(list -> !Objects.equals(list.getId(), listId)).collect(Collectors.toList());
        PrimeFaces.current().executeScript("PF('dlgShareLists').show()");
    }

    public void hideDlgShareList(String listId) {
        PrimeFaces.current().executeScript("PF('dlgShareLists').hide()");
    }

    public void removeListFromShare(String listId) {
        listsToShare = listsToShare.stream().filter(list -> !Objects.equals(listId, list.getId())).collect(Collectors.toList());
        listsNotToShare = lists.stream().filter(list -> !listsToShare.contains(list)).collect(Collectors.toList());
    }

    public void addListToShare() {
        System.err.println("List to become shared " + this.toBecomeShared);
        System.err.println("Lists to share before: " + listsToShare);
        System.err.println("Lists not to share before: " + listsNotToShare);
        listsNotToShare = listsNotToShare.stream().filter(list -> !Objects.equals(toBecomeShared, list.getId())).collect(Collectors.toList());
        listsToShare = lists.stream().filter(list -> !listsNotToShare.contains(list)).collect(Collectors.toList());
        System.err.println("Lists to share after: " + listsToShare);
        System.err.println("Lists not to share after: " + listsNotToShare);
    }

    public void editList(String id) {
        findList(id).ifPresent(list -> list.setEdited(true));
    }

    public void saveList(String id) {
        findList(id).ifPresent(list -> {
            list.setEdited(false);
            listsService.saveList(list.toGroceryList());
        });
    }

    public void expand(String id) {
        findList(id).ifPresent(list -> list.setExpanded(true));
    }

    public void collapse(String id) {

        if (findList(id).isPresent()) {
            System.err.println("List " + id + " is present");
        } else {
            System.err.println("List " + id + " is not present");
        }
        findList(id).ifPresent(list -> list.setExpanded(false));
    }

    public void moveUp(String listId) {
        currenUser().ifPresent(user -> userService.moveUp(user, listId));
    }

    public void moveDown(String listId) {
        currenUser().ifPresent(user -> userService.moveDown(user, listId));
    }

    public void removeList(String id) {
        listsService.removeList(id);
        fetchLists();
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void editItem(String id) {
        findItem(id).ifPresent(item -> item.setEdited(true));
    }

    public void saveItem(String id, String listId) {
        findItem(id).ifPresent(item -> {
            item.setEdited(false);
            listsService.saveItem(item.toGroceryItem(), listId);
        });
    }

    public void toggleDone(String id, String listId) {
        findItem(id).ifPresent(item -> listsService.saveItem(item.toGroceryItem(), listId));
    }

    public void removeItem(String id) {
        currenUser().ifPresent(user -> {
            user.removeListId(id);
            userService.save(user);
            listsService.removeItem(id);
            fetchLists();
        });
    }

    public void addList() {
        System.err.println("Adding new list for user " + securityContext.getCallerPrincipal().getName());
        currenUser().ifPresent(user -> {
            System.err.println("Adding new list for user " + user.getName());
            GroceryListView list = new GroceryListView(UUID.randomUUID().toString(), "", "", new Date());
            user.addListId(list.getId());
            userService.save(user);
            list.setEdited(true);
            lists.add(list);
            listsService.saveList(list.toGroceryList());
        });
    }

    public void addItem(String listId) {
        findList(listId).ifPresent(list -> {
            logger.severe("Adding item to list " + list.getName());
            GroceryItemView item = new GroceryItemView(UUID.randomUUID().toString(), false,"", "", BigDecimal.valueOf(0.0));
            item.setEdited(true);
            list.addItem(item);
            GroceryList groceryList = list.toGroceryList();
            logger.severe("List view has " + list.getItems().size() + ", list has " + groceryList.getItems().size() + " items.");
            logger.severe(groceryList.toString());
            listsService.saveList(groceryList);
        });
    }

    private Optional<GroceryListView> findList(String id) {
        return lists.stream().filter(list -> Objects.equals(list.getId(), id)).findAny();
    }

    private Optional<GroceryItemView> findItem(String id) {
        for (GroceryListView list : lists) {
            for (GroceryItemView item : list.getItems()) {
                if (Objects.equals(item.getId(), id)) return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    public void fetchLists() {
        logger.warning("Lists are fetched.");
        logger.warning("TEST_VARIABLE = " + servletContext.getInitParameter("testVariable"));
        currenUser().ifPresent(user -> lists = listsService.getLists().stream()
                .filter(list -> user.hasListId(list.getId())).sorted(
                        Comparator.comparingInt(l -> user.getIndexOfListId(l.getId()))
                )
                .map(list -> {
                    GroceryListView listView = new GroceryListView(list);
                    findList(list.getId()).ifPresent(oldListView -> {
                        listView.setExpanded(oldListView.isExpanded());
                        listView.setEdited(oldListView.isEdited());
                        listView.setItems(StreamUtils.zip(
                                oldListView.getItems().stream(),
                                listView.getItems().stream(),
                                (oldItemView, newItemView) -> {
                                    newItemView.setEdited(oldItemView.isEdited());
                                    return newItemView;
                                }
                        ).collect(Collectors.toList()));
                    });
                    return listView;
                }).collect(Collectors.toList())
        );
    }

    public void checkConfirmed() {
        System.err.println("CHECK CONFIRMED CALLED");
        currenUser().ifPresent(user -> {
            System.err.println("USER IS PRESENT");
            if (!user.isConfirmed()) {
                System.err.println("USER IS NOT CONFIRMED, REDIRECTING");
                redirect("/request-new-confirmation.xhtml");
            }
        });
    }

    Optional<User> currenUser() {
        if (securityContext != null) {
            Principal caller = securityContext.getCallerPrincipal();
            return userService.findByName(caller.getName());
        } else {
            return Optional.empty();
        }
    }

    private void redirect(String to) {
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + to);
        } catch (IOException e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    ResourceBundle.getBundle("com.szymonharabasz.grocerylistmanager.texts")
                            .getString("generic-error-message"), null));
        }
    }

    public void moveItemUp(String itemId, String listId) {
        listsService.moveItemUp(itemId, listId);
    }

    public void moveItemDown(String itemId, String listId) {
        listsService.moveItemDown(itemId, listId);
    }

    public void shareList() {
        String newBundleId = UUID.randomUUID().toString();
        sharedBundleService.save(new SharedBundle(newBundleId, shareNickname,
                listsToShare.stream().map(GroceryListView::getId).collect(Collectors.toList())));
        linkToShare = System.getProperty("HOST", "https://localhost:8182/") +
                servletContext.getContextPath() + "/shared.xhtml?id=" + newBundleId;
    }
}