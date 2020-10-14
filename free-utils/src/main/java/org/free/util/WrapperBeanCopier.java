package org.free.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cglib.beans.BeanCopier;

public class WrapperBeanCopier {

	private static final Map<String, BeanCopier> BEAN_COPIER_CACHE = new ConcurrentHashMap<>();
	
	public static <T> T copyProperties(Object source, Class<T> targetClass) {
		T t = null;
		try {
			t = targetClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(
					String.format("Create new instance of %s failed: %s", targetClass, e.getMessage()));
		}
		copyProperties(source, t);
		return t;
	}

	private static void copyProperties(Object source, Object target) {
		BeanCopier copier = getBeanCopier(source.getClass(), target.getClass());
		copier.copy(source, target, null);
	}

	private static BeanCopier getBeanCopier(Class<?> sourceClass, Class<?> targetClass) {
		String beanKey = generateKey(sourceClass, targetClass);
		BeanCopier copier = null;
		if (!BEAN_COPIER_CACHE.containsKey(beanKey)) {
			copier = BeanCopier.create(sourceClass, targetClass, false);
			BEAN_COPIER_CACHE.put(beanKey, copier);
		} else {
			copier = BEAN_COPIER_CACHE.get(beanKey);
		}
		return copier;
	}

	/**
	 * 两个类的全限定名拼接起来构成Key
	 *
	 * @param sourceClass
	 * @param targetClass
	 * @return
	 */
	private static String generateKey(Class<?> sourceClass, Class<?> targetClass) {
		return sourceClass.getName() + targetClass.getName();
	}

	
}