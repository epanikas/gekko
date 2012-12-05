package org.gekko.test.beanfactory;

import org.gekko.beanfactory.GekkoApplicationContext;
import org.gekko.test.service.cars.CarService;
import org.gekko.test.service.cars.DelegatingCarService;
import org.gekko.test.service.cars.small.FiatPuntoService;
import org.gekko.test.service.shops.CarShopDirectory;
import org.gekko.test.service.shops.CarShopType;
import org.gekko.test.service.shops.SportCarShop;
import org.gekko.test.service.shops.VacationCarShop;
import org.junit.Assert;
import org.junit.Test;

public class GekkoApplicationContextTest {

	@Test
	public void testLoadGekkoContext() {
		/*
		 * given
		 */

		/*
		 * when
		 */
		GekkoApplicationContext context = new GekkoApplicationContext("gekko/service-cars.xml");
		CarService cars = (CarService) context.getBean("cars1");
		VacationCarShop vacationShop = (VacationCarShop) context.getBean("vacation-car-shop");
		VacationCarShop vacationShop3 = (VacationCarShop) context.getBean("vacation-car-shop#3");
		SportCarShop sportShop = (SportCarShop) context.getBean("sport-car-shop");
		context.getBeanFactory().autowireBean(vacationShop);
		context.getBeanFactory().autowireBean(sportShop);

		DelegatingCarService delegatingSrv = (DelegatingCarService) context.getBean("delegating-car-service");

		FiatPuntoService fiatPunto = (FiatPuntoService) context.getBean("fiat-punto-service");

		/*
		 * should
		 */
		Assert.assertNotNull(cars);
		Assert.assertNotNull(vacationShop);
		Assert.assertNotNull(vacationShop3);
		Assert.assertNotNull(sportShop);
		Assert.assertNotNull(delegatingSrv);
		Assert.assertEquals(cars, delegatingSrv.getDelegate());
		Assert.assertEquals(cars, sportShop.getCars());
		Assert.assertEquals(context.getBean("cars2"), vacationShop.getCars());
		Assert.assertNotNull(fiatPunto);

	}

	@Test
	public void testLoadGekkoContextWithBeanRef() {
		/*
		 * given
		 */

		/*
		 * when
		 */
		GekkoApplicationContext context = new GekkoApplicationContext("gekko/service-cars.xml");

		CarShopDirectory directory = (CarShopDirectory) context.getBean("car-shop-directory");
		SportCarShop sportShop = (SportCarShop) context.getBean("sport-car-shop");
		VacationCarShop vacationShop = (VacationCarShop) context.getBean("vacation-car-shop");

		/*
		 * should
		 */
		Assert.assertNotNull(directory);
		Assert.assertNotNull(vacationShop);
		Assert.assertNotNull(sportShop);
		Assert.assertEquals(sportShop, directory.getCarShop(CarShopType.sport));
		Assert.assertEquals(vacationShop, directory.getCarShop(CarShopType.vacation));
		Assert.assertEquals(directory.getDefaultShop(), sportShop);

	}

	@Test
	public void testLoadGekkoContextWithAutowiring() {
		/*
		 * given
		 */

		/*
		 * when
		 */
		GekkoApplicationContext context = new GekkoApplicationContext("gekko/service-cars.xml");

		CarShopDirectory directory = (CarShopDirectory) context.getBean("car-shop-directory#1");
		SportCarShop sportShop = (SportCarShop) context.getBean("sport-car-shop");
		VacationCarShop vacationShop = (VacationCarShop) context.getBean("vacation-car-shop");

		/*
		 * should
		 */
		Assert.assertNotNull(directory);
		Assert.assertNotNull(vacationShop);
		Assert.assertNotNull(sportShop);
		Assert.assertEquals(sportShop, directory.getCarShop(CarShopType.sport));
		Assert.assertEquals(vacationShop, directory.getCarShop(CarShopType.vacation));
		Assert.assertEquals(directory.getDefaultShop(), sportShop);

	}
}
