package com.example.hypertest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hypertest.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
