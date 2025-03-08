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
	        + "user_idx, store_idx, persona_idx, review_text, base_answer, generate_answer, review_score, include_text, review_type) "
	        + "VALUES ("
	        + "#{loginIdx}, #{storeIdx}, #{personaIdx}, #{reviewText}, #{baseAnswer}, #{generateAnswer}, #{score}, #{includeText}, #{reviewType})")
	@Options(useGeneratedKeys = true, keyProperty = "logIdx", keyColumn = "idx")
	public int insertAnswerGenerateLog(AnswerDto answerDto);
	
	@Select("SELECT ROW_NUMBER() OVER(ORDER BY insert_date DESC) as rownum "
			+ "	, idx as logIdx "
			+ "	, review_text as reviewText "
			+ "	, review_type as reviewType "
			+ "	, generate_answer as generateAnswer "
			+ " , base_answer as baseAnswer "
			+ "	, review_score as score "
			+ " , include_text as includeText "
			+ " , DATE_FORMAT(insert_date, '%Y.%m.%d') as insertDate "
			+ " , DATE_FORMAT(insert_date, '%H:%i') as insertTime "
			+ "FROM answer_generate_log agl "
			+ "WHERE user_idx = #{loginIdx} "
			+ "LIMIT 20")
	public List<Map<String, Object>> getAnswerGenerateLogList(Integer loginIdx);
	
	@Select("SELECT agl.idx as logIdx "
			+ ", store_idx as storeIdx "
			+ ", s.store_name as storeName "
			+ ", include_text as includeText "
			+ ", base_answer as baseAnswer "
			+ ", generate_answer as generateAnswer "
			+ ", review_score as score "
			+ ", review_text as reviewText "
			+ "FROM answer_generate_log agl "
			+ "JOIN store s "
			+ "ON agl.store_idx = s.idx "
			+ "WHERE agl.idx = #{logIdx}")
	public AnswerDto getLogInfo(AnswerDto answerDto);
	
	@Update("UPDATE answer_generate_log "
			+ "SET generate_answer = #{generateAnswer} "
			+ ", base_answer = #{baseAnswer} "
			+ "WHERE "
			+ "idx = #{logIdx}")
	public int updateAnswerGenerateLog(AnswerDto answerDto);

	
	@Select("SELECT store_name as storeName FROM store s WHERE user_idx = #{loginIdx}")
	public String getStoreName(AnswerDto answerDto);
}
