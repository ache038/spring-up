package io.spring.up.boot.resolver;

import io.spring.up.annotations.JsonEntity;
import io.spring.up.epic.Ut;
import io.spring.up.exception.web._500ParameterTypeException;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class JsonEntityResolver implements HandlerMethodArgumentResolver {
    private static final String JSON_REQUEST_BODY = "JSON_REQUEST_BODY";

    @Override
    public boolean supportsParameter(final MethodParameter methodParameter) {
        boolean isMatch = methodParameter.hasParameterAnnotation(JsonEntity.class);
        if (isMatch) {
            // 不是基础类型
            isMatch = !Ut.isPrimary(methodParameter.getParameterType())
                    && !Ut.isJObject(methodParameter.getParameterType())
                    && !Ut.isJArray(methodParameter.getParameterType());
            if (!isMatch) {
                throw new _500ParameterTypeException(this.getClass(), methodParameter.getParameterType());
            }
        }
        return isMatch;
    }

    @Override
    public Object resolveArgument(final MethodParameter methodParameter,
                                  final ModelAndViewContainer modelAndViewContainer,
                                  final NativeWebRequest nativeWebRequest,
                                  final WebDataBinderFactory webDataBinderFactory) throws Exception {
        final String body = Resolver.getRequestBody(methodParameter, nativeWebRequest);
        // 解析参数到固定格式，可支持为空相关计算
        final Object reference = Resolver.resolveJson(this.getClass(), body);
        // 规则基础验证处理
        Resolver.verifyInput(this.getClass(), methodParameter, reference);
        // 反序列化处理
        return Ut.deserialize(body, methodParameter.getParameterType());
    }
}
