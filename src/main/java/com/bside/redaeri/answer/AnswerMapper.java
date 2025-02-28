package com.bside.redaeri.answer;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AnswerMapper {
	
	@Insert("INSERT INTO answer_generate_log ("
	        + "user_idx, store_idx, persona_idx, content, result, review_score, include_text, review_type) "
	        + "VALUES ("
	        + "#{loginIdx}, #{storeIdx}, #{personaIdx}, #{content}, #{result}, #{reviewScore}, #{includeText}, #{reviewType})")
	public int insertAnswerGenerateLog(AnswerDto answerDto);
	
	@Select("SELECT idx as answerGenerateLogIdx FROM answer_generate_log "
			+ "WHERE user_idx = #{loginIdx} ORDER BY insert_date LIMIT 20")
	public List<Map<String, Object>> getAnswerGenerateLogList(Integer loginIdx);
}
