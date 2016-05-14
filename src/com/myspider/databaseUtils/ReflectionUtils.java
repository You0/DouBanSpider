package com.myspider.databaseUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/*
 * 反射的Utils函数集合
 *提供访问私有变量，获取泛型类型class，提取集合中元素属性等功能 
 */
public class ReflectionUtils {

	//获取父类的泛型
	public static Class getSuperClassGenricType(Class clazz, int index) {
		Type genType = clazz.getGenericSuperclass();
		Type[] types = ((ParameterizedType) genType).getActualTypeArguments();
		if (index < types.length || index > 0) {
			return (Class) types[index];
		}
		return Object.class;
	}
	
	
	public static<T> Class<T> getSuperGenericType(Class clazz){
		return getSuperClassGenricType(clazz, 0);
	}
	
	/**
	 * 循环向上转型, 获取对象的 DeclaredField
	 * @param object
	 * @param filedName
	 * @return
	 */

	public static Field getDeclaredField(Object object, String filedName){
		
		for(Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()){
			try {
				return superClass.getDeclaredField(filedName);
			} catch (NoSuchFieldException e) {
				//Field 不在当前类定义, 继续向上转型
			}
		}
		return null;
	}
	
	
	/*
	 *直接设置对象的属性
	 */
	public static void setFieldValue(Object object,String fieldName,Object value) throws Exception
	{
		Field field = getDeclaredField(object, fieldName);
		if(field==null){
			throw new Exception("No filed");
		}
		
		if(!field.isAccessible()){
			field.setAccessible(true);
		}
		
		try {
			field.set(object,value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	
	

}
