package com.realestate.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResponse {
    private Boolean success;
    private SearchData data;
    private String errorMessage;
    private LocalDateTime timestamp;

    @Getter
    @Setter
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
