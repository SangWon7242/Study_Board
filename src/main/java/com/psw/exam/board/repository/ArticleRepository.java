package com.psw.exam.board.repository;

import com.psw.exam.board.dto.Article;
import com.psw.exam.board.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ArticleRepository {
  private int lastId;
  private List<Article> articles;

  public ArticleRepository() {
    lastId = 0;
    articles = new ArrayList<>();
  }

  public int write(int boardId, int memberId, String title, String body) {
    int id = lastId + 1;
    String regDate = Util.getNowDateStr();
    String updateDate = regDate;
    Article article = new Article(id, regDate, updateDate, boardId, memberId, title, body);
    articles.add(article);
    lastId = id;

    return id;
  }

  public List<Article> getArticles(int boardId, String searchKeywordTypeCode, String searchKeyword, String orderBy, int limitStart, int limitCount) {
//    if (boardId == 0 && searchKeyword.length() == 0) { // 둘다 0이면 기존 articles를 반환
//      return articles;
//    }

    List<Article> filteredArticles = new ArrayList<>();

    if (searchKeyword.length() > 0 ) {
      filteredArticles = new ArrayList<>();

      for (Article article : articles) {
        boolean matched = article.getTitle().contains(searchKeyword) || article.getBody().contains(searchKeyword);

        if (matched) {
          filteredArticles.add(article);
        }
      }
    }

    int dataIndex = 0;

    List<Article> sortedArticles = articles;

    boolean orderByIdDesc = orderBy.equals("idDesc");

    if (orderByIdDesc) {
      sortedArticles = Util.reverseList(sortedArticles);
    }

    for (Article article : sortedArticles) {

      if (boardId != 0) { // boardId가 0이 아니라는 건 치뤄야 할 시험이 더 있다는 뜻
        if (article.getBoardId() != boardId) { // 여기까지 다르다는 것은 실패 했다는 뜻
          continue;
        }
      }

      if (dataIndex >= limitStart) {
        filteredArticles.add(article);
      }

      dataIndex++;

      if(filteredArticles.size() == limitCount) {
        break;
      }

      if (searchKeyword.length() > 0) { // 아직 살아 있고 이거까지 있다는 건
        switch (searchKeywordTypeCode) {
          case "body":
            if (!article.getBody().contains(searchKeyword)) { // 거짓이라는 건 존재하지 않는 다는 뜻
              continue;
            }
            break;
          case "title,body":
            if (!article.getTitle().contains(searchKeyword) && !article.getBody().contains(searchKeyword)) {
              // 이 키워드는 title에도 찾아봐도 없고 body에도 찾았을 때 없는 경우
              // !false && !false => true && true -> 이 경우에는 false를 반환
              // !true && !false => false && true -> 이 경우는 계속 true
              continue;
            }
            break;
          case "title":
            if (!article.getTitle().contains(searchKeyword)) {
              continue;
            }
          default:
            break;
        }
      }
    }

    return filteredArticles;
  }

  public void deleteArticleById(int id) {
    Article article = getArticleById(id);

    if (article != null) {
      articles.remove(article);
    }
  }

  public Article getArticleById(int id) {
    for (Article article : articles) {
      if (article.getId() == id) {
        return article;
      }
    }

    return null;
  }

  public void modify(int id, String title, String body) {
    Article article = getArticleById(id);

    article.setTitle(title);
    article.setBody(body);
    article.setUpdateDate(Util.getNowDateStr());

  }
}
