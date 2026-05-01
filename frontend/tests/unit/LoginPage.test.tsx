import { describe, it, expect, beforeEach, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from '../src/contexts/AuthContext';
import { LoginPage } from '../src/pages/LoginPage';

describe('LoginPage', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('should render login form', () => {
    render(
      <BrowserRouter>
        <AuthProvider>
          <LoginPage />
        </AuthProvider>
      </BrowserRouter>
    );

    expect(screen.getByTestId('login-title')).toHaveTextContent('Đăng nhập');
    expect(screen.getByTestId('username-input')).toBeInTheDocument();
    expect(screen.getByTestId('login-button')).toBeInTheDocument();
  });

  it('should show error when username is empty', () => {
    render(
      <BrowserRouter>
        <AuthProvider>
          <LoginPage />
        </AuthProvider>
      </BrowserRouter>
    );

    const submitButton = screen.getByTestId('login-button');
    fireEvent.click(submitButton);

    expect(screen.getByTestId('error-message')).toHaveTextContent('Vui lòng nhập tên đăng nhập');
  });

  it('should handle login submission', () => {
    render(
      <BrowserRouter>
        <AuthProvider>
          <LoginPage />
        </AuthProvider>
      </BrowserRouter>
    );

    const input = screen.getByTestId('username-input') as HTMLInputElement;
    fireEvent.change(input, { target: { value: 'testuser' } });

    expect(input.value).toBe('testuser');
  });
});
