package com.cvovo.gamemanager.game.persist.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cvovo.gamemanager.game.persist.entity.Text;

public interface TextDao extends JpaRepository<Text, Long> {



}
