package com.bside.redaeri.answer;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnswerMapper {
	
	public int insertAnswerGenerateLog(Map<String, Object> param);
	
	public List<Map<String, Object>> getAnswerGenerateLogList(Integer loginIdx);
}
