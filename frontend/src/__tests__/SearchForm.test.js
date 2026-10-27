import React from 'react';
import { render, screen, fireEvent, waitFor, within } from '@testing-library/react';
import SearchForm from '../components/SearchForm';

describe('SearchForm', () => {
  beforeEach(() => {
    global.fetch = jest.fn(async () => ({
      ok: true,
      status: 200,
      json: async () => ['pet friendly', 'pool'],
    }));
  });

  it('loads keyword options from the API into the Keywords dropdown', async () => {
    render(<SearchForm onSearch={jest.fn()} />);

    const keywords = screen.getByLabelText('Keywords');
    expect(keywords.tagName).toBe('SELECT');
    expect(await screen.findByRole('option', { name: 'pet friendly' })).toBeInTheDocument();
    expect(screen.getByRole('option', { name: 'pool' })).toBeInTheDocument();
    expect(within(keywords).getByRole('option', { name: 'Any' })).toBeInTheDocument();
  });

  it('sends the selected keyword with the search', async () => {
    const mockOnSearch = jest.fn();
    render(<SearchForm onSearch={mockOnSearch} />);

    await screen.findByRole('option', { name: 'pool' });
    fireEvent.change(screen.getByLabelText('Keywords'), { target: { value: 'pool' } });
    fireEvent.click(screen.getByRole('button', { name: 'Search Properties' }));

    expect(mockOnSearch).toHaveBeenCalledWith(expect.objectContaining({ keyword: 'pool' }));
  });

  it('keeps only the Any option when the keyword request fails', async () => {
    global.fetch = jest.fn(async () => {
      throw new Error('network down');
    });
    render(<SearchForm onSearch={jest.fn()} />);

    await waitFor(() => expect(global.fetch).toHaveBeenCalled());
    expect(screen.getByLabelText('Keywords').querySelectorAll('option')).toHaveLength(1);
  });

  it('renders the search form', () => {
    const mockOnSearch = jest.fn();
    render(<SearchForm onSearch={mockOnSearch} />);

    expect(screen.getByRole('heading', { name: 'Search Properties' })).toBeInTheDocument();
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

  it('calls onSearch with valid form data', async () => {
    const mockOnSearch = jest.fn();
    render(<SearchForm onSearch={mockOnSearch} />);

    const targetBudgetInput = screen.getByLabelText(/Target Budget/i);
    const cityInput = screen.getByLabelText(/City/i);
    const keywordInput = screen.getByLabelText(/Keywords/i);
    await screen.findByRole('option', { name: 'pet friendly' });

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
