package com.wb.nextgenlibrary.util;


public final class JSONRequest extends Request{

	public JSONRequest(String url) {
		super(url);
	}
	
	@Override
	public String toString() {
		//sb.append(".json");
		return sb.toString();
	}
	
}