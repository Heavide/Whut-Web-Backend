package com.app.backend.controller;

import com.app.backend.common.Result;
import com.app.backend.dto.ArticleRequest;
import com.app.backend.model.Article;
import com.app.backend.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "http://localhost:8081")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping
    public Result<List<Article>> getArticlesByUserId(@RequestParam("userId") Long userId,
                                                     @RequestParam(value = "query", required = false) String query) {
        try {
            Long currentUserId = userId;

            List<Article> articles;
            if (query != null && !query.isEmpty()) {
                articles = articleService.searchArticlesByUserId(currentUserId, query);
            } else {
                articles = articleService.getArticlesByUserId(currentUserId);
            }
            return Result.success(articles);
        } catch (Exception e) {
            log.error("获取文章列表失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Article> getArticleById(@PathVariable Long id, @RequestParam("userId") Long userId) {
        try {
            Article article = articleService.getArticleById(id);
            if (article == null) {
                return Result.fail("文章不存在");
            }

            if (!article.getUserId().equals(userId)) {
                return Result.fail("没有权限访问该文章");
            }

            return Result.success(article);
        } catch (Exception e) {
            log.error("获取文章详情失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping
    public Result<Article> createArticle(@RequestBody ArticleRequest articleRequest) {
        try {
            Article article = articleService.createArticle(articleRequest);
            return Result.success(article);
        } catch (Exception e) {
            log.error("创建文章失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Article> updateArticle(@PathVariable Long id,
                                         @RequestBody ArticleRequest articleRequest,
                                         @RequestParam("userId") Long userId) {
        try {
            Article existingArticle = articleService.getArticleById(id);
            if (existingArticle == null) {
                return Result.fail("文章不存在");
            }

            if (!existingArticle.getUserId().equals(userId)) {
                return Result.fail("没有权限更新该文章");
            }

            articleRequest.setId(id);
            Article article = articleService.updateArticle(articleRequest);
            return Result.success(article);
        } catch (Exception e) {
            log.error("获取新文章失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteArticle(@PathVariable Long id, @RequestParam("userId") Long userId) {
        try {
            Article existingArticle = articleService.getArticleById(id);
            if (existingArticle == null) {
                return Result.fail("文章不存在");
            }

            if (!existingArticle.getUserId().equals(userId)) {
                return Result.fail("没有权限删除该文章");
            }

            boolean result = articleService.deleteArticle(id);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除文章失败", e);
            return Result.fail(e.getMessage());
        }
    }
}