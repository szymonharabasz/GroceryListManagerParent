package com.szymonharabasz.grocerylistmanager;

import com.codepoetics.protonpack.StreamUtils;
import com.szymonharabasz.grocerylistmanager.service.ListsService;
import com.szymonharabasz.grocerylistmanager.service.SharedBundleService;
import com.szymonharabasz.grocerylistmanager.view.GroceryListView;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Named
@RequestScoped
public class SharedBacking {
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
                    return new GroceryListView(list);
                }).collect(Collectors.toList())
        );
    }
}
