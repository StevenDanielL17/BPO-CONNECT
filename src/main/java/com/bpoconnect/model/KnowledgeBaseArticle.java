package com.bpoconnect.model;

import jakarta.persistence.*;

@Entity
@Table(name = "knowledge_base")
@SuppressWarnings("unused")
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

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}


