package com.bside.redaeri.persona;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PersonaMapper {

	@Select("SELECT COUNT(*) FROM store WHERE user_idx = #{loginIdx}")
	public int getStoreCount(Integer loginIdx);
	
	@Select("SELECT idx as storeIdx FROM store WHERE user_idx = #{loginIdx}")
	public int getStoreIdx(Integer loginIdx);
	
	@Update("UPDATE persona "
			+ "SET all_answer = #{allAnswer} "
			+ "WHERE idx = #{personaIdx}")
	public int updatePersonaAnswer(PersonaDto personaDto);
	
	@Select("SELECT idx as personaIdx, persona_select as personaSelect, emotion_select as emotionSelect, "
			+ "length_select as lengthSelect, store_idx as storeIdx, all_answer as allAnswer "
			+ "FROM persona WHERE store_idx = #{storeIdx}")
	public Map<String, Object> getPersonaInfo(int storeIdx);
	
	// 말투 직접 선택해서 저장
	@Insert("INSERT INTO persona ("
			+ "persona_select, emotion_select, length_select, store_idx, all_answer)"
			+ "VALUES ("
			+ "#{personaSelect}, #{emotionSelect}, #{lengthSelect}, #{storeIdx}, #{allAnswer})")
	@Options(useGeneratedKeys = true, keyProperty = "personaIdx", keyColumn = "idx")
	public int insertPersonaInfo(PersonaDto personaDto);
	
	// 말투 직접 수정해서 저장
	@Update("UPDATE persona "
			+ "SET persona_select = #{personaSelect}, emotion_select = #{emotionSelect}, "
			+ "length_select = #{lengthSelect}, all_answer = #{allAnswer} "
			+ "WHERE idx = #{personaIdx}")
	public int updatePersonaInfo(PersonaDto personaDto);
	
	@Select("SELECT COUNT(*) FROM persona WHERE idx = #{personaIdx}")
	public int existPersonaInfo(PersonaDto personaDto);
	
	@Select("SELECT COUNT(*) "
			+ "FROM persona p "
			+ "JOIN store s ON p.store_idx = s.idx "
			+ "WHERE s.user_idx = #{loginIdx} ")
	public int existPersona(Integer loginIdx);
	
	//TODO 선호 페르소나
	public String preferPersona();
}
