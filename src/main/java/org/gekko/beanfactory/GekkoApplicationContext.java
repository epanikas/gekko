package org.gekko.beanfactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GekkoApplicationContext
	extends ClassPathXmlApplicationContext
{

	private final boolean registerJsonPropertyEditors;

	public GekkoApplicationContext(String configLocation) throws BeansException
	{
		super(configLocation);
		registerJsonPropertyEditors = true;
	}

	public GekkoApplicationContext(String configLocation, boolean registerJsonPropertyEditors) throws BeansException
	{
		super(configLocation);
		this.registerJsonPropertyEditors = registerJsonPropertyEditors;
	}

	@Override
	protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader)
	{
		setValidating(false);
		reader.setNamespaceHandlerResolver(new GekkoNamespaceHandlerResolver(new DefaultNamespaceHandlerResolver(
			getClassLoader())));
		super.initBeanDefinitionReader(reader);
	}

	@Override
	protected DefaultListableBeanFactory createBeanFactory()
	{
		//		DefaultListableBeanFactory beanFactory = super.createBeanFactory();
		GekkoBeanFactory beanFactory = new GekkoBeanFactory(getInternalParentBeanFactory());
		beanFactory.setConversionService(new BeanRefConversionService(beanFactory));
		if (registerJsonPropertyEditors) {
			beanFactory.addPropertyEditorRegistrar(new GekkoPropertyEditorRegistrar());
		}
		return beanFactory;
	}

}
