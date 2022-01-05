package com.wpf.api

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature

/**
 *  Author: feipeng.wang
 *  Time:   2021/7/21
 *  Description : This is description.
 */
/**
@Pointcut("execution(" +//执行语句
"@com.wpf.api.AopOnclick" +//注解筛选
"*" + //类路径,*为任意路径
"*" + //方法名,*为任意方法名
"(..)" +//方法参数,'..'为任意个任意类型参数
")" +
" && " +//并集
)
@Aspect：声明切面，标记类
@Pointcut(切点表达式)：定义切点，标记方法
@Before(切点表达式)：前置通知，切点之前执行
@Around(切点表达式)：环绕通知，切点前后执行
@After(切点表达式)：后置通知，切点之后执行
@AfterReturning(切点表达式)：返回通知，切点方法返回结果之后执行
@AfterThrowing(切点表达式)：异常通知，切点抛出异常时执行
 * */
@Aspect
class AopClickAspect {
    /**
     * 定义切点，标记切点为所有被@AopOnclick注解的方法
     * 注意：这里com.wpf.api.AopOnclick需要替换成
     * 你自己项目中AopOnclick这个类的全路径
     */
    @Pointcut("execution(@com.wpf.api.AopOnclick * *(..))")
    fun methodAnnotated(){}
    /**
     * 定义一个切面方法，包裹切点方法
     */
    @Around("methodAnnotated()")
    @Throws(Throwable::class)
    fun aroundJoinPoint(joinPoint: ProceedingJoinPoint) {
        // 取出方法的注解
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method
        if (!method.isAnnotationPresent(AopOnclick::class.java)) {
            return
        }
        val aopOnclick = method.getAnnotation(AopOnclick::class.java)
        // 判断是否快速点击
        if (!AopClickUtil.isFastDoubleClick(aopOnclick.outTime)) {
            // 不是快速点击，执行原方法
            joinPoint.proceed()
        }
    }
}