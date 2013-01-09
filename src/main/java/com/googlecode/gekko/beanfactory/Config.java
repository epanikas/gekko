package com.googlecode.gekko.beanfactory;

import java.util.HashSet;
import java.util.Set;

public class Config
{

	private Set<String> packagesToScan = new HashSet<String>();

	public void setPackagesToScan(Set<String> packagesToScan)
	{
		this.packagesToScan.addAll(packagesToScan);
	}

	public Set<String> getPackagesToScan()
	{
		return packagesToScan;
	}

}
