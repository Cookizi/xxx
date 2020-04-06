package top.cookizi.xxx.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
@Aspect
public class CtrlLogAop {

    @Pointcut("execution(public * top.cookizi.xxx.ctrl.IndexCtrl.*(..))")
    public void pointCut() {
    }

    @Before("pointCut()") //在切入点的方法run之前要干的
    public void logBeforeController(JoinPoint joinPoint) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        StringBuilder requestLog = new StringBuilder();
        Signature signature = joinPoint.getSignature();
        requestLog.append("URL = [").append(request.getRequestURI()).append("],");
//                .append("请求方式 = {").append(request.getMethod()).append("},")
//                .append("请求IP = {").append(request.getRemoteAddr()).append("},")
//                .append("类方法 = {").append(signature.getDeclaringTypeName()).append(".")
//                .append(signature.getName()).append("},");

        // 处理请求参数
        String[] paramNames = ((MethodSignature) signature).getParameterNames();
        Object[] paramValues = joinPoint.getArgs();
        int paramLength = null == paramNames ? 0 : paramNames.length;
        if (paramLength == 0) {
            requestLog.append("param = [] ");
        } else {
            requestLog.append("param = [");
            for (int i = 0; i < paramLength - 1; i++) {
                requestLog.append(paramNames[i]).append("=").append(paramValues[i]).append(",");
            }
            requestLog.append(paramNames[paramLength - 1]).append("=").append(paramValues[paramLength - 1]).append("]");
        }
        log.info(requestLog.toString());
    }

}
