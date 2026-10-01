import React from 'react';

function LoadingSpinner() {
  return (
    <div className="loading-spinner-container">
      <div className="loading-spinner">
        <div className="spinner"></div>
        <p>Loading properties...</p>
      </div>
    </div>
  );
}

export default LoadingSpinner;
