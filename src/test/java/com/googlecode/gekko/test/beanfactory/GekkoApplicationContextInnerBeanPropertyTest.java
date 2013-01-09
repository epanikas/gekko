package com.googlecode.gekko.test.beanfactory;

import java.util.List;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.BeanDefinitionStoreException;

import com.googlecode.gekko.beanfactory.GekkoApplicationContext;
import com.googlecode.gekko.test.service.shops.CarShop;
import com.googlecode.gekko.test.service.shops.CarShopDirectory;
import com.googlecode.gekko.test.service.shops.CarShopType;

public class GekkoApplicationContextInnerBeanPropertyTest
{

	@Test
	public void testLoadGekkoContext()
	{
		/*
		 * when
		 */
		GekkoApplicationContext context = new GekkoApplicationContext("gekko/inner-bean-definition-test.xml");
		CarShopDirectory directory = (CarShopDirectory) context.getBean("dir1");

		/*
		 * should
		 */
		CarShop sportShop = (CarShop) context.getBean("sport-car-shop");
		Assert.assertEquals(directory.getDefaultShop(), sportShop);

	}

	@Test
	public void testLoadGekkoContext2()
	{
		/*
		 * when
		 */
		GekkoApplicationContext context = new GekkoApplicationContext("gekko/inner-bean-definition-test.xml");
		CarShopDirectory directory = (CarShopDirectory) context.getBean("dir2");

		/*
		 * should
		 */
		CarShop sportShop = (CarShop) context.getBean("vacation-car-shop");
		Assert.assertEquals(directory.getDefaultShop(), sportShop);

	}

	@Test
	public void testLoadGekkoContext3()
	{
		/*
		 * when
		 */
		GekkoApplicationContext context = new GekkoApplicationContext("gekko/inner-bean-definition-test.xml");
		CarShopDirectory directory = (CarShopDirectory) context.getBean("dir3");

		/*
		 * should
		 */
		CarShop sportShop = (CarShop) context.getBean("sport-car-shop#1");
		CarShop vacationShop = (CarShop) context.getBean("vacation-car-shop#1");
		List<CarShop> lst = directory.getShopList();
		Assert.assertEquals(sportShop, lst.get(0));
		Assert.assertEquals(vacationShop, lst.get(1));

	}

	@Test
	public void testLoadGekkoContext4()
	{
		/*
		 * when
		 */
		GekkoApplicationContext context = new GekkoApplicationContext("gekko/inner-bean-definition-test.xml");
		CarShopDirectory directory = (CarShopDirectory) context.getBean("dir3");

		/*
		 * should
		 */
		CarShop sportShop = (CarShop) context.getBean("sport-car-shop#1");
		CarShop vacationShop = (CarShop) context.getBean("vacation-car-shop#1");
		List<CarShop> lst = directory.getShopList();
		Assert.assertEquals(sportShop, lst.get(0));
		Assert.assertEquals(vacationShop, lst.get(1));

	}

	@Test
	public void testLoadGekkoContext5()
	{
		/*
		 * when
		 */
		GekkoApplicationContext context = new GekkoApplicationContext("gekko/inner-bean-definition-test.xml");
		CarShopDirectory directory = (CarShopDirectory) context.getBean("dir4");

		/*
		 * should
		 */
		CarShop sportShop = (CarShop) context.getBean("sport-car-shop#2");
		CarShop vacationShop = (CarShop) context.getBean("vacation-car-shop#2");
		Assert.assertEquals(sportShop, directory.getCarShop(CarShopType.sport));
		Assert.assertEquals(vacationShop, directory.getCarShop(CarShopType.vacation));

	}

	@Test(expected = BeanDefinitionStoreException.class)
	public void testLoadGekkoContextInError()
	{
		/*
		 * when
		 */
		GekkoApplicationContext context = new GekkoApplicationContext("gekko/inner-bean-definition-test-in-error.xml");

		/*
		 * should
		 */
		// should throw an exception

	}

	@Test(expected = BeanDefinitionStoreException.class)
	@Ignore
	public void testLoadGekkoContextInError2()
	{
		/*
		 * when
		 */
		GekkoApplicationContext context = new GekkoApplicationContext("gekko/inner-bean-definition-test-in-error2.xml");

		/*
		 * should
		 */
		// should throw an exception

	}

}
