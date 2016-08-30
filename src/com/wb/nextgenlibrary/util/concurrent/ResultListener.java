package com.wb.nextgenlibrary.util.concurrent;

public interface ResultListener<T> {
	 
	void onResult(T result);
	
	<E extends Exception> void  onException(E e);
}


