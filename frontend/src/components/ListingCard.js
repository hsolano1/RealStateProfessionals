import React from 'react';

function ListingCard({ listing }) {
  const formatPrice = (price) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
    }).format(price);
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  const getStatusBadge = (status) => {
    const statusMap = {
      'active': 'badge-active',
      'pending': 'badge-pending',
      'sold': 'badge-sold',
    };
    return statusMap[status] || 'badge-default';
  };

  return (
    <div className="listing-card">
      <div className="listing-header">
        <div className="listing-title">
          <h3>{listing.address}</h3>
          <p className="listing-location">{listing.city}, {listing.state} {listing.zip}</p>
        </div>
        <div className="listing-meta">
          <span className={`badge ${getStatusBadge(listing.status)}`}>
            {listing.status.charAt(0).toUpperCase() + listing.status.slice(1)}
          </span>
          {listing.relevanceScore !== null && (
            <div className="relevance-score">
              Score: <strong>{listing.relevanceScore.toFixed(1)}</strong>
            </div>
          )}
        </div>
      </div>

      <div className="listing-body">
        <div className="listing-price">
          <span className="price-label">List Price</span>
          <span className="price-value">{formatPrice(listing.price)}</span>
        </div>

        <div className="listing-features">
          <div className="feature">
            <span className="feature-value">{listing.bedrooms}</span>
            <span className="feature-label">Beds</span>
          </div>
          <div className="feature">
            <span className="feature-value">{listing.bathrooms}</span>
            <span className="feature-label">Baths</span>
          </div>
          <div className="feature">
            <span className="feature-value">{listing.sqft?.toLocaleString()}</span>
            <span className="feature-label">Sq Ft</span>
          </div>
        </div>

        {listing.description && (
          <div className="listing-description">
            <p>{listing.description}</p>
          </div>
        )}

        <div className="listing-footer">
          <span className="listed-date">Listed: {formatDate(listing.listedDate)}</span>
          {listing.latitude && listing.longitude && (
            <span className="coordinates">
              📍 {listing.latitude.toFixed(4)}, {listing.longitude.toFixed(4)}
            </span>
          )}
        </div>
      </div>
    </div>
  );
}

export default ListingCard;
