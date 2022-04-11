package com.library.music.actuators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHealth implements HealthIndicator {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String DB = "database-service";

    private boolean isHealthGood(){
//        custom logic
        Integer result = jdbcTemplate.queryForObject("select 1",Integer.class);
        if(result.equals(1))
            return true;
        else
            return false;
    }

    @Override
    public Health health() {

        if(isHealthGood())
            return Health.up().withDetail(DB,"The Database service is up and running..").build();
        return Health.down().withDetail(DB,"The Database service is down").build();

    }
}
