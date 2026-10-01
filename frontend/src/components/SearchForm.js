import React, { useState } from 'react';

function SearchForm({ onSearch }) {
  const [formData, setFormData] = useState({
    city: '',
    minPrice: '',
    maxPrice: '',
    minBedrooms: '',
    keyword: '',
    targetBudget: '450000',
  });

  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.targetBudget || formData.targetBudget.trim() === '') {
      newErrors.targetBudget = 'Target budget is required';
    } else if (isNaN(parseFloat(formData.targetBudget)) || parseFloat(formData.targetBudget) <= 0) {
      newErrors.targetBudget = 'Target budget must be a positive number';
    }

    if (formData.minPrice && isNaN(parseFloat(formData.minPrice))) {
      newErrors.minPrice = 'Minimum price must be a valid number';
    }

    if (formData.maxPrice && isNaN(parseFloat(formData.maxPrice))) {
      newErrors.maxPrice = 'Maximum price must be a valid number';
    }

    if (formData.minPrice && formData.maxPrice) {
      if (parseFloat(formData.minPrice) > parseFloat(formData.maxPrice)) {
        newErrors.minPrice = 'Minimum price cannot be greater than maximum price';
      }
    }

    if (formData.minBedrooms && isNaN(parseInt(formData.minBedrooms, 10))) {
      newErrors.minBedrooms = 'Minimum bedrooms must be a valid number';
    }

    if (formData.minBedrooms && parseInt(formData.minBedrooms, 10) < 0) {
      newErrors.minBedrooms = 'Minimum bedrooms cannot be negative';
    }

    return newErrors;
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    const validationErrors = validateForm();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    const searchData = {
      city: formData.city || null,
      minPrice: formData.minPrice ? parseFloat(formData.minPrice) : null,
      maxPrice: formData.maxPrice ? parseFloat(formData.maxPrice) : null,
      minBedrooms: formData.minBedrooms ? parseInt(formData.minBedrooms, 10) : null,
      keyword: formData.keyword || null,
      targetBudget: parseFloat(formData.targetBudget),
    };

    onSearch(searchData);
  };

  return (
    <form className="search-form" onSubmit={handleSubmit}>
      <h2>Search Properties</h2>

      <div className="form-row">
        <div className="form-group">
          <label htmlFor="city">City</label>
          <input
            type="text"
            id="city"
            name="city"
            placeholder="e.g., Springfield"
            value={formData.city}
            onChange={handleChange}
            className={errors.city ? 'input-error' : ''}
          />
          {errors.city && <span className="error-text">{errors.city}</span>}
        </div>

        <div className="form-group">
          <label htmlFor="targetBudget">Target Budget ($) <span className="required">*</span></label>
          <input
            type="number"
            id="targetBudget"
            name="targetBudget"
            placeholder="450000"
            value={formData.targetBudget}
            onChange={handleChange}
            className={errors.targetBudget ? 'input-error' : ''}
            step="1000"
          />
          {errors.targetBudget && <span className="error-text">{errors.targetBudget}</span>}
        </div>
      </div>

      <div className="form-row">
        <div className="form-group">
          <label htmlFor="minPrice">Min Price ($)</label>
          <input
            type="number"
            id="minPrice"
            name="minPrice"
            placeholder="300000"
            value={formData.minPrice}
            onChange={handleChange}
            className={errors.minPrice ? 'input-error' : ''}
            step="1000"
          />
          {errors.minPrice && <span className="error-text">{errors.minPrice}</span>}
        </div>

        <div className="form-group">
          <label htmlFor="maxPrice">Max Price ($)</label>
          <input
            type="number"
            id="maxPrice"
            name="maxPrice"
            placeholder="600000"
            value={formData.maxPrice}
            onChange={handleChange}
            className={errors.maxPrice ? 'input-error' : ''}
            step="1000"
          />
          {errors.maxPrice && <span className="error-text">{errors.maxPrice}</span>}
        </div>
      </div>

      <div className="form-row">
        <div className="form-group">
          <label htmlFor="minBedrooms">Minimum Bedrooms</label>
          <select
            id="minBedrooms"
            name="minBedrooms"
            value={formData.minBedrooms}
            onChange={handleChange}
            className={errors.minBedrooms ? 'input-error' : ''}
          >
            <option value="">Any</option>
            <option value="1">1+</option>
            <option value="2">2+</option>
            <option value="3">3+</option>
            <option value="4">4+</option>
            <option value="5">5+</option>
          </select>
          {errors.minBedrooms && <span className="error-text">{errors.minBedrooms}</span>}
        </div>

        <div className="form-group">
          <label htmlFor="keyword">Keywords</label>
          <input
            type="text"
            id="keyword"
            name="keyword"
            placeholder="e.g., pet friendly, garage"
            value={formData.keyword}
            onChange={handleChange}
          />
        </div>
      </div>

      <button type="submit" className="search-button">Search Properties</button>
    </form>
  );
}

export default SearchForm;
