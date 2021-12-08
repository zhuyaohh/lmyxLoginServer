package com.cvovo.gamemanager.game.alipay;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.3
 *日期：2012-08-10
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。

 *提示：如何获取安全校验码和合作身份者ID
 *1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *2.点击“商家服务”(https://b.alipay.com/order/myOrder.htm)
 *3.点击“查询合作者身份(PID)”、“查询安全校验码(Key)”

 *安全校验码查看时，输入支付密码后，页面呈灰色的现象，怎么办？
 *解决方法：
 *1、检查浏览器配置，不让浏览器做弹框屏蔽设置
 *2、更换浏览器或电脑，重新登录查询。
 */

public class AlipayConfig {

	public static String partner = "2088621250270702";

	// 商户收款账号
	public static String seller_id = "86856695@qq.com";

	// 商户的私钥
	public static String private_key = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMAY9zOhPWNPmF9qm8aeyX+7bxaGTbqB1di54ESFQc/txg0I+nCau/TNi0D/wzrqkUzuM4G0QazYbc090uZmCATcWwW3SC4aa974hhRgQtXOVqjVstH4RcEb1BuSasfDo0R5Nqv51POyl9oGJvO2l+4YZy7EePXWznk8caFGq/utAgMBAAECgYBFVT1zzxB/Fa1jUvEigREtHxN7R8IqPM5YSM5fLNGSILnI3qK1ncOVGdLXBPkL/Lmw0JCakdrabctDtrq2GcFuju6u2n9j+5IvK6zpk5wjzzFcyXerflCAVacUCnSsiNTa36SDd51rvUjb7+Pwjtbx3Anldj/CeKIvJraD+CcMQQJBAPM3q5h6QIh9/xLrv4JjqMugfavNNowQBk77xym9eA1rH7zeDDGN9WQF6KamJQzgQ/sX5LGMq5qh9bNimCWWsvkCQQDKMYKrxzZ2F2dxG0Jw2NcK1mui48HBwKSMC4vIaCTFz4NMjey69K7WzLfwkmZulm5n5wHdA0UDppMRKyVIW8dVAkEAlc/Esrca6PgzFcSAwiAA6OyCfEB7SOrV9/C+TMeYGyvQ8NUVTbDTZjj9hSjxyWyacx/UjkjHqwf3//bLINwigQJBAJkEAQz49SlHqvv5Ej4OjCdigBDE5nQ30w89Cas/zVx6H7aGzbnqxIpAYY/Ja4WTiLhPJaTZ/Ze2ryPWECADfQUCQBp7lta2Jpi7mhZSq7XqtUclSjC/d3k7MpLfjPegcFPDlaB7dur3/vSR5gsi0YGBn/ydwSLKAmafYDSS8Pj2wk8=";

	// ↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	// public static String partner = "2088221766479721";
	//
	// // 商户收款账号
	// public static String seller_id = "dongcai@handmobi.com.cn";
	//
	// // 商户的私钥
	// public static String private_key =
	// "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMlokcUrKBqadimnUSMRFC8reC2wM5XcPJYTB8O/RIzrkTriXTSmUC94+n/KUYXfEEbvZyWRs6UTur//mTdZ0Zd6bfe/P+QRG/bAxyNHcHsm/2OShlWDVOpfMmnUJhyTYPgUuohTsu5p27F2n8AZUg0c52RYiwPauOxK6SuqJv2PAgMBAAECgYEAqHBsoKG0G3OcaL+GarZI1B4dltAqhCU5AiWOM9XNJbGItt9DJdoFa35YuPDh9vyTZ0+mStQskl0KrLG9WH4kenVPhBGHDH6T8gvQuE7D8UQqDB5HPOsNhdYbWCBBEH2Ct/ZsDiMA8kArQzjBrTInKPYzoo/AudJtohzkKa0G2wECQQD+59WjUEaECwWTDZNxCuhEYIjDfw01Orae6N1oPsGGPqXLFv3aW7p+7KajCEXrS8DlGDOLeYF/dO9SkhFmOqAPAkEAykXvvBYvv2ygVo7KhMrrfmv6u1uyPvxWea7jmAhAyO9sARVES6MFrALwHBK4ZlyZU3PrmOA8D44VKYWprihKgQJBAJY6S85UITrww/hGp/6XTwv9WJze5Ana8IX294XYdnWHtm5avoFSTjc/gXXUCmEB5E0gVkB8+7UCVYl0TIzTQEECQCDOKOyDWKkeL/7516Sj2H+/eYHLhGQsvsWrdKQiqocsNYMTdOkjfncAXvremmOXnxJm2Y4IyBTpwUrjr7pcNIECQQCXd+8M1w9gAFxfNpZBtxhViLbPwAGPdcTKIHegt9cPlmorlMT9GmhIYIZXohmadibcykQX6FeK74NhfZV5huA0";

	// 支付宝的公钥，无需修改该值
	public static String ali_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	// ↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	// 调试用，创建TXT日志文件夹路径
	public static String log_path = "log";

	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String input_charset = "utf-8";

	// 签名方式 不需修改
	public static String sign_type = "RSA";

}
