package com.cvovo.gamemanager.util.poi;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.util.ReflectionUtils;

public class PoiEntityInfo {

	private static Map<Class<?>, PoiEntityInfo> entityInfos = new HashMap<Class<?>, PoiEntityInfo>();

	private String sheetName;

	private Map<Field, Integer> cells;

	private PoiEntityInfo(Class<?> clazz) throws Exception {
		cells = new HashMap<Field, Integer>();
		Class<?> currentClass = null;
		Sheet sheet = null;
		do {
			currentClass = clazz;
			sheet = clazz.getAnnotation(Sheet.class);
			clazz = clazz.getSuperclass();
		} while (sheet == null && clazz != null);

		if (sheet != null)
			sheetName = sheet.value();
		else
			throw new Exception("EntityInfo: class[" + clazz.getName() + "] not table name!");

		for (Field field : currentClass.getDeclaredFields()) {
			Cell column = field.getAnnotation(Cell.class);
			if (column != null) {
				ReflectionUtils.makeAccessible(field);
				cells.put(field, column.value());
			}
		}
	}

	public String getSheetName() {
		return sheetName;
	}

	public Set<Entry<Field, Integer>> getCells() {
		return cells.entrySet();
	}

	public static PoiEntityInfo buildEntityInfo(Class<?> clazz) {
		PoiEntityInfo entityInfo = entityInfos.get(clazz);
		if (entityInfo == null)
			try {
				entityInfo = new PoiEntityInfo(clazz);
				entityInfos.put(clazz, entityInfo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		return entityInfo;
	}
}
