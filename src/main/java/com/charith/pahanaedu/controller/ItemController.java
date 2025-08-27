package com.charith.pahanaedu.controller;

import com.charith.pahanaedu.dto.ItemDTO;
import com.charith.pahanaedu.entity.Item;
import com.charith.pahanaedu.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody ItemDTO createItemDto) {
        Item createdItem = itemService.createItem(createItemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(
            @PathVariable Integer id, @RequestBody ItemDTO updateItemDto) {
        Item updatedItem = itemService.updateItem(id, updateItemDto);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable Integer id) {
        try {
            itemService.deleteItem(id);
            return ResponseEntity.ok("Item deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Integer id) {
        Item item = itemService.getItemById(id);
        return ResponseEntity.ok(item);
    }

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Item>> findItemsByName(@RequestParam String name) {
        List<Item> items = itemService.findItemsByName(name);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/low-quantity")
    public ResponseEntity<List<Item>> findItemsWithLowQuantity(@RequestParam Integer quantity) {
        List<Item> items = itemService.findItemsWithQuantityLessThan(quantity);
        return ResponseEntity.ok(items);
    }
}
