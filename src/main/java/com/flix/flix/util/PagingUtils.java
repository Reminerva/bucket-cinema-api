package com.flix.flix.util;

import com.flix.flix.model.response.PagingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PagingUtils {
    public static <T>Page<T> listToPage(List<T> list, Pageable pageable) {
        if (list == null || list.isEmpty()) {
            return Page.empty(pageable);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());

        if (start > end){
            return Page.empty(pageable);
        }

        List<T> sublist = list.subList(start, end);

        return new PageImpl<>(sublist, pageable, list.size());
    }

    public static <T> PagingResponse pageToPagingResponse(Page<T> pageable){
        return PagingResponse.builder()
                .totalPages(pageable.getTotalPages())
                .totalElement(pageable.getTotalElements())
                .page(pageable.getNumber() + 1)
                .size(pageable.getSize())
                .hasNext(pageable.hasNext())
                .hasPrevious(pageable.hasPrevious())
                .build();
    }
}
