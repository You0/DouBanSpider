package com.myspider.databaseUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/*
 * �����Utils��������
 *�ṩ����˽�б�������ȡ��������class����ȡ������Ԫ�����Եȹ��� 
 */
public class ReflectionUtils {

	//��ȡ����ķ���
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
	 * ѭ������ת��, ��ȡ����� DeclaredField
	 * @param object
	 * @param filedName
	 * @return
	 */

	public static Field getDeclaredField(Object object, String filedName){
		
		for(Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()){
			try {
				return superClass.getDeclaredField(filedName);
			} catch (NoSuchFieldException e) {
				//Field ���ڵ�ǰ�ඨ��, ��������ת��
			}
		}
		return null;
	}
	
	
	/*
	 *ֱ�����ö��������
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
