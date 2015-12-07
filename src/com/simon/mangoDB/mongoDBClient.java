package com.simon.mangoDB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class mongoDBClient {
	private static Log log = LogFactory.getLog(mongoDBClient.class);
	private static final String IP = "172.16.12.83";
	private static final int PORT = 50000;
	private static Mongo mg = null;
	private static DB db = null;
	private static DBCollection dbConllection = null;

	/**
	 * 取得mangoDB连接
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Mongo getMongoDB(String ip, int port) {
		try {
			mg = new Mongo(ip, port);
			log.info("【 mongoDB connection server success】");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mg;
	}

	/**
	 * 连接mongoDb的数据库与集合
	 * @param dbName
	 * @param collections
	 * @return
	 */
	public static DBCollection getDBConllection(String dbName,
			String collections) {
		mg = getMongoDB(IP, PORT);
		getDatabase(dbName);
		dbConllection = db.getCollection(collections);
		log.info("【 connection " + dbName + "." + collections + " success】");
		return dbConllection;
	}

	/**
	 * 与库取的连接
	 * @param dbName
	 */
	public static DB getDatabase(String dbName) {
		log.info("【connection " + dbName + " success 】");
		return db = mg.getDB(dbName);
	}

	/**
	 * 对象回收
	 * 
	 * @param mg
	 * @param db
	 * @param dbConllection
	 */
	public static void destory(Mongo mg, DB db, DBCollection dbConllection) {
		if (mg != null)
			mg.close();
		mg = null;
		db = null;
		dbConllection = null;
		log.info("【close " + db + "." + dbConllection + " connection 】");
	}

}
