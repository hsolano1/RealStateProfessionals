import React, { useState, useEffect } from 'react';
import SearchForm from './components/SearchForm';
import ResultsList from './components/ResultsList';
import Pagination from './components/Pagination';
import LoadingSpinner from './components/LoadingSpinner';
import './styles/App.css';

function App() {
  const [listings, setListings] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [totalCount, setTotalCount] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [lastSearchParams, setLastSearchParams] = useState(null);

  const handleSearch = async (formData) => {
    setLoading(true);
    setError(null);
    setCurrentPage(1);
    setLastSearchParams({ ...formData, pageNumber: 1 });

    try {
      const searchRequest = {
        ...formData,
        pageNumber: 1,
        pageSize: pageSize,
      };

      const response = await fetch('http://localhost:8080/api/v1/listings/search', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(searchRequest),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();

      if (data.success) {
        setListings(data.data.listings);
        setTotalCount(data.data.totalCount);
        setCurrentPage(data.data.pageNumber);
        setTotalPages(data.data.totalPages);
      } else {
        setError(data.errorMessage || 'An error occurred while searching');
        setListings([]);
      }
    } catch (err) {
      setError(`Error: ${err.message}`);
      setListings([]);
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = async (newPage) => {
    if (!lastSearchParams) return;

    setLoading(true);
    setError(null);

    try {
      const searchRequest = {
        ...lastSearchParams,
        pageNumber: newPage,
        pageSize: pageSize,
      };

      const response = await fetch('http://localhost:8080/api/v1/listings/search', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(searchRequest),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();

      if (data.success) {
        setListings(data.data.listings);
        setCurrentPage(data.data.pageNumber);
        setTotalPages(data.data.totalPages);
        window.scrollTo({ top: 0, behavior: 'smooth' });
      } else {
        setError(data.errorMessage || 'An error occurred while fetching results');
      }
    } catch (err) {
      setError(`Error: ${err.message}`);
    } finally {
      setLoading(false);
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
