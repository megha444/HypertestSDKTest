package com.example.hypertest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hypertest.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    
}
