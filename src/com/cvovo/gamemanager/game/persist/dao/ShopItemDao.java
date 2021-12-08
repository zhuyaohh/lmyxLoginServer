package com.cvovo.gamemanager.game.persist.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cvovo.gamemanager.game.persist.entity.ShopItem;

public interface ShopItemDao extends JpaRepository<ShopItem, Long> {

	ShopItem findByItemNameAndType(String itemName, String type);
}
