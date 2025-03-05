package com.bside.redaeri.answer;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AnswerMapper {
	
	@Insert("INSERT INTO answer_generate_log ("
	        + "user_idx, store_idx, persona_idx, content, result, review_score, include_text, review_type) "
	        + "VALUES ("
	        + "#{loginIdx}, #{storeIdx}, #{personaIdx}, #{reviewText}, #{generateAnswer}, #{score}, #{includeText}, #{reviewType})")
	@Options(useGeneratedKeys = true, keyProperty = "logIdx", keyColumn = "idx")
	public int insertAnswerGenerateLog(AnswerDto answerDto);
	
	@Select("SELECT ROW_NUMBER() OVER(ORDER BY insert_date DESC) as rownum "
			+ "	, idx as logIdx "
			+ "	, content as reviewText "
			+ "	, review_type as reviewType "
			+ "	, result as generateAnswer "
			+ "	, review_score as score "
			+ " , include_text as includeText "
			+ " , DATE_FORMAT(insert_date, '%Y.%m.%d') as insertDate "
			+ " , DATE_FORMAT(insert_date, '%H:%i') as insertTime "
			+ "FROM answer_generate_log agl "
			+ "WHERE user_idx = #{loginIdx} "
			+ "LIMIT 20")
	public List<Map<String, Object>> getAnswerGenerateLogList(Integer loginIdx);
	
	@Select("SELECT idx as logIdx "
			+ ", store_idx as storeIdx "
			+ ", include_text as includeText "
			+ ", result as generateAnswer "
			+ ", review_score as score "
			+ ", content as reviewText "
			+ "FROM answer_generate_log "
			+ "WHERE idx = #{logIdx}")
	public AnswerDto getLogInfo(AnswerDto answerDto);
	
	@Update("UPDATE answer_generate_log "
			+ "SET "
			+ "result = #{generateAnswer} "
			+ "WHERE "
			+ "idx = #{logIdx}")
	public int updateAnswerGenerateLog(AnswerDto answerDto);
}
