package com.example.hypertest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.hypertest.model.Post;
import com.example.hypertest.repository.PostRepository;

/* 
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
            PreparedStatement ps = connection.prepareStatement(insertQuery, new String[]{"id"});
            ps.setString(1, postName);
            ps.setString(2, postContents);
            return ps;
        }, keyHolder);

        int postId = keyHolder.getKey().intValue();

        String responseFromApi = restTemplate.getForObject("http://worldtimeapi.org/api/timezone/Asia/Kolkata", String.class);

        Map<String, Object> response = new HashMap<>();
        response.put("db_post", postId);
        response.put("http_outbound", responseFromApi);

        return ResponseEntity.ok(response);
    }
}*/

@RestController
@RequestMapping("/api")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping(value = "/createNewPost", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createNewPost(@RequestBody Post post) {
        try {
            Post savedPost = postRepository.save(post);

            // Log the post id from the database
            Long postId = savedPost.getId();
            System.out.println("New Post created with id: " + postId);

            // Make outbound HTTP call
            String apiUrl = "http://worldtimeapi.org/api/timezone/Asia/Kolkata";
            String httpResponseBody = restTemplate.getForObject(apiUrl, String.class);

            // Prepare response
            ApiResponse response = new ApiResponse(postId, httpResponseBody);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Inner class for API response structure
    private static class ApiResponse {
        private Long db_post;
        private String http_outbound;

        public ApiResponse(Long db_post, String http_outbound) {
            this.db_post = db_post;
            this.http_outbound = http_outbound;
        }

        // getters and setters
        public Long getDb_post() {
            return db_post;
        }

        public void setDb_post(Long db_post) {
            this.db_post = db_post;
        }

        public String getHttp_outbound() {
            return http_outbound;
        }

        public void setHttp_outbound(String http_outbound) {
            this.http_outbound = http_outbound;
        }
    }
}
 