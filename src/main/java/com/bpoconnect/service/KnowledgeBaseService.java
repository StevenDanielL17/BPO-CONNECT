package com.bpoconnect.service;

import com.bpoconnect.model.KnowledgeBaseArticle;
import com.bpoconnect.repository.KnowledgeBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class KnowledgeBaseService {
    private final KnowledgeBaseRepository kbRepository;

    @Autowired
    public KnowledgeBaseService(KnowledgeBaseRepository kbRepository) {
        this.kbRepository = kbRepository;
    }

    public List<KnowledgeBaseArticle> searchArticles(String query) {
        return kbRepository.findByTitleContainingOrTagsContaining(query, query);
    }

    public KnowledgeBaseArticle saveArticle(KnowledgeBaseArticle article) {
        return kbRepository.save(article);
    }
}
