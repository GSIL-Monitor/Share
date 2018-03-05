package com.beta.domain.service.redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.beta.utils.mapper.SerializeHelper;
import com.beta.utils.reflect.StringUtil;

@Component
public class RedisCacheManager {

    static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheManager.class);

    static final String GROUP_FORMAT = "G:%s";

    @Autowired
    RedisTemplateService redisTemplateService;

    public void saveCacheGroup(EvaluationContext ctx, Cacheable cacheable, String cacheKey) {
        String[] groups = cacheable.group();
        for (String group : groups) {
            group = String.format(GROUP_FORMAT, group);
            String groupCacheKey = this.getCacheKey(ctx, group, cacheable.args());
            this.redisTemplateService.sadd(groupCacheKey, cacheKey);
            LOGGER.debug("save cache group : {}, members : {}", groupCacheKey, cacheKey);
        }
    }

    public void delByCacheGroup(EvaluationContext ctx, CacheEvict cacheEvict) {
        String[] groups = cacheEvict.group();
        for (String group : groups) {
            group = String.format(GROUP_FORMAT, group);
            String groupCacheKey = this.getCacheKey(ctx, group, cacheEvict.args());
            this.delByCacheGroup(ctx, groupCacheKey);
        }
    }

    public void delByCacheGroup(EvaluationContext ctx, String groupCacheKey) {
        byte[] k = this.redisTemplateService.getDomainKey(groupCacheKey).getBytes();
        this.redisTemplateService.execute(new RedisCallback<Object>() {
            @Override
            public Long doInRedis(RedisConnection conn) throws DataAccessException {
                // 获取所有缓存的 key
                Set<byte[]> vs = conn.sMembers(k);

                if (!vs.isEmpty()) {
                    List<String> vs2 = new ArrayList<>();
                    vs.forEach(v -> {
                        vs2.add(redisTemplateService.getDomainKey((String) SerializeHelper.deserialize(v)));
                    });
                    LOGGER.debug("del cache : {}", vs2.toString());
                    return conn.del(vs2.stream().map(String::getBytes).toArray(byte[][]::new));
                }
                return 0L;
            }
        });
        LOGGER.debug("del cache group : {}", groupCacheKey);
    }

    public void saveCache(String key, Object value, long expireTime) {
        LOGGER.debug("save cache : {}", key);
        this.redisTemplateService.set(key, value, expireTime);
    }

    public Object getCache(String key) {
        LOGGER.debug("get cache : {}", key);
        Object value = this.redisTemplateService.get(key);
        return value;
    }

    public void clearCache(EvaluationContext ctx, CacheEvict cacheEvict) {

        // 按key删除
        String[] keys = cacheEvict.key();
        for (String key : keys) {
            if (!StringUtil.isEmpty(key)) {
                key = this.getCacheKey(ctx, key, cacheEvict.args());
                this.redisTemplateService.delete(key);
                LOGGER.debug("del cache : {}", key);
            }
        }

        // 按keyPrefix删除
        Arrays.asList(cacheEvict.keyPrefix()).forEach(keyPrefix -> {
            String k = this.getCacheKey(ctx, keyPrefix, new String[0]);
            this.redisTemplateService.deleteByPrefix(k);
            LOGGER.debug("del cache : {}", k);
        });

        // 按group删除
        this.delByCacheGroup(ctx, cacheEvict);
    }

    /**
     * 
     * @param keyEl
     *            key 的EL表达式
     * @return
     */
    public String getCacheKey(EvaluationContext ctx, String keyEl, String[] args) {

        Object[] argValues = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            ExpressionParser parser = new SpelExpressionParser();
            Object v = parser.parseExpression(arg).getValue(ctx);
            String argValue = null;
            if (v != null) {
                argValue = v.toString();
            }
            argValues[i] = argValue;
        }
        String cacheKey = String.format(keyEl, argValues);
        return cacheKey;
    }

    public EvaluationContext getArgValues(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        String[] paramNames = methodSignature.getParameterNames();
        Object[] paramValues = joinPoint.getArgs();
        return this.getArgValues(paramNames, paramValues);
    }

    public EvaluationContext getArgValues(String[] paramNames, Object[] paramValues) {
        // 使用直接量表达式
        EvaluationContext ctx = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) {
            String paramName = paramNames[i];
            Object paramValue = paramValues[i];

            ctx.setVariable(paramName, paramValue);
        }
        return ctx;
    }

}
