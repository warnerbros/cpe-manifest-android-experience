package com.wb.nextgenlibrary.util;

public class Request {
	
	protected StringBuilder sb = new StringBuilder();
	
	public Request() {
	}
	
	public Request(String url) {
		sb.append(url);
	}
	
	public Request set(String string) {
		sb.append(string);
		return this;
	}
	
	@Override
	public String toString() {
		return sb.toString();
	}


	public String toStringNoJson() {
		return sb.toString();
	}
	
}
