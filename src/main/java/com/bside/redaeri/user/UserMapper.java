package com.bside.redaeri.user;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
	
	@Insert("INSERT INTO user(user_id) VALUES(#{id})")
	@Options(useGeneratedKeys = true, keyProperty = "idx", keyColumn = "idx")
	public int insertUser(UserDto userDto);

	@Select("SELECT " +
	        "u.idx AS userIdx, " +
	        "user_id AS userId, " +
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
	
	@Select("SELECT idx as userIdx FROM user WHERE user_id = #{id}")
	public Integer existUser(String id);
	
	public int deleteUser(Integer loginIdx);
}
