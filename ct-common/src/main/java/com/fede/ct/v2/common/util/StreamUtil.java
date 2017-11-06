package com.fede.ct.v2.common.util;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by f.barbano on 05/11/2017.
 */
public class StreamUtil {

	public static <T> List<T> filter(Collection<T> source, Predicate<T> filter) {
		return source.stream().filter(filter).collect(Collectors.toList());
	}
	public static <T,U> List<U> map(Collection<T> source, Function<T,U> mapper) {
		return source.stream().map(mapper).collect(Collectors.toList());
	}
	public static <T,U> List<U> filterAndMap(Collection<T> source, Predicate<T> filter, Function<T,U> mapper) {
		return source.stream().filter(filter).map(mapper).collect(Collectors.toList());
	}

	public static <T> String join(Collection<T> source, String separator) {
		return join(source, separator, String::valueOf);
	}
	public static <T> String join(Collection<T> source, String separator, Function<T,String> mapFunc) {
		if(source == null)	return null;
		return source.stream().map(mapFunc).collect(Collectors.joining(separator));
	}


}
