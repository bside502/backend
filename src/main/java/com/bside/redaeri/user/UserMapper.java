package com.bside.redaeri.user;

import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
	
	@Insert("INSERT INTO user(user_id, access_token) VALUES(#{userId}, #{accessToken})")
	@Options(useGeneratedKeys = true, keyProperty = "idx", keyColumn = "idx")
	public int insertUser(UserDto userDto);
	
	@Update("UPDATE user SET access_token = #{accessToken} WHERE user_id = #{userId}")
	public int updateUserToken(UserDto userDto);
	
	@Select("SELECT access_token FROM user WHERE idx = #{loginIdx}")
	public String getUserAccessToken(Integer loginIdx);

	@Select("SELECT " +
	        "u.idx AS userIdx, " +
	        "user_id AS userId, " +
	        "s.idx AS storeIdx, " +
	        "p.idx AS personaIdx, " +
	        "store_name AS storeName, " +
	        "store_type AS storeType, " +
	        "persona_select AS personaSelect, " +
	        "emotion_select AS emotionSelect, " +
	        "length_select AS lengthSelect, " +
	        "all_answer AS allAnswer " +
	        "FROM user u " +
	        "LEFT JOIN store s ON s.user_idx = u.idx " +
	        "LEFT JOIN persona p ON s.idx = p.store_idx " +
	        "WHERE u.idx = #{loginIdx} LIMIT 1") // limit 임시
	public Map<String, Object> getUserInfo(Integer loginIdx);
	
	@Select("SELECT COUNT(*) FROM answer_generate_log WHERE user_idx = #{loginIdx}")
	public int countUserAnswer(Integer loginIdx);
	
	@Select("SELECT COUNT(*) FROM user WHERE idx = #{loginIdx}")
	public int existsUser(int loginIdx);
	
	@Select("SELECT idx as userIdx FROM user WHERE user_id = #{id} ORDER BY idx limit 1") // 임시
	public Integer existUser(String id);
	
	@Delete("DELETE FROM user WHERE idx = #{loginIdx}")
	public int deleteUser(Integer loginIdx);
	
	@Delete("DELETE FROM store s WHERE s.user_idx = #{loginIdx}")
	public int deleteStore(Integer loginIdx);
	
	@Delete("DELETE FROM persona p WHERE p.store_idx = (SELECT idx FROM store s WHERE s.user_idx = #{loginIdx})")
	public int deletePersona(Integer loginIdx);
	
	@Delete("DELETE FROM answer_generate_log WHERE user_idx = #{loginIdx}")
	public int deleteAnswerGenerateLog(Integer loginIdx);
	
}
