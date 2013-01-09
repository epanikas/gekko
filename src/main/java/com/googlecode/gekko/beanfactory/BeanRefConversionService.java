package com.googlecode.gekko.beanfactory;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

public class BeanRefConversionService implements ConversionService {

	private final GekkoBeanFactory hostBeanFactory;

	public BeanRefConversionService(GekkoBeanFactory hostBeanFactory) {
		super();
		this.hostBeanFactory = hostBeanFactory;
	}

	@Override
	public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
		return sourceType.isAssignableFrom(BeanRef.class);
	}

	@Override
	public <T> T convert(Object source, Class<T> targetType) {
		throw new UnsupportedOperationException("convert not implemented");
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		//		/*
		//		 * this kind of conversion is only for strings
		//		 */
		//		if (String.class.isAssignableFrom(sourceType.getType()) == false) {
		//			return false;
		//		}
		//
		//		/*
		//		 * this kind of conversion is only for interfaces
		//		 */
		//		if (targetType.getType().isInterface() == false) {
		//			return false;
		//		}
		//
		//		/*
		//		 * skip the creation of java standard interfaces
		//		 */
		//		if (targetType.getType().getPackage().getName().indexOf("java.") == 0) {
		//			return false;
		//		}
		//
		//		/*
		//		 * guess if there is a custom editor that would be invoked for this type
		//		 */
		//		PropertyEditorRegistry registry = new PropertyEditorRegistrySupport();
		//		Set<PropertyEditorRegistrar> registrars = hostBeanFactory.getPropertyEditorRegistrars();
		//		for (PropertyEditorRegistrar r : registrars) {
		//			r.registerCustomEditors(registry);
		//		}
		//		PropertyEditor existingPropEditor = registry.findCustomEditor(targetType.getType(), null);
		//
		//		if (existingPropEditor != null) {
		//			/*
		//			 * let the custom editor do the work
		//			 */
		//			return false;
		//		}
		//		return true;

		return sourceType.getType().isAssignableFrom(BeanRef.class);

	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		BeanRef beanRef = (BeanRef) source;
		String ref = beanRef.getRef();
		String autowired = beanRef.getAutowired();
		String qualified = beanRef.getQualified();

		if (ref != null && ref.length() > 0) {
			return hostBeanFactory.getBean(ref);
		}

		if ((autowired != null && autowired.length() > 0) || //
				(qualified != null && qualified.length() > 0)) {
			return hostBeanFactory.resolveAutowired(targetType.getType(), beanRef);
		}

		throw new IllegalArgumentException("can't resolve bean with ref: " + beanRef);
	}
}
