package com.szymonharabasz.grocerylistmanager;

import com.codepoetics.protonpack.StreamUtils;
import com.szymonharabasz.grocerylistmanager.service.ListsService;
import com.szymonharabasz.grocerylistmanager.service.SharedBundleService;
import com.szymonharabasz.grocerylistmanager.view.GroceryListView;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Named
@SessionScoped
public class SharedBacking implements Serializable {
    private String id;
    private String from;
    private List<GroceryListView> lists = new ArrayList<>();

    private final ListsService listsService;
    private final SharedBundleService sharedBundleService;

    @Inject
    public SharedBacking(ListsService listsService, SharedBundleService sharedBundleService) {
        this.listsService = listsService;
        this.sharedBundleService = sharedBundleService;
    }

    public SharedBacking() {
        this(null, null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public List<GroceryListView> getLists() {
        return lists;
    }

    public void fetchLists() {
        sharedBundleService.findById(id).ifPresent(bundle -> lists = listsService.getLists().stream()
                .filter(list -> bundle.hasListId(list.getId())).sorted(
                        Comparator.comparingInt(l -> bundle.getIndexOfListId(l.getId()))
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

    private Optional<GroceryListView> findList(String id) {
        return lists.stream().filter(list -> Objects.equals(list.getId(), id)).findAny();
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
        sharedBundleService.findById(id).ifPresent(bundle -> sharedBundleService.moveUp(bundle, listId));
    }

    public void moveDown(String listId) {
        sharedBundleService.findById(id).ifPresent(bundle -> sharedBundleService.moveDown(bundle, listId));
    }

}
