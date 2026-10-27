import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import Pagination from '../components/Pagination';

describe('Pagination', () => {
  it('renders Previous, one button per page, and Next', () => {
    render(<Pagination currentPage={1} totalPages={4} onPageChange={jest.fn()} />);

    expect(screen.getByRole('button', { name: 'Previous' })).toBeInTheDocument();
    ['1', '2', '3', '4'].forEach(label => {
      expect(screen.getByRole('button', { name: label })).toBeInTheDocument();
    });
    expect(screen.getByRole('button', { name: 'Next' })).toBeInTheDocument();
  });

  it('disables Previous on the first page and Next on the last page', () => {
    const { rerender } = render(
      <Pagination currentPage={1} totalPages={3} onPageChange={jest.fn()} />
    );
    expect(screen.getByRole('button', { name: 'Previous' })).toBeDisabled();
    expect(screen.getByRole('button', { name: 'Next' })).not.toBeDisabled();

    rerender(<Pagination currentPage={3} totalPages={3} onPageChange={jest.fn()} />);
    expect(screen.getByRole('button', { name: 'Previous' })).not.toBeDisabled();
    expect(screen.getByRole('button', { name: 'Next' })).toBeDisabled();
  });

  it('marks the current page as active and disables its button', () => {
    render(<Pagination currentPage={2} totalPages={4} onPageChange={jest.fn()} />);

    const current = screen.getByRole('button', { name: '2' });
    expect(current).toHaveClass('active');
    expect(current).toBeDisabled();
    expect(current).toHaveAttribute('aria-current', 'page');
  });

  it('calls onPageChange with the clicked page number', () => {
    const onPageChange = jest.fn();
    render(<Pagination currentPage={1} totalPages={4} onPageChange={onPageChange} />);

    fireEvent.click(screen.getByRole('button', { name: '3' }));
    expect(onPageChange).toHaveBeenLastCalledWith(3);
  });

  it('calls onPageChange with the adjacent page for Previous and Next', () => {
    const onPageChange = jest.fn();
    render(<Pagination currentPage={2} totalPages={4} onPageChange={onPageChange} />);

    fireEvent.click(screen.getByRole('button', { name: 'Next' }));
    expect(onPageChange).toHaveBeenLastCalledWith(3);

    fireEvent.click(screen.getByRole('button', { name: 'Previous' }));
    expect(onPageChange).toHaveBeenLastCalledWith(1);
  });
});
