package com.bside.redaeri.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.bside.redaeri.filter.JWTService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class LoginIdxArgumentResolver implements HandlerMethodArgumentResolver {

	@Autowired
	public JWTService jwtService;
	
	@Override
    public boolean supportsParameter(MethodParameter parameter) {
		 return parameter.hasParameterAnnotation(LoginIdx.class) 
		            && (parameter.getParameterType().equals(Integer.class) || parameter.getParameterType().equals(int.class));
	}

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, 
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        
        String token = request.getHeader("token");
        int login_idx = jwtService.getUserIdx(token);
        
        // todo 토큰이 없는경우, 만료된 경우
        System.out.println("login_idx --> " + login_idx);
        
        return login_idx; // 토큰에서 user_id 추출
    }
}

