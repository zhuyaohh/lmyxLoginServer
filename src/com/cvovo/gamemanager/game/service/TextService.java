package com.cvovo.gamemanager.game.service;

import com.cvovo.gamemanager.game.persist.dao.TextDao;
import com.cvovo.gamemanager.game.persist.entity.Text;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TextService {

    @Autowired
    private TextDao textDao ;

    /**
     * 创建Text对象
     * @param name
     * @param password
     * @return
     */
    public Text create(String name, String password) {

        Text tt = new Text(name, password);
        Text textResult = textDao.save(tt);

        return textResult;
    }
}
