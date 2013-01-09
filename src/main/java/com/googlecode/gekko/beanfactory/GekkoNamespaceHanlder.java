package com.googlecode.gekko.beanfactory;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GekkoNamespaceHanlder
	implements NamespaceHandler
{
	public static final Set<String> BOOTSTRAP_PACKAGES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(//
		Config.class.getPackage().getName(), //
		CustomEditorConfigurer.class.getPackage().getName())));

	private final Gson gson = new GsonBuilder().create();

	private final Type setOfStringsType = new ParameterizedType() {

		@Override
		public Type getRawType()
		{
			return Set.class;
		}

		@Override
		public Type getOwnerType()
		{
			return null;
		}

		@Override
		public Type[] getActualTypeArguments()
		{
			return new Class[]{String.class};
		}
	};

	@Override
	public void init()
	{
		throw new UnsupportedOperationException("init not implemented");
	}

	private BeanDefinition getBeanDefSilently(BeanDefinitionRegistry registry, String beanName)
	{
		try {
			return registry.getBeanDefinition(beanName);
		}
		catch (Exception e) {
			return null;
		}
	}

	@Override
	public BeanDefinition parse(Element ele, ParserContext parserContext)
	{
		return parse1(ele, parserContext).getBeanDefinition();
	}

	public BeanDefinitionHolder parse1(Element ele, ParserContext parserContext)
	{
		Assert.isTrue("http://www.gekko.org/schema/gk".equals(ele.getNamespaceURI()), "the element " + ele
			+ " is not in the gk namespace");

		BeanDefinitionHolder bdh = registerBeanDefinition(ele, parserContext);

		NodeList nl = ele.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element) {
				Element el = (Element) node;
				/*
				 * the bean class should be resolved by now
				 */
				Class<?> beanCls =
					findClass(bdh.getBeanDefinition().getBeanClassName(), Collections.<String> emptySet());
				PropertyDescriptor propertyDesc = findPropertyName(beanCls, el.getNodeName().substring(3));
				//				BeanDefinitionHolder propertyBd = handleProperty(el, parserContext);
				Object propValue = handleProperty(el, propertyDesc, parserContext);
				MutablePropertyValues mpv = bdh.getBeanDefinition().getPropertyValues();
				mpv.addPropertyValue(new PropertyValue(propertyDesc.getName(), propValue));
				//(propertyName, new RuntimeBeanReference(propertyBd.getBeanName()));

			}
		}

		return bdh;
	}

	private PropertyDescriptor findPropertyName(Class<?> beanClass, String propertyName)
	{
		PropertyDescriptor desc = BeanUtils.getPropertyDescriptor(beanClass, propertyName);
		if (desc != null) {
			return desc;
		}

		String propertyName1 = capitalizeDashes(propertyName, false);
		desc = BeanUtils.getPropertyDescriptor(beanClass, propertyName1);
		if (desc != null) {
			return desc;
		}

		throw new IllegalArgumentException("can't find property " + propertyName + " for class " + beanClass);
	}

	private Object handleProperty(Element ele, PropertyDescriptor descriptor, ParserContext parserContext)
	{
		List<BeanRef> lstBeans = null;
		Map<String, BeanRef> mapBeans = null;
		BeanRef beanRef = new BeanRef();
		if (List.class.isAssignableFrom(descriptor.getPropertyType())) {
			lstBeans = new ArrayList<BeanRef>();
		}
		else if (Map.class.isAssignableFrom(descriptor.getPropertyType())) {
			mapBeans = new HashMap<String, BeanRef>();
		}
		else {
			// it's a reference to a single bean
		}

		NodeList nl = ele.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element) {
				Element el = (Element) node;
				Node nodeMapKey = el.getAttributes().getNamedItem("gk:map-key");
				String mapKey = null;
				if (nodeMapKey != null) {
					mapKey = nodeMapKey.getNodeValue();
				}
				BeanDefinitionHolder bdh = registerBeanDefinition(el, parserContext);
				if (mapKey != null) {
					Assert.isTrue(lstBeans == null && mapBeans != null && beanRef.getRef() == null,
						"can't mix list and map entries");
					if (mapBeans != null) {
						mapBeans.put(mapKey, new BeanRef(bdh.getBeanName()));
					}
				}
				else if (lstBeans != null) {
					Assert.isTrue(mapBeans == null && beanRef.getRef() == null, "can't mix list and map entries");
					lstBeans.add(new BeanRef(bdh.getBeanName()));
				}
				else {
					Assert.isTrue(beanRef.getRef() == null && mapBeans == null,
						"can't have more than one entry in bean ref");
					beanRef = new BeanRef(bdh.getBeanName());
				}
			}
		}

		if (mapBeans != null) {
			return mapBeans;
		}

		if (lstBeans != null) {
			return lstBeans;
		}

		return beanRef;
	}

	private BeanDefinitionHolder registerBeanDefinition(Element ele, ParserContext parserContext)
	{
		NamedNodeMap attrs = ele.getAttributes();

		String elemName = ele.getNodeName().substring(3);
		String beanName = "";
		if (attrs.getNamedItem("gk:id") != null) {
			/*
			 * take the given name directly
			 */
			beanName = attrs.getNamedItem("gk:id").getNodeValue();
		}
		else {
			/*
			 * the element name is gk:xxx, so skip the namespace
			 */
			beanName = elemName;
			int i = 0;
			while (getBeanDefSilently(parserContext.getRegistry(), beanName) != null) {
				beanName = elemName + "#" + ++i;
			}
		}

		GenericBeanDefinition bd = new GenericBeanDefinition();
		bd.setAbstract(false);
		bd.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
		bd.setAutowireCandidate(true);
		bd.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO);

		/*
		 * handle qualifiers
		 */
		if (attrs.getNamedItem("gk:qualifiers") != null) {
			String strQualifiers = attrs.getNamedItem("gk:qualifiers").getNodeValue();

			Set<String> qualifiers = gson.fromJson(strQualifiers, setOfStringsType);

			//			for (String qualif : qualifiers) {
			//				AutowireCandidateQualifier q1 = new AutowireCandidateQualifier(Qualifier.class, qualif);
			//				bd.addQualifier(q1);
			//			}
			registerQualifiedBean(beanName, qualifiers, parserContext.getRegistry());
		}

		Set<String> packagesToScan = getPackagesToScan(parserContext);

		Class<?> beanCls = null;
		try {
			beanCls = findClass(elemName, packagesToScan);
		}
		catch (Exception e) {
			beanCls = findClass(capitalizeDashes(elemName, true), packagesToScan);
		}
		bd.setBeanClass(beanCls);
		bd.setBeanClassName(beanCls.getName());

		MutablePropertyValues mpv = new MutablePropertyValues();
		for (int i = 0; i < attrs.getLength(); ++i) {
			Node nd = attrs.item(i);
			String propName = nd.getNodeName();
			if (propName.indexOf("gk:") >= 0) {
				continue;
			}

			String propValue = nd.getNodeValue();

			BeanRef ref = null;
			if (propValue.matches("\\{ *autowired *:.*\\}") || //
				propValue.matches("\\{ *qualified *:.*\\}") || //
				propValue.matches("\\{ *ref *:.*\\}")) {
				ref = gson.fromJson(propValue, BeanRef.class);
				ref.validate();
			}

			PropertyValue pv = null;

			if (ref != null) {
				/*
				 * it's a special value, that will be resolved to a bean later
				 */
				pv = new PropertyValue(propName, ref);
			}
			else {
				/*
				 * it's a standard string value
				 */
				pv = new PropertyValue(propName, propValue);
			}
			mpv.addPropertyValue(pv);
		}
		bd.setPropertyValues(mpv);

		BeanDefinitionHolder bdh = new BeanDefinitionHolder(bd, beanName);
		BeanDefinitionReaderUtils.registerBeanDefinition(bdh, parserContext.getRegistry());

		return bdh;

	}

	private void registerQualifiedBean(String beanName, Set<String> qualifiers, BeanDefinitionRegistry registry)
	{

		String qBeanName = "qualified:" + beanName;

		GenericBeanDefinition bd = new GenericBeanDefinition();
		bd.setAbstract(false);
		bd.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
		bd.setAutowireCandidate(true);
		bd.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

		bd.setBeanClass(QualifiedBean.class);
		bd.setBeanClassName(QualifiedBean.class.getName());

		MutablePropertyValues mpv = new MutablePropertyValues();
		mpv.addPropertyValue(new PropertyValue("beanName", beanName));
		mpv.addPropertyValue(new PropertyValue("qualifiers", qualifiers));
		bd.setPropertyValues(mpv);

		BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(bd, qBeanName), registry);

	}

	private Set<String> getPackagesToScan(ParserContext parserContext)
	{
		Set<String> packagesToScan = new HashSet<String>(BOOTSTRAP_PACKAGES);

		try {
			BeanDefinition gkConfig = parserContext.getRegistry().getBeanDefinition("config");
			if (gkConfig != null) {
				if (gkConfig.getPropertyValues() != null) {
					PropertyValue pkgToScanProperty = gkConfig.getPropertyValues().getPropertyValue("packagesToScan");
					if (pkgToScanProperty != null) {
						packagesToScan.addAll((Set<String>) gson.fromJson((String) pkgToScanProperty.getValue(),
							setOfStringsType));
					}
				}
			}
		}
		catch (Exception e) {
			// silence...
		}

		/*
		 * populate the packages to scan recursively
		 */
		Set<String> res = new HashSet<String>(packagesToScan);
		for (String pkg : packagesToScan) {
			if (pkg.lastIndexOf(".") == pkg.length() - 1) {
				extractPackagesRecursively(pkg.substring(0, pkg.length() - 1), res);
			}
		}
		packagesToScan.addAll(res);

		return packagesToScan;
	}

	private void extractPackagesRecursively(String pkg, Set<String> packagesToScan)
	{
		/*
		 * reference code: AnnotationSessionFactoryBean.scanPackages
		 */
		final String RESOURCE_PATTERN = "/**";

		try {
			String pattern =
				ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(pkg)
					+ RESOURCE_PATTERN;
			ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
			Resource[] resources = resourcePatternResolver.getResources(pattern);

			for (Resource resource : resources) {
				if (resource.isReadable() == false) {
					String filename = resource.getFilename();
					String pkgToAdd = pkg + "." + filename;
					packagesToScan.add(pkgToAdd);
					extractPackagesRecursively(pkgToAdd, packagesToScan);
				}
			}
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Failed to scan classpath for unlisted classes", ex);
		}
	}

	@Override
	public BeanDefinitionHolder decorate(Node source, BeanDefinitionHolder definition, ParserContext parserContext)
	{
		throw new UnsupportedOperationException("decorate not implemented");
	}

	private String capitalizeDashes(String elemName, boolean capitalizeFirst)
	{
		String className = "";

		String[] chunks = elemName.split("-");
		boolean first = true;
		for (String chunk : chunks) {
			if (!capitalizeFirst && first) {
				className += chunk;
			}
			else {
				className += WordUtils.capitalize(chunk);
			}
			first = false;
		}

		return className;
	}

	private Class<?> findClass(String className, Set<String> packagesToScan)
	{

		try {
			return Class.forName(className);
		}
		catch (ClassNotFoundException e) {
			// silently continue...
		}

		for (String pkg : packagesToScan) {
			try {
				return Class.forName(pkg + '.' + className);
			}
			catch (ClassNotFoundException e) {
				// silently continue...
			}
		}

		throw new IllegalArgumentException("can't find class for " + className + " in packages " + packagesToScan);
	}
}
