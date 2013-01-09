package com.googlecode.gekko.beanfactory;

import org.springframework.util.Assert;

public class BeanRef
{

	private String ref;
	private String autowired;
	private String qualified;

	public BeanRef()
	{

	}

	public BeanRef(String ref)
	{
		super();
		this.ref = ref;
	}

	public BeanRef(String autowired, String qualified)
	{
		super();
		this.autowired = autowired;
		this.qualified = qualified;
	}

	public String getRef()
	{
		return ref;
	}

	public void setRef(String ref)
	{
		this.ref = ref;
	}

	public String getAutowired()
	{
		return autowired != null ? autowired : (qualified != null ? "required" : null);
	}

	public void setAutowired(String autowired)
	{
		this.autowired = autowired;
	}

	public String getQualified()
	{
		return qualified;
	}

	public void setQualified(String qualified)
	{
		this.qualified = qualified;
	}

	public void validate()
	{
		Assert.isTrue(ref != null || autowired != null || qualified != null);
		if (ref != null) {
			Assert.isTrue(autowired == null && qualified == null);
		}
		else {
			Assert.isTrue(autowired != null || qualified != null);
		}
	}

	@Override
	public String toString()
	{
		return BeanRef.class.getName() + "[ref: " + ref + ", autowired: " + autowired + ", qualified: " + qualified
			+ "]";
	}
}
