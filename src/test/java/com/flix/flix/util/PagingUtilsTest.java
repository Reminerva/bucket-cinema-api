package com.flix.flix.util;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.flix.flix.model.response.ArtistResponse;
import com.flix.flix.model.response.PagingResponse;

public class PagingUtilsTest {

    @Test
    void testListToPage() {
        List<ArtistResponse> list = List.of(new ArtistResponse());
        PageRequest pageable = PageRequest.of(0, 10);
        Page<ArtistResponse> page = PagingUtils.listToPage(list, pageable);

        assert(page.getContent().size() == 1);
    }

    @Test
    void testListToPage_withEmptyList() {
        List<ArtistResponse> list = List.of();
        PageRequest pageable = PageRequest.of(0, 10);
        Page<ArtistResponse> page = PagingUtils.listToPage(list, pageable);

        assert(page.getContent().size() == 0);
    }

    @Test
    void testListToPage_withNullList() {
        List<ArtistResponse> list = null;
        PageRequest pageable = PageRequest.of(0, 10);
        Page<ArtistResponse> page = PagingUtils.listToPage(list, pageable);

        assert(page.getContent().size() == 0);
    }

    @Test
    void testPageToPagingResponse() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<ArtistResponse> page = PagingUtils.listToPage(List.of(new ArtistResponse()), pageable);
        PagingResponse pagingResponse = PagingUtils.pageToPagingResponse(page);

        assert(pagingResponse.getTotalPages() == 1);
    }
}
