package com.googlecode.gekko.beanfactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public class GekkoBeanFactory
	extends DefaultListableBeanFactory
{

	public GekkoBeanFactory()
	{
		super();
	}

	public GekkoBeanFactory(BeanFactory parentBeanFactory)
	{
		super(parentBeanFactory);
	}

	public Object resolveAutowired(Class<?> targetCls, BeanRef beanRef)
	{

		/*
		 * try first the spring's standard autowiring mechanism
		 */
		DependencyDescriptor descriptor = createBeanRefDescriptor(beanRef, targetCls);

		//		Map<String, Object> matchingBeans = super.findAutowireCandidates(beanRef.getSourceBean(), targetCls, descriptor);
		//
		//		if (matchingBeans.size() == 1) {
		//			return matchingBeans.values().iterator().next();
		//		}
		//
		//		if (matchingBeans.size() > 1) {
		//			throw new NoSuchBeanDefinitionException(targetCls, "expected single matching bean but found " + matchingBeans.size() + ": "
		//					+ matchingBeans.keySet());
		//		}

		List<Object> res = new ArrayList<Object>();
		List<String> retainedCandidateNames = new ArrayList<String>();

		Object standardResolved = null;
		try {
			standardResolved = resolveDependency(descriptor, null, null, null);
			if (standardResolved != null) {
				res.add(standardResolved);
			}
			Map<String, Object> m = findAutowireCandidates(null, targetCls, descriptor);
			for (Map.Entry<String, Object> e : m.entrySet()) {
				if (e.getValue() == standardResolved) {
					retainedCandidateNames.add(e.getKey());
				}
			}
		}
		catch (NoSuchBeanDefinitionException e) {
			if (e.getMessage().indexOf("expected at least 1 bean") < 0) {
				throw e;
			}
		}

		/*
		 * if it didn't work out, try gekko's qualifiers, if applicable
		 */
		if (beanRef.getQualified() != null) {
			String[] candidateNames =
				BeanFactoryUtils.beanNamesForTypeIncludingAncestors(this, targetCls, true, descriptor.isEager());

			for (String candidate : candidateNames) {
				QualifiedBean b = null;
				try {
					b = (QualifiedBean) getBean("qualified:" + candidate);
				}
				catch (Exception e) {
					continue;
				}
				if (b.getQualifiers().contains(beanRef.getQualified())) {
					Object beanCandidate = getBean(candidate);
					if (CollectionUtils.cardinality(beanCandidate, res) == 0) {
						res.add(beanCandidate);
						retainedCandidateNames.add(candidate);
					}
				}
			}
		}

		if (res.size() == 1) {
			return res.get(0);
		}

		if (res.size() == 0) {
			if (descriptor.isRequired()) {
				throw new NoSuchBeanDefinitionException(targetCls, "",
					"expected at least 1 bean which qualifies as autowire candidate for this dependency "
						+ "qualified as : " + beanRef.getQualified());
			}
			return null;
		}

		throw new NoSuchBeanDefinitionException(targetCls, "expected single matching bean but found " + res.size()
			+ ": " + retainedCandidateNames);
	}

	private DependencyDescriptor createBeanRefDescriptor(BeanRef beanRef, Class<?> targetCls)
	{
		try {
			return new BeanRefDependencyDescriptor(beanRef, targetCls);
		}
		catch (SecurityException e) {
			throw new IllegalArgumentException(e);
		}
		catch (NoSuchFieldException e) {
			throw new IllegalArgumentException(e);
		}

	}
}
