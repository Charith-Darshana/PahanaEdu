package com.charith.pahanaedu.service;

import com.charith.pahanaedu.dto.ItemDTO;
import com.charith.pahanaedu.entity.Item;

import java.util.List;

public interface ItemService {
    Item createItem(ItemDTO itemDTO);
    Item updateItem(Integer id, ItemDTO itemDto);
    Item updateItemQuantity(Integer id, Integer quantity);
    void deleteItem(Integer id);
    Item getItemById(Integer id);
    List<Item> getAllItems();
    List<Item> findItemsByName(String name);
    List<Item> findItemsWithQuantityLessThan(Integer quantity);
}
