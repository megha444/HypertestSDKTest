package com.example.hypertest.controller;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class PostController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/api/createNewPost")
    public ResponseEntity<Map<String, Object>> createNewPost(@RequestBody Map<String, String> request) {
        String postName = request.get("post_name");
        String postContents = request.get("post_contents");

        String insertQuery = "INSERT INTO posts (name, contents) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            try {
                PreparedStatement ps = connection.prepareStatement(insertQuery, new String[]{"id"});
                ps.setString(1, postName);
                ps.setString(2, postContents);
                return ps;
            } catch (SQLException ex) {
            }
            return null;
        }, keyHolder);

        int postId = keyHolder.getKey().intValue();

        String responseFromApi = restTemplate.getForObject("http://worldtimeapi.org/api/timezone/Asia/Kolkata", String.class);

        Map<String, Object> response = new HashMap<>();
        response.put("db_post", postId);
        response.put("http_outbound", responseFromApi);

        return ResponseEntity.ok(response);
    }
}