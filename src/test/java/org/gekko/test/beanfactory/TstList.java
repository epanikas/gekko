package org.gekko.test.beanfactory;

import org.gekko.beanfactory.GekkoApplicationContext;
import org.junit.Test;

public class TstList
{

	@Test
	public void testLoadGekkoContext()
	{
		GekkoApplicationContext context = new GekkoApplicationContext("gekko/list-test.xml");
	}
}
