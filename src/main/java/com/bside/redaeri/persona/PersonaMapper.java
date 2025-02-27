package com.bside.redaeri.persona;

import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PersonaMapper {
	
	public int updatePersonaAnswer(Map<String, Object> param);
	
	public Map<String, Object> getPersonaInfo(int loginIdx);
	
	
	// 말투 직접 선택해서 저장
	public int insertPersonaInfo(Map<String, Object> param);
	
	// 말투 직접 수정해서 저장
	public int updatePersonaInfo(Map<String, Object> param);
}
