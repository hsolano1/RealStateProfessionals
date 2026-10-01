import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import SearchForm from '../components/SearchForm';

describe('SearchForm', () => {
  it('renders the search form', () => {
    const mockOnSearch = jest.fn();
    render(<SearchForm onSearch={mockOnSearch} />);

    expect(screen.getByText('Search Properties')).toBeInTheDocument();
    expect(screen.getByLabelText(/Target Budget/i)).toBeInTheDocument();
  });

  it('validates target budget is required', () => {
    const mockOnSearch = jest.fn();
    render(<SearchForm onSearch={mockOnSearch} />);

    const targetBudgetInput = screen.getByLabelText(/Target Budget/i);
    fireEvent.change(targetBudgetInput, { target: { value: '' } });

    const submitButton = screen.getByRole('button', { name: /Search Properties/i });
    fireEvent.click(submitButton);

    expect(screen.getByText(/Target budget is required/i)).toBeInTheDocument();
  });

  it('validates min price is not greater than max price', () => {
    const mockOnSearch = jest.fn();
    render(<SearchForm onSearch={mockOnSearch} />);

    const minPriceInput = screen.getByLabelText(/Min Price/i);
    const maxPriceInput = screen.getByLabelText(/Max Price/i);

    fireEvent.change(minPriceInput, { target: { value: '600000' } });
    fireEvent.change(maxPriceInput, { target: { value: '400000' } });

    const submitButton = screen.getByRole('button', { name: /Search Properties/i });
    fireEvent.click(submitButton);

    expect(screen.getByText(/Minimum price cannot be greater than maximum price/i)).toBeInTheDocument();
  });

  it('calls onSearch with valid form data', () => {
    const mockOnSearch = jest.fn();
    render(<SearchForm onSearch={mockOnSearch} />);

    const targetBudgetInput = screen.getByLabelText(/Target Budget/i);
    const cityInput = screen.getByLabelText(/City/i);
    const keywordInput = screen.getByLabelText(/Keywords/i);

    fireEvent.change(targetBudgetInput, { target: { value: '450000' } });
    fireEvent.change(cityInput, { target: { value: 'Springfield' } });
    fireEvent.change(keywordInput, { target: { value: 'pet friendly' } });

    const submitButton = screen.getByRole('button', { name: /Search Properties/i });
    fireEvent.click(submitButton);

    expect(mockOnSearch).toHaveBeenCalledWith(expect.objectContaining({
      targetBudget: 450000,
      city: 'Springfield',
      keyword: 'pet friendly'
    }));
  });

  it('clears error message when user corrects input', () => {
    const mockOnSearch = jest.fn();
    render(<SearchForm onSearch={mockOnSearch} />);

    const targetBudgetInput = screen.getByLabelText(/Target Budget/i);
    fireEvent.change(targetBudgetInput, { target: { value: '' } });

    const submitButton = screen.getByRole('button', { name: /Search Properties/i });
    fireEvent.click(submitButton);

    expect(screen.getByText(/Target budget is required/i)).toBeInTheDocument();

    fireEvent.change(targetBudgetInput, { target: { value: '450000' } });
    expect(screen.queryByText(/Target budget is required/i)).not.toBeInTheDocument();
  });
});
