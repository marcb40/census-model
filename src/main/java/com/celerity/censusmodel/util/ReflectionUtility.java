/**
 * Property of Celerity Innovation Design Center.
 */
package com.celerity.censusmodel.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility for handling reflection methods.
 * 
 * @author Harry Ulrich
 * 
 */
public class ReflectionUtility {

    private static final Logger log = LoggerFactory.getLogger(ReflectionUtility.class);

    /**
     * Hidden constructor for utility class.
     */
    private ReflectionUtility() {

    }

    /**
     * Constructs a class with the given name, throwing a runtime exception on
     * failure.
     * 
     * @param className
     *            the class name to construct
     * @return the class
     */
    public static Class safelyGetClass(final String className) {
        Class c = null;

        try {
            c = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to find class with name : " + className + ".", e);
        }
        return c;
    }

    /**
     * Constructs an instance of this class.
     * 
     * @param <T>
     *            the type of instance
     * @param clazz
     *            the class
     * @return the default instance, if it possible
     */
    public static <T extends Object> T safelyConstruct(final Class<T> clazz) {
        T data = null;

        try {
            data = clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to construct: " + clazz.getName(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Constructor is not accessible : " + clazz.getName(), e);
        }

        return data;
    }

    /**
     * Sets the value for the given write method. This method attempts to
     * transform the given value to the expected value.
     * 
     * @param target
     *            the target object
     * @param descriptor
     *            the property descriptor
     * @param value
     *            the value to write
     */
    public static void setValue(final Object target, final PropertyDescriptor descriptor, Object value) {
    	Class returnType = descriptor.getReadMethod().getReturnType();
        Class returnTypeClass = descriptor.getReadMethod().getReturnType().getClass();
        if (String.class.equals(returnTypeClass)) {
            value = "" + value;
        } else if (Enum.class.isAssignableFrom(returnTypeClass) && !Enum.class.isAssignableFrom(value.getClass())) {
            value = Enum.valueOf(returnTypeClass, "" + value);
        } else if (returnType.isEnum() && value.getClass().isEnum()) {
        	value = Enum.valueOf((Class<Enum>)returnType, value.toString());
        } else if (returnType.isEnum() && String.class.equals(value.getClass())) {
        	value = Enum.valueOf((Class<Enum>)returnType, (String)value);
        }
        ReflectionUtility.safelyInvoke(descriptor.getWriteMethod(), target, value);

    }

    /**
     * Attempts to find the property with the given property name.
     * 
     * @param name
     *            the property name
     * @param target
     *            the target object
     * @return the descriptor if found
     */
    public static PropertyDescriptor findPropertyWithName(final String name, final Object target) {
        PropertyDescriptor descriptor = null;
        PropertyDescriptor[] descriptors = ReflectionUtility.getPropertyDescriptors(target.getClass());
        for (PropertyDescriptor desc : descriptors) {
            if (name.equals(desc.getName())) {
                descriptor = desc;
                break;
            }
        }
        return descriptor;
    }

    /**
     * Copies the properties from the source bean to the target bean.
     * 
     * @param source
     *            the source bean
     * @param target
     *            the target bean
     */
    public static void copyBeans(final Object source, final Object target) {
        try {
            BeanUtils.copyProperties(target, source);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to copy bean properties : " + e, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Unable to copy bean properties : " + e, e);
        }
    }

    /**
     * Returns the field name for the property. If the property is a boolean or
     * Boolean, it prepends the "is". So, 'deleted' would become 'isDeleted'.
     * 
     * @param descriptor
     *            the property descriptor
     * @return the field name for the property
     */
    public static String getFieldName(final PropertyDescriptor descriptor) {
        String fieldName = descriptor.getName();
        Method method = descriptor.getReadMethod();

        Class rt = method.getReturnType();
        if ((method.getReturnType() != null)
                && ((Boolean.class.equals(method.getReturnType())) || ("boolean".equals(method.getReturnType().getCanonicalName())))) {
            fieldName = "is" + ("" + fieldName.charAt(0)).toUpperCase() + fieldName.substring(1);
        }
        return fieldName;
    }

    public static boolean isAnnotationPresent(final PropertyDescriptor descriptor, final Class<?> target,
            final List<Class<? extends Annotation>> annotationClasses) {
        boolean exists = false;
        for (Annotation annotation : ReflectionUtility.getPropertyAnnotations(descriptor, target)) {
            for (Class<? extends Annotation> annotationClass : annotationClasses) {
                if (annotation.annotationType().equals(annotationClass)) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                break;
            }
        }
        return exists;
    }

    public static List<Annotation> getPropertyAnnotations(final PropertyDescriptor descriptor, final Class<?> target) {
        List<Annotation> annotations = new ArrayList<Annotation>();
        Method method = descriptor.getReadMethod();
        String fieldName = ReflectionUtility.getFieldName(descriptor);
        annotations.addAll(ReflectionUtility.getMethodAnnotations(method));
        annotations.addAll(ReflectionUtility.getFieldAnnotations(fieldName, target));

        return annotations;
    }

    public static List<Annotation> getMethodAnnotations(final Method method) {
        List<Annotation> annotations = new ArrayList<Annotation>();
        annotations.addAll(Arrays.asList(method.getAnnotations()));
        annotations.addAll(Arrays.asList(method.getDeclaredAnnotations()));

        return annotations;
    }

    public static List<Annotation> getFieldAnnotations(final String fieldName, final Class<?> clazz) {
        List<Annotation> annotations = new ArrayList<Annotation>();
        Field field = null;

        try {
            field = clazz.getField(fieldName);
        } catch (NoSuchFieldException e) {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ne) {
                if (clazz.getSuperclass() != null) {
                    annotations.addAll(ReflectionUtility.getFieldAnnotations(fieldName, clazz.getSuperclass()));
                }
            }
        }
        if (field != null) {
            field.setAccessible(true);
            annotations.addAll(Arrays.asList(field.getAnnotations()));
        }

        return annotations;
    }

    /**
     * Method that safely returns the bean info class, throwing a runtime
     * exception if necessary.
     * 
     * @param type
     *            the target class
     * @return the bean info
     */
    public static BeanInfo getBeanInfo(final Class<?> type) {
        try {
            return Introspector.getBeanInfo(type);
        } catch (IntrospectionException e) {
            throw new RuntimeException("Illegal arguments while generating bean property values.", e);
        }
    }

    /**
     * Returns the array of read/write property descriptors for this bean.
     * 
     * @param type
     *            the bean type
     * @return the descriptors
     */
    public static PropertyDescriptor[] getPropertyDescriptors(final Class<?> type) {
        return ReflectionUtility.getPropertyDescriptors(type, true);
    }

    /**
     * Returns the array of property descriptors for this bean.
     * 
     * @param type
     *            the bean type
     * @param readWriteOnly
     *            whether to return only read/write properties
     * @return the descriptors
     */
    public static PropertyDescriptor[] getPropertyDescriptors(final Class<?> type, final boolean readWriteOnly) {
        PropertyDescriptor[] descs = ReflectionUtility.getBeanInfo(type).getPropertyDescriptors();
        if (readWriteOnly) {
            List<PropertyDescriptor> list = new ArrayList<PropertyDescriptor>();
            for (PropertyDescriptor desc : descs) {
                if ((desc.getReadMethod() != null) && (desc.getWriteMethod() != null)) {
                    list.add(desc);
                }
                descs = list.toArray(new PropertyDescriptor[list.size()]);
            }
        }
        return descs;
    }

    /**
     * Invokes the given method, throwing a runtime exception on failure.
     * 
     * @param method
     *            the method to invoke
     * @param target
     *            the target instance
     * @param args
     *            the method arguments
     * @return the method result
     */
    public static Object safelyInvoke(final Method method, final Object target, final Object... args) {
        try {
            return method.invoke(target, args);
        } catch (IllegalArgumentException e) {
			throw new RuntimeException(
					"Illegal arguments while generating bean property values. /nMethod: "
							+ method + "/nTarget: " + target + "/nArgs : "
							+ args, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Method is not accessible : " + method.getName(), e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Exception while invoking method : " + method.getName() + " -- " + e, e);
        }
    }
}
