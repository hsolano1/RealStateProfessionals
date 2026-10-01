import React from 'react';
import ListingCard from './ListingCard';

function ResultsList({ listings }) {
  if (!listings || listings.length === 0) {
    return (
      <div className="no-results">
        <p>No listings available.</p>
      </div>
    );
  }

  return (
    <div className="results-list">
      {listings.map(listing => (
        <ListingCard key={listing.id} listing={listing} />
      ))}
    </div>
  );
}

export default ResultsList;
