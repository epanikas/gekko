=Hands on=

Gekko is a small project to extend the standard Spring's XML configuration functionalities by allowing for more succinct and Json-like way of configuration.

Here is an example.
a standard bean definition in Spring XML would something like this:
{{{
  <bean class="com.googlecode.gekko.test.service.shops.SportCarShop">
    <property name="shopType" value="sport" />
  </bean>
}}}

Using Gekko, this bean definition is reduced to the following:

{{{
  <gk:sport-car-shop shop-type="sport" />
}}}

=Introduction=

If you are familiar with the popular Spring framework, you might have already asked yourself whether it is inevitable that the XML configuration files are so verbose and not easily maintainable.

In this bean definition we can easily distinguish standard XML blocks that belong to the XML schema, and are "boler plate": they add nothing relevant to the specificity of the bean definition. So let's strip them out! This will yield a definition like this (already shown above)

{{{
  <gk:SportCarShop shopType="sport" />
}}}

or, in dashed notation, which is somewhat more suitable for XML

{{{
  <gk:sport-car-shop shop-type="sport" />
}}}

We simply eliminate all the XML "boiler plate" configuration, living only the essential stuff, that is actually specific for the bean we are defining. In this case it is the bean's class and the property value. 

Gekko will resolve the class name by scanning the provided packages, and it will create a standard Spring bean definition corresponding to that. This definition will be used later on by the standard Spring bean instantiation mechanism. Gekko will also invent an id for this bean, which is identical to the name of the XML element, which is in our case sport-car-shop.

That might not look like a big improvement. Indeed, we can read 3 lines of XML code without serious brain damage. However, big XML files with lots of interconnected beans are not rare in the industry, and in this case the difference can be striking. 

So, a bean class in Gekko can be defined in 3 ways: 
 * fully qualified class name: _`com.googlecode.gekko.test.service.cars.SportCarShop`_
 * simple name of a class: _`SportCarShop`_
 * or its name in a dashed notation: _`sport-car-shop`_
In the two latter cases the provided package names will be scanned to resolve the full class name.

Likewise, a property of a bean can be accessed either by its real name, or by its name in the dashed notation. 

 * standard Java bean property name: _`shopType`_
 * property name in dashed notation: _`shop-type`_

Gekko relies heavily on Json for concise definition of common data structures such as list, map and set. Thanks to the corresponding property editors these data structures can be defined in a much more succinct way.

To demonstrate all this, let's have a look at the examples

=Examples=

Here are few more quick examples:

The following bean definitions are equivalent
{{{
  <gk:SportCarShop />

  <gk:com.googlecode.gekko.test.service.cars.SportCarShop />

  <gk:sport-car-shop />
}}}

A property can be accessed in two ways: standard bean notation, and dashed notation
{{{
  <gk:sport-car-shop shopType="sport" />

  <gk:sport-car-shop shop-type="sport" />
}}}

Specify the desired id for a bean
{{{
  <gk:sport-car-shop gk:id="sport-shop" />
}}}

List and map in Json notation
{{{
  <gk:sport-car-shop employees="[John, Alex, Bob]" />

  <gk:vacation-car-service employeesByJob="{guru: Alex, manager: Bob, technician: John}"/>
}}}

===Referencing a bean=== 
{{{
  <gk:car-service />
}}}

 * direct reference: _`{ref: car-service}`_
 * autowiring: _`{autowired: required/optional}`_
 * qualified autowiring:  _`{autowired: required/optional, qualified: my-qualifier}`_

In case of qualified autowiring, the autowired term is optional; it is assumed to be _`required`_ by default. However, in case of normal autowiring, when _`qualified`_ is not used, the _`autowired`_ term is mandatory, with _`required`_ or _`optional`_ explicitly specified.

These references can be used directly as a property value
{{{
  <gk:sport-car-shop car-service="{ref: car-service}" />
}}}

Or they can be used in lists and maps
{{{
  <gk:sport-car-service gk:id="sport-service" />
  <gk:vacation-car-service gk:id="vacation-service" />

  <gk:sport-car-shop car-services="[{ref: sport-service}, {ref: vacation-service}]" />

  <gk:sport-car-shop car-service-by-type="{sport: {ref: sport-service}, vacation: {ref: vacation}}" />
}}}

Autowiring
{{{
  <gk:sport-car-service gk:id="sport-service" />

  <gk:sport-car-shop car-service="{autowired: required}" />

  <gk:vacation-car-shop car-service="{autowired: optional}" />
}}}

Autowiring with qualifiers
{{{
  <gk:sport-car-service gk:qualifiers="[sport]" />
  <gk:vacation-car-service gk:qualifiers="[vacation]" />

  <gk:sport-car-shop car-service="{qualified: sport}" />

  <gk:sport-car-shop car-services="[{qualified: sport}, {qualified: vacation}]" />

  <gk:sport-car-shop car-service-by-type="{
    sport: {qualified: sport}, vacation: {autowired: optional, qualified: vacation}
  }" />
}}}

As has been mentioned above, if the _`autowired`_ term is omitted, it is considered as required.

Gekko allows to specify several qualifiers for a single bean:
{{{
  <gk:emergency-car-service gk:qualifiers="[sport, vacation]" />

  <gk:sport-car-shop car-service="{qualified: sport}" />

  <gk:vacation-car-shop car-services="{qualified: vacation}" />
}}}
in this case both car shops - _`sport`_ and _`vacation`_ - will refer to the same car service - _`emergency-car-service`_.


=Under the Hood=

The main idea behind the Gekko project is to allow for shorter XML configuration files by fully leveraging the XML's ability to define complex objects. Let's map a Java class to an XML block, named accordingly. Naturally the properties of this class would map to standard XML attributes. Like in the example above, the Java class _`SportCarShop`_ is mapped to the XML block _`<sport-car-shop>`_, while its property `shopType` naturally becomes an attribute with the same name.

The Gekko style can seamlessly integrated in a standard Srping XML configuration file. 

So, using Gekko, to define a bean in a Spring XML file one should define an XML element with the name corresponding to that class. The following conventions apply to class name resolution given an XML element:
  * if the XML element name contains dashes, they are removed, and each letter following the dashes is capitalized. So `sport-car-shop` becomes `SportCarShop`.

To allow for the most succint bean definitions Gekko defines several conventions and defaults:

