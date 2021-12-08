package com.cvovo.gamemanager.util;

public class ServerOperate {

	public static String DEPLOY = "unzip -oq {0} -d ~/{1} && chmod 755 ~/{1}/*.sh\n";
	public static String START = "cd ~/{0} && ./start.sh\n";
	public static String STOP = "cd ~/{0} && ./stop.sh\n";
}
