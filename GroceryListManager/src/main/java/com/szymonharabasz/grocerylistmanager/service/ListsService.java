package com.szymonharabasz.grocerylistmanager.service;

import com.szymonharabasz.grocerylistmanager.domain.GroceryItem;
import com.szymonharabasz.grocerylistmanager.domain.GroceryList;
import com.szymonharabasz.grocerylistmanager.domain.ListsRepository;
import jakarta.nosql.mapping.Database;
import jakarta.nosql.mapping.DatabaseType;
import jakarta.nosql.mapping.document.DocumentTemplate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static jakarta.nosql.document.DocumentQuery.select;

@Named
@ApplicationScoped
public class ListsService {

    private final Logger logger = Logger.getLogger(ListsService.class.getName());

    @Inject
    @Database(DatabaseType.DOCUMENT)
    private ListsRepository repository;

    @Inject
    private DocumentTemplate template;

    public List<GroceryList> getLists() {
        return repository.findAll();
    }

    public void setLists(List<GroceryList> lists) {
        repository.save(lists);
    }

    public void saveList(GroceryList list) {
        logger.severe("Saving list " + list.getName() + " with " + list.getItems().size() + " items.");
        logger.severe(list.toString());
        repository.save(list);
    }

    public void saveItem(GroceryItem item, String listId) {
        findList(listId).ifPresent(list -> {
            AtomicBoolean wasItemFound = new AtomicBoolean(false);
            List<GroceryItem> items = list.getItems().stream().map(i -> {
                if (Objects.equals(i.getId(), item.getId())) {
                    wasItemFound.set(true);
                    return item;
                } else {
                    return i;
                }
            }).collect(Collectors.toList());
            if (!wasItemFound.get()) {
                items.add(item);
            }
            list.setItems(items);
            repository.save(list);
        });
    }

    public Optional<GroceryList> findList(String id) {
        return repository.findById(id);
    }

    public void removeList(String id) {
        repository.deleteById(id);
    }

    public void removeItem(String id) {
        for (GroceryList list : repository.findAll()) {
            list.setItems(
                    list.getItems().stream().filter(item ->
                            !Objects.equals(item.getId(), id)).collect(Collectors.toList())
            );
            repository.save(list);
        }
    }
}
