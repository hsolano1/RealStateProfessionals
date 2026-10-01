package com.realestate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResponse {
    private Boolean success;
    private SearchData data;
    private String errorMessage;
    private LocalDateTime timestamp;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchData {
        private List<ListingDTO> listings;
        private Long totalCount;
        private Integer pageNumber;
        private Integer pageSize;
        private Integer totalPages;
    }
}
