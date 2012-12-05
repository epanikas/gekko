package org.gekko.beanfactory;

import org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.NamespaceHandlerResolver;

public class GekkoNamespaceHandlerResolver implements NamespaceHandlerResolver {

	private final DefaultNamespaceHandlerResolver defaultHandlerResolver;

	public GekkoNamespaceHandlerResolver(DefaultNamespaceHandlerResolver defaultHandlerResolver) {
		this.defaultHandlerResolver = defaultHandlerResolver;
	}

	@Override
	public NamespaceHandler resolve(String namespaceUri) {
		if (namespaceUri.equals("http://www.gekko.org/schema/gk")) {
			GekkoNamespaceHanlder handler = new GekkoNamespaceHanlder();
			return handler;
		}

		return defaultHandlerResolver.resolve(namespaceUri);
	}

}
