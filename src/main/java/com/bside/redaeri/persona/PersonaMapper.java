package com.bside.redaeri.persona;

import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PersonaMapper {
	
	public int updatePersonaAnswer(Map<String, Object> param);
	
	public Map<String, Object> getPersonaInfo(int loginIdx);
}
