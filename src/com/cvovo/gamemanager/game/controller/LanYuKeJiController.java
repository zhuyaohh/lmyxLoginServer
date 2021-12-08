package com.cvovo.gamemanager.game.controller;


import com.alibaba.fastjson.JSONObject;
import com.cvovo.gamemanager.game.persist.entity.Text;
import com.cvovo.gamemanager.game.service.TextService;
import net.sf.json.JSONException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;

@RestController
@RequestMapping("/")
public class LanYuKeJiController {

    @Autowired
    private TextService textService;

    /**
     * 返回Text对象测试
     * @param name
     * @param password
     * @return
     * @throws JSONException
     */
    @RequestMapping(value = "textJDBC", method = { RequestMethod.GET, RequestMethod.POST })
    public Text creatText(String name, String password, @NotNull Map<String , Object> postMsg) {
        Text result = textService.create(name, password);

        for (Map.Entry<String, Object> entry : postMsg.entrySet()) {
            String mapKey = entry.getKey();
            String mapValue = (String) entry.getValue();
            System.out.println(mapKey + "：" + mapValue);
        }

        return result;
    }

    /**
     * 返回JSON格式测试
     * @param response
     * @throws JSONException
     * @throws IOException
     */
    @RequestMapping(value = "textJSON", method = { RequestMethod.GET, RequestMethod.POST })
    public void jsonText(HttpServletRequest req, HttpServletResponse response) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
        StringBuffer sb=new StringBuffer();
        String s=null;
        while((s=br.readLine())!=null){
            sb.append(s);
        }
        JSONObject jsonObject = JSONObject.parseObject(sb.toString()); //接收
        JSONObject jsonObjectResult = new JSONObject(); //返回

        jsonObjectResult.put("code", "200");
        jsonObjectResult.put("msg", "fuck world!");

        String name = jsonObject.getString("nameGet");
        String password = jsonObject.getString("passwordGet");

        jsonObjectResult.put("nameGet", name);
        jsonObjectResult.put("passwordGet", password);

        jsonObjectResult.put("end", "end over flash");

        PrintWriter writer = response.getWriter();
        response.getWriter().write(jsonObjectResult.toString());
        writer.close();
    }
}
