package org.gekko.beanfactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.DependencyDescriptor;

public class BeanRefDependencyDescriptor extends DependencyDescriptor {

	private static final long serialVersionUID = 7205879577517496151L;

	private final BeanRef beanRef;
	private final Class<?> targetCls;

	public final String defField = null;

	public BeanRefDependencyDescriptor(BeanRef beanRef, Class<?> targetCls) throws SecurityException, NoSuchFieldException {
		super(BeanRefDependencyDescriptor.class.getField("defField"), true, true);
		this.beanRef = beanRef;
		this.targetCls = targetCls;
	}

	/**
	 * Return whether this dependency is required.
	 */
	@Override
	public boolean isRequired() {
		return "required".equals(beanRef.getAutowired());
	}

	@Override
	public boolean isEager() {
		return true;
	}

	@Override
	public String getDependencyName() {
		return null;/*beanRef.getSourceBean();*/
	}

	@Override
	public Class<?> getDependencyType() {
		return targetCls;
	}

	@Override
	public Type getGenericDependencyType() {
		return null;
	}

	@Override
	public Class<?> getCollectionType() {
		return null;
	}

	@Override
	public Class<?> getMapKeyType() {
		return null;
	}

	@Override
	public Class<?> getMapValueType() {
		return null;
	}

	@Override
	public Annotation[] getAnnotations() {
		if (beanRef.getQualified() != null) {
			final String qualified = beanRef.getQualified();
			return new Annotation[] {new Qualifier() {

				@Override
				public Class<? extends Annotation> annotationType() {
					return Qualifier.class;
				}

				@Override
				public String value() {
					return qualified;
				}

				@Override
				public String toString() {
					return BeanRefDependencyDescriptor.class.getName() + "." + Qualifier.class.getSimpleName() + "[" + qualified + "]";
				}

			}};
		}

		return new Annotation[] {};
	}

	@Override
	public String toString() {
		return beanRef + ": " + targetCls;
	}
}
