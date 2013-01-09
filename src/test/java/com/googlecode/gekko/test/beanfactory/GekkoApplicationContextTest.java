package com.googlecode.gekko.test.beanfactory;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.googlecode.gekko.beanfactory.GekkoApplicationContext;
import com.googlecode.gekko.test.service.cars.CarService;
import com.googlecode.gekko.test.service.cars.DelegatingCarService;
import com.googlecode.gekko.test.service.cars.small.FiatPuntoService;
import com.googlecode.gekko.test.service.shops.CarShopDirectory;
import com.googlecode.gekko.test.service.shops.CarShopType;
import com.googlecode.gekko.test.service.shops.SportCarShop;
import com.googlecode.gekko.test.service.shops.VacationCarShop;

public class GekkoApplicationContextTest
{

	@Test
	@Ignore
	public void testLoadGekkoContext()
	{
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
	@Ignore
	public void testLoadGekkoContextWithBeanRef()
	{
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
	@Ignore
	public void testLoadGekkoContextWithAutowiring()
	{
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
