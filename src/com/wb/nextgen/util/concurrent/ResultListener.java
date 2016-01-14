package com.wb.nextgen.util.concurrent;

public interface ResultListener<T> {
	 
	void onResult(T result);
	
	<E extends Exception> void  onException(E e);
}


