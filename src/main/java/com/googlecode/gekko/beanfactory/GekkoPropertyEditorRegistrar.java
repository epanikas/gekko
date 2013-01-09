package com.googlecode.gekko.beanfactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;

import com.googlecode.gekko.beanfactory.propertyeditor.JsonPropertyEditorForList;
import com.googlecode.gekko.beanfactory.propertyeditor.JsonPropertyEditorForMap;
import com.googlecode.gekko.beanfactory.propertyeditor.JsonPropertyEditorForSet;

public class GekkoPropertyEditorRegistrar implements PropertyEditorRegistrar {

	@Override
	public void registerCustomEditors(PropertyEditorRegistry registry) {
		registry.registerCustomEditor(Set.class, new JsonPropertyEditorForSet());
		registry.registerCustomEditor(List.class, new JsonPropertyEditorForList());
		registry.registerCustomEditor(Map.class, new JsonPropertyEditorForMap());
	}

}
