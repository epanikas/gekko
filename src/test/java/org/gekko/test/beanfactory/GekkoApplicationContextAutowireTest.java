package org.gekko.test.beanfactory;

import org.gekko.beanfactory.GekkoApplicationContext;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;

public class GekkoApplicationContextAutowireTest {

	@Test
	public void testLoadGekkoContext() {
		GekkoApplicationContext context = new GekkoApplicationContext("gekko/autowiring-service-cars1.xml");
	}

	@Test(expected = BeanCreationException.class)
	public void testLoadGekkoContext2() {
		GekkoApplicationContext context = new GekkoApplicationContext("gekko/autowiring-service-cars2.xml");
	}

	@Test
	public void testLoadGekkoContext3() {
		GekkoApplicationContext context = new GekkoApplicationContext("gekko/autowiring-service-cars3.xml");
	}

	@Test(expected = BeanCreationException.class)
	public void testLoadGekkoContext4() {
		GekkoApplicationContext context = new GekkoApplicationContext("gekko/autowiring-service-cars4.xml");
	}

	@Test
	public void testLoadGekkoContext5() {
		GekkoApplicationContext context = new GekkoApplicationContext("gekko/autowiring-service-cars5.xml");
	}

	@Test(expected = BeanCreationException.class)
	public void testLoadGekkoContext6() {
		GekkoApplicationContext context = new GekkoApplicationContext("gekko/autowiring-service-cars6.xml");
	}

	@Test
	public void testLoadGekkoContext7() {
		GekkoApplicationContext context = new GekkoApplicationContext("gekko/autowiring-service-cars7.xml");
	}

}
