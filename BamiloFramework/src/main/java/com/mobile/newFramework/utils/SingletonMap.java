/**
 * 
 */
package com.mobile.newFramework.utils;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * @author nutzer2
 *
 */
public class SingletonMap<T> {
	
	private final LinkedHashMap<Class<? extends T>, T> singletons;

	/**
	 * 
	 */
	public SingletonMap(T... singletons) {
		this.singletons = new LinkedHashMap<Class<? extends T>, T>(singletons.length);
		for(T singleton : singletons) {
			add(singleton);
		}
	}

	private void add(T singleton) {
		singletons.put((Class<? extends T>) singleton.getClass(), singleton);
	}
	
	public <S extends T> S get(Class<S> clazz) {
		return (S) singletons.get(clazz);
	}
	
	/**
	 * @return
	 * @see java.util.HashMap#values()
	 */
	public Collection<T> values() {
		return singletons.values();
	}

}
