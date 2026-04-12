package com.bpoconnect.service;

import com.bpoconnect.model.KnowledgeBaseArticle;
import com.bpoconnect.repository.KnowledgeBaseRepository;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class KnowledgeBaseService {
    private final KnowledgeBaseRepository kbRepository;


    public KnowledgeBaseService(KnowledgeBaseRepository kbRepository) {
        this.kbRepository = kbRepository;
    }

    public List<KnowledgeBaseArticle> searchArticles(String query) {
        String safeQuery = Objects.requireNonNull(query, "query");
        return kbRepository.findByTitleContainingOrTagsContaining(safeQuery, safeQuery);
    }

    public List<KnowledgeBaseArticle> getAllArticles() {
        return kbRepository.findAll();
    }

    public KnowledgeBaseArticle saveArticle(KnowledgeBaseArticle article) {
        KnowledgeBaseArticle safeArticle = Objects.requireNonNull(article, "article");
        if (safeArticle.getArticleId() == null || safeArticle.getArticleId().isBlank()) {
            safeArticle.setArticleId("KB-" + UUID.randomUUID().toString().substring(0, 8));
        }
        return kbRepository.save(safeArticle);
    }
}
