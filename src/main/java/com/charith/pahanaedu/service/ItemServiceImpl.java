package com.charith.pahanaedu.service;

import com.charith.pahanaedu.dto.ItemDTO;
import com.charith.pahanaedu.entity.Category;
import com.charith.pahanaedu.entity.Item;
import com.charith.pahanaedu.exception.ResourceNotFoundException;
import com.charith.pahanaedu.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CategoryService categoryService;

    @Override
    public Item createItem(ItemDTO createItemDto) {
        Category category = categoryService.getCategoryByName(createItemDto.getCategoryName());

        if (category == null) {
            throw new ResourceNotFoundException("Category not found with name: " + createItemDto.getCategoryName());
        }

        Item item = new Item();
        item.setName(createItemDto.getName());
        item.setDescription(createItemDto.getDescription());
        item.setCategory(category);
        item.setAuthor(createItemDto.getAuthorName());
        item.setPublisher(createItemDto.getPublisherName());
        item.setIsbn(createItemDto.getIsbn());
        item.setPrice(createItemDto.getPrice());
        item.setQuantity(createItemDto.getQuantity());
        item.setCreatedAt(LocalDateTime.now());

        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Integer id, ItemDTO updateItemDto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));

        if (updateItemDto.getName() != null && !updateItemDto.getName().trim().isEmpty()) {
            item.setName(updateItemDto.getName());
        }
        if (updateItemDto.getDescription() != null && !updateItemDto.getDescription().trim().isEmpty()) {
            item.setDescription(updateItemDto.getDescription());
        }
        if (updateItemDto.getPrice() != null) {
            item.setPrice(updateItemDto.getPrice());
        }
        if (updateItemDto.getQuantity() != null) {
            item.setQuantity(updateItemDto.getQuantity());
        }

        return itemRepository.save(item);
    }

    @Override
    public Item updateItemQuantity(Integer id, Integer quantity) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));

        item.setQuantity(item.getQuantity() - quantity);
        return itemRepository.save(item);
    }

    @Override
    public void deleteItem(Integer id) {
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item not found with id: " + id);
        }

        try {
            itemRepository.deleteById(id);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to delete item with id: " + id, ex);
        }
    }

    @Override
    public Item getItemById(Integer id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public List<Item> findItemsByName(String keyword) {
        return itemRepository.findByNameContaining(keyword);
    }

    @Override
    public List<Item> findItemsWithQuantityLessThan(Integer quantity) {
        return itemRepository.findByQuantityLessThan(quantity);
    }
}
