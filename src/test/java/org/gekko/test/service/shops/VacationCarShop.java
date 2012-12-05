package org.gekko.test.service.shops;

import org.gekko.test.service.cars.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class VacationCarShop implements CarShop {

	private CarService cars;

	private String shopType;

	@Autowired
	@Qualifier("vacation")
	public void setCars(CarService cars) {
		this.cars = cars;
	}

	public CarService getCars() {
		return cars;
	}

	public void setShopType(String shopType) {
		this.shopType = shopType;
	}
}
