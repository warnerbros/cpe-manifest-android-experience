package com.wb.nextgen.util.concurrent;

public interface Task<R> {
	
	R doInBackgroud() throws Exception;
	
	void onResult(R result);
	
	void onException(Exception e);
}
