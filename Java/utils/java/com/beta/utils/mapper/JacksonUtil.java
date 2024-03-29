package com.beta.utils.mapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class JacksonUtil {

	private static Logger logger = LoggerFactory.getLogger(JacksonUtil.class);

	private static ObjectMapper objectMapper;

	public static ObjectMapper me() {
		return objectMapper;
	}

	static {
		objectMapper = new ObjectMapper();
		// NULL值是否输出
		// objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

		// objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
	}

	/**
	 * 根据类型和字段名获取getter方法
	 * 
	 * @param clazz
	 *            该字段的类型
	 * @param field
	 *            字段名
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static String getSetterMethod(Class clazz, String field) {
		// 布尔类型的字段，如果以“iss”开头把is去掉
		if (field.toLowerCase().startsWith("iss")) {
			field = field.substring(2);
		}
		field = (field.substring(0, 1).toUpperCase() + field.substring(1));
		if (clazz.equals(boolean.class)) {
		}
		return "set" + field;
	}

	/**
	 * 根据类型和字段名获取getter方法
	 * 
	 * @param clazz
	 *            该字段的类型
	 * @param field
	 *            字段名
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getGetterMethod(Class clazz, String field) {
		// 布尔类型的字段，如果以“iss”开头把is去掉
		if (field.toLowerCase().startsWith("iss")) {
			return field;
		}
		// serialVersionUID
		else if (field.toLowerCase().equals("serialVersionUID")) {
			return "getSerialVersionUID";
		}
		/* 首字母大写 */
		field = (field.substring(0, 1).toUpperCase() + field.substring(1));
		return "get" + field;
	}

	public static void setDateFormate(DateFormat dateFormat) {
		objectMapper.setDateFormat(dateFormat);
	}

	/**
	 * @deprecated use {@link #fromJsonToObj(String, Class)} instead
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> T toObj(String json, Class<T> clazz) {
		try {
			return objectMapper.readValue(json, clazz);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("解析json错误");
		}
	}

	public static <T> T fromJsonToObj(String json, Class<T> clazz) {
		try {
			return objectMapper.readValue(json, clazz);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("解析json错误");
		}
	}

	public static String toJson(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("解析对象错误");
		}
	}

	private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
		return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
	}

	/**
	 * @deprecated use {@link #toList(String, Class)} instead
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> toList(String json, Class<T> clazz) {
		JavaType javaType = getCollectionType(ArrayList.class, clazz);
		try {
			return objectMapper.readValue(json, javaType);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("解析json错误");
		}
	}

	/**
	 * 默认返回 {@link ArrayList} 结果；<br>
	 * 如果要返回其他类型，use {@link #fromBytesToList(byte[], Class, Class)} instead
	 * 
	 * @param bytes
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> fromBytesToList(byte[] bytes, Class<T> clazz) {
		JavaType javaType = getCollectionType(ArrayList.class, clazz);
		try {
			return objectMapper.readValue(bytes, javaType);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("解析json错误");
		}
	}

	public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
		JavaType javaType = getCollectionType(ArrayList.class, clazz);
		try {
			return objectMapper.readValue(json, javaType);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("解析json错误");
		}
	}

	public static <T> T fromJson(String json, Class<T> parametrized, Class<?>... parameterClasses) {
		JavaType javaType = objectMapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
		try {
			return objectMapper.readValue(json, javaType);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("解析json错误");
		}
	}

	/**
	 * 指定返回的集合泛型类型
	 * 
	 * @param bytes
	 * @param clazz
	 *            泛型类型（Array,Collection,Map）
	 * @param genericType
	 *            被泛型管理的参数类型
	 * @return
	 * 
	 * @see TypeFactory#constructParametricType(Class, JavaType...)
	 */
	public static <T, C> List<T> fromBytes(byte[] bytes, Class<C> clazz,
			@SuppressWarnings("unchecked") Class<T>... genericType) {
		if (!Collection.class.isAssignableFrom(clazz)) {
			if (!Map.class.isAssignableFrom(clazz)) {
				if (!clazz.isArray()) {
					logger.warn("unknown type(" + genericType + ")");
				}
			}
		}
		JavaType javaType = getCollectionType(clazz, genericType);
		try {
			return objectMapper.readValue(bytes, javaType);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("解析json错误");
		}
	}

	/**
	 * 将实体转换成Map
	 * 
	 * @param t
	 *            实体
	 * @return 将实体转换为Map的结果
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	public static <T> Map<String, Object> toMap(T t) {
		try {
			String json = objectMapper.writeValueAsString(t);
			return fromJsonToMap(json, String.class, Object.class);
		} catch (IOException e) {
			logger.error("", e);
		}
		return null;
	}

	/**
	 * 獲取該類所有的字段，包括父類
	 * 
	 * @param clazz
	 * @param fields
	 * @return
	 */
	@SuppressWarnings("unused")
	private static List<Field> getFields(Class<?> clazz, List<Field> fields) {
		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		if (clazz.getSuperclass() != null) {
			fields.addAll(getFields(clazz.getSuperclass(), fields));
		}
		return fields;
	}

	public static <K, V> Map<K, V> fromJsonToMap(String json, Class<K> keyType, Class<V> valueType) {
		JavaType javaType = objectMapper.getTypeFactory().constructParametricType(Map.class, keyType, valueType);
		try {
			return objectMapper.readValue(json, javaType);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("解析json错误");
		}
	}

	/**
	 * 从List<Map>转换为List<Entity>
	 * 
	 * @param mapList
	 * @param objClass
	 * @return
	 */
	public static <T> List<T> fromMapListToObjList(List<Map<String, Object>> mapList, Class<T> objClass) {
		JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, objClass);
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			objectMapper.writeValue(bos, mapList);
			return objectMapper.readValue(bos.toByteArray(), javaType);
		} catch (Exception e) {
			// logger.error(e.getMessage(), e);
			throw new RuntimeException("解析json错误", e);
		}
	}

	public static <T> T fromMapToObj(Map<?, ?> map, Class<T> objClass) {
		JavaType javaType = objectMapper.getTypeFactory().constructType(objClass);
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			objectMapper.writeValue(bos, map);
			return objectMapper.readValue(bos.toByteArray(), javaType);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("解析json错误");
		}
	}

	public static byte[] toBytes(Object obj) {
		try {
			return objectMapper.writeValueAsBytes(obj);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("解析对象错误");
		}
	}

	public static <T> T fromBytes(byte[] rsBytes, Class<T> clazz) {
		try {
			T t = objectMapper.readValue(rsBytes, clazz);
			return t;
		} catch (NullPointerException e) {
		} catch (JsonParseException e) {
			logger.error("Json parse error : " + e.getMessage());
		} catch (JsonMappingException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}