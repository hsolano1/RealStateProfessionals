package com.realestate.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResponse {
    public Boolean success;
    public SearchData data;
    public String errorMessage;
    public LocalDateTime timestamp;

    public SearchResponse() {}

    public SearchResponse(Boolean success, SearchData data, String errorMessage, LocalDateTime timestamp) {
        this.success = success;
        this.data = data;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
    }

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public SearchData getData() { return data; }
    public void setData(SearchData data) { this.data = data; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public static class SearchData {
        public List<ListingDTO> listings;
        public Long totalCount;
        public Integer pageNumber;
        public Integer pageSize;
        public Integer totalPages;

        public SearchData() {}

        public SearchData(List<ListingDTO> listings, Long totalCount, Integer pageNumber, Integer pageSize, Integer totalPages) {
            this.listings = listings;
            this.totalCount = totalCount;
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
            this.totalPages = totalPages;
        }

        public List<ListingDTO> getListings() { return listings; }
        public void setListings(List<ListingDTO> listings) { this.listings = listings; }

        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }

        public Integer getPageNumber() { return pageNumber; }
        public void setPageNumber(Integer pageNumber) { this.pageNumber = pageNumber; }

        public Integer getPageSize() { return pageSize; }
        public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }

        public Integer getTotalPages() { return totalPages; }
        public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }
    }
}
