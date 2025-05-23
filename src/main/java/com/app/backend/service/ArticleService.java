package com.app.backend.service;

import com.app.backend.dto.ArticleRequest;
import com.app.backend.model.Article;

import java.util.List;

public interface ArticleService {
    List<Article> getArticlesByUserId(Long userId);
    List<Article> searchArticlesByUserId(Long userId, String query);
    Article getArticleById(Long id);
    Article createArticle(ArticleRequest articleRequest);
    Article updateArticle(ArticleRequest articleRequest);
    boolean deleteArticle(Long id);
}