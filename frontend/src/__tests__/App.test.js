import React from 'react';
import { render, screen, fireEvent, waitFor, within } from '@testing-library/react';
import App from '../App';

const TOTAL_COUNT = 10;

function makeListing(index) {
  return {
    id: `id-${index}`,
    address: `${index} Test St`,
    city: 'Springfield',
    state: 'VA',
    zip: '22150',
    price: 450000,
    bedrooms: 2,
    bathrooms: 1.5,
    sqft: 980,
    latitude: 38.7,
    longitude: -77.1,
    listedDate: '2026-08-29',
    status: 'active',
    description: 'Test listing',
    relevanceScore: 80 - index,
  };
}

function searchResponse(body) {
  const pageSize = body.pageSize;
  const pageNumber = body.pageNumber;
  const totalPages = Math.ceil(TOTAL_COUNT / pageSize);
  const start = (pageNumber - 1) * pageSize;
  const count = Math.min(pageSize, TOTAL_COUNT - start);
  const listings = Array.from({ length: count }, (_, i) => makeListing(start + i + 1));
  return {
    ok: true,
    status: 200,
    json: async () => ({
      success: true,
      data: { listings, totalCount: TOTAL_COUNT, pageNumber, pageSize, totalPages },
    }),
  };
}

const KEYWORDS_RESPONSE = {
  ok: true,
  status: 200,
  json: async () => ['pool', 'garage'],
};

function routeFetch(searchImpl) {
  return jest.fn(async (url, options) => {
    if (String(url).endsWith('/listings/keywords')) return KEYWORDS_RESPONSE;
    return searchImpl(url, options);
  });
}

function sentBodies() {
  return global.fetch.mock.calls
    .filter(([url]) => String(url).endsWith('/listings/search'))
    .map(([, options]) => JSON.parse(options.body));
}

async function runSearch() {
  fireEvent.click(screen.getByRole('button', { name: 'Search Properties' }));
  await waitFor(() => expect(document.querySelector('.results-list')).not.toBeNull());
}

describe('App results pagination', () => {
  beforeEach(() => {
    global.fetch = routeFetch(async (url, options) => searchResponse(JSON.parse(options.body)));
    window.scrollTo = jest.fn();
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('shows 3 results per page by default', async () => {
    render(<App />);
    await runSearch();

    expect(sentBodies()[0].pageSize).toBe(3);
    expect(document.querySelectorAll('.results-list > *')).toHaveLength(3);
    expect(within(document.querySelector('.page-size-control')).getByRole('combobox')).toHaveValue('3');
  });

  it('offers 3, 5 and 10 page size options', async () => {
    render(<App />);
    await runSearch();

    const options = within(document.querySelector('.page-size-control'))
      .getAllByRole('option')
      .map(option => option.value);
    expect(options).toEqual(['3', '5', '10']);
  });

  it('re-runs the search from page 1 when the page size changes', async () => {
    render(<App />);
    await runSearch();

    fireEvent.change(
      within(document.querySelector('.page-size-control')).getByRole('combobox'),
      { target: { value: '5' } }
    );

    await waitFor(() => expect(sentBodies()).toHaveLength(2));
    expect(sentBodies()[1]).toMatchObject({ pageNumber: 1, pageSize: 5 });
    await waitFor(() =>
      expect(document.querySelectorAll('.results-list > *')).toHaveLength(5)
    );
  });

  it('requests the selected page with the current page size', async () => {
    render(<App />);
    await runSearch();

    fireEvent.click(screen.getByRole('button', { name: '2' }));

    await waitFor(() => expect(sentBodies()).toHaveLength(2));
    expect(sentBodies()[1]).toMatchObject({ pageNumber: 2, pageSize: 3 });
    await waitFor(() =>
      expect(document.querySelectorAll('.results-list > *')).toHaveLength(3)
    );
  });

  it('keeps the chosen page size when navigating pages', async () => {
    render(<App />);
    await runSearch();

    fireEvent.change(
      within(document.querySelector('.page-size-control')).getByRole('combobox'),
      { target: { value: '10' } }
    );
    await waitFor(() => expect(sentBodies()).toHaveLength(2));
    await waitFor(() =>
      expect(document.querySelectorAll('.results-list > *')).toHaveLength(10)
    );
    expect(screen.queryByRole('button', { name: 'Next' })).toBeNull();
  });

  it('does not send a search request before a search has run', async () => {
    render(<App />);
    expect(document.querySelector('.page-size-control')).toBeNull();
    expect(sentBodies()).toHaveLength(0);
  });

  it('shows the server error message when a search fails', async () => {
    global.fetch = routeFetch(async () => ({
      ok: true,
      status: 200,
      json: async () => ({ success: false, errorMessage: 'Search is down' }),
    }));
    render(<App />);
    fireEvent.click(screen.getByRole('button', { name: 'Search Properties' }));

    expect(await screen.findByText('Search is down')).toBeInTheDocument();
  });
});
