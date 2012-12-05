package org.gekko.test.service.shops;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

public class CarShopDirectory
{

	private Map<CarShopType, CarShop> shops = Collections.emptyMap();
	private List<CarShop> shopList = Collections.emptyList();

	@Autowired(required = false)
	public CarShop defaultShop;

	public void setShops(Map<CarShopType, CarShop> shops)
	{
		this.shops = shops;
	}

	public CarShop getCarShop(CarShopType type)
	{
		return shops.get(type);
	}

	public List<CarShop> getShopList()
	{
		return shopList;
	}

	public void setShopList(List<CarShop> shopList)
	{
		this.shopList = shopList;
	}

	public CarShop getDefaultShop()
	{
		return defaultShop;
	}

	public void setDefaultShop(CarShop defaultShop)
	{
		this.defaultShop = defaultShop;
	}

}
