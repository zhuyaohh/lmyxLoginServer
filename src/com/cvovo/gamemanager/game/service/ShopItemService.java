package com.cvovo.gamemanager.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cvovo.gamemanager.game.persist.dao.ShopItemDao;
import com.cvovo.gamemanager.game.persist.entity.ShopItem;

@Service
@Transactional
public class ShopItemService {

	@Autowired
	private ShopItemDao shopItemDao;

	public ShopItem checkPriceAndShopItem(String itemName) {
		ShopItem shopItemp = shopItemDao.findByItemNameAndType(itemName, "1");
		return shopItemp;
	}
}
