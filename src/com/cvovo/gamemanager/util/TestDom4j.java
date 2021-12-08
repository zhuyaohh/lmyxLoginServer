package com.cvovo.gamemanager.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class TestDom4j {
	
	 /**
	     * @description 将xml字符串转换成map
	     * @param xml
	     * @return Map
	     */
	    public static Map readStringXmlOut(String xml) {
	        Map map = new HashMap();
	        Document doc = null;
	        try {
	            // 将字符串转为XML
	            doc = DocumentHelper.parseText(xml); 
	            // 获取根节点
	            Element rootElt = doc.getRootElement(); 
	            // 拿到根节点的名称
	            System.out.println("根节点：" + rootElt.getName()); 

	            // 获取根节点下的子节点head
	            Iterator recordEle = rootElt.elementIterator("message"); 
	        
	            while (recordEle.hasNext()) {
	            	
	            	Element itemEle = (Element) recordEle.next();
	            	// 拿到head节点下的子节点title值
	                String is_test = itemEle.elementTextTrim("is_test"); 
	                System.out.println("is_test:" + is_test);
	                map.put("is_test", is_test);
	                
	                String channel = itemEle.elementTextTrim("channel"); 
	                System.out.println("channel:" + channel);
	                map.put("channel", channel);
	                
	                
	                String channel_uid = itemEle.elementTextTrim("channel_uid"); 
	                System.out.println("channel_uid:" + channel_uid);
	                map.put("channel_uid", channel_uid);
	                
	                String game_order = itemEle.elementTextTrim("game_order"); 
	                System.out.println("game_order:" + game_order);
	                map.put("game_order", game_order);
	                
	                String order_no = itemEle.elementTextTrim("order_no"); 
	                System.out.println("order_no:" + order_no);
	                map.put("order_no", order_no);
	                
	                String pay_time = itemEle.elementTextTrim("pay_time"); 
	                System.out.println("pay_time" + pay_time);
	                map.put("pay_time", pay_time);
	                
	                String amount = itemEle.elementTextTrim("amount"); 
	                System.out.println("amount:" + amount);
	                map.put("amount", amount);
	                
	                String status = itemEle.elementTextTrim("status"); 
	                System.out.println("status:" + status);
	                map.put("status", status);
	                
	                String extras_params = itemEle.elementTextTrim("extras_params"); 
	                System.out.println("extras_params:" + extras_params);
	                map.put("extras_params", extras_params);
	            }
	            	
	               
	        } catch (DocumentException e) {
	            e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return map;
	    }

	    public static void main(String[] args) {

	        // 下面是需要解析的xml字符串例子
	        String xmlString = "<quicksdk_message>" + "<message>"
	                + "<is_test>0</is_test>" + "<channel>8888</channel>"
	                + "<channel_uid>231845</channel_uid>" + "<game_order>123456789</game_order>" + "<order_no>12520160612114220441168433</order_no>"
	                + "<pay_time>2016-06-12 11:42:20</pay_time>" + "<amount>1.00</amount>"
	                + "<status>0</status>" + "<extras_params>{1}_{2}</extras_params>"
	                + "</message>" + "</quicksdk_message>";

	        /*
	         * Test2 test = new Test2(); test.readStringXml(xmlString);
	         */
	        Map map = readStringXmlOut(xmlString);
	        Iterator iters = map.keySet().iterator();
	        while (iters.hasNext()) {
	            String key = iters.next().toString(); // 拿到键
	            String val = map.get(key).toString(); // 拿到值
	            System.out.println(key + "=" + val);
	        }
	    }
}
