package org.gekko.beanfactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gekko.beanfactory.propertyeditor.JsonPropertyEditorForList;
import org.gekko.beanfactory.propertyeditor.JsonPropertyEditorForMap;
import org.gekko.beanfactory.propertyeditor.JsonPropertyEditorForSet;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;

public class GekkoPropertyEditorRegistrar implements PropertyEditorRegistrar {

	@Override
	public void registerCustomEditors(PropertyEditorRegistry registry) {
		registry.registerCustomEditor(Set.class, new JsonPropertyEditorForSet());
		registry.registerCustomEditor(List.class, new JsonPropertyEditorForList());
		registry.registerCustomEditor(Map.class, new JsonPropertyEditorForMap());
	}

}
