package com.app.backend.service.impl;

import com.app.backend.dto.ArticleRequest;
import com.app.backend.mapper.ArticleMapper;
import com.app.backend.model.Article;
import com.app.backend.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public List<Article> getArticlesByUserId(Long userId) {
        return articleMapper.findByUserId(userId);
    }

    @Override
    public List<Article> searchArticlesByUserId(Long userId, String query) {
        if (query == null || query.trim().isEmpty()) {
            return getArticlesByUserId(userId);
        }
        return articleMapper.searchByUserId(userId, query);
    }

    @Override
    public Article getArticleById(Long id) {
        return articleMapper.findById(id);
    }

    @Override
    @Transactional
    public Article createArticle(ArticleRequest articleRequest) {
        Article article = new Article();
        article.setTitle(articleRequest.getTitle());
        article.setContent(articleRequest.getContent());
        article.setUserId(articleRequest.getUserId());

        Date now = new Date();
        article.setCreateTime(now);
        article.setUpdateTime(now);

        int result = articleMapper.insert(article);
        if (result <= 0) {
            throw new RuntimeException("创建文章失败");
        }

        return articleMapper.findById(article.getId());
    }

    @Override
    @Transactional
    public Article updateArticle(ArticleRequest articleRequest) {
        Article existingArticle = articleMapper.findById(articleRequest.getId());
        if (existingArticle == null) {
            throw new RuntimeException("文章不存在");
        }

        existingArticle.setTitle(articleRequest.getTitle());
        existingArticle.setContent(articleRequest.getContent());
        existingArticle.setUpdateTime(new Date());

        int result = articleMapper.update(existingArticle);
        if (result <= 0) {
            throw new RuntimeException("更新文章失败");
        }

        return articleMapper.findById(existingArticle.getId());
    }

    @Override
    @Transactional
    public boolean deleteArticle(Long id) {
        Article article = articleMapper.findById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        return articleMapper.deleteById(id) > 0;
    }
}