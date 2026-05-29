package com.claudev.agenda.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class TokenBlackListService {

    private static final String PREFIX = "blacklist";

    private final StringRedisTemplate redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public TokenBlackListService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Aggiunge il token alla blacklist con TTL = scadenza residua del token.
     * Redis elimina automaticamente la chiave quando scade → zero manutenzione.
     */

    public void blacklistToken (String token , Date dateExpiration) {
        long ttl = dateExpiration.getTime() - System.currentTimeMillis();
        if (ttl > 0) {
            redisTemplate.opsForValue().set(PREFIX + token,"revoked",ttl, TimeUnit.MILLISECONDS);
        }
    }

    public boolean isBlacklisted (String token) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + token));
        } catch (Exception exception) {
            logger.error("Redis error during blacklist check", exception);
            // Se Redis è down, SEMPRE blacklisare (fail-safe)
            return true; // Blocca la richiesta se non possiamo verificare
        }
    }

    // BLACK LIST TOKEN CON REDIS

    // CONFIGURAZIONE BLACKLIST TOKEN SENZA REDIS
    /*
    // token -> data di scadenza ( serve per la pulizia automatica )
    private final ConcurrentHashMap<String, Date> blackList = new ConcurrentHashMap<>();

    public void  blackListToken (String token , Date expiration) {
        blackList.put(token,expiration);
    }

    public boolean isBlackListed (String token) {
        return blackList.containsKey(token);
    }

    // pulizia periodica dei token giá scaduti
    @Scheduled(fixedRate = 3600000)
    public void cleanExpiredTokens () {
        Date now = new Date();
        blackList.entrySet().removeIf(entry -> entry.getValue().before(now));
    }

     */


}
