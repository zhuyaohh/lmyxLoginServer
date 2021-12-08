package com.cvovo.gamemanager.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.springframework.util.ReflectionUtils;

public class ObjectUtils {
	public static <T, E extends T> E clone(final T obj, Class<E> clazz) throws InstantiationException, IllegalAccessException {
		E entity = clazz.newInstance();
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
				ReflectionUtils.makeAccessible(field);
				Object value = field.get(obj);
				field.set(entity, value);
			}
		}
		return entity;
	}

}
