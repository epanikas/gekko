package org.gekko.beanfactory;

import java.util.Set;

public class QualifiedBean {

	private String beanName;
	private Set<String> qualifiers;

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public Set<String> getQualifiers() {
		return qualifiers;
	}

	public void setQualifiers(Set<String> qualifiers) {
		this.qualifiers = qualifiers;
	}

}
