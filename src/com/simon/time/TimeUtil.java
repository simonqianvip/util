package com.simon.time;

public class TimeUtil {
	/**
	 * 统计程序运行时间
	 * @param callBack
	 */
	public void countTime(CallBack callBack){
		long beginTime = System.currentTimeMillis();
		callBack.execute();
		long endTime = System.currentTimeMillis();
		System.out.println("use time :"+(endTime-beginTime)+"ms");
	}

}

