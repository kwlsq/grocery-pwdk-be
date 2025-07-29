package com.pwdk.grocereach.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponse<T> {
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;
  private boolean hasNext;
  private boolean hasPrevious;
  private List<T> content;

  public static class Utils {
    public static <E, R> PaginatedResponse<R> from(Page<E> page, List<R> content) {
      PaginatedResponse<R> response = new PaginatedResponse<>();
      response.setPage(page.getNumber());
      response.setSize(page.getSize());
      response.setTotalElements(page.getTotalElements());
      response.setTotalPages(page.getTotalPages());
      response.setHasNext(page.hasNext());
      response.setHasPrevious(page.hasPrevious());
      response.setContent(content);
      return response;
    }
  }
}
