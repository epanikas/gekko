package com.googlecode.gekko.test.beanfactory;

import org.junit.Test;

import com.googlecode.gekko.beanfactory.GekkoApplicationContext;

public class TstList
{

	@Test
	public void testLoadGekkoContext()
	{
		GekkoApplicationContext context = new GekkoApplicationContext("gekko/list-test.xml");
	}
}
