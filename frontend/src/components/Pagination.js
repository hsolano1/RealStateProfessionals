import React from 'react';

function Pagination({ currentPage, totalPages, onPageChange }) {
  const pages = [];
  const maxPagesToShow = 5;
  let startPage = Math.max(1, currentPage - Math.floor(maxPagesToShow / 2));
  let endPage = Math.min(totalPages, startPage + maxPagesToShow - 1);

  if (endPage - startPage + 1 < maxPagesToShow) {
    startPage = Math.max(1, endPage - maxPagesToShow + 1);
  }

  for (let i = startPage; i <= endPage; i++) {
    pages.push(i);
  }

  const handlePageClick = (page) => {
    if (page !== currentPage && page >= 1 && page <= totalPages) {
      onPageChange(page);
    }
  };

  return (
    <div className="pagination">
      <button
        className="pagination-button"
        onClick={() => handlePageClick(1)}
        disabled={currentPage === 1}
      >
        « First
      </button>

      <button
        className="pagination-button"
        onClick={() => handlePageClick(currentPage - 1)}
        disabled={currentPage === 1}
      >
        ‹ Previous
      </button>

      <div className="pagination-pages">
        {startPage > 1 && (
          <>
            <button
              className="pagination-button"
              onClick={() => handlePageClick(1)}
            >
              1
            </button>
            {startPage > 2 && <span className="pagination-ellipsis">...</span>}
          </>
        )}

        {pages.map(page => (
          <button
            key={page}
            className={`pagination-button ${currentPage === page ? 'active' : ''}`}
            onClick={() => handlePageClick(page)}
          >
            {page}
          </button>
        ))}

        {endPage < totalPages && (
          <>
            {endPage < totalPages - 1 && <span className="pagination-ellipsis">...</span>}
            <button
              className="pagination-button"
              onClick={() => handlePageClick(totalPages)}
            >
              {totalPages}
            </button>
          </>
        )}
      </div>

      <button
        className="pagination-button"
        onClick={() => handlePageClick(currentPage + 1)}
        disabled={currentPage === totalPages}
      >
        Next ›
      </button>

      <button
        className="pagination-button"
        onClick={() => handlePageClick(totalPages)}
        disabled={currentPage === totalPages}
      >
        Last »
      </button>

      <span className="pagination-info">
        Page {currentPage} of {totalPages}
      </span>
    </div>
  );
}

export default Pagination;
