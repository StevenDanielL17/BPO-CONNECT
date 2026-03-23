package com.bpoconnect.repository;

import com.bpoconnect.model.KnowledgeBaseArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBaseArticle, String> {
    List<KnowledgeBaseArticle> findByTitleContainingOrTagsContaining(String title, String tags);
}
