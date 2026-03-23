package com.bpoconnect.model;

import jakarta.persistence.*;

@Entity
@Table(name = "knowledge_base")
public class KnowledgeBaseArticle {
    @Id
    private String articleId;
    private String title;
    @Column(length = 2000)
    private String content;
    private String category;
    private String tags;

    public KnowledgeBaseArticle() {}

    public KnowledgeBaseArticle(String articleId, String title, String content, String category) {
        this.articleId = articleId;
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public String getArticleId() { return articleId; }
    public String getTitle() { return title; }
}
