import React, { useState } from 'react';
import SearchForm from './components/SearchForm';
import ResultsList from './components/ResultsList';
import Pagination from './components/Pagination';
import LoadingSpinner from './components/LoadingSpinner';
import './styles/App.css';

const API_URL = 'http://localhost:8080/api/v1/listings/search';
const PAGE_SIZE_OPTIONS = [3, 5, 10];
const DEFAULT_PAGE_SIZE = 3;

async function requestPage(params, pageNumber, pageSize) {
  const response = await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ ...params, pageNumber, pageSize }),
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  const data = await response.json();
  if (!data.success) {
    throw new Error(data.errorMessage || 'An error occurred while searching');
  }
  return data.data;
}

function App() {
  const [listings, setListings] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [totalCount, setTotalCount] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [pageSize, setPageSize] = useState(DEFAULT_PAGE_SIZE);
  const [lastSearchParams, setLastSearchParams] = useState(null);

  const loadPage = async (params, pageNumber, size) => {
    setLoading(true);
    setError(null);

    try {
      const data = await requestPage(params, pageNumber, size);
      setListings(data.listings);
      setTotalCount(data.totalCount);
      setCurrentPage(data.pageNumber);
      setTotalPages(data.totalPages);
    } catch (err) {
      setError(err.message);
      setListings([]);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (formData) => {
    setLastSearchParams(formData);
    return loadPage(formData, 1, pageSize);
  };

  const handlePageChange = (newPage) => {
    if (!lastSearchParams) return;
    loadPage(lastSearchParams, newPage, pageSize);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handlePageSizeChange = (event) => {
    const newSize = Number(event.target.value);
    setPageSize(newSize);
    if (lastSearchParams) {
      loadPage(lastSearchParams, 1, newSize);
    }
  };

  return (
    <div className="App">
      <header className="app-header">
        <div className="header-content">
          <h1>Real Estate Professionals</h1>
          <p>Find Your Perfect Property</p>
        </div>
      </header>

      <main className="app-main">
        <div className="search-container">
          <SearchForm onSearch={handleSearch} />
        </div>

        {loading && <LoadingSpinner />}

        {error && (
          <div className="error-message">
            <p>{error}</p>
          </div>
        )}

        {!loading && listings.length === 0 && !error && lastSearchParams && (
          <div className="no-results">
            <p>No listings found matching your criteria. Try adjusting your filters.</p>
          </div>
        )}

        {listings.length > 0 && (
          <div className="results-container">
            <div className="results-header">
              <p>Found <strong>{totalCount}</strong> properties</p>
              <label className="page-size-control">
                Show
                <select value={pageSize} onChange={handlePageSizeChange}>
                  {PAGE_SIZE_OPTIONS.map(size => (
                    <option key={size} value={size}>{size}</option>
                  ))}
                </select>
                per page
              </label>
            </div>

            <ResultsList listings={listings} />

            {totalPages > 1 && (
              <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={handlePageChange}
              />
            )}
          </div>
        )}
      </main>

      <footer className="app-footer">
        <p>&copy; 2026 Real Estate Professionals. All rights reserved.</p>
      </footer>
    </div>
  );
}

export default App;
