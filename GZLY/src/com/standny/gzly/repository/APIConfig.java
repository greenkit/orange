package com.standny.gzly.repository;

public class APIConfig {
	public final static String ApiUrl="http://m.517gzz.net/appapi/";//"http://192.168.1.9/appapi/";
	public static String GetFullPath(String path){
		if (path.startsWith("http//"))
			return path;
		if (path.startsWith("~/"))
			return ApiUrl.substring(0,ApiUrl.indexOf("/", 10))+path.substring(1);
		if (path.startsWith("/"))
			return ApiUrl.substring(0,ApiUrl.indexOf("/", 10))+path;
		else
			return ApiUrl+path;
			
	}
}
